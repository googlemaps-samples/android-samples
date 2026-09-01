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

import android.content.Context
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.db.SampleCatalogDatabase
import com.example.common_ui.catalog.db.SampleEvaluationDao
import com.example.common_ui.catalog.db.SampleEvaluationEntity
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
        metadata: SampleItem
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
                lastUpdated = System.currentTimeMillis()
            )
            dao.upsertEvaluation(entity)
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
    }
}
