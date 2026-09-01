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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manifest-registered broadcast receiver that allows CI, automation scripts, and ADB commands
 * (`adb shell am broadcast -a com.google.maps.EXPORT_EVALUATIONS`) to export the latest evaluation report
 * and screenshots to public app storage at any time.
 */
class EvaluationExportReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "com.google.maps.EXPORT_EVALUATIONS") {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val repository = SampleReviewRepository.getInstance(context)
                    val reportFile = repository.exportAiringOfGrievances(context)
                    Log.i("EvaluationExport", "Successfully exported report to: ${reportFile.absolutePath}")
                } catch (e: Exception) {
                    Log.e("EvaluationExport", "Failed to export report", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
