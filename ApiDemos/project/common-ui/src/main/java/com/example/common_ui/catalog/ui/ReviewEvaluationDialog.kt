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
import android.widget.EditText
import android.widget.Toast
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.repository.SampleReviewRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * On-device dialog for recording reviewer ratings and notes directly from a running sample or catalog card.
 */
object ReviewEvaluationDialog {

    @JvmStatic
    fun show(
        activity: Activity,
        sample: SampleItem,
        framework: Framework,
        initialStatus: ReviewStatus = ReviewStatus.PASSING
    ) {
        val targetFqcn = sample.getTargetFqcn(framework)
        val repository = SampleReviewRepository.getInstance(activity)

        val input = EditText(activity).apply {
            hint = if (initialStatus == ReviewStatus.PASSING) {
                "Notes & Feedback (optional for passing)"
            } else {
                "Describe what's broken, unexpected behavior, or UI flaws..."
            }
            minLines = 3
            maxLines = 6
            setPadding(48, 32, 48, 32)
        }

        val dialogTitle = if (initialStatus == ReviewStatus.PASSING) {
            "👍 Good Job: ${sample.title}"
        } else {
            "⚠️ Something's Wrong: ${sample.title}"
        }

        val positiveBtnText = if (initialStatus == ReviewStatus.PASSING) {
            "Record Pass 👍"
        } else {
            "Record Issue ⚠️"
        }

        MaterialAlertDialogBuilder(activity)
            .setTitle(dialogTitle)
            .setMessage("Recording evaluation for:\n$targetFqcn (${framework.displayName})")
            .setView(input)
            .setPositiveButton(positiveBtnText) { _, _ ->
                val notes = input.text.toString().trim()
                repository.saveEvaluation(targetFqcn, initialStatus, notes, sample)
                val statusMsg = if (initialStatus == ReviewStatus.PASSING) "Recorded as Passing 👍" else "Flagged as Needs Work ⚠️"
                Toast.makeText(activity, "$statusMsg for ${sample.title}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
