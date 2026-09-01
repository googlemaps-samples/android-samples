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

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleCatalogRegistry
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.db.SampleCatalogDatabase
import com.example.common_ui.catalog.db.SampleEvaluationDao
import com.example.common_ui.catalog.db.SampleEvaluationEntity
import com.example.common_ui.catalog.ui.UnifiedCatalogActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Thread-safe singleton repository for managing sample review ratings, notes, and grievance reports.
 *
 * Uses Fully Qualified Class Names (FQCN) as primary sample keys to isolate framework implementations.
 */
class SampleReviewRepository private constructor(
    private val dao: SampleEvaluationDao,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {

    val allEvaluationsFlow: Flow<List<SampleEvaluationEntity>> = dao.getAllEvaluationsFlow()

    fun getEvaluationFlow(sampleId: String): Flow<SampleEvaluationEntity?> {
        return dao.getEvaluationFlow(sampleId)
    }

    suspend fun getEvaluation(sampleId: String): SampleEvaluationEntity? {
        return withContext(Dispatchers.IO) {
            dao.getEvaluation(sampleId)
        }
    }

    fun saveEvaluation(
        targetFqcn: String,
        status: ReviewStatus,
        notes: String,
        metadata: SampleItem,
        screenshotPath: String? = null,
        onComplete: (() -> Unit)? = null
    ) {
        coroutineScope.launch {
            val frameworkName = if (targetFqcn.contains("mapdemo")) "JAVA" else "KOTLIN"
            val entity = SampleEvaluationEntity(
                sampleId = targetFqcn,
                sampleTitle = metadata.title,
                activityName = targetFqcn,
                category = metadata.category,
                framework = frameworkName,
                status = status.name,
                notes = notes,
                screenshotPath = screenshotPath,
                lastUpdated = System.currentTimeMillis()
            )
            dao.upsertEvaluation(entity)
            withContext(Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }

    fun clearAllEvaluations(onComplete: (() -> Unit)? = null) {
        coroutineScope.launch {
            dao.clearAll()
            withContext(Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }

    fun deleteEvaluation(targetFqcn: String, onComplete: (() -> Unit)? = null) {
        coroutineScope.launch {
            dao.deleteEvaluation(targetFqcn)
            withContext(Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }

    suspend fun getNextUncheckedSample(
        currentSampleId: String?,
        framework: Framework
    ): SampleItem? = withContext(Dispatchers.IO) {
        val evaluations = dao.getAllEvaluations().associateBy { it.sampleId }
        val eligibleSamples = SampleCatalogRegistry.SAMPLES.filter {
            it.getActivityForFramework(framework) != null
        }
        if (eligibleSamples.isEmpty()) return@withContext null

        val uncheckedSamples = eligibleSamples.filter { sample ->
            val fqcn = sample.getTargetFqcn(framework)
            val eval = evaluations[fqcn]
            eval == null || eval.status == ReviewStatus.UNCHECKED.name
        }
        if (uncheckedSamples.isEmpty()) return@withContext null

        if (currentSampleId == null) {
            return@withContext uncheckedSamples.first()
        }

        val currentIndex = eligibleSamples.indexOfFirst {
            it.id == currentSampleId || it.getTargetFqcn(framework) == currentSampleId
        }

        if (currentIndex == -1) {
            return@withContext uncheckedSamples.first()
        }

        // Search forward after current sample
        for (i in (currentIndex + 1) until eligibleSamples.size) {
            val sample = eligibleSamples[i]
            val fqcn = sample.getTargetFqcn(framework)
            val eval = evaluations[fqcn]
            if (eval == null || eval.status == ReviewStatus.UNCHECKED.name) {
                return@withContext sample
            }
        }

        // Wrap around from beginning up to current sample
        for (i in 0 until currentIndex) {
            val sample = eligibleSamples[i]
            val fqcn = sample.getTargetFqcn(framework)
            val eval = evaluations[fqcn]
            if (eval == null || eval.status == ReviewStatus.UNCHECKED.name) {
                return@withContext sample
            }
        }

        return@withContext null
    }

    suspend fun getPreviousSample(
        currentSampleId: String?,
        framework: Framework
    ): SampleItem? = withContext(Dispatchers.IO) {
        val eligibleSamples = SampleCatalogRegistry.SAMPLES.filter {
            it.getActivityForFramework(framework) != null
        }
        if (eligibleSamples.isEmpty()) return@withContext null

        val currentIndex = eligibleSamples.indexOfFirst {
            it.id == currentSampleId || it.getTargetFqcn(framework) == currentSampleId
        }

        if (currentIndex <= 0) {
            return@withContext eligibleSamples.last()
        } else {
            return@withContext eligibleSamples[currentIndex - 1]
        }
    }

    fun getNextUncheckedSampleAsync(
        currentSampleId: String?,
        framework: Framework,
        callback: (SampleItem?) -> Unit
    ) {
        coroutineScope.launch {
            val result = getNextUncheckedSample(currentSampleId, framework)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    fun getPreviousSampleAsync(
        currentSampleId: String?,
        framework: Framework,
        callback: (SampleItem?) -> Unit
    ) {
        coroutineScope.launch {
            val result = getPreviousSample(currentSampleId, framework)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    suspend fun exportAiringOfGrievances(context: Context): File {
        return withContext(Dispatchers.IO) {
            val allEvaluations = dao.getAllEvaluations()
            GrievanceReportExporter.generateAndSaveReport(context, allEvaluations)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SampleReviewRepository? = null

        fun getInstance(context: Context): SampleReviewRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val db = SampleCatalogDatabase.getInstance(context.applicationContext)
                    SampleReviewRepository(db.sampleEvaluationDao()).also { INSTANCE = it }
                }
            }
        }

        fun launchSample(activity: Activity, sample: SampleItem, framework: Framework) {
            val className = sample.getActivityForFramework(framework) ?: return
            val intent = Intent().apply {
                setClassName(activity.packageName, className)
                putExtra(UnifiedCatalogActivity.EXTRA_SAMPLE_ID, sample.id)
            }
            activity.finish()
            activity.startActivity(intent)
        }
    }
}
