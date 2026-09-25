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

import java.util.Properties

/**
 * This script enforces that required API keys are present in a `secrets.properties` file
 * before allowing build or install tasks to proceed. It ignores syncs and test tasks.
 *
 * Usage in build.gradle.kts:
 *
 * extra["requiredSecrets"] = listOf("MAPS3D_API_KEY", "PLACES_API_KEY")
 * apply(from = "gradle/secrets-enforcement.gradle.kts")
 */

// Use the provided isCI flag if available, otherwise detect it from environment variables
val isCI = if (project.extra.has("isCI")) {
    project.extra["isCI"] as? Boolean ?: false
} else {
    System.getenv("CI")?.toBoolean() ?: false
}

if (!isCI) {
    val secretsFile = rootProject.file("secrets.properties")
    val requestedTasks = gradle.startParameter.taskNames
    @Suppress("UNCHECKED_CAST")
    val requiredKeys = (project.extra["requiredSecrets"] as? List<String>) ?: emptyList()

    if (requestedTasks.isEmpty() && !secretsFile.exists()) {
        // It's likely an IDE sync if no tasks are specified, so just issue a warning.
        println("Warning: secrets.properties not found. Gradle sync may succeed, but building/running the app will fail.")
    } else if (requestedTasks.isNotEmpty()) {
        val buildTaskKeywords = setOf("build", "install", "assemble")
        val testTaskKeywords = setOf("test", "report", "lint")

        val isBuildTask = requestedTasks.any { name ->
            buildTaskKeywords.any { kw -> name.contains(kw, ignoreCase = true) }
        }
        val isTestTask = requestedTasks.any { name ->
            testTaskKeywords.any { kw -> name.contains(kw, ignoreCase = true) }
        }
        val isDebugTask = requestedTasks.any { task ->
            task.contains("Debug", ignoreCase = true) || task.contains("installAndLaunch", ignoreCase = true)
        }

        if (isBuildTask && !isTestTask && isDebugTask) {
            val defaultsFile = rootProject.file("local.defaults.properties")
            val requiredKeysMessage = if (defaultsFile.exists()) {
                defaultsFile.readText()
            } else {
                requiredKeys.joinToString("\n") { "$it=<YOUR_API_KEY>" }
            }

            if (!secretsFile.exists()) {
                throw GradleException("secrets.properties file not found. Please create a 'secrets.properties' file in the root project directory with the following content:\n\n$requiredKeysMessage")
            }

            val secrets = Properties()
            secretsFile.inputStream().use { secrets.load(it) }

            requiredKeys.forEach { key ->
                val value = secrets.getProperty(key)
                if (value.isNullOrBlank() || !value.matches(Regex("^AIza[a-zA-Z0-9_-]{35}$"))) {
                    throw GradleException("Invalid or missing $key in secrets.properties. Please provide a valid Google API key (starts with 'AIza').")
                }
            }
        }
    }
}
