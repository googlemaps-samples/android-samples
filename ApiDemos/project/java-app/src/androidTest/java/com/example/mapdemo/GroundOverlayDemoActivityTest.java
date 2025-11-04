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
package com.example.mapdemo;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.mapdemo.utils.MapDemoActivityTest;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlay;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.android.gms.maps.model.LatLng;

import com.example.common_ui.R;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class GroundOverlayDemoActivityTest extends MapDemoActivityTest<GroundOverlayDemoActivity> {

    private GroundOverlay groundOverlay;

    // A coordinate known to be on the rotated overlay
    private final LatLng OVERLAY_COORDINATE = new LatLng(40.714904490859396, -74.23857238143682);

    public GroundOverlayDemoActivityTest() {
        super(GroundOverlayDemoActivity.class);
    }

    @Before
    public void setUpChild() {
        scenario.onActivity(activity -> {
            groundOverlay = activity.groundOverlayRotated;
        });
    }

    @Test
    public void testTransparencySeekBar() {
        final float[] initialTransparency = new float[1];
        scenario.onActivity(activity -> {
            initialTransparency[0] = activity.groundOverlay.getTransparency();
        });

        Espresso.onView(ViewMatchers.withId(R.id.transparencySeekBar)).perform(ViewActions.swipeRight());

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlay.getTransparency()).isNotEqualTo(initialTransparency[0]);
            assertThat(activity.groundOverlayRotated.getTransparency()).isEqualTo(initialTransparency[0]);
        });
    }

    @Test
    public void testSwitchImageButton() {
        final BitmapDescriptor[] initialBitmapDescriptor = new BitmapDescriptor[1];
        scenario.onActivity(activity -> {
            initialBitmapDescriptor[0] = (BitmapDescriptor) activity.groundOverlay.getTag();
        });

        Espresso.onView(ViewMatchers.withId(R.id.switchImage)).perform(ViewActions.click());

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlay.getTag()).isNotEqualTo(initialBitmapDescriptor[0]);
        });
    }

    @Test
    public void testToggleClickability_DisablesClicks() {
        // 1. VERIFY CLICKABLE (WHEN ENABLED)
        scenario.onActivity(activity -> {
            // Ensure it's clickable for the first part of the test
            activity.groundOverlayRotated.setClickable(true);
        });

        clickOnMapAt(OVERLAY_COORDINATE);

        idlingResource.waitForIdle(200); // Wait for listener to fire

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlayRotatedClickCount).isEqualTo(1);
        });

        // 2. DISABLE CLICKABILITY
        Espresso.onView(ViewMatchers.withId(R.id.toggleClickability))
                .perform(ViewActions.click());

        // 3. VERIFY NOT CLICKABLE (WHEN DISABLED)

        // Click the *exact same spot* again
        clickOnMapAt(OVERLAY_COORDINATE);

        idlingResource.waitForIdle(); // Give it time (listener shouldn't fire)

        // Assert the count did not change
        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlayRotatedClickCount).isEqualTo(1);
        });

        // And back to clickability
        Espresso.onView(ViewMatchers.withId(R.id.toggleClickability))
                .perform(ViewActions.click());

        // Click the *exact same spot* again
        clickOnMapAt(OVERLAY_COORDINATE);

        idlingResource.waitForIdle(200); // Give it time (listener shouldn't fire)

        // Assert the count did not change
        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlayRotatedClickCount).isEqualTo(2);
        });
    }

    @Test
    public void testMainOverlay_IsNotClickable() {
        final float[] initialTransparency = new float[1];
        scenario.onActivity(activity -> {
            initialTransparency[0] = activity.groundOverlay.getTransparency();
        });

        Espresso.onView(ViewMatchers.withId(R.id.map)).perform(ViewActions.click());
        idlingResource.waitForIdle();

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlay.getTransparency()).isEqualTo(initialTransparency[0]);
        });
    }
}