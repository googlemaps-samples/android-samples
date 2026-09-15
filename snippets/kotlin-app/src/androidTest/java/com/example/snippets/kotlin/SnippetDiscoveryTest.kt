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

package com.example.snippets.kotlin

import android.content.Intent
import android.widget.FrameLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.snippets.common.R
import com.google.android.gms.maps.MapView
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class SnippetDiscoveryTest {

    @Test
    fun verifySnippetGroupsLoaded() {
        val groups = SnippetRegistry.getSnippetGroups()
        assertTrue("Snippet groups should not be empty", groups.isNotEmpty())
        for (group in groups) {
            assertTrue("Group title should not be blank", group.title.isNotBlank())
            for (item in group.items) {
                assertTrue("Item title should not be blank", item.title.isNotBlank())
                assertTrue("Item group title should match", item.groupTitle == group.title)
            }
        }
    }

    @Test
    fun verifyStreetViewSnippetRegistered() {
        val groups = SnippetRegistry.getSnippetGroups()
        val streetViewGroup = groups.find { it.title.equals("Street View", ignoreCase = true) }
        assertNotNull("Registry should contain 'Street View' snippet group", streetViewGroup)
        assertTrue("Street View group should contain items", streetViewGroup!!.items.isNotEmpty())
    }

    @Test
    fun verifyMapConfigurationsLoaded() {
        val snippets = SnippetRegistry.snippets
        assertTrue("Registry should contain Map Color Scheme snippet", snippets.keys.any { it.contains("Color Scheme", ignoreCase = true) })
        assertTrue("Registry should contain Traffic Layer snippet", snippets.keys.any { it.contains("Traffic", ignoreCase = true) })
    }

    @Test
    fun verifyDataDrivenBoundarySnippetsRegistered() {
        val groups = SnippetRegistry.getSnippetGroups()
        val boundaryGroup = groups.find { it.title.contains("Boundary", ignoreCase = true) }
        assertNotNull("Registry should contain Data-Driven Boundary Styling snippet group", boundaryGroup)
        assertTrue("Boundary group should contain items", boundaryGroup!!.items.isNotEmpty())
    }

    @Test
    fun verifyDatasetLayerSnippetsRegistered() {
        val groups = SnippetRegistry.getSnippetGroups()
        val datasetGroup = groups.find { it.title.contains("Dataset", ignoreCase = true) }
        assertNotNull("Registry should contain Custom Geospatial Datasets snippet group", datasetGroup)
        assertTrue("Dataset group should contain items", datasetGroup!!.items.isNotEmpty())
    }

    @Test
    fun verifyCloudCustomizationSnippetsRegistered() {
        val groups = SnippetRegistry.getSnippetGroups()
        val cloudGroup = groups.find { it.title.contains("Cloud Customization", ignoreCase = true) }
        assertNotNull("Registry should contain Cloud Customization snippet group", cloudGroup)
        assertTrue("Cloud Customization group should contain items", cloudGroup!!.items.isNotEmpty())
    }

    @Test
    fun verifyAllSnippetsLaunchWithoutCrash() {
        val snippets = SnippetRegistry.snippets
        assertTrue("Registry should contain snippets", snippets.isNotEmpty())

        for ((key, snippet) in snippets) {
            val intent = Intent(ApplicationProvider.getApplicationContext(), MapActivity::class.java).apply {
                putExtra(MapActivity.EXTRA_SNIPPET_TITLE, snippet.title)
                putExtra("group_title", snippet.groupTitle)
            }

            val latch = CountDownLatch(1)
            var mapView: MapView? = null

            ActivityScenario.launch<MapActivity>(intent).use { scenario ->
                scenario.onActivity { activity ->
                    val holder = activity.findViewById<FrameLayout>(R.id.map_view_holder)
                    mapView = activity.mapView ?: (holder?.getChildAt(0) as? MapView)
                    latch.countDown()
                }

                val loaded = latch.await(5, TimeUnit.SECONDS)
                assertTrue("Timed out waiting for activity to launch for snippet: $key", loaded)
                assertNotNull("MapView should not be null for snippet: $key", mapView)
            }
        }
    }
}
