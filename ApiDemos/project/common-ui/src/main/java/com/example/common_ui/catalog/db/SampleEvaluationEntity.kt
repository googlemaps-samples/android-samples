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

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Room database entity storing manual review status, evaluation, and notes for a sample.
 */
@Entity(tableName = "sample_evaluations")
data class SampleEvaluationEntity(
    @PrimaryKey
    val sampleId: String,
    val sampleTitle: String,
    val activityName: String,
    val category: String,
    val framework: String,
    val status: String,
    val notes: String,
    val lastUpdated: Long = System.currentTimeMillis()
) : Serializable
