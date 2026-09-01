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

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.repository.SampleReviewRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.File

/**
 * On-device reviewer grading dialog with automated screen capture, interactive in-app markup
 * with pinch-to-zoom, panning, aspect-ratio preservation, external system editor integration,
 * and persistent issue recording.
 */
object ReviewEvaluationDialog {

    @JvmStatic
    fun show(
        activity: Activity,
        sample: SampleItem,
        framework: Framework,
        initialStatus: ReviewStatus = ReviewStatus.PASSING
    ) {
        if (initialStatus == ReviewStatus.NEEDS_WORK) {
            // Capture the current screen before rendering the dialog
            ScreenCaptureHelper.captureActivity(activity) { capturedBitmap ->
                showEvaluationDialogInternal(activity, sample, framework, initialStatus, capturedBitmap)
            }
        } else {
            showEvaluationDialogInternal(activity, sample, framework, initialStatus, null)
        }
    }

    private fun showEvaluationDialogInternal(
        activity: Activity,
        sample: SampleItem,
        framework: Framework,
        status: ReviewStatus,
        capturedBitmap: Bitmap?
    ) {
        val targetFqcn = sample.getTargetFqcn(framework)
        val repository = SampleReviewRepository.getInstance(activity)

        val scrollContainer = ScrollView(activity).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            isFillViewport = true
        }

        val contentLayout = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 16)
        }
        scrollContainer.addView(contentLayout)

        // Notes Input
        val input = EditText(activity).apply {
            hint = if (status == ReviewStatus.PASSING) {
                "Notes & Feedback (optional for passing)"
            } else {
                "Describe what's broken, unexpected behavior, or UI flaws..."
            }
            minLines = if (status == ReviewStatus.PASSING) 3 else 2
            maxLines = 4
            setPadding(28, 20, 28, 20)
        }
        contentLayout.addView(input)

        var canvasView: AnnotationCanvasView? = null

        if (status == ReviewStatus.NEEDS_WORK && capturedBitmap != null) {
            // Header for Annotation
            val markupLabel = TextView(activity).apply {
                text = "📸 Highlight / Circle Problem Areas (Pinch to zoom & pan):"
                textSize = 13f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 20, 0, 10)
            }
            contentLayout.addView(markupLabel)

            // Toolbar for drawing tools
            val toolsScroll = HorizontalScrollView(activity).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                isHorizontalScrollBarEnabled = false
            }
            val toolsLayout = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 0, 0, 10)
            }
            toolsScroll.addView(toolsLayout)

            val penBtn = MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = "🔴 Circle"
                textSize = 11f
                setPadding(20, 8, 20, 8)
                strokeColor = android.content.res.ColorStateList.valueOf(Color.parseColor("#E53935"))
            }
            val highlightBtn = MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = "🟡 Highlight"
                textSize = 11f
                setPadding(20, 8, 20, 8)
            }
            val panZoomBtn = MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = "🖐️ Pan/Zoom"
                textSize = 11f
                setPadding(20, 8, 20, 8)
            }
            val resetZoomBtn = MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = "🔍 1:1 Reset"
                textSize = 11f
                setPadding(20, 8, 20, 8)
            }
            val undoBtn = MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = "↩️ Undo"
                textSize = 11f
                setPadding(20, 8, 20, 8)
            }
            val clearBtn = MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = "🔄 Clear"
                textSize = 11f
                setPadding(20, 8, 20, 8)
            }
            val systemEditorBtn = MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = "✏️ System Editor"
                textSize = 11f
                setPadding(20, 8, 20, 8)
            }

            toolsLayout.addView(penBtn)
            toolsLayout.addView(highlightBtn)
            toolsLayout.addView(panZoomBtn)
            toolsLayout.addView(resetZoomBtn)
            toolsLayout.addView(undoBtn)
            toolsLayout.addView(clearBtn)
            toolsLayout.addView(systemEditorBtn)
            contentLayout.addView(toolsScroll)

            // Drawing canvas container with generous height preserving natural aspect ratio
            val canvasContainer = FrameLayout(activity).apply {
                val displayMetrics = activity.resources.displayMetrics
                val targetHeight = (displayMetrics.heightPixels * 0.42).toInt()
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, targetHeight)
                setBackgroundColor(Color.parseColor("#1E1E2C"))
            }

            val canvas = AnnotationCanvasView(activity).apply {
                layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setBaseBitmap(capturedBitmap)
            }
            canvasView = canvas
            canvasContainer.addView(canvas)
            contentLayout.addView(canvasContainer)

            fun updateToolSelection(activeTool: AnnotationCanvasView.ToolMode) {
                canvas.currentTool = activeTool
                val activeStroke = android.content.res.ColorStateList.valueOf(Color.parseColor("#1976D2"))
                val defaultStroke = android.content.res.ColorStateList.valueOf(Color.parseColor("#BDBDBD"))
                val redStroke = android.content.res.ColorStateList.valueOf(Color.parseColor("#E53935"))

                penBtn.strokeColor = if (activeTool == AnnotationCanvasView.ToolMode.PEN_CIRCLE) redStroke else defaultStroke
                highlightBtn.strokeColor = if (activeTool == AnnotationCanvasView.ToolMode.HIGHLIGHTER) activeStroke else defaultStroke
                panZoomBtn.strokeColor = if (activeTool == AnnotationCanvasView.ToolMode.PAN_ZOOM) activeStroke else defaultStroke
            }

            penBtn.setOnClickListener {
                updateToolSelection(AnnotationCanvasView.ToolMode.PEN_CIRCLE)
                Toast.makeText(activity, "🔴 Pen / Circle: Draw red annotations", Toast.LENGTH_SHORT).show()
            }
            highlightBtn.setOnClickListener {
                updateToolSelection(AnnotationCanvasView.ToolMode.HIGHLIGHTER)
                Toast.makeText(activity, "🟡 Highlight: Yellow translucent marker", Toast.LENGTH_SHORT).show()
            }
            panZoomBtn.setOnClickListener {
                updateToolSelection(AnnotationCanvasView.ToolMode.PAN_ZOOM)
                Toast.makeText(activity, "🖐️ Pan/Zoom: Drag 1 finger to pan, pinch to zoom", Toast.LENGTH_SHORT).show()
            }
            resetZoomBtn.setOnClickListener {
                canvas.resetZoomAndPan()
                Toast.makeText(activity, "🔍 Zoom reset to 1:1", Toast.LENGTH_SHORT).show()
            }
            undoBtn.setOnClickListener {
                canvas.undoStroke()
            }
            clearBtn.setOnClickListener {
                canvas.clearAllStrokes()
            }
            systemEditorBtn.setOnClickListener {
                try {
                    val merged = canvas.createAnnotatedBitmap() ?: capturedBitmap
                    val tempFile = ScreenCaptureHelper.saveBitmap(activity, merged, sample.id)
                    val uri: Uri = FileProvider.getUriForFile(
                        activity,
                        "${activity.packageName}.fileprovider",
                        tempFile
                    )
                    val editIntent = Intent(Intent.ACTION_EDIT).apply {
                        setDataAndType(uri, "image/png")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    }
                    activity.startActivity(Intent.createChooser(editIntent, "Edit Screenshot with Markup"))
                } catch (e: Exception) {
                    Toast.makeText(activity, "System editor unavailable: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val dialogTitle = if (status == ReviewStatus.PASSING) {
            "👍 Good Job: ${sample.title}"
        } else {
            "⚠️ Something's Wrong: ${sample.title}"
        }

        val positiveBtnText = if (status == ReviewStatus.PASSING) "Save Pass 👍" else "Save Issue ⚠️"

        MaterialAlertDialogBuilder(activity)
            .setTitle(dialogTitle)
            .setMessage("Evaluation for ${framework.displayName}")
            .setView(scrollContainer)
            .setPositiveButton("Save & Next ⏭️") { _, _ ->
                val notes = input.text.toString().trim()
                var screenshotPath: String? = null
                if (canvasView != null) {
                    val finalBitmap = canvasView.createAnnotatedBitmap() ?: capturedBitmap
                    if (finalBitmap != null) {
                        val savedFile = ScreenCaptureHelper.saveBitmap(activity, finalBitmap, sample.id)
                        screenshotPath = savedFile.absolutePath
                    }
                }
                repository.saveEvaluation(targetFqcn, status, notes, sample, screenshotPath) {
                    advanceToNextUnchecked(activity, sample, framework, repository)
                }
            }
            .setNeutralButton(positiveBtnText) { _, _ ->
                val notes = input.text.toString().trim()
                var screenshotPath: String? = null
                if (canvasView != null) {
                    val finalBitmap = canvasView.createAnnotatedBitmap() ?: capturedBitmap
                    if (finalBitmap != null) {
                        val savedFile = ScreenCaptureHelper.saveBitmap(activity, finalBitmap, sample.id)
                        screenshotPath = savedFile.absolutePath
                    }
                }
                repository.saveEvaluation(targetFqcn, status, notes, sample, screenshotPath) {
                    showUndoSnackbar(activity, targetFqcn, sample, framework, status, repository)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun advanceToNextUnchecked(
        activity: Activity,
        currentSample: SampleItem,
        framework: Framework,
        repository: SampleReviewRepository
    ) {
        if (activity is AppCompatActivity) {
            activity.lifecycleScope.launch {
                val nextSample = repository.getNextUncheckedSample(currentSample.id, framework)
                if (nextSample != null) {
                    SampleReviewRepository.launchSample(activity, nextSample, framework)
                } else {
                    Toast.makeText(activity, "🎉 All ${framework.displayName} samples reviewed!", Toast.LENGTH_LONG).show()
                    activity.finish()
                }
            }
        }
    }

    fun showUndoSnackbar(
        activity: Activity,
        targetFqcn: String,
        sample: SampleItem,
        framework: Framework,
        status: ReviewStatus,
        repository: SampleReviewRepository
    ) {
        val rootView = activity.findViewById<View>(android.R.id.content) ?: return
        val statusText = if (status == ReviewStatus.PASSING) "Marked Pass 👍" else "Flagged Issue ⚠️"

        Snackbar.make(rootView, "$statusText: ${sample.title}", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                repository.deleteEvaluation(targetFqcn) {
                    Toast.makeText(activity, "Reverted ${sample.title} to Unchecked", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }
}
