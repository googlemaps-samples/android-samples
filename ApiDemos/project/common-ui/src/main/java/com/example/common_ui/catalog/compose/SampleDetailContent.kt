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

import android.graphics.BitmapFactory
import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.db.SampleEvaluationEntity
import java.io.File

/**
 * Full-screen detail dialog wrapper for [SampleDetailContent].
 */
@Composable
fun SampleDetailFullScreenDialog(
    sample: SampleItem,
    targetFqcn: String,
    framework: Framework,
    isReviewerMode: Boolean,
    existingEvaluation: SampleEvaluationEntity?,
    onDismiss: () -> Unit,
    onSaveEvaluation: (ReviewStatus, String) -> Unit,
    onLaunch: (Framework) -> Unit,
    onSaveAndNext: ((ReviewStatus, String) -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        SampleDetailContent(
            sample = sample,
            targetFqcn = targetFqcn,
            framework = framework,
            isReviewerMode = isReviewerMode,
            existingEvaluation = existingEvaluation,
            onDismiss = onDismiss,
            onSaveEvaluation = onSaveEvaluation,
            onLaunch = onLaunch,
            onSaveAndNext = onSaveAndNext
        )
    }
}

/**
 * Comprehensive "Info & Code" view containing:
 * 1. Title, Category, Complexity & Launch header
 * 2. Target Class (FQCN) banner
 * 3. Card 1: Acceptance Criteria, Purpose, What to Verify, Edge Cases
 * 4. Card 2: Full-width syntax-highlighted Source Code Snippet with Kotlin/Java tabs
 * 5. Card 3: Reviewer Evaluation controls (Pass/Needs Work/Unchecked, notes, attached screenshot)
 * 6. Pinned bottom action bar with Framework Switch, Save, and Save & Next
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleDetailContent(
    sample: SampleItem,
    targetFqcn: String,
    framework: Framework,
    isReviewerMode: Boolean,
    existingEvaluation: SampleEvaluationEntity?,
    onDismiss: () -> Unit,
    onSaveEvaluation: (ReviewStatus, String) -> Unit,
    onLaunch: (Framework) -> Unit,
    onSaveAndNext: ((ReviewStatus, String) -> Unit)? = null
) {
    var currentStatus by rememberSaveable { mutableStateOf(ReviewStatus.fromString(existingEvaluation?.status)) }
    var notesText by rememberSaveable { mutableStateOf(existingEvaluation?.notes.orEmpty()) }

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
                        fontFamily = FontFamily.Monospace,
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
                        text = if (isReviewerMode) "🎯 Purpose & Verification Criteria" else "💡 About This Sample",
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
                            textView.text = Html.fromHtml(sample.getFormattedHelpHtml(isReviewerMode), Html.FROM_HTML_MODE_COMPACT)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Card 2: Key API Calls (Focused API indicators instead of clunky code blocks)
            if (sample.apiCalls.isNotEmpty()) {
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Key API Calls",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        sample.apiCalls.forEach { apiCall ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Text(
                                    text = apiCall,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }

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

                        if (!existingEvaluation?.screenshotPath.isNullOrBlank()) {
                            val sFile = File(existingEvaluation.screenshotPath)
                            if (sFile.exists()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "📸 Attached Screenshot & Markup:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val bitmap = remember(sFile.absolutePath) {
                                    BitmapFactory.decodeFile(sFile.absolutePath)
                                }
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Annotated Issue Screenshot",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (existingEvaluation != null) {
                                OutlinedButton(
                                    onClick = {
                                        currentStatus = ReviewStatus.UNCHECKED
                                        notesText = ""
                                        onSaveEvaluation(ReviewStatus.UNCHECKED, "")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Reset", color = MaterialTheme.colorScheme.error)
                                }
                            }

                            OutlinedButton(
                                onClick = { onSaveEvaluation(currentStatus, notesText) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save", maxLines = 1)
                            }
                            Button(
                                onClick = {
                                    if (onSaveAndNext != null) {
                                        onSaveAndNext(currentStatus, notesText)
                                    } else {
                                        onSaveEvaluation(currentStatus, notesText)
                                    }
                                },
                                modifier = Modifier.weight(1.5f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Save & Next ⏭️", fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Extra bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
