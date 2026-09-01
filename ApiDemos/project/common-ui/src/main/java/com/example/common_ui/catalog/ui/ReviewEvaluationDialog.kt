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
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageButton
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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.File

/**
 * On-device reviewer grading dialog with pinned bottom action bar containing
 * "Save & Next ⏭️", "Save Issue ⚠️", and "Cancel".
 *
 * Preserves exact aspect ratio, supports multi-touch pinch-to-zoom, pan, drawing,
 * and external editor integration.
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
            // Freeze and capture the current screen before rendering the dialog
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

        val dialog = Dialog(activity, com.google.android.material.R.style.Theme_Material3_DayNight_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val displayMetrics = activity.resources.displayMetrics
        val dialogMaxHeight = (displayMetrics.heightPixels * 0.88).toInt()

        // Main Dialog Root Layout (Vertical)
        val rootLayout = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                (displayMetrics.widthPixels * 0.94).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.WHITE)
        }

        // 1. Header Bar (Pinned Top)
        val headerLayout = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(40, 32, 24, 20)
            setBackgroundColor(Color.parseColor("#F5F5F7"))
        }

        val titleCol = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val headerTitle = TextView(activity).apply {
            text = if (status == ReviewStatus.PASSING) "👍 Good Job: ${sample.title}" else "⚠️ Something's Wrong: ${sample.title}"
            textSize = 17f
            setTypeface(null, Typeface.BOLD)
            setTextColor(if (status == ReviewStatus.PASSING) Color.parseColor("#2E7D32") else Color.parseColor("#C62828"))
        }
        val headerSub = TextView(activity).apply {
            text = "Evaluation for ${framework.displayName}"
            textSize = 12f
            setTextColor(Color.parseColor("#757575"))
        }
        titleCol.addView(headerTitle)
        titleCol.addView(headerSub)

        val closeBtn = ImageButton(activity).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setBackgroundColor(Color.TRANSPARENT)
            setOnClickListener { dialog.dismiss() }
        }

        headerLayout.addView(titleCol)
        headerLayout.addView(closeBtn)
        rootLayout.addView(headerLayout)

        // 2. Scrollable Body Content (Fills remaining height)
        val scrollBody = ScrollView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            isFillViewport = true
        }

        val bodyLayout = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 24, 40, 24)
        }
        scrollBody.addView(bodyLayout)

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
            setBackgroundResource(android.R.drawable.edit_text)
        }
        bodyLayout.addView(input)

        var canvasView: AnnotationCanvasView? = null
        var externalEditedFile: File? = null

        if (status == ReviewStatus.NEEDS_WORK && capturedBitmap != null) {
            val markupLabel = TextView(activity).apply {
                text = "📸 Highlight / Circle Problem Areas (Auto-saved on Save/Next):"
                textSize = 13f
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 24, 0, 10)
                setTextColor(Color.parseColor("#37474F"))
            }
            bodyLayout.addView(markupLabel)

            // Drawing Toolbar
            val toolsScroll = HorizontalScrollView(activity).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                isHorizontalScrollBarEnabled = false
            }
            val toolsLayout = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 0, 0, 10)
            }
            toolsScroll.addView(toolsLayout)

            fun createEmojiBtn(emoji: String, tooltip: String, minWidthDp: Int = 46): MaterialButton {
                return MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                    text = emoji
                    textSize = 17f
                    setPadding(16, 6, 16, 6)
                    minimumWidth = (minWidthDp * resources.displayMetrics.density).toInt()
                    strokeWidth = (1.5f * resources.displayMetrics.density).toInt()
                    strokeColor = ColorStateList.valueOf(Color.parseColor("#CFD8DC"))
                    setBackgroundColor(Color.TRANSPARENT)
                    tooltipText = tooltip
                    contentDescription = tooltip
                }
            }

            val penBtn = createEmojiBtn("🔴", "Draw Red Circle").apply { minWidth = 140 }
            val highlightBtn = createEmojiBtn("🟡", "Yellow Highlighter").apply { minWidth = 140 }
            val panZoomBtn = createEmojiBtn("🖐️", "Pan & Pinch Zoom").apply { minWidth = 140 }
            val resetZoomBtn = createEmojiBtn("🔍", "Reset Zoom to 1:1")
            val undoBtn = createEmojiBtn("↩️", "Undo Stroke")
            val clearBtn = createEmojiBtn("🗑️", "Clear All Strokes")
            val systemEditorBtn = createEmojiBtn("✏️", "Open in System Markup Editor")
            val reloadEditorBtn = createEmojiBtn("📥", "Reload External Edits")

            // Divider views for clear visual groups
            fun createDivider(): View {
                return View(activity).apply {
                    val params = LinearLayout.LayoutParams((1 * resources.displayMetrics.density).toInt(), (28 * resources.displayMetrics.density).toInt()).apply {
                        marginStart = 12
                        marginEnd = 12
                        gravity = Gravity.CENTER_VERTICAL
                    }
                    layoutParams = params
                    setBackgroundColor(Color.parseColor("#CFD8DC"))
                }
            }

            toolsLayout.addView(penBtn)
            toolsLayout.addView(highlightBtn)
            toolsLayout.addView(panZoomBtn)
            toolsLayout.addView(createDivider())
            toolsLayout.addView(resetZoomBtn)
            toolsLayout.addView(undoBtn)
            toolsLayout.addView(clearBtn)
            toolsLayout.addView(createDivider())
            toolsLayout.addView(systemEditorBtn)
            toolsLayout.addView(reloadEditorBtn)
            bodyLayout.addView(toolsScroll)

            // Active Tool Status Label (Unmistakable clear indicator)
            val activeToolStatus = TextView(activity).apply {
                textSize = 12f
                setTypeface(null, Typeface.BOLD)
                setPadding(24, 8, 24, 8)
                setBackgroundColor(Color.parseColor("#FFEBEE"))
                setTextColor(Color.parseColor("#C62828"))
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    bottomMargin = 14
                }
                layoutParams = params
                text = "Active Tool: 🔴 Draw Red Circle"
            }
            bodyLayout.addView(activeToolStatus)

            // Canvas Container
            val canvasHeight = (displayMetrics.heightPixels * 0.38).toInt()
            val canvasContainer = FrameLayout(activity).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, canvasHeight)
                setBackgroundColor(Color.parseColor("#1E1E2C"))
            }

            val canvas = AnnotationCanvasView(activity).apply {
                layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setBaseBitmap(capturedBitmap)
            }
            canvasView = canvas
            canvasContainer.addView(canvas)
            bodyLayout.addView(canvasContainer)

            fun updateToolSelection(activeTool: AnnotationCanvasView.ToolMode) {
                canvas.currentTool = activeTool
                val density = activity.resources.displayMetrics.density
                val normalBorderWidth = (1.5f * density).toInt()
                val activeBorderWidth = (2.5f * density).toInt()
                val inactiveStroke = ColorStateList.valueOf(Color.parseColor("#CFD8DC"))

                when (activeTool) {
                    AnnotationCanvasView.ToolMode.PEN_CIRCLE -> {
                        penBtn.setBackgroundColor(Color.parseColor("#FFCDD2")) // Light red fill
                        penBtn.strokeColor = ColorStateList.valueOf(Color.parseColor("#D32F2F"))
                        penBtn.strokeWidth = activeBorderWidth

                        highlightBtn.setBackgroundColor(Color.TRANSPARENT)
                        highlightBtn.strokeColor = inactiveStroke
                        highlightBtn.strokeWidth = normalBorderWidth

                        panZoomBtn.setBackgroundColor(Color.TRANSPARENT)
                        panZoomBtn.strokeColor = inactiveStroke
                        panZoomBtn.strokeWidth = normalBorderWidth

                        activeToolStatus.text = "Active Tool: 🔴 Draw Red Circle (Touch and drag on image)"
                        activeToolStatus.setBackgroundColor(Color.parseColor("#FFEBEE"))
                        activeToolStatus.setTextColor(Color.parseColor("#C62828"))
                    }
                    AnnotationCanvasView.ToolMode.HIGHLIGHTER -> {
                        penBtn.setBackgroundColor(Color.TRANSPARENT)
                        penBtn.strokeColor = inactiveStroke
                        penBtn.strokeWidth = normalBorderWidth

                        highlightBtn.setBackgroundColor(Color.parseColor("#FFF9C4")) // Light yellow fill
                        highlightBtn.strokeColor = ColorStateList.valueOf(Color.parseColor("#F57F17"))
                        highlightBtn.strokeWidth = activeBorderWidth

                        panZoomBtn.setBackgroundColor(Color.TRANSPARENT)
                        panZoomBtn.strokeColor = inactiveStroke
                        panZoomBtn.strokeWidth = normalBorderWidth

                        activeToolStatus.text = "Active Tool: 🟡 Yellow Highlighter (Swipe across text/map)"
                        activeToolStatus.setBackgroundColor(Color.parseColor("#FFFDE7"))
                        activeToolStatus.setTextColor(Color.parseColor("#F57F17"))
                    }
                    AnnotationCanvasView.ToolMode.PAN_ZOOM -> {
                        penBtn.setBackgroundColor(Color.TRANSPARENT)
                        penBtn.strokeColor = inactiveStroke
                        penBtn.strokeWidth = normalBorderWidth

                        highlightBtn.setBackgroundColor(Color.TRANSPARENT)
                        highlightBtn.strokeColor = inactiveStroke
                        highlightBtn.strokeWidth = normalBorderWidth

                        panZoomBtn.setBackgroundColor(Color.parseColor("#BBDEFB")) // Light blue fill
                        panZoomBtn.strokeColor = ColorStateList.valueOf(Color.parseColor("#1565C0"))
                        panZoomBtn.strokeWidth = activeBorderWidth

                        activeToolStatus.text = "Active Tool: 🖐️ Pan & Zoom (Drag 1 finger to pan, pinch to zoom)"
                        activeToolStatus.setBackgroundColor(Color.parseColor("#E3F2FD"))
                        activeToolStatus.setTextColor(Color.parseColor("#1565C0"))
                    }
                }
            }

            // Set initial selection
            updateToolSelection(AnnotationCanvasView.ToolMode.PEN_CIRCLE)

            penBtn.setOnClickListener {
                updateToolSelection(AnnotationCanvasView.ToolMode.PEN_CIRCLE)
            }
            highlightBtn.setOnClickListener {
                updateToolSelection(AnnotationCanvasView.ToolMode.HIGHLIGHTER)
            }
            panZoomBtn.setOnClickListener {
                updateToolSelection(AnnotationCanvasView.ToolMode.PAN_ZOOM)
            }
            resetZoomBtn.setOnClickListener {
                canvas.resetZoomAndPan()
                Toast.makeText(activity, "🔍 Zoom reset to 1:1", Toast.LENGTH_SHORT).show()
            }
            undoBtn.setOnClickListener {
                canvas.undoStroke()
                Toast.makeText(activity, "↩️ Undid last stroke", Toast.LENGTH_SHORT).show()
            }
            clearBtn.setOnClickListener {
                canvas.clearAllStrokes()
                Toast.makeText(activity, "🗑️ Cleared all strokes", Toast.LENGTH_SHORT).show()
            }
            systemEditorBtn.setOnClickListener {
                try {
                    val merged = canvas.createAnnotatedBitmap() ?: capturedBitmap
                    val tempFile = ScreenCaptureHelper.saveBitmap(activity, merged, sample.id)
                    externalEditedFile = tempFile
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
                    Toast.makeText(activity, "Save in editor, then tap '📥 Reload'", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(activity, "System editor unavailable: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            reloadEditorBtn.setOnClickListener {
                val file = externalEditedFile
                if (file != null && file.exists()) {
                    val reloadedBitmap = BitmapFactory.decodeFile(file.absolutePath)
                    if (reloadedBitmap != null) {
                        canvas.setBaseBitmap(reloadedBitmap)
                        Toast.makeText(activity, "📥 Loaded edits from system editor!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "No external edit file found yet", Toast.LENGTH_SHORT).show()
                }
            }
        }

        rootLayout.addView(scrollBody)

        // 3. Pinned Bottom Action Bar (ALWAYS VISIBLE!)
        val bottomBar = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(24, 16, 24, 20)
            setBackgroundColor(Color.parseColor("#F5F5F7"))
        }

        val cancelBtn = MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
            text = "Cancel"
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f)
            setOnClickListener { dialog.dismiss() }
        }

        val saveCurrentBtn = MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
            text = if (status == ReviewStatus.PASSING) "Save Pass 👍" else "Save Issue ⚠️"
            textSize = 12f
            val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.1f).apply {
                marginStart = 12
                marginEnd = 12
            }
            layoutParams = params
            setOnClickListener {
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
                    dialog.dismiss()
                    showUndoSnackbar(activity, targetFqcn, sample, framework, status, repository)
                }
            }
        }

        val saveAndNextBtn = MaterialButton(activity).apply {
            text = "Save & Next ⏭️"
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setBackgroundColor(Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f)
            setOnClickListener {
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
                    dialog.dismiss()
                    advanceToNextUnchecked(activity, sample, framework, repository)
                }
            }
        }

        bottomBar.addView(cancelBtn)
        bottomBar.addView(saveCurrentBtn)
        bottomBar.addView(saveAndNextBtn)
        rootLayout.addView(bottomBar)

        dialog.setContentView(rootLayout)
        dialog.window?.setLayout((displayMetrics.widthPixels * 0.94).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
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
