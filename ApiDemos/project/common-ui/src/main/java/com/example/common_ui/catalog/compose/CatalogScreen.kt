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
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    var selectedStatusFilter by remember { mutableStateOf<ReviewStatus?>(null) }
    val selectedTags = remember { mutableStateListOf<String>() }
    var searchQuery by remember { mutableStateOf("") }
    var activeSampleForDetail by remember { mutableStateOf<SampleItem?>(null) }
    var activeQuickGrading by remember { mutableStateOf<Pair<SampleItem, ReviewStatus>?>(null) }

    // Collapsible header controls state
    var isHeaderControlsVisible by remember { mutableStateOf(true) }
    val lazyListState = rememberLazyListState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // When user scrolls content up (dragging upwards, available.y is negative), collapse tabs and search bar
                if (available.y < -10f && isHeaderControlsVisible) {
                    isHeaderControlsVisible = false
                } else if (available.y > 10f && !isHeaderControlsVisible) {
                    isHeaderControlsVisible = true
                }
                return Offset.Zero
            }
        }
    }

    // Dynamic review status counts for active framework
    val statusCounts = remember(selectedFramework, evaluations) {
        val totalSamples = SampleCatalogRegistry.filter(framework = selectedFramework)
        var unchecked = 0
        var passing = 0
        var needsWork = 0
        for (s in totalSamples) {
            val targetFqcn = s.getTargetFqcn(selectedFramework)
            val eval = evaluations[targetFqcn] ?: evaluations[s.id]
            when (ReviewStatus.fromString(eval?.status)) {
                ReviewStatus.UNCHECKED -> unchecked++
                ReviewStatus.PASSING -> passing++
                ReviewStatus.NEEDS_WORK -> needsWork++
            }
        }
        Triple(unchecked, passing, needsWork)
    }
    val (uncheckedCount, passingCount, needsWorkCount) = statusCounts

    val filteredSamples = remember(
        selectedFramework,
        selectedComplexity,
        selectedStatusFilter,
        selectedTags.toList(),
        searchQuery,
        evaluations
    ) {
        SampleCatalogRegistry.filter(
            framework = selectedFramework,
            complexity = selectedComplexity,
            selectedTags = selectedTags.toSet(),
            searchQuery = searchQuery
        ).filter { sample ->
            if (selectedStatusFilter == null) true
            else {
                val targetFqcn = sample.getTargetFqcn(selectedFramework)
                val eval = evaluations[targetFqcn] ?: evaluations[sample.id]
                val status = ReviewStatus.fromString(eval?.status)
                status == selectedStatusFilter
            }
        }
    }

    val grievancesCount = remember(evaluations) {
        evaluations.values.count { it.status == "NEEDS_WORK" || it.notes.isNotBlank() }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
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
                                text = if (isReviewerMode) {
                                    val filterSummary = if (selectedStatusFilter == ReviewStatus.UNCHECKED) " • ⚪ Unchecked Only" else ""
                                    "${selectedFramework.badge} ${selectedFramework.displayName} • ${filteredSamples.size} samples$filterSummary"
                                } else {
                                    "Unified Multi-Framework Catalog • ${filteredSamples.size} samples"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isReviewerMode) Color(0xFFD93025) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        // Quick toggle button for Unchecked Only in Reviewer Mode
                        if (isReviewerMode) {
                            IconButton(
                                onClick = {
                                    selectedStatusFilter = if (selectedStatusFilter == ReviewStatus.UNCHECKED) null else ReviewStatus.UNCHECKED
                                }
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (uncheckedCount > 0) {
                                            Badge { Text("$uncheckedCount") }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selectedStatusFilter == ReviewStatus.UNCHECKED) Icons.Default.CheckCircleOutline else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Filter Unchecked Only",
                                        tint = if (selectedStatusFilter == ReviewStatus.UNCHECKED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        // Toggle search/filters button
                        IconButton(onClick = { isHeaderControlsVisible = !isHeaderControlsVisible }) {
                            Icon(
                                imageVector = if (isHeaderControlsVisible) Icons.Default.FilterListOff else Icons.Default.FilterList,
                                contentDescription = "Toggle Filters & Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

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
                                        contentDescription = "Generate Report",
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

                // Built-in Compose Animated Collapsible Controls (Tabs, Search Bar, Filter Chips)
                AnimatedVisibility(
                    visible = isHeaderControlsVisible,
                    enter = expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) + fadeIn(animationSpec = tween(200)),
                    exit = shrinkVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeOut(animationSpec = tween(150))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
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
                                text = { Text("💜 Kotlin", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedFramework == Framework.JAVA_VIEWS,
                                onClick = { selectedFramework = Framework.JAVA_VIEWS },
                                text = { Text("☕ Java", fontWeight = FontWeight.Bold) }
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
                                    onClick = { selectedStatusFilter = null },
                                    label = { Text("All Status") }
                                )
                                FilterChip(
                                    selected = selectedStatusFilter == ReviewStatus.UNCHECKED,
                                    onClick = {
                                        selectedStatusFilter = if (selectedStatusFilter == ReviewStatus.UNCHECKED) null else ReviewStatus.UNCHECKED
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
                                        selectedStatusFilter = if (selectedStatusFilter == ReviewStatus.NEEDS_WORK) null else ReviewStatus.NEEDS_WORK
                                    },
                                    label = { Text("🔴 Needs Work ($needsWorkCount)") }
                                )
                                FilterChip(
                                    selected = selectedStatusFilter == ReviewStatus.PASSING,
                                    onClick = {
                                        selectedStatusFilter = if (selectedStatusFilter == ReviewStatus.PASSING) null else ReviewStatus.PASSING
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
                                onClick = { selectedComplexity = null },
                                label = { Text("All Complexity") }
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
                }
            }
        },
        floatingActionButton = {
            if (isReviewerMode && onExportGrievances != null) {
                FloatingActionButton(
                    onClick = onExportGrievances,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = "Generate Report"
                    )
                }
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (selectedStatusFilter == ReviewStatus.UNCHECKED) "🎉 All samples in this framework have been evaluated!" else "No matching samples found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (selectedStatusFilter != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { selectedStatusFilter = null }) {
                            Text("Clear Status Filter")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                state = lazyListState,
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
                        onInfoClick = { activeSampleForDetail = sample },
                        onQuickGrade = { gradeStatus ->
                            activeQuickGrading = Pair(sample, gradeStatus)
                        }
                    )
                }
            }
        }
    }

    // Full-Screen Sample Detail & Code Viewer Dialog
    activeSampleForDetail?.let { sample ->
        val targetFqcn = sample.getTargetFqcn(selectedFramework)
        val existingEval = evaluations[targetFqcn] ?: evaluations[sample.id]
        SampleDetailFullScreenDialog(
            sample = sample,
            targetFqcn = targetFqcn,
            framework = selectedFramework,
            isReviewerMode = isReviewerMode,
            existingEvaluation = existingEval,
            onDismiss = { activeSampleForDetail = null },
            onSaveEvaluation = { status, notes ->
                onSaveEvaluation?.invoke(targetFqcn, status, notes, sample)
                activeSampleForDetail = null
            },
            onLaunch = { fw ->
                activeSampleForDetail = null
                onLaunchSample(sample, fw)
            }
        )
    }

    // Quick Grading Dialog from List Card (Allows adding notes before saving)
    activeQuickGrading?.let { (sample, gradeStatus) ->
        val targetFqcn = sample.getTargetFqcn(selectedFramework)
        val existingEval = evaluations[targetFqcn] ?: evaluations[sample.id]
        var notes by remember { mutableStateOf(existingEval?.notes.orEmpty()) }

        AlertDialog(
            onDismissRequest = { activeQuickGrading = null },
            title = {
                Text(
                    text = if (gradeStatus == ReviewStatus.PASSING) "👍 Good Job: ${sample.title}" else "⚠️ Something's Wrong: ${sample.title}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Target: ${targetFqcn.substringAfterLast('.')}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional for pass, describe issues if broken)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveEvaluation?.invoke(targetFqcn, gradeStatus, notes, sample)
                        activeQuickGrading = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gradeStatus == ReviewStatus.PASSING) Color(0xFF2E7D32) else Color(0xFFC62828),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (gradeStatus == ReviewStatus.PASSING) "Record Pass 👍" else "Record Issue ⚠️")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeQuickGrading = null }) {
                    Text("Cancel")
                }
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
    onQuickGrade: (ReviewStatus) -> Unit
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
            // Header: Category and Complexity Badge
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

                SuggestionChip(
                    onClick = {},
                    label = { Text("${sample.complexity.badge} ${sample.complexity.displayName}", fontSize = 11.sp) }
                )
            }

            // Title
            Spacer(modifier = Modifier.height(4.dp))
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

            // Reviewer Status Badge & Notes (Reviewer Mode Only)
            if (isReviewerMode) {
                Spacer(modifier = Modifier.height(8.dp))
                val (statusText, statusBg, statusFg) = when (status) {
                    ReviewStatus.PASSING -> Triple("🟢 Pass", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                    ReviewStatus.NEEDS_WORK -> Triple("🔴 Needs Work", Color(0xFFFFEBEE), Color(0xFFC62828))
                    ReviewStatus.UNCHECKED -> Triple("⚪ Unchecked", Color(0xFFEEEEEE), Color(0xFF616161))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusBg,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusFg,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

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

                // Reviewer Quick Grading Buttons (In-card)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onQuickGrade(ReviewStatus.PASSING) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFE8F5E9),
                            contentColor = Color(0xFF2E7D32)
                        ),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("👍 Good Job", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { onQuickGrade(ReviewStatus.NEEDS_WORK) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFFFEBEE),
                            contentColor = Color(0xFFC62828)
                        ),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("⚠️ Issue", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    Text("Info & Code", fontSize = 12.sp)
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

/**
 * Full-screen detailed view of a sample including Purpose, Success Criteria,
 * syntax-highlighted code editor, and reviewer evaluation controls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleDetailFullScreenDialog(
    sample: SampleItem,
    targetFqcn: String,
    framework: Framework,
    isReviewerMode: Boolean,
    existingEvaluation: SampleEvaluationEntity?,
    onDismiss: () -> Unit,
    onSaveEvaluation: (ReviewStatus, String) -> Unit,
    onLaunch: (Framework) -> Unit
) {
    var currentStatus by remember { mutableStateOf(ReviewStatus.fromString(existingEvaluation?.status)) }
    var notesText by remember { mutableStateOf(existingEvaluation?.notes.orEmpty()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = sample.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "${sample.category} • ${sample.complexity.badge} ${sample.complexity.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    actions = {
                        Button(
                            onClick = { onLaunch(framework) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Launch", fontSize = 12.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val altFramework = when (framework) {
                            Framework.KOTLIN_VIEWS -> if (sample.javaActivity != null) Framework.JAVA_VIEWS else null
                            Framework.JAVA_VIEWS -> if (sample.kotlinActivity != null) Framework.KOTLIN_VIEWS else null
                        }

                        if (altFramework != null) {
                            OutlinedButton(
                                onClick = { onLaunch(altFramework) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Switch to ${altFramework.displayName}", fontSize = 12.sp, maxLines = 1)
                            }
                        }

                        Button(
                            onClick = { onLaunch(framework) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Launch ${framework.badge}", fontSize = 12.sp)
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // FQCN Target Info Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Target Class (FQCN)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = targetFqcn,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Card 1: Purpose & Criteria HTML Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🎯 Purpose & Verification Criteria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AndroidView(
                            factory = { context ->
                                TextView(context).apply {
                                    textSize = 14f
                                    setLineSpacing(4f, 1.15f)
                                }
                            },
                            update = { textView ->
                                textView.text = Html.fromHtml(sample.getFormattedHelpHtml(), Html.FROM_HTML_MODE_COMPACT)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Card 2: Full-Width Code Snippet Viewer (No Squishing!)
                CodeSnippetView(
                    sample = sample,
                    currentFramework = framework,
                    initiallyExpanded = true,
                    isCollapsible = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Card 3: Reviewer Evaluation Controls (Reviewer Mode Only)
                if (isReviewerMode) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "🔍 Reviewer Evaluation & Grievances",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Status Buttons (Clean, Non-wrapping layout)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { currentStatus = ReviewStatus.PASSING },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentStatus == ReviewStatus.PASSING) Color(0xFF2E7D32) else Color(0xFFE8F5E9),
                                        contentColor = if (currentStatus == ReviewStatus.PASSING) Color.White else Color(0xFF2E7D32)
                                    ),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("🟢 Pass", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { currentStatus = ReviewStatus.NEEDS_WORK },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentStatus == ReviewStatus.NEEDS_WORK) Color(0xFFC62828) else Color(0xFFFFEBEE),
                                        contentColor = if (currentStatus == ReviewStatus.NEEDS_WORK) Color.White else Color(0xFFC62828)
                                    ),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("🔴 Needs Work", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { currentStatus = ReviewStatus.UNCHECKED },
                                    modifier = Modifier.weight(1.1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentStatus == ReviewStatus.UNCHECKED) Color(0xFF616161) else Color(0xFFEEEEEE),
                                        contentColor = if (currentStatus == ReviewStatus.UNCHECKED) Color.White else Color(0xFF616161)
                                    ),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("⚪ Unchecked", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = notesText,
                                onValueChange = { notesText = it },
                                label = { Text("Reviewer Notes & Grievances (Bugs, reproduction steps, UI flaws)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 6,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))
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
                    }
                }

                // Extra bottom spacing
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
