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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleItem

/**
 * Main scrollable list for the catalog containing the filter bar, empty state, and sample cards.
 */
@Composable
fun CatalogSampleList(
    state: CatalogState,
    paddingValues: PaddingValues,
    onLaunchSample: (SampleItem, Framework) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = state.lazyListState,
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "catalog_filter_bar") {
            CatalogFilterBar(
                selectedFramework = state.selectedFramework,
                onFrameworkSelected = { state.selectedFramework = it },
                searchQuery = state.searchQuery,
                onSearchQueryChange = { state.searchQuery = it },
                isReviewerMode = state.isReviewerMode,
                selectedStatusFilter = state.selectedStatusFilter,
                onStatusFilterSelected = { state.selectedStatusFilter = it },
                uncheckedCount = state.uncheckedCount,
                needsWorkCount = state.needsWorkCount,
                passingCount = state.passingCount,
                selectedComplexity = state.selectedComplexity,
                onComplexitySelected = { state.selectedComplexity = it },
                selectedTags = state.selectedTags,
                onTagToggled = { tag ->
                    state.selectedTags = if (state.selectedTags.contains(tag)) state.selectedTags - tag else state.selectedTags + tag
                }
            )
        }

        val filteredSamples = state.filteredSamples
        if (filteredSamples.isEmpty()) {
            item(key = "empty_samples_state") {
                CatalogEmptyState(
                    selectedStatusFilter = state.selectedStatusFilter,
                    onClearStatusFilter = { state.selectedStatusFilter = null }
                )
            }
        } else {
            items(filteredSamples, key = { it.id }) { sample ->
                val targetFqcn = sample.getTargetFqcn(state.selectedFramework)
                val eval = state.evaluations[targetFqcn] ?: state.evaluations[sample.id]
                val status = ReviewStatus.fromString(eval?.status)
                Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                    SampleComposeCard(
                        sample = sample,
                        targetFqcn = targetFqcn,
                        framework = state.selectedFramework,
                        isReviewerMode = state.isReviewerMode,
                        evaluation = eval,
                        status = status,
                        onSampleClick = { onLaunchSample(sample, state.selectedFramework) },
                        onInfoClick = { state.activeSampleDetailId = sample.id },
                        onQuickGrade = { gradeStatus ->
                            state.activeQuickGradingSampleId = sample.id
                            state.activeQuickGradingStatus = gradeStatus
                        }
                    )
                }
            }
        }
    }
}

/**
 * Empty state view shown when no samples match the active filters or search query.
 */
@Composable
fun CatalogEmptyState(
    selectedStatusFilter: ReviewStatus?,
    onClearStatusFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (selectedStatusFilter == ReviewStatus.UNCHECKED) {
                    "🎉 All samples in this framework have been evaluated!"
                } else {
                    "No matching samples found."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (selectedStatusFilter != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onClearStatusFilter) {
                    Text("Clear Status Filter")
                }
            }
        }
    }
}
