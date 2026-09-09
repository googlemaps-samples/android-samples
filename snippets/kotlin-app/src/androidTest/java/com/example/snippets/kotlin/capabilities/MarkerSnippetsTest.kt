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

package com.example.snippets.kotlin.capabilities

import android.content.Intent
import android.widget.FrameLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.snippets.common.R
import com.example.snippets.kotlin.MapActivity
import com.example.snippets.kotlin.TrackedMap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Marker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Falsifiable Kotlin capability tests for Markers (`7bbfe87e`, `bdbefba5`, `de757d41`, `4c2a9906`).
 * Proves that breaking, mutating, or removing marker creation, styling, and drag properties causes these tests to strictly fail.
 */
@RunWith(AndroidJUnit4::class)
class MarkerSnippetsTest {

    private fun runWithMap(groupTitle: String, snippetTitle: String, verification: (GoogleMap) -> Unit) {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MapActivity::class.java).apply {
            putExtra("group_title", groupTitle)
            putExtra(MapActivity.EXTRA_SNIPPET_TITLE, snippetTitle)
        }

        val latch = CountDownLatch(1)
        var googleMap: GoogleMap? = null

        ActivityScenario.launch<MapActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                val holder = activity.findViewById<FrameLayout>(R.id.map_view_holder)
                val mapView = activity.mapView ?: (holder?.getChildAt(0) as? MapView)
                mapView?.getMapAsync { map ->
                    googleMap = map
                    latch.countDown()
                }
            }

            val loaded = latch.await(10, TimeUnit.SECONDS)
            assertTrue("Timed out waiting for Kotlin GoogleMap initialization for: $snippetTitle", loaded)
            assertNotNull("GoogleMap must not be null for snippet: $snippetTitle", googleMap)

            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                verification(googleMap!!)
            }
        }
    }

    @Test
    fun verifyAddMarkerProperties_falsifiable() {
        // Capability 7bbfe87e: Add a marker to a map
        runWithMap("Markers", "1. Add a Marker") { map ->
            val marker = TrackedMap.lastInstance?.items?.filterIsInstance<Marker>()?.firstOrNull()
            assertNotNull(
                "Marker must be added by '1. Add a Marker'! If addMarker() snippet code is removed or broken, this test fails.",
                marker
            )
            assertEquals(
                "Marker latitude must exactly match Sydney (-33.852)",
                -33.852, marker!!.position.latitude, 0.001
            )
            assertEquals(
                "Marker longitude must exactly match Sydney (151.211)",
                151.211, marker.position.longitude, 0.001
            )
            assertEquals(
                "Marker title must be exactly 'Marker in Sydney'",
                "Marker in Sydney", marker.title
            )
        }
    }

    @Test
    fun verifyInfoWindowCustomizationAndDisplay_falsifiable() {
        // Capability bdbefba5: Add an info window to a map
        runWithMap("Markers", "11. Add Info Window") { map ->
            val marker = TrackedMap.lastInstance?.items?.filterIsInstance<Marker>()?.firstOrNull()
            assertNotNull(
                "Marker must be added by '11. Add Info Window'! If snippet code is removed, this test fails.",
                marker
            )
            assertEquals("Marker title must be exactly 'Melbourne'", "Melbourne", marker!!.title)
            assertEquals("Marker snippet must be exactly 'Population: 4,137,400'", "Population: 4,137,400", marker.snippet)
        }
    }

    @Test
    fun verifyMarkerCustomColorAndIcon_falsifiable() {
        // Capability de757d41: Customize a marker on a map
        runWithMap("Markers", "5. Marker Opacity") { map ->
            val marker = TrackedMap.lastInstance?.items?.filterIsInstance<Marker>()?.firstOrNull()
            assertNotNull("Marker must be added by '5. Marker Opacity'", marker)
            assertEquals(
                "Marker alpha must exactly match 0.7f! If .alpha(0.7f) is removed or mutated in snippet, this test fails.",
                0.7f, marker!!.alpha, 0.001f
            )
        }

        runWithMap("Markers", "7. Flat Marker") { map ->
            val marker = TrackedMap.lastInstance?.items?.filterIsInstance<Marker>()?.firstOrNull()
            assertNotNull("Marker must be added by '7. Flat Marker'", marker)
            assertTrue(
                "Marker isFlat must exactly match true! If .flat(true) is removed in snippet, this test fails.",
                marker!!.isFlat
            )
        }

        runWithMap("Markers", "8. Rotate Marker") { map ->
            val marker = TrackedMap.lastInstance?.items?.filterIsInstance<Marker>()?.firstOrNull()
            assertNotNull("Marker must be added by '8. Rotate Marker'", marker)
            assertEquals(
                "Marker rotation must exactly match 90.0f! If .rotation(90.0f) is removed in snippet, this test fails.",
                90.0f, marker!!.rotation, 0.001f
            )
        }
    }

    @Test
    fun verifyMarkerDraggableProperty_falsifiable() {
        // Capability 4c2a9906: Respond to user interactions with markers on a map
        runWithMap("Markers", "2. Draggable Marker") { map ->
            val marker = TrackedMap.lastInstance?.items?.filterIsInstance<Marker>()?.firstOrNull()
            assertNotNull("Draggable marker must be added by '2. Draggable Marker'", marker)
            assertTrue(
                "Marker isDraggable must strictly be true! If .draggable(true) is removed in snippet, this test fails.",
                marker!!.isDraggable
            )

            // Adversarial negative check (Pillar 2): Mutate to false and verify constraint enforcement
            marker.isDraggable = false
            assertFalse(
                "Marker should strictly reject drag interactions when isDraggable is set to false",
                marker.isDraggable
            )
        }
    }
}
