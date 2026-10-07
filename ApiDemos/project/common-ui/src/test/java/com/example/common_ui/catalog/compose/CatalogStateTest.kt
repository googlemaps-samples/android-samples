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

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleCatalogRegistry
import com.example.common_ui.catalog.SampleEvaluation
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Test

class CatalogStateTest {

    private fun createTestCatalogState(
        isReviewerMode: Boolean = true,
        evaluations: Map<String, SampleEvaluation> = emptyMap(),
        initialFramework: Framework = Framework.KOTLIN_VIEWS
    ): CatalogState {
        return CatalogState(
            isReviewerMode = isReviewerMode,
            evaluations = evaluations,
            lazyListState = LazyListState(),
            snackbarHostState = SnackbarHostState(),
            coroutineScope = CoroutineScope(Dispatchers.Unconfined),
            initialFramework = initialFramework
        )
    }

    @Test
    fun initialState_defaultsCorrectly() {
        val state = createTestCatalogState()
        assertThat(state.selectedFramework).isEqualTo(Framework.KOTLIN_VIEWS)
        assertThat(state.selectedComplexity).isNull()
        assertThat(state.selectedStatusFilter).isNull()
        assertThat(state.selectedTags).isEmpty()
        assertThat(state.searchQuery).isEmpty()
        assertThat(state.activeSampleDetailId).isNull()
        assertThat(state.activeQuickGrading).isNull()
    }

    @Test
    fun initialState_respectsCustomInitialFramework() {
        val state = createTestCatalogState(initialFramework = Framework.JAVA_VIEWS)
        assertThat(state.selectedFramework).isEqualTo(Framework.JAVA_VIEWS)
        assertThat(state.frameworkSamples).isNotEmpty()
        for (sample in state.frameworkSamples) {
            assertThat(sample.getActivityForFramework(Framework.JAVA_VIEWS)).isNotEmpty()
        }
    }

    @Test
    fun statusCounts_calculatesCorrectly() {
        val sample1 = SampleCatalogRegistry.SAMPLES[0]
        val sample2 = SampleCatalogRegistry.SAMPLES[1]

        val fqcn1 = sample1.getTargetFqcn(Framework.KOTLIN_VIEWS)
        val fqcn2 = sample2.getTargetFqcn(Framework.KOTLIN_VIEWS)

        val evals = mapOf(
            fqcn1 to SampleEvaluation(sampleId = sample1.id, status = "PASSING", notes = "All good"),
            fqcn2 to SampleEvaluation(sampleId = sample2.id, status = "NEEDS_WORK", notes = "Bug found")
        )

        val state = createTestCatalogState(evaluations = evals)
        assertThat(state.passingCount).isEqualTo(1)
        assertThat(state.needsWorkCount).isEqualTo(1)
        val expectedUnchecked = state.frameworkSamples.size - 2
        assertThat(state.uncheckedCount).isEqualTo(expectedUnchecked)
    }

    @Test
    fun grievancesCount_countsIssuesAndNotes() {
        val evals = mapOf(
            "sample1" to SampleEvaluation(sampleId = "sample1", status = "NEEDS_WORK", notes = "Broken"),
            "sample2" to SampleEvaluation(sampleId = "sample2", status = "PASSING", notes = "Good with note"),
            "sample3" to SampleEvaluation(sampleId = "sample3", status = "PASSING", notes = "")
        )

        val state = createTestCatalogState(evaluations = evals)
        // sample1 is NEEDS_WORK, sample2 has non-blank note -> 2 grievances
        assertThat(state.grievancesCount).isEqualTo(2)
    }

    @Test
    fun filteredSamples_appliesStatusFilter() {
        val sample1 = SampleCatalogRegistry.SAMPLES[0]
        val fqcn1 = sample1.getTargetFqcn(Framework.KOTLIN_VIEWS)

        val evals = mapOf(
            fqcn1 to SampleEvaluation(sampleId = sample1.id, status = "PASSING", notes = "Pass")
        )

        val state = createTestCatalogState(evaluations = evals)
        state.selectedStatusFilter = ReviewStatus.PASSING

        assertThat(state.filteredSamples).hasSize(1)
        assertThat(state.filteredSamples.first().id).isEqualTo(sample1.id)
    }

    @Test
    fun filteredSamples_appliesComplexityFilter() {
        val state = createTestCatalogState()
        state.selectedComplexity = Complexity.SNIPPET

        assertThat(state.filteredSamples).isNotEmpty()
        for (sample in state.filteredSamples) {
            assertThat(sample.complexity).isEqualTo(Complexity.SNIPPET)
        }
    }

    @Test
    fun findNextUncheckedSample_returnsNextInOrder() {
        val samples = SampleCatalogRegistry.SAMPLES
        val s0 = samples[0]
        val s1 = samples[1]
        val fqcn0 = s0.getTargetFqcn(Framework.KOTLIN_VIEWS)

        // Mark s0 as passing
        val evals = mapOf(
            fqcn0 to SampleEvaluation(sampleId = s0.id, status = "PASSING", notes = "Pass")
        )

        val state = createTestCatalogState(evaluations = evals)
        val next = state.findNextUncheckedSample()
        assertThat(next).isNotNull()
        assertThat(next?.id).isEqualTo(s1.id)
    }

    @Test
    fun findNextUncheckedSample_cyclicalLookupAfterCurrent() {
        val samples = SampleCatalogRegistry.SAMPLES
        val s0 = samples[0]
        val s1 = samples[1]
        val s2 = samples[2]

        val fqcn0 = s0.getTargetFqcn(Framework.KOTLIN_VIEWS)
        val fqcn1 = s1.getTargetFqcn(Framework.KOTLIN_VIEWS)

        // Mark s0 and s1 as passing
        val evals = mapOf(
            fqcn0 to SampleEvaluation(sampleId = s0.id, status = "PASSING", notes = "Pass"),
            fqcn1 to SampleEvaluation(sampleId = s1.id, status = "PASSING", notes = "Pass")
        )

        val state = createTestCatalogState(evaluations = evals)
        // Starting after s1, next unchecked should be s2
        val next = state.findNextUncheckedSample(currentSampleId = s1.id)
        assertThat(next).isNotNull()
        assertThat(next?.id).isEqualTo(s2.id)
    }

    @Test
    fun statusVisuals_returnsExpectedStyles() {
        val (passText, passBg, passFg) = statusVisuals(ReviewStatus.PASSING)
        assertThat(passText).contains("Pass")

        val (needsText, needsBg, needsFg) = statusVisuals(ReviewStatus.NEEDS_WORK)
        assertThat(needsText).contains("Needs Work")

        val (uncheckedText, uncheckedBg, uncheckedFg) = statusVisuals(ReviewStatus.UNCHECKED)
        assertThat(uncheckedText).contains("Unchecked")
    }
}
