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

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.power.assert) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.secrets.gradle.plugin) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint().editorConfigOverride(
            mapOf(
                "indent_size" to "4",
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                "max_line_length" to "120",
                // Naming rules are disabled because fixing them requires renaming packages,
                // files or properties that developers copy from these samples.
                "ktlint_standard_package-name" to "disabled",
                "ktlint_standard_property-naming" to "disabled",
                "ktlint_standard_backing-property-naming" to "disabled",
                "ktlint_standard_filename" to "disabled",
                // annotation-spacing would move region tags placed between an annotation and its
                // declaration to above the annotation, changing the published snippet.
                "ktlint_standard_annotation-spacing" to "disabled",
            ),
        )
        trimTrailingWhitespace()
        endWithNewline()
    }
    java {
        target("**/*.java")
        targetExclude("**/build/**")
        googleJavaFormat().aosp()
    }
}
