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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleCatalogRegistry
import com.example.common_ui.catalog.SampleEvaluation
import com.example.common_ui.catalog.SampleItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Holds all state, filter selections, and query results for the sample catalog screen.
 */
@Stable
class CatalogState(
    val isReviewerMode: Boolean,
    val evaluations: Map<String, SampleEvaluation>,
    val lazyListState: LazyListState,
    val snackbarHostState: SnackbarHostState,
    val coroutineScope: CoroutineScope,
    initialFramework: Framework = Framework.KOTLIN_VIEWS
) {
    var selectedFramework by mutableStateOf(initialFramework)
    var selectedComplexity by mutableStateOf<Complexity?>(null)
    var selectedStatusFilter by mutableStateOf<ReviewStatus?>(null)
    var selectedTags by mutableStateOf(emptySet<String>())
    var searchQuery by mutableStateOf("")

    var activeSampleDetailId by mutableStateOf<String?>(null)
    var activeQuickGradingSampleId by mutableStateOf<String?>(null)
    var activeQuickGradingStatus by mutableStateOf<ReviewStatus?>(null)
    var showClearConfirmDialog by mutableStateOf(false)

    val activeSampleForDetail: SampleItem?
        get() = SampleCatalogRegistry.findById(activeSampleDetailId)

    val activeQuickGrading: Pair<SampleItem, ReviewStatus>?
        get() {
            val sample = SampleCatalogRegistry.findById(activeQuickGradingSampleId)
            val status = activeQuickGradingStatus
            return if (sample != null && status != null) Pair(sample, status) else null
        }

    val frameworkSamples: List<SampleItem>
        get() = SampleCatalogRegistry.filter(framework = selectedFramework)

    val statusCounts: Triple<Int, Int, Int>
        get() {
            var unchecked = 0
            var passing = 0
            var needsWork = 0
            for (s in frameworkSamples) {
                val targetFqcn = s.getTargetFqcn(selectedFramework)
                val eval = evaluations[targetFqcn] ?: evaluations[s.id]
                when (ReviewStatus.fromString(eval?.status)) {
                    ReviewStatus.UNCHECKED -> unchecked++
                    ReviewStatus.PASSING -> passing++
                    ReviewStatus.NEEDS_WORK -> needsWork++
                }
            }
            return Triple(unchecked, passing, needsWork)
        }

    val uncheckedCount: Int get() = statusCounts.first
    val passingCount: Int get() = statusCounts.second
    val needsWorkCount: Int get() = statusCounts.third

    val filteredSamples: List<SampleItem>
        get() = SampleCatalogRegistry.filter(
            framework = selectedFramework,
            complexity = selectedComplexity,
            selectedTags = selectedTags,
            searchQuery = searchQuery
        ).filter { sample ->
            if (selectedStatusFilter == null) true
            else {
                val targetFqcn = sample.getTargetFqcn(selectedFramework)
                val eval = evaluations[targetFqcn] ?: evaluations[sample.id]
                ReviewStatus.fromString(eval?.status) == selectedStatusFilter
            }
        }

    val grievancesCount: Int
        get() = evaluations.values.count { it.status == "NEEDS_WORK" || it.notes.isNotBlank() }

    fun findNextUncheckedSample(currentSampleId: String? = null): SampleItem? {
        val searchList = if (currentSampleId != null) {
            val allFw = frameworkSamples
            val currIdx = allFw.indexOfFirst { it.id == currentSampleId }
            if (currIdx >= 0) allFw.drop(currIdx + 1) + allFw.take(currIdx) else allFw
        } else {
            val inFiltered = filteredSamples.firstOrNull { isSampleUnchecked(it) }
            if (inFiltered != null) return inFiltered
            frameworkSamples
        }

        return searchList.firstOrNull { isSampleUnchecked(it) }
    }

    private fun isSampleUnchecked(sample: SampleItem): Boolean {
        val targetFqcn = sample.getTargetFqcn(selectedFramework)
        val eval = evaluations[targetFqcn] ?: evaluations[sample.id]
        return eval == null || ReviewStatus.fromString(eval.status) == ReviewStatus.UNCHECKED
    }

    fun launchNextUnchecked(onLaunchSample: (SampleItem, Framework) -> Unit) {
        val next = findNextUncheckedSample()
        if (next != null) {
            onLaunchSample(next, selectedFramework)
        }
    }

    fun scrollToTop() {
        coroutineScope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }
}

/**
 * Remembers and creates a [CatalogState] instance.
 */
@Composable
fun rememberCatalogState(
    isReviewerMode: Boolean,
    evaluations: Map<String, SampleEvaluation>,
    lazyListState: LazyListState = rememberLazyListState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): CatalogState {
    return remember(isReviewerMode, evaluations, lazyListState, snackbarHostState, coroutineScope) {
        CatalogState(
            isReviewerMode = isReviewerMode,
            evaluations = evaluations,
            lazyListState = lazyListState,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope
        )
    }
}
