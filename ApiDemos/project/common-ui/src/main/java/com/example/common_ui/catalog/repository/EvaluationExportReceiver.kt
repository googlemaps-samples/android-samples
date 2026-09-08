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

package com.example.common_ui.catalog.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleCatalogRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manifest-registered broadcast receiver that allows CI, automation scripts, and ADB commands
 * to:
 * 1. Record evaluations into Room DB:
 *    `adb shell am broadcast -a com.google.maps.RECORD_EVALUATION --es fqcn "<FQCN>" --es status "<PASSING|NEEDS_WORK|UNCHECKED>" --es notes "<notes>" [--es screenshot "<path>"]`
 * 2. Clear all evaluations and screenshots:
 *    `adb shell am broadcast -a com.google.maps.CLEAR_EVALUATIONS`
 * 3. Export the latest evaluation report:
 *    `adb shell am broadcast -a com.google.maps.EXPORT_EVALUATIONS`
 */
class EvaluationExportReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = SampleReviewRepository.getInstance(context)
                when (action) {
                    "com.google.maps.RECORD_EVALUATION" -> {
                        val fqcn = intent.getStringExtra("fqcn") ?: return@launch
                        val statusStr = intent.getStringExtra("status") ?: ReviewStatus.UNCHECKED.name
                        val status = try {
                            ReviewStatus.valueOf(statusStr.uppercase())
                        } catch (e: Exception) {
                            ReviewStatus.UNCHECKED
                        }
                        val rawNotes = if (intent.hasExtra("notes_b64")) {
                            try {
                                val b64 = intent.getStringExtra("notes_b64") ?: ""
                                String(android.util.Base64.decode(b64, android.util.Base64.DEFAULT), Charsets.UTF_8)
                            } catch (e: Exception) {
                                intent.getStringExtra("notes") ?: ""
                            }
                        } else {
                            intent.getStringExtra("notes") ?: ""
                        }
                        val notes = rawNotes
                        val screenshot = intent.getStringExtra("screenshot")

                        val sampleItem = SampleCatalogRegistry.findById(fqcn)
                        if (sampleItem != null) {
                            repository.saveEvaluation(
                                targetFqcn = fqcn,
                                status = status,
                                notes = notes,
                                metadata = sampleItem,
                                screenshotPath = screenshot
                            )
                            Log.i("EvaluationExportReceiver", "Recorded evaluation for $fqcn: status=$status")
                        } else {
                            Log.w("EvaluationExportReceiver", "Sample item not found in registry for FQCN: $fqcn")
                        }
                    }

                    "com.google.maps.CLEAR_EVALUATIONS" -> {
                        repository.clearAllEvaluations()
                        val screenshotDir = context.getExternalFilesDir("screenshots")
                        screenshotDir?.listFiles()?.forEach { it.delete() }
                        val reportsDir = context.getExternalFilesDir("reports")
                        reportsDir?.listFiles()?.forEach { it.delete() }
                        Log.i("EvaluationExportReceiver", "Cleared all evaluations, reports, and screenshots.")
                    }

                    "com.google.maps.EXPORT_EVALUATIONS" -> {
                        val reportFile = repository.exportAiringOfGrievances(context)
                        Log.i("EvaluationExportReceiver", "Successfully exported report to: ${reportFile.absolutePath}")
                    }
                }
            } catch (e: Exception) {
                Log.e("EvaluationExportReceiver", "Failed to process broadcast: $action", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
