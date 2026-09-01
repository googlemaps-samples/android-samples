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
import kotlin.math.max
import kotlin.math.min

/**
 * Interactive touch-drawing overlay that preserves exact screenshot aspect ratio
 * and supports smooth multi-touch pinch-to-zoom, panning, and precise drawing
 * in native bitmap coordinate space.
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

    // Transformation Matrix & Zoom/Pan state
    private val viewMatrix = Matrix()
    private val inverseMatrix = Matrix()
    private val matrixValues = FloatArray(9)

    private var userScale = 1.0f
    private var translationX = 0.0f
    private var translationY = 0.0f

    private var lastTouchX = 0.0f
    private var lastTouchY = 0.0f
    private var isMultiTouch = false

    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val prevScale = userScale
            userScale = (userScale * scaleFactor).coerceIn(1.0f, 6.0f)

            // Adjust translation to zoom toward gesture focus point
            val focusX = detector.focusX
            val focusY = detector.focusY
            translationX += (focusX - translationX) * (1 - userScale / prevScale)
            translationY += (focusY - translationY) * (1 - userScale / prevScale)

            clampTranslation()
            updateMatrix()
            invalidate()
            return true
        }
    })

    fun setBaseBitmap(bitmap: Bitmap) {
        this.baseBitmap = bitmap
        strokes.clear()
        currentStroke = null
        resetZoomAndPan()
    }

    fun resetZoomAndPan() {
        userScale = 1.0f
        translationX = 0.0f
        translationY = 0.0f
        updateMatrix()
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
        updateMatrix()
    }

    private fun updateMatrix() {
        val bmp = baseBitmap ?: return
        if (width <= 0 || height <= 0) return

        // Compute fit-center scale keeping exact aspect ratio
        val scaleX = width.toFloat() / bmp.width.toFloat()
        val scaleY = height.toFloat() / bmp.height.toFloat()
        val fitScale = min(scaleX, scaleY)

        val fitWidth = bmp.width * fitScale
        val fitHeight = bmp.height * fitScale

        val baseOffsetX = (width - fitWidth) / 2f
        val baseOffsetY = (height - fitHeight) / 2f

        viewMatrix.reset()
        // 1. Center fitted bitmap in view
        viewMatrix.postTranslate(baseOffsetX, baseOffsetY)
        viewMatrix.postScale(fitScale, fitScale, baseOffsetX, baseOffsetY)
        // 2. Apply user zoom and pan
        val centerX = width / 2f
        val centerY = height / 2f
        viewMatrix.postScale(userScale, userScale, centerX, centerY)
        viewMatrix.postTranslate(translationX, translationY)

        viewMatrix.invert(inverseMatrix)
    }

    private fun clampTranslation() {
        val bmp = baseBitmap ?: return
        val maxPanX = width.toFloat() * (userScale - 0.8f).coerceAtLeast(0f)
        val maxPanY = height.toFloat() * (userScale - 0.8f).coerceAtLeast(0f)
        translationX = translationX.coerceIn(-maxPanX, maxPanX)
        translationY = translationY.coerceIn(-maxPanY, maxPanY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Disallow parent ScrollView from stealing touch gestures
        parent?.requestDisallowInterceptTouchEvent(true)

        scaleDetector.onTouchEvent(event)

        if (event.pointerCount > 1) {
            isMultiTouch = true
            // Discard active stroke if user started multi-touch zoom
            if (currentStroke != null) {
                strokes.remove(currentStroke)
                currentStroke = null
            }
            return true
        }

        if (isMultiTouch && event.actionMasked == MotionEvent.ACTION_UP) {
            isMultiTouch = false
            return true
        }

        val screenX = event.x
        val screenY = event.y

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = screenX
                lastTouchY = screenY

                if (currentTool == ToolMode.PAN_ZOOM) {
                    return true
                }

                // Map touch coordinate into native bitmap coordinate space
                val pts = floatArrayOf(screenX, screenY)
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
            MotionEvent.ACTION_MOVE -> {
                val dx = screenX - lastTouchX
                val dy = screenY - lastTouchY
                lastTouchX = screenX
                lastTouchY = screenY

                if (currentTool == ToolMode.PAN_ZOOM || isMultiTouch) {
                    translationX += dx
                    translationY += dy
                    clampTranslation()
                    updateMatrix()
                    invalidate()
                    return true
                }

                val pts = floatArrayOf(screenX, screenY)
                inverseMatrix.mapPoints(pts)
                val bmpX = pts[0]
                val bmpY = pts[1]

                currentStroke?.path?.lineTo(bmpX, bmpY)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                currentStroke = null
                isMultiTouch = false
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
        canvas.concat(viewMatrix)

        // Draw original screenshot with native dimensions
        canvas.drawBitmap(bmp, 0f, 0f, null)

        // Draw reviewer strokes in native bitmap coordinates
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
     * directly at original resolution without any distortion, cropping, or aspect ratio loss.
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
