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

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleCatalogRegistry
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.db.SampleEvaluationEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    isReviewerMode: Boolean = false,
    evaluations: Map<String, SampleEvaluationEntity> = emptyMap(),
    onSaveEvaluation: ((targetFqcn: String, status: ReviewStatus, notes: String, sample: SampleItem) -> Unit)? = null,
    onLaunchSample: (SampleItem, Framework) -> Unit,
    onExportGrievances: (() -> Unit)? = null
) {
    var selectedFramework by remember { mutableStateOf(Framework.KOTLIN_VIEWS) }
    var selectedComplexity by remember { mutableStateOf<Complexity?>(null) }
    val selectedTags = remember { mutableStateListOf<String>() }
    var searchQuery by remember { mutableStateOf("") }
    var activeSampleForExpectations by remember { mutableStateOf<SampleItem?>(null) }

    val filteredSamples = remember(selectedFramework, selectedComplexity, selectedTags.toList(), searchQuery) {
        SampleCatalogRegistry.filter(
            framework = selectedFramework,
            complexity = selectedComplexity,
            selectedTags = selectedTags.toSet(),
            searchQuery = searchQuery
        )
    }

    val grievancesCount = remember(evaluations) {
        evaluations.values.count { it.status == "NEEDS_WORK" || it.notes.isNotBlank() }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Top App Bar
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (isReviewerMode) "GMP Sample Reviewer" else "Google Maps Platform Samples",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isReviewerMode) "Reviewer Mode Active • Room DB Backed" else "Unified Multi-Framework Catalog",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isReviewerMode) Color(0xFFD93025) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        if (isReviewerMode && onExportGrievances != null) {
                            IconButton(onClick = onExportGrievances) {
                                BadgedBox(
                                    badge = {
                                        if (grievancesCount > 0) {
                                            Badge { Text("$grievancesCount") }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assessment,
                                        contentDescription = "Export Airing of Grievances",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Framework Tabs (Kotlin Views & Java Views)
                PrimaryTabRow(
                    selectedTabIndex = when (selectedFramework) {
                        Framework.KOTLIN_VIEWS -> 0
                        Framework.JAVA_VIEWS -> 1
                    }
                ) {
                    Tab(
                        selected = selectedFramework == Framework.KOTLIN_VIEWS,
                        onClick = { selectedFramework = Framework.KOTLIN_VIEWS },
                        text = { Text("💜 Kotlin") }
                    )
                    Tab(
                        selected = selectedFramework == Framework.JAVA_VIEWS,
                        onClick = { selectedFramework = Framework.JAVA_VIEWS },
                        text = { Text("☕ Java") }
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    placeholder = { Text("Search samples, tags, or categories...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
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
                        onClick = { selectedComplexity = null },
                        label = { Text("All") }
                    )
                    FilterChip(
                        selected = selectedComplexity == Complexity.SNIPPET,
                        onClick = { selectedComplexity = if (selectedComplexity == Complexity.SNIPPET) null else Complexity.SNIPPET },
                        label = { Text("🔹 Snippet") }
                    )
                    FilterChip(
                        selected = selectedComplexity == Complexity.SIMPLE,
                        onClick = { selectedComplexity = if (selectedComplexity == Complexity.SIMPLE) null else Complexity.SIMPLE },
                        label = { Text("🟢 Simple") }
                    )
                    FilterChip(
                        selected = selectedComplexity == Complexity.ADVANCED,
                        onClick = { selectedComplexity = if (selectedComplexity == Complexity.ADVANCED) null else Complexity.ADVANCED },
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
                            onClick = {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            },
                            label = { Text(tag, fontSize = 12.sp) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (isReviewerMode && onExportGrievances != null) {
                ExtendedFloatingActionButton(
                    onClick = onExportGrievances,
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    text = { Text("Airing of Grievances") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { paddingValues ->
        if (filteredSamples.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matching samples found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredSamples, key = { it.id }) { sample ->
                    val targetFqcn = sample.getTargetFqcn(selectedFramework)
                    val eval = evaluations[targetFqcn] ?: evaluations[sample.id]
                    val status = ReviewStatus.fromString(eval?.status)
                    SampleComposeCard(
                        sample = sample,
                        targetFqcn = targetFqcn,
                        framework = selectedFramework,
                        isReviewerMode = isReviewerMode,
                        evaluation = eval,
                        status = status,
                        onSampleClick = { onLaunchSample(sample, selectedFramework) },
                        onInfoClick = { activeSampleForExpectations = sample },
                        onStatusClick = { activeSampleForExpectations = sample }
                    )
                }
            }
        }
    }

    // Modal Expectations & Review BottomSheet
    activeSampleForExpectations?.let { sample ->
        val targetFqcn = sample.getTargetFqcn(selectedFramework)
        val existingEval = evaluations[targetFqcn] ?: evaluations[sample.id]
        SampleExpectationsModalSheet(
            sample = sample,
            targetFqcn = targetFqcn,
            framework = selectedFramework,
            isReviewerMode = isReviewerMode,
            existingEvaluation = existingEval,
            onDismiss = { activeSampleForExpectations = null },
            onSaveEvaluation = { status, notes ->
                onSaveEvaluation?.invoke(targetFqcn, status, notes, sample)
                activeSampleForExpectations = null
            },
            onLaunch = { fw ->
                activeSampleForExpectations = null
                onLaunchSample(sample, fw)
            }
        )
    }
}

@Composable
fun SampleComposeCard(
    sample: SampleItem,
    targetFqcn: String,
    framework: Framework,
    isReviewerMode: Boolean,
    evaluation: SampleEvaluationEntity?,
    status: ReviewStatus,
    onSampleClick: () -> Unit,
    onInfoClick: () -> Unit,
    onStatusClick: () -> Unit
) {
    val hasActivity = sample.getActivityForFramework(framework) != null

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (hasActivity) onSampleClick() else onInfoClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Category, Complexity, Review Status
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("${sample.complexity.badge} ${sample.complexity.displayName}", fontSize = 11.sp) }
                    )

                    if (isReviewerMode) {
                        val (statusText, statusBg, statusFg) = when (status) {
                            ReviewStatus.PASSING -> Triple("🟢 Pass", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                            ReviewStatus.NEEDS_WORK -> Triple("🔴 Needs Work", Color(0xFFFFEBEE), Color(0xFFC62828))
                            ReviewStatus.UNCHECKED -> Triple("⚪ Unchecked", Color(0xFFEEEEEE), Color(0xFF616161))
                        }

                        Button(
                            onClick = onStatusClick,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = statusBg,
                                contentColor = statusFg
                            ),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(statusText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Title
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = sample.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // FQCN Target identifier
            Text(
                text = targetFqcn.substringAfterLast('.'),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            // Description
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = sample.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Reviewer Notes preview (if reviewer mode and notes present)
            if (isReviewerMode && !evaluation?.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF3E0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝 Note: ${evaluation?.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100),
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(8.dp)
                    )
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onInfoClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Info / Criteria", fontSize = 12.sp)
                }

                Button(
                    onClick = onSampleClick,
                    enabled = hasActivity,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        if (hasActivity) "Launch Sample" else "No ${framework.badge} Impl",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleExpectationsModalSheet(
    sample: SampleItem,
    targetFqcn: String,
    framework: Framework,
    isReviewerMode: Boolean,
    existingEvaluation: SampleEvaluationEntity?,
    onDismiss: () -> Unit,
    onSaveEvaluation: (ReviewStatus, String) -> Unit,
    onLaunch: (Framework) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentStatus by remember { mutableStateOf(ReviewStatus.fromString(existingEvaluation?.status)) }
    var notesText by remember { mutableStateOf(existingEvaluation?.notes.orEmpty()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            // Title & Complexity
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = sample.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                SuggestionChip(
                    onClick = {},
                    label = { Text("${sample.complexity.badge} ${sample.complexity.displayName}") }
                )
            }

            Text(
                text = "${sample.category} • ${sample.tags.joinToString(" ")}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )

            // FQCN Target Identifier
            Text(
                text = "Target: $targetFqcn",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 2.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Formatted HTML Expectations Box
            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        textSize = 14f
                        setLineSpacing(4f, 1.1f)
                    }
                },
                update = { textView ->
                    textView.text = Html.fromHtml(sample.getFormattedHelpHtml(), Html.FROM_HTML_MODE_COMPACT)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Collapsible, Syntax-Highlighted Code Snippet View
            Spacer(modifier = Modifier.height(10.dp))
            CodeSnippetView(
                sample = sample,
                currentFramework = framework,
                initiallyExpanded = sample.complexity == Complexity.SNIPPET,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Reviewer Controls (Only in Reviewer Mode!)
            if (isReviewerMode) {
                Text(
                    text = "🔍 Reviewer Status Evaluation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentStatus == ReviewStatus.PASSING,
                        onClick = { currentStatus = ReviewStatus.PASSING },
                        label = { Text("🟢 Passing") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = currentStatus == ReviewStatus.NEEDS_WORK,
                        onClick = { currentStatus = ReviewStatus.NEEDS_WORK },
                        label = { Text("🔴 Needs Work") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = currentStatus == ReviewStatus.UNCHECKED,
                        onClick = { currentStatus = ReviewStatus.UNCHECKED },
                        label = { Text("⚪ Unchecked") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Reviewer Notes & Grievances (Bugs / Improvements)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onSaveEvaluation(currentStatus, notesText) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Review Evaluation")
                }
            }

            // Cross-Framework Switcher
            val altFramework = when (framework) {
                Framework.KOTLIN_VIEWS -> if (sample.javaActivity != null) Framework.JAVA_VIEWS else null
                Framework.JAVA_VIEWS -> if (sample.kotlinActivity != null) Framework.KOTLIN_VIEWS else null
            }

            if (altFramework != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { onLaunch(altFramework) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Switch to ${altFramework.displayName}")
                }
            }

            // Launch Sample Primary Action
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { onLaunch(framework) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Launch ${framework.displayName}")
            }
        }
    }
}
