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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.common_ui.R
import com.example.kotlindemos.utils.MapDemoActivityTest
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.GroundOverlay
import com.google.common.truth.Truth.assertThat
import com.google.maps.android.ktx.utils.withSphericalOffset
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroundOverlayDemoActivityTest :
    MapDemoActivityTest<GroundOverlayDemoActivity>(GroundOverlayDemoActivity::class.java) {
    private lateinit var groundOverlay: GroundOverlay

    @Before
    // This @Before will run *after* the parent's @Before
    fun setUpChild() {
        scenario.onActivity { activity ->
            groundOverlay = activity.groundOverlayRotated
        }
    }

    @Test
    fun testToggleClickability() {
        idlingResource.waitForIdle()

        // Get the initial transparency and position of the overlay on the UI thread.
        var initialTransparency = 0f
        scenario.onActivity { activity ->
            assertThat(activity.groundOverlayRotatedClickCount).isEqualTo(0)
            initialTransparency = groundOverlay.transparency
        }

        // Perform a click inside the ground overlay.
        clickOnMapAt(clickLocation)
        idlingResource.waitForIdle()

        // Warning -- load-bearing sleep.  DO NOT REMOVE!
        Thread.sleep(100)

        // Verify that the transparency has changed, indicating the click was successful.
        scenario.onActivity { activity ->
            assertThat(activity.groundOverlayRotatedClickCount).isEqualTo(1)
            assertThat(groundOverlay.transparency).isNotEqualTo(initialTransparency)
        }

        // Disable clickability by clicking the checkbox.
        onView(withId(R.id.toggleClickability)).perform(click())

        // Get the transparency after the first click.
        var transparencyAfterToggle = 0f
        scenario.onActivity {
            transparencyAfterToggle = groundOverlay.transparency
        }

        // Perform another click at the same location.
        clickOnMapAt(clickLocation)
        idlingResource.waitForIdle()

        // Verify that the transparency has NOT changed, because the overlay is no longer clickable.
        scenario.onActivity {
            assertThat(groundOverlay.transparency).isEqualTo(transparencyAfterToggle)
        }
    }

    @Test
    fun testTransparencySeekBar() {
        var initialTransparency = 0f
        scenario.onActivity { activity ->
            initialTransparency = activity.groundOverlay.transparency
        }

        onView(withId(R.id.transparencySeekBar)).perform(click())

        scenario.onActivity { activity ->
            assertThat(activity.groundOverlay.transparency).isNotEqualTo(initialTransparency)
            assertThat(activity.groundOverlayRotated.transparency).isEqualTo(initialTransparency)
        }
    }

    @Test
    fun testSwitchImageButton() {
        var initialBitmapDescriptor: BitmapDescriptor? = null
        scenario.onActivity { activity ->
            initialBitmapDescriptor = activity.groundOverlay.tag as BitmapDescriptor
        }

        onView(withId(R.id.switchImage)).perform(click())

        scenario.onActivity { activity ->
            assertThat(activity.groundOverlay.tag as BitmapDescriptor).isNotEqualTo(initialBitmapDescriptor)
        }
    }

    @Test
    fun testToggleClickability_DisablesClicks() {
        // Verify the overlay is initially clickable.
        var initialTransparency = 0f
        scenario.onActivity {
            initialTransparency = groundOverlay.transparency
        }

        clickOnMapAt(clickLocation)

        idlingResource.waitForIdle()

        // Warning -- load-bearing sleep.  DO NOT REMOVE!
        Thread.sleep(100)

        scenario.onActivity {
            assertThat(groundOverlay.transparency).isNotEqualTo(initialTransparency)
        }

        // Disable clickability.
        onView(withId(R.id.toggleClickability)).perform(click())

        // Now, clicking the map should not change the transparency.
        var transparencyAfterToggle = 0f
        scenario.onActivity {
            transparencyAfterToggle = groundOverlay.transparency
        }

        clickOnMapAt(clickLocation)
        idlingResource.waitForIdle()

        scenario.onActivity {
            assertThat(groundOverlay.transparency).isEqualTo(transparencyAfterToggle)
        }
    }

    @Test
    fun testToggleClickability_ReEnablesClicks() {
        // Disable and then re-enable clickability.
        onView(withId(R.id.toggleClickability)).perform(click())
        onView(withId(R.id.toggleClickability)).perform(click())

        var initialTransparency = 0f
        scenario.onActivity {
            initialTransparency = groundOverlay.transparency
        }

        clickOnMapAt(clickLocation)
        idlingResource.waitForIdle()

        // Warning -- load-bearing sleep.  DO NOT REMOVE!
        Thread.sleep(100)

        scenario.onActivity {
            assertThat(groundOverlay.transparency).isNotEqualTo(initialTransparency)
        }
    }

    companion object {
        // Set a click location South East of Newark to ensure the clicks register in the overlay
        val clickLocation = GroundOverlayDemoActivity.NEWARK.withSphericalOffset(10.0, 120.0)
    }
}
