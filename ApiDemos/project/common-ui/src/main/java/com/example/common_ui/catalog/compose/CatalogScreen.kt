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

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleEvaluation
import com.example.common_ui.catalog.SampleItem

/**
 * Clean, declarative multi-framework sample catalog and reviewer screen.
 */
@Composable
fun CatalogScreen(
    isReviewerMode: Boolean = false,
    evaluations: Map<String, SampleEvaluation> = emptyMap(),
    onSaveEvaluation: ((targetFqcn: String, status: ReviewStatus, notes: String, sample: SampleItem) -> Unit)? = null,
    onLaunchSample: (SampleItem, Framework) -> Unit,
    onExportGrievances: (() -> Unit)? = null,
    onClearEvaluations: (() -> Unit)? = null,
    onSwitchMode: (() -> Unit)? = null
) {
    val state = rememberCatalogState(
        isReviewerMode = isReviewerMode,
        evaluations = evaluations
    )

    Scaffold(
        topBar = {
            CatalogTopBar(
                isReviewerMode = isReviewerMode,
                selectedFramework = state.selectedFramework,
                sampleCount = state.filteredSamples.size,
                uncheckedCount = state.uncheckedCount,
                selectedStatusFilter = state.selectedStatusFilter,
                onToggleUncheckedFilter = {
                    state.selectedStatusFilter = if (state.selectedStatusFilter == ReviewStatus.UNCHECKED) null else ReviewStatus.UNCHECKED
                },
                onLaunchNextUnchecked = { state.launchNextUnchecked(onLaunchSample) },
                onClearEvaluationsClick = if (onClearEvaluations != null) { { state.showClearConfirmDialog = true } } else null,
                onScrollToTop = state::scrollToTop,
                onExportGrievances = onExportGrievances,
                onSwitchMode = onSwitchMode
            )
        },
        snackbarHost = { SnackbarHost(state.snackbarHostState) },
        floatingActionButton = {
            CatalogFloatingActionButtons(
                isReviewerMode = isReviewerMode,
                uncheckedCount = state.uncheckedCount,
                grievancesCount = state.grievancesCount,
                onLaunchNextUnchecked = { state.launchNextUnchecked(onLaunchSample) },
                onExportGrievances = onExportGrievances
            )
        }
    ) { paddingValues ->
        CatalogSampleList(
            state = state,
            paddingValues = paddingValues,
            onLaunchSample = onLaunchSample
        )
    }

    CatalogDialogHost(
        state = state,
        onSaveEvaluation = onSaveEvaluation,
        onClearEvaluations = onClearEvaluations,
        onLaunchSample = onLaunchSample
    )
}
