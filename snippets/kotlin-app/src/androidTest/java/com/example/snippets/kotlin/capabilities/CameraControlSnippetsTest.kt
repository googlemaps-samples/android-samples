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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Falsifiable Kotlin capability tests for Camera Controls (`2a3e0c25`, `0e6b228f`).
 * Proves that breaking or removing zoom constraints or panning restrictions strictly causes these tests to fail.
 */
@RunWith(AndroidJUnit4::class)
class CameraControlSnippetsTest {

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

            verification(googleMap!!)
        }
    }

    @Test
    fun verifyCameraMovementsAndZoomConstraints_falsifiable() {
        // Capability 2a3e0c25: Zoom Level Constraints
        runWithMap("Camera", "1. Zoom Level Constraints") { map ->
            var clampedMinZoom = 0.0f
            var clampedMaxZoom = 0.0f

            // Run camera mutations and reads strictly on UI main thread using runOnMainSync
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                // Attempt to zoom out below minZoom (6.0f)
                map.moveCamera(CameraUpdateFactory.zoomTo(2.0f))
                clampedMinZoom = map.cameraPosition.zoom

                // Attempt to zoom in above maxZoom (14.0f)
                map.moveCamera(CameraUpdateFactory.zoomTo(21.0f))
                clampedMaxZoom = map.cameraPosition.zoom
            }

            assertTrue(
                "Zoom level ($clampedMinZoom) must strictly clamp to >= 6.0f when minZoomPreference is enabled! " +
                "If setMinZoomPreference(6.0f) is removed or broken in the snippet, this test fails.",
                clampedMinZoom >= 5.9f
            )
            assertTrue(
                "Zoom level ($clampedMaxZoom) must strictly clamp to <= 14.0f when maxZoomPreference is enabled! " +
                "If setMaxZoomPreference(14.0f) is removed or broken in the snippet, this test fails.",
                clampedMaxZoom <= 14.1f
            )
        }
    }

    @Test
    fun verifyCameraClampingToAustralia_falsifiable() {
        // Capability 0e6b228f: Panning Restrictions (Constrain target to geographic bounds)
        runWithMap("Camera", "4. Panning Restrictions") { map ->
            var currentTarget: LatLng? = null

            // Run camera mutations strictly on UI main thread using runOnMainSync
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                // Attempt to pan camera to London, UK (51.5074, -0.1278)
                val london = LatLng(51.5074, -0.1278)
                map.moveCamera(CameraUpdateFactory.newLatLng(london))
                currentTarget = map.cameraPosition.target
            }

            assertNotNull("Camera target must not be null", currentTarget)
            // Adelaide bounds are LatLng(-35.0, 138.58) to LatLng(-34.9, 138.61).
            // When clamped, target latitude MUST NOT be near London (51.5), but clamped inside/near Adelaide (-35 to -34.9).
            assertTrue(
                "Camera target latitude (${currentTarget!!.latitude}) must be clamped near Adelaide bounds (-35.0 to -34.9) and NOT allowed to move to London (51.5)! " +
                "If setLatLngBoundsForCameraTarget() is removed from the snippet, this test fails.",
                currentTarget!!.latitude < -30.0
            )
            assertTrue(
                "Camera target longitude (${currentTarget!!.longitude}) must be clamped near Adelaide bounds (138.58 to 138.61) and NOT London (-0.12)! " +
                "If setLatLngBoundsForCameraTarget() is removed from the snippet, this test fails.",
                currentTarget!!.longitude > 130.0
            )
        }
    }
}
