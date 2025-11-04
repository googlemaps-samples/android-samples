// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.kotlindemos

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.example.kotlindemos.truth.LatLngBoundsSubject
import com.example.kotlindemos.truth.LatLngSubject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CameraClampingDemoActivityTest {

    private lateinit var idlingResource: MapIdlingResource
    private lateinit var scenario: ActivityScenario<CameraClampingDemoActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(CameraClampingDemoActivity::class.java)
        scenario.onActivity { activity ->
            idlingResource = MapIdlingResource(activity.map)
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @Test
    fun testClampToAdelaide() {
        // 1. Clamp to Adelaide
        onView(withText("Clamp to Adelaide")).perform(click())

        // 2. Try to move the camera outside the bounds
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(0.0, 0.0)))
        }

        // 3. Assert that the camera is still inside the Adelaide bounds
        scenario.onActivity { activity ->
            val adelaideBounds = LatLngBounds(
                LatLng(-35.0, 138.58), LatLng(-34.9, 138.61)
            )
            val cameraPosition: CameraPosition = activity.map.cameraPosition
            LatLngBoundsSubject.assertThat(adelaideBounds).containsWithTolerance(cameraPosition.target)
        }
    }

    @Test
    fun testClampToPacific() {
        // 1. Clamp to Pacific
        onView(withText("Clamp to Pacific")).perform(click())

        // 2. Try to move the camera outside the bounds
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(45.0, 0.0)))
        }

        // 3. Assert that the camera is still inside the Pacific bounds
        scenario.onActivity { activity ->
            val pacificBounds = LatLngBounds(
                LatLng(-45.0, 160.0), LatLng(45.0, -160.0)
            )
            val cameraPosition: CameraPosition = activity.map.cameraPosition
            LatLngBoundsSubject.assertThat(pacificBounds).containsWithTolerance(cameraPosition.target)
        }
    }

    @Test
    fun testLatLngClampReset() {
        // 1. Clamp to Adelaide first
        onView(withText("Clamp to Adelaide")).perform(click())

        // 2. Reset the clamp
        onView(withText("Reset LatLng Bounds")).perform(click())

        // 3. Verify the clamp is gone by moving the camera far away
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(0.0, 0.0)))
        }

        // 4. Assert the camera is now at the new position (0,0)
        scenario.onActivity { activity ->
            val cameraPosition = activity.map.cameraPosition
            LatLngSubject.assertThat(cameraPosition.target).isNear(LatLng(0.0, 0.0))
        }
    }

    @Test
    fun testSetMinZoomClamp() {
        onView(withText("MinZoom++")).perform(click())
        scenario.onActivity { activity ->
            // Try to zoom out
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(1.0f))
            assertEquals(4.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }
    }

    @Test
    fun testSetMaxZoomClamp() {
        onView(withText("MaxZoom--")).perform(click())
        scenario.onActivity { activity ->
            // Try to zoom in
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(22.0f))
            assertEquals(20.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }
    }

    @Test
    fun testMinMaxZoomClampReset() {
        // Set a high min zoom and verify it's active.
        onView(withText("MinZoom++")).perform(click()) // min zoom -> 4.0
        onView(withText("MinZoom++")).perform(click()) // min zoom -> 6.0
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(5.0f))
        }
        scenario.onActivity { activity ->
            assertEquals("Min zoom clamp should be 6.0", 6.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }

        // Set a low max zoom and verify it's active.
        onView(withText("MaxZoom--")).perform(click()) // max zoom -> 20.0
        onView(withText("MaxZoom--")).perform(click()) // max zoom -> 18.0
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(19.0f))
        }
        scenario.onActivity { activity ->
            assertEquals("Max zoom clamp should be 18.0", 18.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }

        // Reset the zoom bounds.
        onView(withText("Reset Zoom Bounds")).perform(click())

        // Verify the min clamp is gone.
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(5.0f))
        }
        scenario.onActivity { activity ->
            assertEquals("Min zoom clamp should be gone", 5.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }

        // Verify the max clamp is gone.
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(19.0f))
        }
        scenario.onActivity { activity ->
            assertEquals("Max zoom clamp should be gone", 19.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        scenario.close()
    }
}