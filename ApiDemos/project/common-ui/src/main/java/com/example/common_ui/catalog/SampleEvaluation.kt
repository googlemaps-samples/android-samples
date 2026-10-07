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

package com.example.common_ui.catalog

import java.io.Serializable

/**
 * Domain data model representing a sample manual review evaluation.
 */
data class SampleEvaluation(
    val sampleId: String,
    val sampleTitle: String = "",
    val activityName: String = "",
    val category: String = "",
    val framework: String = "",
    val status: String = ReviewStatus.UNCHECKED.name,
    val notes: String = "",
    val screenshotPath: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) : Serializable
