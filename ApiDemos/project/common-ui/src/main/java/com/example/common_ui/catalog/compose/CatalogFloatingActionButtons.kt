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
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.common_ui.R

/**
 * Floating action buttons for Reviewer Mode: "Review Next" and "Generate Report".
 */
@Composable
fun CatalogFloatingActionButtons(
    isReviewerMode: Boolean,
    uncheckedCount: Int,
    grievancesCount: Int,
    onLaunchNextUnchecked: () -> Unit,
    onExportGrievances: (() -> Unit)?
) {
    if (!isReviewerMode) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (uncheckedCount > 0) {
            ExtendedFloatingActionButton(
                onClick = onLaunchNextUnchecked,
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                text = { Text("Review Next ($uncheckedCount)") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (onExportGrievances != null) {
            FloatingActionButton(
                onClick = onExportGrievances,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                BadgedBox(
                    badge = {
                        if (grievancesCount > 0) {
                            Badge { Text("$grievancesCount") }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_grievances),
                        contentDescription = "Generate Report"
                    )
                }
            }
        }
    }
}
