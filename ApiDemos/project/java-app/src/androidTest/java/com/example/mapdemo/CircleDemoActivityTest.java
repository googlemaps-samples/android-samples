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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.mapdemo.truth.LatLngSubject.assertThat;
import static com.google.common.truth.Truth.assertThat;
import androidx.test.espresso.action.Tap;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.common_ui.R;
import com.example.mapdemo.utils.MapDemoActivityTest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CircleDemoActivityTest extends MapDemoActivityTest<CircleDemoActivity> {

    private static final LatLng HERE_BE_DRAGONS = new LatLng(-34.91459615762562, 138.60572619793246);

    public CircleDemoActivityTest() {
        super(CircleDemoActivity.class);
    }

    @Test
    public void testMapIsDisplayed() {
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testInitialCircleIsDrawn() {
        scenario.onActivity(activity -> {
            assertThat(activity.circles).hasSize(1);
        });
    }

    @Test
    public void testFillColorChanges() {
        scenario.onActivity(activity -> {
            Circle circle = activity.circles.get(0).circle;
            int initialColor = circle.getFillColor();

            activity.binding.fillHueSeekBar.setProgress(120);

            assertThat(circle.getFillColor()).isNotEqualTo(initialColor);
        });
    }

    @Test
    public void testStrokeColorChanges() {
        scenario.onActivity(activity -> {
            Circle circle = activity.circles.get(0).circle;
            int initialColor = circle.getStrokeColor();

            activity.binding.strokeHueSeekBar.setProgress(120);

            assertThat(circle.getStrokeColor()).isNotEqualTo(initialColor);
        });
    }

    @Test
    public void testStrokeWidthChanges() {
        scenario.onActivity(activity -> {
            Circle circle = activity.circles.get(0).circle;
            float initialWidth = circle.getStrokeWidth();

            activity.binding.strokeWidthSeekBar.setProgress(20);

            assertThat(circle.getStrokeWidth()).isNotEqualTo(initialWidth);
        });
    }

    @Test
    public void testToggleClickability() {
        final boolean[] initialClickable = new boolean[1];
        scenario.onActivity(activity -> {
            Circle circle = activity.circles.get(0).circle;
            initialClickable[0] = circle.isClickable();
        });

        onView(withId(R.id.toggleClickability)).perform(click());

        scenario.onActivity(activity -> {
            Circle circle = activity.circles.get(0).circle;
            assertThat(circle.isClickable()).isEqualTo(!initialClickable[0]);
        });
    }

    @Test
    public void testLongClickAddsNewCircle() {
        final CameraPosition[] camera = new CameraPosition[1];

        // 1. Initial state check and camera setup
        scenario.onActivity(activity -> {
            // Assert that there is initially only one circle on the map.
            assertThat(activity.circles).hasSize(1);

            // Store the initial camera position so we can restore it later.
            camera[0] = map.getCameraPosition();

            // Move the camera to center on the target coordinates (HERE_BE_DRAGONS).
            // The clickOnMapAt() function translates a LatLng coordinate to screen pixels.
            // This translation loses precision, especially if the coordinate is far from the
            // screen's center. By centering the camera on the target, we ensure the click
            // is as accurate as possible.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(HERE_BE_DRAGONS, 12.0f));
        });

        // Wait for the map to finish moving and become idle.
        idlingResource.waitForIdle();

        // 2. Perform the action
        // Simulate a long click on the map at the specified coordinates.
        clickOnMapAt(HERE_BE_DRAGONS, Tap.LONG);

        // It can take a moment for the new circle to be rendered. A brief pause
        // helps prevent the test from failing intermittently.
        idlingResource.waitForIdle(200);

        // 3. Restore camera and verify results
        scenario.onActivity(activity -> {
            // Restore the camera to its original position.
            map.moveCamera(CameraUpdateFactory.newCameraPosition(camera[0]));
        });

        idlingResource.waitForIdle();

        scenario.onActivity(activity -> {
            // Assert that a new circle was added.
            assertThat(activity.circles).hasSize(2);
            Circle newCircle = activity.circles.get(1).circle;

            // Verify the new circle is located at the target coordinates, allowing for a
            // small margin of error (10 meters) due to the LatLng-to-pixel conversion.
            assertThat(newCircle.getCenter()).isWithin(10).of(HERE_BE_DRAGONS);

            // Ensure the new circle has a visible size.
            assertThat(newCircle.getRadius()).isNotEqualTo(0.0);
        });
    }
}
