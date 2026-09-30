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

package com.example.common_ui.catalog.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common_ui.R
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleItem

/**
 * Host component managing the display of catalog dialogs (confirmation, detail viewer, quick grading).
 */
@Composable
fun CatalogDialogHost(
    state: CatalogState,
    onSaveEvaluation: ((targetFqcn: String, status: ReviewStatus, notes: String, sample: SampleItem) -> Unit)?,
    onClearEvaluations: (() -> Unit)?,
    onLaunchSample: (SampleItem, Framework) -> Unit
) {
    if (state.showClearConfirmDialog) {
        ClearEvaluationsDialog(
            onConfirm = {
                onClearEvaluations?.invoke()
                state.showClearConfirmDialog = false
            },
            onDismiss = { state.showClearConfirmDialog = false }
        )
    }

    state.activeSampleForDetail?.let { sample ->
        val targetFqcn = sample.getTargetFqcn(state.selectedFramework)
        val existingEval = state.evaluations[targetFqcn] ?: state.evaluations[sample.id]
        SampleDetailFullScreenDialog(
            sample = sample,
            targetFqcn = targetFqcn,
            framework = state.selectedFramework,
            isReviewerMode = state.isReviewerMode,
            existingEvaluation = existingEval,
            onDismiss = { state.activeSampleDetailId = null },
            onSaveEvaluation = { status, notes ->
                onSaveEvaluation?.invoke(targetFqcn, status, notes, sample)
                state.activeSampleDetailId = null
            },
            onLaunch = { fw ->
                state.activeSampleDetailId = null
                onLaunchSample(sample, fw)
            }
        )
    }

    state.activeQuickGrading?.let { (sample, gradeStatus) ->
        val targetFqcn = sample.getTargetFqcn(state.selectedFramework)
        val existingEval = state.evaluations[targetFqcn] ?: state.evaluations[sample.id]

        QuickGradingDialog(
            sample = sample,
            targetFqcn = targetFqcn,
            gradeStatus = gradeStatus,
            initialNotes = existingEval?.notes.orEmpty(),
            onSave = { notes ->
                onSaveEvaluation?.invoke(targetFqcn, gradeStatus, notes, sample)
                state.activeQuickGradingSampleId = null
                state.activeQuickGradingStatus = null
            },
            onSaveAndNext = { notes ->
                onSaveEvaluation?.invoke(targetFqcn, gradeStatus, notes, sample)
                state.activeQuickGradingSampleId = null
                state.activeQuickGradingStatus = null
                val nextUnchecked = state.findNextUncheckedSample(currentSampleId = sample.id)
                if (nextUnchecked != null) {
                    onLaunchSample(nextUnchecked, state.selectedFramework)
                }
            },
            onDismiss = {
                state.activeQuickGradingSampleId = null
                state.activeQuickGradingStatus = null
            }
        )
    }
}

/**
 * Confirmation dialog for clearing / resetting all evaluations back to unchecked.
 */
@Composable
fun ClearEvaluationsDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_undo),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Reset All Review Evaluations?") },
        text = {
            Text("This will reset all ratings, status marks, and reviewer notes across all Kotlin and Java samples back to Unchecked.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Reset All")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Quick grading dialog for entering optional notes and saving or saving & advancing to the next sample.
 */
@Composable
fun QuickGradingDialog(
    sample: SampleItem,
    targetFqcn: String,
    gradeStatus: ReviewStatus,
    initialNotes: String,
    onSave: (notes: String) -> Unit,
    onSaveAndNext: (notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var notes by rememberSaveable { mutableStateOf(initialNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (gradeStatus == ReviewStatus.PASSING) "👍 Good Job: ${sample.title}" else "⚠️ Something's Wrong: ${sample.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Target: ${targetFqcn.substringAfterLast('.')}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional for pass, describe issues if broken)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onSave(notes) }) {
                    Text(if (gradeStatus == ReviewStatus.PASSING) "Save Pass 👍" else "Save Issue ⚠️")
                }
                Button(
                    onClick = { onSaveAndNext(notes) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Save & Next ⏭️", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
