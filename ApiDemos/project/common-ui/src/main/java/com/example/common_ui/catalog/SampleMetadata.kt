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

package com.example.common_ui.catalog

import java.io.Serializable

/**
 * Annotation for Google Maps Platform sample activities and snippet entry points.
 *
 * Provides metadata consumed by the dynamic catalog builder and the on-device reviewer mode.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Sample(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val complexity: Complexity = Complexity.SIMPLE,
    val tags: Array<String> = [],
    val purpose: String = "",
    val successCriteria: String = "",
    val failureIndicators: String = "",
    val helpHtml: String = "",
    val framework: Framework = Framework.KOTLIN_VIEWS
)

/**
 * Complexity classification for samples.
 */
enum class Complexity(val displayName: String, val badge: String, val order: Int) : Serializable {
    SNIPPET("Snippet", "🔹", 1),
    SIMPLE("Simple", "🟢", 2),
    ADVANCED("Advanced", "🔴", 3);

    companion object {
        fun fromString(value: String?): Complexity {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: SIMPLE
        }
    }
}

/**
 * Supported development frameworks.
 */
enum class Framework(
    val id: String,
    val displayName: String,
    val badge: String,
    val iconEmoji: String,
    val accentColorHex: Long
) : Serializable {
    KOTLIN_VIEWS(
        id = "kotlin",
        displayName = "Kotlin Views",
        badge = "Kotlin",
        iconEmoji = "💜",
        accentColorHex = 0xFF7F52FF
    ),
    JAVA_VIEWS(
        id = "java",
        displayName = "Java Views",
        badge = "Java",
        iconEmoji = "☕",
        accentColorHex = 0xFFE76F51
    ),
    COMPOSE(
        id = "compose",
        displayName = "Jetpack Compose",
        badge = "Compose",
        iconEmoji = "⚛️",
        accentColorHex = 0xFF4285F4
    );

    companion object {
        fun fromId(id: String?): Framework {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: KOTLIN_VIEWS
        }
    }
}

/**
 * Manual review evaluation status for a sample.
 */
enum class ReviewStatus(
    val displayName: String,
    val badge: String,
    val iconEmoji: String,
    val colorHex: Long
) : Serializable {
    UNCHECKED("Unchecked", "UNCHECKED", "⚪", 0xFF9E9E9E),
    NEEDS_WORK("Needs Work", "NEEDS_WORK", "🔴", 0xFFF44336),
    PASSING("Passing", "PASSING", "🟢", 0xFF4CAF50);

    companion object {
        fun fromString(value: String?): ReviewStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: UNCHECKED
        }
    }
}

/**
 * Immutable domain model representing a sample entry across all frameworks.
 */
data class SampleItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val complexity: Complexity = Complexity.SIMPLE,
    val tags: List<String> = emptyList(),
    val purpose: String = "",
    val successCriteria: String = "",
    val failureIndicators: String = "",
    val helpHtml: String = "",
    val kotlinActivity: String? = null,
    val javaActivity: String? = null,
    val composeActivity: String? = null
) : Serializable {

    /**
     * Builds a comprehensive HTML formatted help box for reviewer and developer guidance.
     */
    fun getFormattedHelpHtml(): String {
        if (helpHtml.isNotBlank()) {
            return helpHtml
        }
        val builder = StringBuilder()
        builder.append("<h3><b>${title}</b></h3>")
        builder.append("<p><i>${description}</i></p>")
        builder.append("<hr/>")

        if (purpose.isNotBlank()) {
            builder.append("<p><b>🎯 Purpose:</b><br/>${purpose}</p>")
        }
        if (successCriteria.isNotBlank()) {
            builder.append("<p><b>✅ Success Criteria:</b><br/>${successCriteria}</p>")
        }
        if (failureIndicators.isNotBlank()) {
            builder.append("<p><b>⚠️ Failure / Broken Indicators:</b><br/>${failureIndicators}</p>")
        }

        if (tags.isNotEmpty()) {
            builder.append("<p><b>🏷️ Tags:</b> ")
            builder.append(tags.joinToString(" "))
            builder.append("</p>")
        }
        return builder.toString()
    }

    /**
     * Resolves the activity class name for a given target framework.
     */
    fun getActivityForFramework(framework: Framework): String? {
        return when (framework) {
            Framework.KOTLIN_VIEWS -> kotlinActivity ?: javaActivity ?: composeActivity
            Framework.JAVA_VIEWS -> javaActivity ?: kotlinActivity ?: composeActivity
            Framework.COMPOSE -> composeActivity ?: kotlinActivity ?: javaActivity
        }
    }
}
