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

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SampleCatalogRegistryTest {

    @Test
    fun registry_containsNonEmptySampleList() {
        val samples = SampleCatalogRegistry.SAMPLES
        assertThat(samples).isNotEmpty()
        assertThat(samples.size).isAtLeast(30)
    }

    @Test
    fun allSamples_haveValidMetadata() {
        for (sample in SampleCatalogRegistry.SAMPLES) {
            assertThat(sample.id).isNotEmpty()
            assertThat(sample.title).isNotEmpty()
            assertThat(sample.description).isNotEmpty()
            assertThat(sample.category).isNotEmpty()
            assertThat(sample.tags).isNotEmpty()
            assertThat(sample.complexity).isNotNull()
            // Every sample must support at least one framework
            val hasImplementation = sample.kotlinActivity != null || sample.javaActivity != null
            assertThat(hasImplementation).isTrue()
        }
    }

    @Test
    fun getAllTags_returnsSortedDistinctTags() {
        val tags = SampleCatalogRegistry.getAllTags()
        assertThat(tags).isNotEmpty()
        assertThat(tags).contains("#map")
        assertThat(tags).isInOrder()
        assertThat(tags.toSet().size).isEqualTo(tags.size)
    }

    @Test
    fun getCategories_returnsSortedDistinctCategories() {
        val categories = SampleCatalogRegistry.getCategories()
        assertThat(categories).isNotEmpty()
        assertThat(categories).contains("Map Initialization")
        assertThat(categories).isInOrder()
        assertThat(categories.toSet().size).isEqualTo(categories.size)
    }

    @Test
    fun filter_byFramework() {
        val kotlinSamples = SampleCatalogRegistry.filter(framework = Framework.KOTLIN_VIEWS)
        assertThat(kotlinSamples).isNotEmpty()
        for (sample in kotlinSamples) {
            assertThat(sample.getActivityForFramework(Framework.KOTLIN_VIEWS)).isNotNull()
        }

        val javaSamples = SampleCatalogRegistry.filter(framework = Framework.JAVA_VIEWS)
        assertThat(javaSamples).isNotEmpty()
        for (sample in javaSamples) {
            assertThat(sample.getActivityForFramework(Framework.JAVA_VIEWS)).isNotNull()
        }
    }

    @Test
    fun filter_byComplexity() {
        val snippets = SampleCatalogRegistry.filter(complexity = Complexity.SNIPPET)
        assertThat(snippets).isNotEmpty()
        for (sample in snippets) {
            assertThat(sample.complexity).isEqualTo(Complexity.SNIPPET)
        }

        val advanced = SampleCatalogRegistry.filter(complexity = Complexity.ADVANCED)
        assertThat(advanced).isNotEmpty()
        for (sample in advanced) {
            assertThat(sample.complexity).isEqualTo(Complexity.ADVANCED)
        }
    }

    @Test
    fun filter_byTags() {
        val tagged = SampleCatalogRegistry.filter(selectedTags = setOf("#init"))
        assertThat(tagged).isNotEmpty()
        for (sample in tagged) {
            assertThat(sample.tags).contains("#init")
        }
    }

    @Test
    fun filter_bySearchQuery() {
        val byTitle = SampleCatalogRegistry.filter(searchQuery = "Basic Map")
        assertThat(byTitle).isNotEmpty()
        assertThat(byTitle.first().title).contains("Basic Map")

        val byCategory = SampleCatalogRegistry.filter(searchQuery = "Camera Controls")
        assertThat(byCategory).isNotEmpty()
        for (sample in byCategory) {
            val matches = sample.category.contains("Camera Controls", ignoreCase = true) ||
                sample.title.contains("Camera Controls", ignoreCase = true) ||
                sample.description.contains("Camera Controls", ignoreCase = true)
            assertThat(matches).isTrue()
        }
    }

    @Test
    fun findById_matchesCorrectSample() {
        val basic = SampleCatalogRegistry.findById("com.example.kotlindemos.BasicMapDemoActivity")
        assertThat(basic).isNotNull()
        assertThat(basic?.title).isEqualTo("Basic Map")

        val byJava = SampleCatalogRegistry.findById("com.example.mapdemo.BasicMapDemoActivity")
        assertThat(byJava).isNotNull()
        assertThat(byJava?.title).isEqualTo("Basic Map")

        val missing = SampleCatalogRegistry.findById("non.existent.Activity")
        assertThat(missing).isNull()

        val nullLookup = SampleCatalogRegistry.findById(null)
        assertThat(nullLookup).isNull()
    }

    @Test
    fun sampleItem_targetFqcn_resolvesPerFramework() {
        val sample = SampleItem(
            id = "com.example.kotlindemos.TestActivity",
            title = "Test",
            description = "Test desc",
            category = "Testing",
            complexity = Complexity.SIMPLE,
            tags = listOf("#test"),
            apiCalls = emptyList(),
            purpose = "Testing",
            successCriteria = "Pass",
            failureIndicators = "Fail",
            kotlinActivity = "com.example.kotlindemos.TestKotlinActivity",
            javaActivity = "com.example.mapdemo.TestJavaActivity"
        )

        assertThat(sample.getTargetFqcn(Framework.KOTLIN_VIEWS)).isEqualTo("com.example.kotlindemos.TestKotlinActivity")
        assertThat(sample.getTargetFqcn(Framework.JAVA_VIEWS)).isEqualTo("com.example.mapdemo.TestJavaActivity")
    }

    @Test
    fun reviewStatus_fromString_parsesCorrectly() {
        assertThat(ReviewStatus.fromString("PASSING")).isEqualTo(ReviewStatus.PASSING)
        assertThat(ReviewStatus.fromString("passing")).isEqualTo(ReviewStatus.PASSING)
        assertThat(ReviewStatus.fromString("NEEDS_WORK")).isEqualTo(ReviewStatus.NEEDS_WORK)
        assertThat(ReviewStatus.fromString("needs_work")).isEqualTo(ReviewStatus.NEEDS_WORK)
        assertThat(ReviewStatus.fromString("UNCHECKED")).isEqualTo(ReviewStatus.UNCHECKED)
        assertThat(ReviewStatus.fromString("unchecked")).isEqualTo(ReviewStatus.UNCHECKED)
        assertThat(ReviewStatus.fromString(null)).isEqualTo(ReviewStatus.UNCHECKED)
        assertThat(ReviewStatus.fromString("UNKNOWN_VALUE")).isEqualTo(ReviewStatus.UNCHECKED)
    }
}
