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

import java.io.StringReader
import java.util.Properties
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Enforces that required secrets are configured in the root `secrets.properties` file before
 * device installation tasks (`install*`) execute.
 *
 * ### Design Rationale
 * Every sample app module configures `secrets-gradle-plugin` with
 * `defaultPropertiesFileName = "local.defaults.properties"`, which ships placeholder values such as
 * `MAPS_API_KEY=DEFAULT_API_KEY`. That pairing intentionally allows contributors and CI workflows
 * to clone the repository and run `./gradlew build`, `./gradlew assembleDebug`, `./gradlew test`,
 * and `./gradlew lint` without needing a Google Cloud project or a real API key.
 *
 * However, installing a sample onto a device or emulator with placeholder secrets produces a blank
 * or non-functional map at runtime without an obvious build-time explanation. To fail fast at the
 * moment a developer actually deploys an app while keeping compilation, unit testing, and IDE sync
 * unblocked, this script:
 * 1. Registers a configuration-cache-compatible [VerifySecretsTask] (`:verifySecrets`) with
 *    `secrets.properties` tracked via Gradle's [org.gradle.api.provider.ProviderFactory.fileContents]
 *    API for incremental `UP-TO-DATE` checks and execution-time validation.
 * 2. Wires `:verifySecrets` as a dependency of non-test `install*` tasks across all projects rather
 *    than inspecting `gradle.startParameter.taskNames` at configuration time.
 *
 * ### Usage in root `build.gradle.kts`
 * ```kotlin
 * extra["requiredSecrets"] = listOf("MAPS_API_KEY")
 * apply(from = "gradle/secrets-enforcement.gradle.kts")
 * ```
 */
abstract class VerifySecretsTask : DefaultTask() {

    @get:Input
    abstract val ciEnvironment: Property<Boolean>

    @get:Input
    abstract val requiredSecrets: ListProperty<String>

    @get:Optional
    @get:Input
    abstract val secretsFileContent: Property<String>

    @get:OutputFile
    abstract val verifiedMarker: RegularFileProperty

    @TaskAction
    fun verify() {
        if (ciEnvironment.getOrElse(false)) {
            writeMarker()
            return
        }

        val keys = requiredSecrets.getOrElse(emptyList())
        if (keys.isEmpty()) {
            writeMarker()
            return
        }

        // Format guidance strictly from `requiredSecrets` rather than echoing
        // `local.defaults.properties`, which may contain optional keys or local test values.
        val requiredKeysMessage = keys.joinToString("\n") { key ->
            "$key=${placeholderFor(key)}"
        }

        val content = secretsFileContent.orNull
            ?: throw GradleException(
                "secrets.properties file not found. Please create a 'secrets.properties' file in " +
                    "the root project directory with the following content:\n\n$requiredKeysMessage",
            )

        val secrets = Properties().apply {
            load(StringReader(content))
        }

        val apiKeyRegex = Regex("^AIza[a-zA-Z0-9_-]{35}$")

        keys.forEach { key ->
            val value = secrets.getProperty(key)?.trim()
            val isPlaceholder = value.isNullOrEmpty() ||
                value == key ||
                value == "DEFAULT_API_KEY" ||
                value == "DEFAULT_$key" ||
                value == "YOUR_API_KEY" ||
                value.startsWith("<YOUR_")

            if (isPlaceholder) {
                throw GradleException(
                    "Missing or placeholder value for '$key' in secrets.properties. " +
                        "Please configure a real value before installing on a device:\n\n" +
                        "$key=${placeholderFor(key)}",
                )
            }

            // Only keys ending in `_API_KEY` are Google API keys; other secrets (such as `MAP_ID`
            // or `*_DATASET_ID`) only require the presence and non-placeholder check above.
            if (key.endsWith("_API_KEY") && !value.matches(apiKeyRegex)) {
                throw GradleException(
                    "Value for '$key' in secrets.properties does not look like a Google API key " +
                        "(expected to start with 'AIza' followed by 35 alphanumeric, '_', or '-' characters).",
                )
            }
        }

        writeMarker()
    }

    private fun placeholderFor(key: String): String =
        if (key.endsWith("_API_KEY")) "<YOUR_API_KEY>" else "<YOUR_VALUE>"

    private fun writeMarker() {
        val marker = verifiedMarker.get().asFile
        marker.parentFile?.mkdirs()
        marker.writeText("verified\n")
    }
}

@Suppress("UNCHECKED_CAST")
val requiredKeys =
    (rootProject.extra.properties["requiredSecrets"] as? List<String>) ?: emptyList()

val extraIsCI = (rootProject.extra.properties["isCI"] as? Boolean) ?: false
val isCIProvider = providers.environmentVariable("CI")
    .map { it.toBoolean() }
    .orElse(extraIsCI)

val verifySecrets = rootProject.tasks.register<VerifySecretsTask>("verifySecrets") {
    group = "verification"
    description =
        "Verifies that required secrets in secrets.properties are configured before device installation."
    ciEnvironment.set(isCIProvider)
    requiredSecrets.set(requiredKeys)
    secretsFileContent.set(
        rootProject.providers.fileContents(
            rootProject.layout.projectDirectory.file("secrets.properties"),
        ).asText,
    )
    verifiedMarker.set(
        rootProject.layout.buildDirectory.file("intermediates/secrets_verification/verified.txt"),
    )
}

allprojects {
    tasks.matching { task ->
        task.name.startsWith("install", ignoreCase = true) &&
            !task.name.contains("AndroidTest", ignoreCase = true) &&
            !task.name.contains("Uninstall", ignoreCase = true)
    }.configureEach {
        dependsOn(verifySecrets)
    }
}
