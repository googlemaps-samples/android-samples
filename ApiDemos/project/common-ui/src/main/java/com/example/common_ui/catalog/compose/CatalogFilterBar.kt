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

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleCatalogRegistry

/**
 * Filter header composable housing framework tabs, search field, review status chips,
 * complexity filters, and dynamic tag chips.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogFilterBar(
    selectedFramework: Framework,
    onFrameworkSelected: (Framework) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isReviewerMode: Boolean,
    selectedStatusFilter: ReviewStatus?,
    onStatusFilterSelected: (ReviewStatus?) -> Unit,
    uncheckedCount: Int,
    needsWorkCount: Int,
    passingCount: Int,
    selectedComplexity: Complexity?,
    onComplexitySelected: (Complexity?) -> Unit,
    selectedTags: Set<String>,
    onTagToggled: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 6.dp)
    ) {
        // Framework Tabs
        PrimaryTabRow(
            selectedTabIndex = when (selectedFramework) {
                Framework.KOTLIN_VIEWS -> 0
                Framework.JAVA_VIEWS -> 1
            }
        ) {
            Tab(
                selected = selectedFramework == Framework.KOTLIN_VIEWS,
                onClick = { onFrameworkSelected(Framework.KOTLIN_VIEWS) },
                text = { Text("💜 Kotlin Views", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedFramework == Framework.JAVA_VIEWS,
                onClick = { onFrameworkSelected(Framework.JAVA_VIEWS) },
                text = { Text("☕ Java Views", fontWeight = FontWeight.Bold) }
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            placeholder = { Text("Search samples, tags, or categories...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        // Review Status Filter Chips (Reviewer Mode Only)
        if (isReviewerMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { onStatusFilterSelected(null) },
                    label = { Text("All Status") }
                )
                FilterChip(
                    selected = selectedStatusFilter == ReviewStatus.UNCHECKED,
                    onClick = {
                        onStatusFilterSelected(if (selectedStatusFilter == ReviewStatus.UNCHECKED) null else ReviewStatus.UNCHECKED)
                    },
                    label = {
                        Text(
                            "⚪ Unchecked ($uncheckedCount)",
                            fontWeight = if (selectedStatusFilter == ReviewStatus.UNCHECKED) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                FilterChip(
                    selected = selectedStatusFilter == ReviewStatus.NEEDS_WORK,
                    onClick = {
                        onStatusFilterSelected(if (selectedStatusFilter == ReviewStatus.NEEDS_WORK) null else ReviewStatus.NEEDS_WORK)
                    },
                    label = { Text("🔴 Needs Work ($needsWorkCount)") }
                )
                FilterChip(
                    selected = selectedStatusFilter == ReviewStatus.PASSING,
                    onClick = {
                        onStatusFilterSelected(if (selectedStatusFilter == ReviewStatus.PASSING) null else ReviewStatus.PASSING)
                    },
                    label = { Text("🟢 Passing ($passingCount)") }
                )
            }
        }

        // Complexity Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedComplexity == null,
                onClick = { onComplexitySelected(null) },
                label = { Text("All Complexity") }
            )
            FilterChip(
                selected = selectedComplexity == Complexity.SNIPPET,
                onClick = { onComplexitySelected(if (selectedComplexity == Complexity.SNIPPET) null else Complexity.SNIPPET) },
                label = { Text("🔹 Snippet") }
            )
            FilterChip(
                selected = selectedComplexity == Complexity.SIMPLE,
                onClick = { onComplexitySelected(if (selectedComplexity == Complexity.SIMPLE) null else Complexity.SIMPLE) },
                label = { Text("🟢 Simple") }
            )
            FilterChip(
                selected = selectedComplexity == Complexity.ADVANCED,
                onClick = { onComplexitySelected(if (selectedComplexity == Complexity.ADVANCED) null else Complexity.ADVANCED) },
                label = { Text("🔴 Advanced") }
            )
        }

        // Dynamic Hashtags Row
        val allTags = remember { SampleCatalogRegistry.getAllTags() }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            allTags.forEach { tag ->
                val isSelected = selectedTags.contains(tag)
                FilterChip(
                    selected = isSelected,
                    onClick = { onTagToggled(tag) },
                    label = { Text(tag, fontSize = 12.sp) }
                )
            }
        }
    }
}
