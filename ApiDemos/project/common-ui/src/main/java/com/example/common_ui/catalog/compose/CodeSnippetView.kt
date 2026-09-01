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

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.SampleItem

/**
 * Collapsible, syntax-highlighted code viewer composable.
 *
 * Renders Kotlin and Java source snippets with line numbers, theme-adaptive coloring, and one-tap clipboard copy.
 */
@Composable
fun CodeSnippetView(
    sample: SampleItem,
    currentFramework: Framework = Framework.KOTLIN_VIEWS,
    initiallyExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    var selectedFramework by remember(currentFramework) { mutableStateOf(currentFramework) }
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val rawCode = remember(sample.id, selectedFramework) {
        SampleCodeProvider.getCode(sample.id, selectedFramework)
    }

    val highlightedCode = remember(rawCode, isDark) {
        CodeHighlighter.highlight(rawCode, isDark = isDark)
    }

    val codeLines = remember(rawCode) {
        rawCode.lines()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E1E2E) else Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar (Tap to toggle collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                        text = "Source Code Snippet",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SuggestionChip(
                        onClick = { isExpanded = !isExpanded },
                        label = { Text(if (selectedFramework == Framework.KOTLIN_VIEWS) "Kotlin" else "Java", fontSize = 11.sp) }
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expandable Content Body
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    // Toolbar: Language Switcher and Copy Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Language Tab Selector
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = selectedFramework == Framework.KOTLIN_VIEWS,
                                onClick = { selectedFramework = Framework.KOTLIN_VIEWS },
                                label = { Text("💜 Kotlin", fontSize = 12.sp) }
                            )
                            FilterChip(
                                selected = selectedFramework == Framework.JAVA_VIEWS,
                                onClick = { selectedFramework = Framework.JAVA_VIEWS },
                                label = { Text("☕ Java", fontSize = 12.sp) }
                            )
                        }

                        // Copy Button
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(rawCode))
                                Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy code",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Code Editor Box with Line Numbers & Monospace Font
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        color = if (isDark) Color(0xFF14141E) else Color(0xFFE8EAED)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            // Line numbers gutter
                            Column(
                                modifier = Modifier.padding(start = 10.dp, end = 12.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                codeLines.indices.forEach { index ->
                                    Text(
                                        text = (index + 1).toString().padStart(2, '0'),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = if (isDark) Color(0xFF6272A4) else Color(0xFF9AA0A6),
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            // Highlighted code text
                            Text(
                                text = highlightedCode,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
