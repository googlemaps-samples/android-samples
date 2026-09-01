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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import java.util.regex.Pattern

/**
 * Lightweight, pure-Compose syntax highlighter for Kotlin and Java source code.
 *
 * Converts raw source code into an [AnnotatedString] styled with theme-aware syntax tokens.
 */
object CodeHighlighter {

    // Kotlin & Java Keywords
    private val KEYWORDS = setOf(
        "abstract", "actual", "annotation", "as", "break", "by", "byte", "case", "catch",
        "char", "class", "companion", "const", "constructor", "continue", "crossinline",
        "data", "default", "delegate", "do", "double", "dynamic", "else", "enum", "expect",
        "extends", "external", "false", "field", "file", "final", "finally", "float", "for",
        "fun", "get", "if", "implements", "import", "in", "infix", "init", "inline",
        "inner", "instanceof", "int", "interface", "internal", "is", "it", "lateinit",
        "long", "native", "new", "noinline", "null", "object", "open", "operator", "out",
        "override", "package", "param", "private", "property", "protected", "public",
        "reified", "return", "sealed", "set", "short", "static", "strictfp", "super",
        "suspend", "switch", "synchronized", "tailrec", "this", "throw", "throws",
        "transient", "true", "try", "typealias", "typeof", "val", "value", "var",
        "vararg", "void", "volatile", "when", "where", "while", "yield"
    )

    private val COMMENT_REGEX = Pattern.compile("(//.*?$|/\\*.*?\\*/)", Pattern.MULTILINE or Pattern.DOTALL)
    private val STRING_REGEX = Pattern.compile("(\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^'\\\\])*')", Pattern.MULTILINE)
    private val ANNOTATION_REGEX = Pattern.compile("@[A-Za-z0-9_]+")
    private val NUMBER_REGEX = Pattern.compile("\\b(\\d+(\\.\\d+)?[fFL]?|0x[0-9a-fA-F]+)\\b")
    private val WORD_REGEX = Pattern.compile("\\b[A-Za-z_][A-Za-z0-9_]*\\b")

    /**
     * Highlights code and returns an [AnnotatedString].
     */
    fun highlight(code: String, isDark: Boolean = true): AnnotatedString {
        val keywordColor = if (isDark) Color(0xFFFF79C6) else Color(0xFF9C27B0)
        val annotationColor = if (isDark) Color(0xFFFFB86C) else Color(0xFFEF6C00)
        val stringColor = if (isDark) Color(0xFF50FA7B) else Color(0xFF2E7D32)
        val commentColor = if (isDark) Color(0xFF6272A4) else Color(0xFF757575)
        val numberColor = if (isDark) Color(0xFFBD93F9) else Color(0xFF1565C0)
        val typeColor = if (isDark) Color(0xFF8BE9FD) else Color(0xFF00838F)
        val plainColor = if (isDark) Color(0xFFF8F8F2) else Color(0xFF212121)

        val fullText = code.trimIndent()
        val textLength = fullText.length

        val stringBuilder = buildAnnotatedString {
            append(fullText)

            // Base style
            addStyle(SpanStyle(color = plainColor), 0, textLength)

            // 1. Types / Classes and Keywords
            val wordMatcher = WORD_REGEX.matcher(fullText)
            while (wordMatcher.find()) {
                val start = wordMatcher.start()
                val end = wordMatcher.end()
                val word = fullText.substring(start, end)

                if (KEYWORDS.contains(word)) {
                    addStyle(
                        SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold),
                        start,
                        end
                    )
                } else if (word.first().isUpperCase()) {
                    addStyle(
                        SpanStyle(color = typeColor, fontWeight = FontWeight.SemiBold),
                        start,
                        end
                    )
                }
            }

            // 2. Numbers
            val numberMatcher = NUMBER_REGEX.matcher(fullText)
            while (numberMatcher.find()) {
                addStyle(
                    SpanStyle(color = numberColor),
                    numberMatcher.start(),
                    numberMatcher.end()
                )
            }

            // 3. Annotations
            val annotationMatcher = ANNOTATION_REGEX.matcher(fullText)
            while (annotationMatcher.find()) {
                addStyle(
                    SpanStyle(color = annotationColor, fontWeight = FontWeight.Medium),
                    annotationMatcher.start(),
                    annotationMatcher.end()
                )
            }

            // 4. Strings (overrides previous styles)
            val stringMatcher = STRING_REGEX.matcher(fullText)
            while (stringMatcher.find()) {
                addStyle(
                    SpanStyle(color = stringColor),
                    stringMatcher.start(),
                    stringMatcher.end()
                )
            }

            // 5. Comments (highest precedence)
            val commentMatcher = COMMENT_REGEX.matcher(fullText)
            while (commentMatcher.find()) {
                addStyle(
                    SpanStyle(color = commentColor, fontStyle = FontStyle.Italic),
                    commentMatcher.start(),
                    commentMatcher.end()
                )
            }
        }

        return stringBuilder
    }
}
