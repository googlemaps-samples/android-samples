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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Falsifiable Kotlin capability tests for Map Events & Interactions (`b34458f3`).
 * Proves that breaking or removing click disabling or POI event registration strictly causes these tests to fail.
 */
@RunWith(AndroidJUnit4::class)
class EventsSnippetsTest {

    private fun runWithMap(groupTitle: String, snippetTitle: String, verification: (GoogleMap, ActivityScenario<MapActivity>) -> Unit) {
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

            verification(googleMap!!, scenario)
        }
    }

    @Test
    fun verifyMapClickListenerAndEvents_falsifiable() {
        // Capability b34458f3 Check 1: MapView Disable Click Event (maps_android_events_disable_clicks_mapview)
        runWithMap("Events", "1. MapView Disable Click Event") { map, scenario ->
            var isClickable = true
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                scenario.onActivity { activity ->
                    val holder = activity.findViewById<FrameLayout>(R.id.map_view_holder)
                    val mapView = activity.mapView ?: (holder?.getChildAt(0) as? MapView)
                    isClickable = mapView?.isClickable ?: true
                }
            }
            assertFalse(
                "MapView.isClickable must be strictly false when '1. MapView Disable Click Event' is executed! " +
                "If mapView?.isClickable = false is removed or broken in EventsSnippets.kt, this test fails.",
                isClickable
            )
        }

        // Capability b34458f3 Check 2: POI Click Listener (maps_android_on_poi_click_demo) & Event Simulation (Pillar 3)
        runWithMap("Events", "4. POI Click Listener") { map, scenario ->
            var registeredListener: GoogleMap.OnPoiClickListener? = null
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                registeredListener = TrackedMap.lastInstance?.registeredPoiClickListener
                // Simulate a POI click directly on the registered listener to verify callback handling (Pillar 3)
                registeredListener?.onPoiClick(PointOfInterest(LatLng(-33.88, 151.21), "ChIJN1t_tDeuEmsRUsoyG83frY4", "Sydney Opera House"))
            }

            assertNotNull(
                "OnPoiClickListener must be registered in TrackedMap when '4. POI Click Listener' is run! " +
                "If map.setOnPoiClickListener is removed or broken in EventsSnippets.kt, this test fails.",
                registeredListener
            )
        }
    }
}
