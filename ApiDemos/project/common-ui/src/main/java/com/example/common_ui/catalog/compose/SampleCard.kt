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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleEvaluation
import com.example.common_ui.catalog.SampleItem

/**
 * Returns label, background, and foreground color styling for review statuses.
 */
fun statusVisuals(status: ReviewStatus): Triple<String, Color, Color> = when (status) {
    ReviewStatus.PASSING -> Triple("🟢 Pass", Color(0xFFE8F5E9), Color(0xFF2E7D32))
    ReviewStatus.NEEDS_WORK -> Triple("🔴 Needs Work", Color(0xFFFFEBEE), Color(0xFFC62828))
    ReviewStatus.UNCHECKED -> Triple("⚪ Unchecked", Color(0xFFEEEEEE), Color(0xFF616161))
}

/**
 * Reusable visual badge representing sample review status.
 */
@Composable
fun ReviewStatusBadge(
    status: ReviewStatus,
    modifier: Modifier = Modifier
) {
    val (statusText, statusBg, statusFg) = statusVisuals(status)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = statusBg,
        modifier = modifier
    ) {
        Text(
            text = statusText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = statusFg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

/**
 * Interactive card displaying sample metadata, review evaluation status, and launch actions.
 */
@Composable
fun SampleComposeCard(
    sample: SampleItem,
    targetFqcn: String,
    framework: Framework,
    isReviewerMode: Boolean,
    evaluation: SampleEvaluation?,
    status: ReviewStatus,
    onSampleClick: () -> Unit,
    onInfoClick: () -> Unit,
    onQuickGrade: (ReviewStatus) -> Unit
) {
    val hasActivity = sample.getActivityForFramework(framework) != null
    val isReviewed = isReviewerMode && (status == ReviewStatus.PASSING || status == ReviewStatus.NEEDS_WORK)
    var isExpandedManually by remember(sample.id, status) { mutableStateOf<Boolean?>(null) }
    val isCardExpanded = isExpandedManually ?: (!isReviewed)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpandedManually = !isCardExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isCardExpanded) 2.dp else 1.dp)
    ) {
        if (!isCardExpanded) {
            SampleCardCollapsedRow(
                sample = sample,
                status = status,
                evaluation = evaluation,
                hasActivity = hasActivity,
                onSampleClick = onSampleClick
            )
        } else {
            SampleCardExpandedContent(
                sample = sample,
                targetFqcn = targetFqcn,
                framework = framework,
                isReviewerMode = isReviewerMode,
                status = status,
                evaluation = evaluation,
                hasActivity = hasActivity,
                onCollapse = { isExpandedManually = false },
                onQuickGrade = onQuickGrade,
                onInfoClick = onInfoClick,
                onSampleClick = onSampleClick
            )
        }
    }
}

@Composable
private fun SampleCardCollapsedRow(
    sample: SampleItem,
    status: ReviewStatus,
    evaluation: SampleEvaluation?,
    hasActivity: Boolean,
    onSampleClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = sample.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            ReviewStatusBadge(status = status)

            if (!evaluation?.notes.isNullOrBlank()) {
                Text(
                    text = "📝",
                    fontSize = 12.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (hasActivity) {
                IconButton(
                    onClick = onSampleClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Launch Sample",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SampleCardExpandedContent(
    sample: SampleItem,
    targetFqcn: String,
    framework: Framework,
    isReviewerMode: Boolean,
    status: ReviewStatus,
    evaluation: SampleEvaluation?,
    hasActivity: Boolean,
    onCollapse: () -> Unit,
    onQuickGrade: (ReviewStatus) -> Unit,
    onInfoClick: () -> Unit,
    onSampleClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Top Header: Category, Complexity Chip, and Collapse Chevron
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = sample.category,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text("${sample.complexity.badge} ${sample.complexity.displayName}", fontSize = 11.sp) }
                )

                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Collapse",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Title
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = sample.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // FQCN Target Identifier
        Text(
            text = targetFqcn.substringAfterLast('.'),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )

        // Description
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = sample.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Review Status Badge & Notes (Reviewer Mode Only)
        if (isReviewerMode) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReviewStatusBadge(status = status)

                if (!evaluation?.notes.isNullOrBlank()) {
                    Text(
                        text = "📝 ${evaluation.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // In-Card Quick Grading Buttons
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = { onQuickGrade(ReviewStatus.PASSING) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = Color(0xFF2E7D32)
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("👍 Good Job", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = { onQuickGrade(ReviewStatus.NEEDS_WORK) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFC62828)
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("⚠️ Issue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Hashtags
        if (sample.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sample.tags.joinToString(" "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Action Row
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onInfoClick,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("About & APIs", fontSize = 12.sp)
            }

            Button(
                onClick = onSampleClick,
                enabled = hasActivity,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    if (hasActivity) "Launch Sample" else "No ${framework.badge} Impl",
                    fontSize = 12.sp
                )
            }
        }
    }
}
