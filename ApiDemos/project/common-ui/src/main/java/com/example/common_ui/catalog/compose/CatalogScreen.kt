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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import java.io.File
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleCatalogRegistry
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.db.SampleEvaluationEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    isReviewerMode: Boolean = false,
    evaluations: Map<String, SampleEvaluationEntity> = emptyMap(),
    onSaveEvaluation: ((targetFqcn: String, status: ReviewStatus, notes: String, sample: SampleItem) -> Unit)? = null,
    onLaunchSample: (SampleItem, Framework) -> Unit,
    onExportGrievances: (() -> Unit)? = null,
    onClearEvaluations: (() -> Unit)? = null
) {
    var selectedFramework by rememberSaveable { mutableStateOf(Framework.KOTLIN_VIEWS) }
    var selectedComplexity by rememberSaveable { mutableStateOf<Complexity?>(null) }
    var selectedStatusFilter by rememberSaveable { mutableStateOf<ReviewStatus?>(null) }
    var selectedTags by rememberSaveable { mutableStateOf(emptySet<String>()) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var activeSampleDetailId by rememberSaveable { mutableStateOf<String?>(null) }
    val activeSampleForDetail: SampleItem? = remember(activeSampleDetailId) {
        SampleCatalogRegistry.findById(activeSampleDetailId)
    }
    var activeQuickGradingSampleId by rememberSaveable { mutableStateOf<String?>(null) }
    var activeQuickGradingStatus by rememberSaveable { mutableStateOf<ReviewStatus?>(null) }
    val activeQuickGrading: Pair<SampleItem, ReviewStatus>? = remember(activeQuickGradingSampleId, activeQuickGradingStatus) {
        val sId = activeQuickGradingSampleId
        val st = activeQuickGradingStatus
        val sample = SampleCatalogRegistry.findById(sId)
        if (sample != null && st != null) Pair(sample, st) else null
    }
    var showClearConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val frameworkSamples = remember(selectedFramework) {
        SampleCatalogRegistry.filter(framework = selectedFramework)
    }

    // Dynamic review status counts for active framework
    val statusCounts = remember(selectedFramework, evaluations, frameworkSamples) {
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
        Triple(unchecked, passing, needsWork)
    }
    val (uncheckedCount, passingCount, needsWorkCount) = statusCounts

    val filteredSamples = remember(
        selectedFramework,
        selectedComplexity,
        selectedStatusFilter,
        selectedTags,
        searchQuery,
        evaluations
    ) {
        SampleCatalogRegistry.filter(
            framework = selectedFramework,
            complexity = selectedComplexity,
            selectedTags = selectedTags,
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
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
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
                                "${selectedFramework.displayName} • ${filteredSamples.size} samples$filterSummary"
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
                                    tint = if (selectedStatusFilter == ReviewStatus.UNCHECKED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Next Unchecked Action Button
                        if (uncheckedCount > 0) {
                            IconButton(onClick = {
                                val nextUnchecked = filteredSamples.firstOrNull { sample ->
                                    val eval = evaluations[sample.getTargetFqcn(selectedFramework)]
                                    eval == null || eval.status == ReviewStatus.UNCHECKED.name
                                } ?: frameworkSamples.firstOrNull { sample ->
                                    val eval = evaluations[sample.getTargetFqcn(selectedFramework)]
                                    eval == null || eval.status == ReviewStatus.UNCHECKED.name
                                }
                                if (nextUnchecked != null) {
                                    onLaunchSample(nextUnchecked, selectedFramework)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.FastForward,
                                    contentDescription = "Launch Next Unchecked Sample",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Direct Reset / Clear Reviews Button (Always Available)
                        if (onClearEvaluations != null) {
                            IconButton(onClick = { showClearConfirmDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = "Reset All Evaluations",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Jump to Search & Filters Button
                    IconButton(onClick = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    }) {
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
                                DropdownMenuItem(
                                    text = { Text("🔄 Reset All Evaluations", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMoreMenu = false
                                        showClearConfirmDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("📊 Generate Evaluation Report") },
                                    onClick = {
                                        showMoreMenu = false
                                        onExportGrievances?.invoke()
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(if (selectedStatusFilter == ReviewStatus.UNCHECKED) "Show All Samples" else "⚪ Show Unchecked Only")
                                    },
                                    onClick = {
                                        showMoreMenu = false
                                        selectedStatusFilter = if (selectedStatusFilter == ReviewStatus.UNCHECKED) null else ReviewStatus.UNCHECKED
                                    }
                                )
                                HorizontalDivider()
                            }
                            DropdownMenuItem(
                                text = { Text("Scroll to Top") },
                                onClick = {
                                    showMoreMenu = false
                                    coroutineScope.launch { lazyListState.animateScrollToItem(0) }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isReviewerMode) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uncheckedCount > 0) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                val nextUnchecked = filteredSamples.firstOrNull { sample ->
                                    val eval = evaluations[sample.getTargetFqcn(selectedFramework)]
                                    eval == null || eval.status == ReviewStatus.UNCHECKED.name
                                } ?: frameworkSamples.firstOrNull { sample ->
                                    val eval = evaluations[sample.getTargetFqcn(selectedFramework)]
                                    eval == null || eval.status == ReviewStatus.UNCHECKED.name
                                }
                                if (nextUnchecked != null) {
                                    onLaunchSample(nextUnchecked, selectedFramework)
                                }
                            },
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
                                    imageVector = Icons.Default.Assessment,
                                    contentDescription = "Generate Report"
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Item 1: Framework Tabs
            item(key = "header_framework_tabs") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(bottom = 6.dp)
                ) {
                    PrimaryTabRow(
                        selectedTabIndex = when (selectedFramework) {
                            Framework.KOTLIN_VIEWS -> 0
                            Framework.JAVA_VIEWS -> 1
                        }
                    ) {
                        Tab(
                            selected = selectedFramework == Framework.KOTLIN_VIEWS,
                            onClick = { selectedFramework = Framework.KOTLIN_VIEWS },
                            text = { Text("💜 Kotlin Views", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedFramework == Framework.JAVA_VIEWS,
                            onClick = { selectedFramework = Framework.JAVA_VIEWS },
                            text = { Text("☕ Java Views", fontWeight = FontWeight.Bold) }
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
                                    selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                },
                                label = { Text(tag, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // Empty State Handling
            if (filteredSamples.isEmpty()) {
                item(key = "empty_samples_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
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
                }
            } else {
                // Sample Cards
                items(filteredSamples, key = { it.id }) { sample ->
                    val targetFqcn = sample.getTargetFqcn(selectedFramework)
                    val eval = evaluations[targetFqcn] ?: evaluations[sample.id]
                    val status = ReviewStatus.fromString(eval?.status)
                    Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                        SampleComposeCard(
                            sample = sample,
                            targetFqcn = targetFqcn,
                            framework = selectedFramework,
                            isReviewerMode = isReviewerMode,
                            evaluation = eval,
                            status = status,
                            onSampleClick = { onLaunchSample(sample, selectedFramework) },
                            onInfoClick = { activeSampleDetailId = sample.id },
                            onQuickGrade = { gradeStatus ->
                                activeQuickGradingSampleId = sample.id
                                activeQuickGradingStatus = gradeStatus
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Clearing / Resetting All Evaluations
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            icon = { Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Reset All Review Evaluations?") },
            text = {
                Text("This will reset all ratings, status marks, and reviewer notes across all Kotlin and Java samples back to Unchecked.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearEvaluations?.invoke()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Reset All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
            onDismiss = { activeSampleDetailId = null },
            onSaveEvaluation = { status, notes ->
                onSaveEvaluation?.invoke(targetFqcn, status, notes, sample)
                activeSampleDetailId = null
            },
            onLaunch = { fw ->
                activeSampleDetailId = null
                onLaunchSample(sample, fw)
            }
        )
    }

    // Quick Grading Dialog from List Card (Allows adding notes before saving)
    activeQuickGrading?.let { (sample, gradeStatus) ->
        val targetFqcn = sample.getTargetFqcn(selectedFramework)
        val existingEval = evaluations[targetFqcn] ?: evaluations[sample.id]
        var notes by rememberSaveable { mutableStateOf(existingEval?.notes.orEmpty()) }

        AlertDialog(
            onDismissRequest = { activeQuickGradingSampleId = null; activeQuickGradingStatus = null },
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            onSaveEvaluation?.invoke(targetFqcn, gradeStatus, notes, sample)
                            activeQuickGradingSampleId = null
                            activeQuickGradingStatus = null
                        }
                    ) {
                        Text(if (gradeStatus == ReviewStatus.PASSING) "Save Pass 👍" else "Save Issue ⚠️")
                    }
                    Button(
                        onClick = {
                            onSaveEvaluation?.invoke(targetFqcn, gradeStatus, notes, sample)
                            activeQuickGradingSampleId = null
                            activeQuickGradingStatus = null
                            val allFw = SampleCatalogRegistry.filter(framework = selectedFramework)
                            val currIdx = allFw.indexOfFirst { it.id == sample.id }
                            val nextUnchecked = if (currIdx >= 0) {
                                (allFw.drop(currIdx + 1) + allFw.take(currIdx)).firstOrNull { s ->
                                    val fqcn = s.getTargetFqcn(selectedFramework)
                                    val ev = evaluations[fqcn] ?: evaluations[s.id]
                                    ReviewStatus.fromString(ev?.status) == ReviewStatus.UNCHECKED
                                }
                            } else null
                            if (nextUnchecked != null) {
                                onLaunchSample(nextUnchecked, selectedFramework)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save & Next ⏭️", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    activeQuickGradingSampleId = null
                    activeQuickGradingStatus = null
                }) {
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
    val isReviewed = isReviewerMode && (status == ReviewStatus.PASSING || status == ReviewStatus.NEEDS_WORK)
    var isExpandedManually by remember(sample.id, status) { mutableStateOf<Boolean?>(null) }
    val isCardExpanded = isExpandedManually ?: (!isReviewed)

    val (statusText, statusBg, statusFg) = when (status) {
        ReviewStatus.PASSING -> Triple("🟢 Pass", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        ReviewStatus.NEEDS_WORK -> Triple("🔴 Needs Work", Color(0xFFFFEBEE), Color(0xFFC62828))
        ReviewStatus.UNCHECKED -> Triple("⚪ Unchecked", Color(0xFFEEEEEE), Color(0xFF616161))
    }

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
            // === CLEAN COMPACT COLLAPSED ROW (Title + Status + Expand Arrow) ===
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

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusBg
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusFg,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

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
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        } else {
            // === FULL DETAILED EXPANDED CARD ===
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
                            onClick = { isExpandedManually = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.ExpandLess,
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
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusBg
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusFg,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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
    }
}

