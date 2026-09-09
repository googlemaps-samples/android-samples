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

package com.example.common_ui.catalog.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for querying and persisting sample evaluation records.
 */
@Dao
interface SampleEvaluationDao {

    @Query("SELECT * FROM sample_evaluations ORDER BY category ASC, sampleTitle ASC")
    fun getAllEvaluationsFlow(): Flow<List<SampleEvaluationEntity>>

    @Query("SELECT * FROM sample_evaluations ORDER BY category ASC, sampleTitle ASC")
    fun getAllEvaluations(): List<SampleEvaluationEntity>

    @Query("SELECT * FROM sample_evaluations WHERE sampleId = :sampleId LIMIT 1")
    fun getEvaluationFlow(sampleId: String): Flow<SampleEvaluationEntity?>

    @Query("SELECT * FROM sample_evaluations WHERE sampleId = :sampleId LIMIT 1")
    fun getEvaluation(sampleId: String): SampleEvaluationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertEvaluation(evaluation: SampleEvaluationEntity): Long

    @Query("UPDATE sample_evaluations SET status = :status, lastUpdated = :timestamp WHERE sampleId = :sampleId")
    fun updateStatus(sampleId: String, status: String, timestamp: Long): Int

    @Query("UPDATE sample_evaluations SET notes = :notes, lastUpdated = :timestamp WHERE sampleId = :sampleId")
    fun updateNotes(sampleId: String, notes: String, timestamp: Long): Int

    @Query("SELECT * FROM sample_evaluations WHERE status = 'NEEDS_WORK' OR (notes IS NOT NULL AND notes != '') ORDER BY lastUpdated DESC")
    fun getGrievances(): List<SampleEvaluationEntity>

    @Query("DELETE FROM sample_evaluations WHERE sampleId = :sampleId")
    fun deleteEvaluation(sampleId: String): Int

    @Query("DELETE FROM sample_evaluations")
    fun clearAll(): Int
}
