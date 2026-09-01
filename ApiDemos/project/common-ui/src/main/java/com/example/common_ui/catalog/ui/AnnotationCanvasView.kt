/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.common_ui.catalog.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

/**
 * Interactive touch-drawing overlay that preserves exact screenshot aspect ratio
 * and provides rock-solid multi-touch pinch-to-zoom and panning without jitter or release jumps.
 */
class AnnotationCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class ToolMode {
        PEN_CIRCLE,
        HIGHLIGHTER,
        PAN_ZOOM
    }

    private data class Stroke(
        val path: Path,
        val color: Int,
        val strokeWidth: Float,
        val alpha: Int
    )

    private var baseBitmap: Bitmap? = null
    private val strokes = mutableListOf<Stroke>()
    private var currentStroke: Stroke? = null

    var currentTool: ToolMode = ToolMode.PEN_CIRCLE

    private val penColor = Color.parseColor("#E53935") // Bright Red
    private val highlightColor = Color.parseColor("#FFD600") // Vivid Yellow

    // Transformation Matrices
    private val baseFitMatrix = Matrix()
    private val userTransformMatrix = Matrix()
    private val totalMatrix = Matrix()
    private val inverseMatrix = Matrix()

    private val matrixValues = FloatArray(9)
    private var currentScale = 1.0f

    // Smooth Touch & Pointer Tracking
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isPinching = false

    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isPinching = true
            // Discard active drawing stroke if user starts pinching
            if (currentStroke != null) {
                strokes.remove(currentStroke)
                currentStroke = null
                invalidate()
            }
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val targetScale = currentScale * scaleFactor

            if (targetScale in 0.95f..6.0f) {
                currentScale = targetScale.coerceIn(1.0f, 6.0f)
                userTransformMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                clampTransform()
                recalculateMatrices()
                invalidate()
            }
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            isPinching = false
            clampTransform()
            recalculateMatrices()
            invalidate()
        }
    })

    fun setBaseBitmap(bitmap: Bitmap) {
        this.baseBitmap = bitmap
        strokes.clear()
        currentStroke = null
        resetZoomAndPan()
    }

    fun resetZoomAndPan() {
        currentScale = 1.0f
        userTransformMatrix.reset()
        updateBaseFitMatrix()
        recalculateMatrices()
        invalidate()
    }

    fun undoStroke() {
        if (strokes.isNotEmpty()) {
            strokes.removeAt(strokes.size - 1)
            invalidate()
        }
    }

    fun clearAllStrokes() {
        strokes.clear()
        currentStroke = null
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateBaseFitMatrix()
        recalculateMatrices()
    }

    private fun updateBaseFitMatrix() {
        val bmp = baseBitmap ?: return
        if (width <= 0 || height <= 0) return

        val scaleX = width.toFloat() / bmp.width.toFloat()
        val scaleY = height.toFloat() / bmp.height.toFloat()
        val fitScale = kotlin.math.min(scaleX, scaleY)

        val fitWidth = bmp.width * fitScale
        val fitHeight = bmp.height * fitScale

        val baseOffsetX = (width - fitWidth) / 2f
        val baseOffsetY = (height - fitHeight) / 2f

        baseFitMatrix.reset()
        baseFitMatrix.postScale(fitScale, fitScale)
        baseFitMatrix.postTranslate(baseOffsetX, baseOffsetY)
    }

    private fun recalculateMatrices() {
        totalMatrix.set(baseFitMatrix)
        totalMatrix.postConcat(userTransformMatrix)
        totalMatrix.invert(inverseMatrix)
    }

    private fun clampTransform() {
        val bmp = baseBitmap ?: return
        if (width <= 0 || height <= 0) return

        // Compute current scale from matrix
        userTransformMatrix.getValues(matrixValues)
        val scaleX = matrixValues[Matrix.MSCALE_X]
        currentScale = scaleX.coerceIn(1.0f, 6.0f)

        // Prevent dragging image completely offscreen
        val maxPanX = width.toFloat() * (currentScale - 0.5f).coerceAtLeast(0f)
        val maxPanY = height.toFloat() * (currentScale - 0.5f).coerceAtLeast(0f)

        val transX = matrixValues[Matrix.MTRANS_X].coerceIn(-maxPanX, maxPanX)
        val transY = matrixValues[Matrix.MTRANS_Y].coerceIn(-maxPanY, maxPanY)

        matrixValues[Matrix.MTRANS_X] = transX
        matrixValues[Matrix.MTRANS_Y] = transY
        userTransformMatrix.setValues(matrixValues)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Prevent parent ScrollView from stealing touch gestures while interacting with canvas
        parent?.requestDisallowInterceptTouchEvent(true)

        scaleDetector.onTouchEvent(event)

        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = event.getPointerId(0)
                lastTouchX = event.getX(0)
                lastTouchY = event.getY(0)

                if (currentTool == ToolMode.PAN_ZOOM) {
                    return true
                }

                // Map touch to native bitmap coordinates
                val pts = floatArrayOf(lastTouchX, lastTouchY)
                inverseMatrix.mapPoints(pts)
                val bmpX = pts[0]
                val bmpY = pts[1]

                val bmp = baseBitmap
                if (bmp != null && bmpX in 0f..bmp.width.toFloat() && bmpY in 0f..bmp.height.toFloat()) {
                    val path = Path().apply { moveTo(bmpX, bmpY) }
                    val strokeWidth = if (currentTool == ToolMode.PEN_CIRCLE) 12f else 36f
                    val color = if (currentTool == ToolMode.PEN_CIRCLE) penColor else highlightColor
                    val alpha = if (currentTool == ToolMode.PEN_CIRCLE) 255 else 115
                    val stroke = Stroke(path, color, strokeWidth, alpha)
                    currentStroke = stroke
                    strokes.add(stroke)
                    invalidate()
                }
                return true
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // Secondary finger touched down -> start multi-touch panning
                isPinching = true
                if (currentStroke != null) {
                    strokes.remove(currentStroke)
                    currentStroke = null
                    invalidate()
                }
                val index = event.actionIndex
                lastTouchX = event.getX(index)
                lastTouchY = event.getY(index)
                activePointerId = event.getPointerId(index)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerId)
                if (pointerIndex == -1) return true

                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)

                val dx = x - lastTouchX
                val dy = y - lastTouchY
                lastTouchX = x
                lastTouchY = y

                // If currently zooming, multi-touching, or in pan/zoom mode: pan the viewport
                if (isPinching || event.pointerCount > 1 || currentTool == ToolMode.PAN_ZOOM) {
                    userTransformMatrix.postTranslate(dx, dy)
                    clampTransform()
                    recalculateMatrices()
                    invalidate()
                    return true
                }

                // Drawing in bitmap space
                val pts = floatArrayOf(x, y)
                inverseMatrix.mapPoints(pts)
                val bmpX = pts[0]
                val bmpY = pts[1]

                currentStroke?.path?.lineTo(bmpX, bmpY)
                invalidate()
                return true
            }

            MotionEvent.ACTION_POINTER_UP -> {
                // One finger lifted up: smoothly switch active pointer to prevent jumps!
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = event.getX(newPointerIndex)
                    lastTouchY = event.getY(newPointerIndex)
                    activePointerId = event.getPointerId(newPointerIndex)
                }
                isPinching = false
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
                currentStroke = null
                isPinching = false
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bmp = baseBitmap ?: return
        canvas.save()
        canvas.concat(totalMatrix)

        // 1. Draw base screenshot maintaining natural aspect ratio
        canvas.drawBitmap(bmp, 0f, 0f, null)

        // 2. Draw reviewer strokes in native bitmap coordinates
        for (stroke in strokes) {
            val paint = Paint().apply {
                color = stroke.color
                this.strokeWidth = stroke.strokeWidth
                this.alpha = stroke.alpha
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
                isAntiAlias = true
            }
            canvas.drawPath(stroke.path, paint)
        }

        canvas.restore()
    }

    /**
     * Merges the original screenshot bitmap with the reviewer's drawn annotations
     * directly at original resolution without any distortion or crop.
     */
    fun createAnnotatedBitmap(): Bitmap? {
        val bmp = baseBitmap ?: return null
        val output = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        // Draw base screenshot
        canvas.drawBitmap(bmp, 0f, 0f, null)

        // Render all annotation paths directly in bitmap coordinates
        for (stroke in strokes) {
            val paint = Paint().apply {
                color = stroke.color
                this.strokeWidth = stroke.strokeWidth
                this.alpha = stroke.alpha
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
                isAntiAlias = true
            }
            canvas.drawPath(stroke.path, paint)
        }

        return output
    }
}
