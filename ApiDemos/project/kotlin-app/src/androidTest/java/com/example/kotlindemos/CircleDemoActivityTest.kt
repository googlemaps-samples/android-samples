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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.common_ui.R
import com.example.kotlindemos.truth.LatLngSubject.Companion.assertThat
import com.example.kotlindemos.utils.MapDemoActivityTest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CircleDemoActivityTest :
    MapDemoActivityTest<CircleDemoActivity>(CircleDemoActivity::class.java) {

    @Test
    fun testMapIsDisplayed() {
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun testInitialCircleProperties() {
        scenario.onActivity { activity ->
            assertThat(activity.circles).hasSize(1)
            val circle = activity.circles[0].circle
            assertThat(circle.center).isNear(LatLng(-33.87365, 151.20689))
            assertThat(circle.radius).isWithin(1e-6).of(1000000.0)
            // Initial colors are set based on the initial state of the seekbars
            // Let's check if they are not transparent
            assertThat(circle.fillColor).isNotEqualTo(0)
            assertThat(circle.strokeColor).isNotEqualTo(0)
        }
    }

    @Test
    fun testFillColorChanges() {
        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            val initialColor = circle.fillColor

            activity.binding.fillHueSeekBar.progress = 120

            assertThat(circle.fillColor).isNotEqualTo(initialColor)
        }
    }

    @Test
    fun testStrokeColorChanges() {
        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            val initialColor = circle.strokeColor

            activity.binding.strokeHueSeekBar.progress = 120

            assertThat(circle.strokeColor).isNotEqualTo(initialColor)
        }
    }

    @Test
    fun testStrokeWidthChanges() {
        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            val initialWidth = circle.strokeWidth

            activity.binding.strokeWidthSeekBar.progress = 20

            assertThat(circle.strokeWidth).isNotEqualTo(initialWidth)
        }
    }

    @Test
    fun testToggleClickability() {
        var initialClickable = false
        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            initialClickable = circle.isClickable
        }

        onView(withId(R.id.toggleClickability)).perform(click())

        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            assertThat(circle.isClickable).isEqualTo(!initialClickable)
        }
    }

    @Test
    fun testLongClickAddsNewCircle() {
        lateinit var camera: CameraPosition

        // 1. Initial state check and camera setup
        scenario.onActivity { activity ->
            // Assert that there is initially only one circle on the map.
            assertThat(activity.circles).hasSize(1)

            // Store the initial camera position so we can restore it later.
            camera = map.cameraPosition

            // Move the camera to center on the target coordinates (HERE_BE_DRAGONS).
            // The clickOnMapAt() function translates a LatLng coordinate to screen pixels.
            // This translation loses precision, especially if the map is zoomed out.
            // By zoom in on the target, we ensure the click is more accurate.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(HERE_BE_DRAGONS, 12.0f))
        }

        // Wait for the map to finish moving and become idle.
        idlingResource.waitForIdle()

        // 2. Perform the action
        // Simulate a long click on the map at the specified coordinates.
        clickOnMapAt(HERE_BE_DRAGONS, tapType = Tap.LONG)

        // It can take a moment for the new circle to be added to the map. A brief pause
        // helps prevent the test from failing intermittently.
        Thread.sleep(200)

        // 3. Restore camera and verify results
        scenario.onActivity {
            // Restore the camera to its original position.
            map.moveCamera(CameraUpdateFactory.newCameraPosition(camera))
        }

        idlingResource.waitForIdle()

        scenario.onActivity { activity ->
            // Assert that a new circle was added.
            assertThat(activity.circles).hasSize(2)
            val newCircle = activity.circles[1].circle

            // Verify the new circle is located at the target coordinates, allowing for a
            // small margin of error (10 meters) due to the LatLng-to-pixel conversion.
            assertThat(newCircle.center).isWithin(10).of(HERE_BE_DRAGONS)

            // We cannot test the exact size since it is dependent on the screen resolution
            assertThat(newCircle.radius).isAtLeast(1000.0)
        }
    }

    companion object {
        val HERE_BE_DRAGONS = LatLng(-34.91459615762562, 138.60572619793246)
    }
}
