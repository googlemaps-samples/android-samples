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
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import android.widget.FrameLayout
import com.example.snippets.common.R
import com.example.snippets.kotlin.MapActivity
import com.example.snippets.kotlin.TrackedMap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Marker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Falsifiable capability tests for Map Initialization (`232ecd00`, `20793ebb`, `25bf9dfd`, `c511ea57`).
 * Proves that breaking or mutating the sample code strictly causes these tests to fail.
 */
@RunWith(AndroidJUnit4::class)
class MapInitSnippetsTest {

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
            assertTrue("Timed out waiting for GoogleMap initialization for: $snippetTitle", loaded)
            assertNotNull("GoogleMap must not be null for snippet: $snippetTitle", googleMap)

            // Allow map layout and camera animations to settle while UI main thread is unblocked
            Thread.sleep(1000)

            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                verification(googleMap!!)
            }
        }
    }

    @Test
    fun verifyBasicMapActivity_falsifiable() {
        // Capability 232ecd00: Add a customizable, interactive map to a web page or mobile app
        runWithMap("Map Initialization", "1. Basic Map Activity") { map ->
            val sydney = com.google.android.gms.maps.model.LatLng(-34.0, 151.0)
            val marker = TrackedMap.lastInstance?.items?.filterIsInstance<Marker>()?.firstOrNull()
            assertNotNull("Basic Map Activity snippet must add Marker in Sydney", marker)
            assertEquals("Marker title must be exactly 'Marker in Sydney'", "Marker in Sydney", marker!!.title)
            assertEquals("Marker latitude must exactly match Sydney (-34.0)", sydney.latitude, marker.position.latitude, 0.001)
            assertEquals("Marker longitude must exactly match Sydney (151.0)", sydney.longitude, marker.position.longitude, 0.001)
        }
    }

    @Test
    fun verifyEnableTrafficLayer_falsifiable() {
        // Capability 20793ebb: Add a traffic layer to a map
        runWithMap("Map Initialization", "11. Enable Traffic Layer") { map ->
            // Falsifiability Check: If snippet code is removed or changed to isTrafficEnabled = false, this test STRICTLY FAILS!
            assertTrue("googleMap.isTrafficEnabled MUST be true after snippet execution", map.isTrafficEnabled)
        }
    }

    @Test
    fun verifySetMapTypeToHybrid_falsifiable() {
        // Capability c511ea57: Change the map type
        runWithMap("Map Initialization", "3. Set Map Type") { map ->
            // Falsifiability Check: If changed to MAP_TYPE_NORMAL or commented out, this test STRICTLY FAILS!
            assertEquals("GoogleMap type must be exactly MAP_TYPE_HYBRID (4)", GoogleMap.MAP_TYPE_HYBRID, map.mapType)
        }
    }
}
