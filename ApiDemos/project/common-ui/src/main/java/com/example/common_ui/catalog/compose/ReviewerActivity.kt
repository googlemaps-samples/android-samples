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

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.repository.GrievanceReportExporter
import com.example.common_ui.catalog.repository.SampleReviewRepository
import kotlinx.coroutines.launch

/**
 * Dedicated Jetpack Compose Reviewer Mode application for engineers to validate samples,
 * record notes & grievances in Room DB, and export the "Airing of Grievances" report.
 */
open class ReviewerActivity : ComponentActivity() {

    private lateinit var repository: SampleReviewRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = SampleReviewRepository.getInstance(this)

        setContent {
            CatalogTheme {
                val evaluationsList by repository.allEvaluationsFlow.collectAsState(initial = emptyList())
                val evaluationsMap = evaluationsList.associateBy { it.sampleId }

                CatalogScreen(
                    isReviewerMode = true,
                    evaluations = evaluationsMap,
                    onSaveEvaluation = { sampleId, status, notes, sample ->
                        repository.saveEvaluation(sampleId, status, notes, sample)
                        Toast.makeText(this, "Saved evaluation for ${sample.title}", Toast.LENGTH_SHORT).show()
                    },
                    onLaunchSample = { sample, framework ->
                        launchSample(sample, framework)
                    },
                    onExportGrievances = {
                        exportAiringOfGrievances()
                    },
                    onClearEvaluations = {
                        repository.clearAllEvaluations {
                            Toast.makeText(this@ReviewerActivity, "All evaluations cleared", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
        val receiverFlags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.content.Context.RECEIVER_EXPORTED
        } else {
            0
        }
        registerReceiver(object : android.content.BroadcastReceiver() {
            override fun onReceive(context: android.content.Context?, intent: Intent?) {
                exportAiringOfGrievances(silent = true)
            }
        }, android.content.IntentFilter("com.google.maps.EXPORT_EVALUATIONS"), receiverFlags)
    }

    private fun exportAiringOfGrievances(silent: Boolean = false) {
        lifecycleScope.launch {
            try {
                val file = repository.exportAiringOfGrievances(this@ReviewerActivity)
                if (!silent) {
                    val shareIntent = GrievanceReportExporter.createShareIntent(this@ReviewerActivity, file)
                    startActivity(Intent.createChooser(shareIntent, "Share Evaluation Report"))
                } else {
                    android.util.Log.i("GMPReviewer", "Exported evaluation report to ${file.absolutePath}")
                }
            } catch (e: Exception) {
                if (!silent) {
                    Toast.makeText(this@ReviewerActivity, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun launchSample(sample: SampleItem, framework: Framework) {
        val className = sample.getActivityForFramework(framework)
        if (className.isNullOrBlank()) {
            Toast.makeText(this, "No ${framework.displayName} implementation available for ${sample.title}", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent().setClassName(packageName, className).apply {
                putExtra("extra_sample_id", sample.id)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not launch ${sample.title}: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
