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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.common_ui.R
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus

/**
 * Top app bar composable for the GMP Sample Catalog, providing title stats, quick filters,
 * navigation shortcuts, and reviewer overflow actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogTopBar(
    isReviewerMode: Boolean,
    selectedFramework: Framework,
    sampleCount: Int,
    uncheckedCount: Int,
    selectedStatusFilter: ReviewStatus?,
    onToggleUncheckedFilter: () -> Unit,
    onLaunchNextUnchecked: () -> Unit,
    onClearEvaluationsClick: (() -> Unit)?,
    onScrollToTop: () -> Unit,
    onExportGrievances: (() -> Unit)?,
    onSwitchMode: (() -> Unit)?
) {
    var showMoreMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = if (isReviewerMode) "GMP Sample Reviewer" else "Google Maps Platform Samples",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isReviewerMode) {
                        val filterSummary = if (selectedStatusFilter == ReviewStatus.UNCHECKED) " • ⚪ Unchecked Only" else ""
                        "${selectedFramework.displayName} • $sampleCount samples$filterSummary"
                    } else {
                        "Unified Multi-Framework Catalog • $sampleCount samples"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isReviewerMode) Color(0xFFD93025) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            // Quick toggle button for Unchecked Only in Reviewer Mode
            if (isReviewerMode) {
                IconButton(onClick = onToggleUncheckedFilter) {
                    BadgedBox(
                        badge = {
                            if (uncheckedCount > 0) {
                                Badge { Text("$uncheckedCount") }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (selectedStatusFilter == ReviewStatus.UNCHECKED) R.drawable.ic_status_passing
                                else R.drawable.ic_status_unchecked
                            ),
                            contentDescription = "Filter Unchecked Only",
                            tint = if (selectedStatusFilter == ReviewStatus.UNCHECKED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Next Unchecked Action Button
                if (uncheckedCount > 0) {
                    IconButton(onClick = onLaunchNextUnchecked) {
                        Icon(
                            painter = painterResource(R.drawable.ic_skip_next),
                            contentDescription = "Launch Next Unchecked Sample",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Direct Reset / Clear Reviews Button (Always Available)
                if (onClearEvaluationsClick != null) {
                    IconButton(onClick = onClearEvaluationsClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_undo),
                            contentDescription = "Reset All Evaluations",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Jump to Search & Filters Button
            IconButton(onClick = onScrollToTop) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Jump to Search & Filters",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // More Options Overflow Menu
            Box {
                IconButton(onClick = { showMoreMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                }

                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false }
                ) {
                    if (isReviewerMode) {
                        if (onClearEvaluationsClick != null) {
                            DropdownMenuItem(
                                text = { Text("🔄 Reset All Evaluations", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMoreMenu = false
                                    onClearEvaluationsClick()
                                }
                            )
                        }
                        if (onExportGrievances != null) {
                            DropdownMenuItem(
                                text = { Text("📊 Generate Evaluation Report") },
                                onClick = {
                                    showMoreMenu = false
                                    onExportGrievances()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text(if (selectedStatusFilter == ReviewStatus.UNCHECKED) "Show All Samples" else "⚪ Show Unchecked Only")
                            },
                            onClick = {
                                showMoreMenu = false
                                onToggleUncheckedFilter()
                            }
                        )
                        HorizontalDivider()
                    }
                    if (onSwitchMode != null) {
                        DropdownMenuItem(
                            text = {
                                Text(if (isReviewerMode) "📱 Switch to Developer Mode" else "🛠️ Switch to Reviewer Mode")
                            },
                            onClick = {
                                showMoreMenu = false
                                onSwitchMode()
                            }
                        )
                        HorizontalDivider()
                    }
                    DropdownMenuItem(
                        text = { Text("Scroll to Top") },
                        onClick = {
                            showMoreMenu = false
                            onScrollToTop()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
