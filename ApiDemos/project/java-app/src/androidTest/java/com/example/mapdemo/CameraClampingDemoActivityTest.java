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

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.example.mapdemo.truth.LatLngBoundsSubject;
import com.example.mapdemo.truth.LatLngSubject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CameraClampingDemoActivityTest {

    private MapIdlingResource idlingResource;
    private ActivityScenario<CameraClampingDemoActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(CameraClampingDemoActivity.class);
        scenario.onActivity(activity -> {
            idlingResource = new MapIdlingResource(activity.mMap);
            IdlingRegistry.getInstance().register(idlingResource);
        });
    }

    @Test
    public void testClampToAdelaide() {
        // 1. Clamp to Adelaide
        Espresso.onView(ViewMatchers.withText("Clamp to Adelaide")).perform(ViewActions.click());

        // 2. Try to move the camera outside the bounds
        scenario.onActivity(activity -> {
            activity.mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(new LatLng(0, 0)));
        });

        // 3. Assert that the camera is still inside the Adelaide bounds
        scenario.onActivity(activity -> {
            LatLngBounds adelaideBounds = new LatLngBounds(
                new LatLng(-35.0, 138.58), new LatLng(-34.9, 138.61));
            CameraPosition cameraPosition = activity.mMap.getCameraPosition();
            LatLngBoundsSubject.assertThat(adelaideBounds).containsWithTolerance(cameraPosition.target);
        });
    }

    @Test
    public void testClampToPacific() {
        // 1. Clamp to Pacific
        Espresso.onView(ViewMatchers.withText("Clamp to Pacific")).perform(ViewActions.click());

        // 2. Try to move the camera outside the bounds
        scenario.onActivity(activity -> {
            activity.mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(new LatLng(45, 0)));
        });

        // 3. Assert that the camera is still inside the Pacific bounds
        scenario.onActivity(activity -> {
            LatLngBounds pacificBounds = new LatLngBounds(
                new LatLng(-45, 160), new LatLng(45, -160));
            CameraPosition cameraPosition = activity.mMap.getCameraPosition();
            LatLngBoundsSubject.assertThat(pacificBounds).containsWithTolerance(cameraPosition.target);
        });
    }

    @Test
    public void testLatLngClampReset() {
        // 1. Clamp to Adelaide first
        Espresso.onView(ViewMatchers.withText("Clamp to Adelaide")).perform(ViewActions.click());

        // 2. Reset the clamp
        Espresso.onView(ViewMatchers.withText("Reset LatLng Bounds")).perform(ViewActions.click());

        // 3. Verify the clamp is gone by moving the camera far away
        scenario.onActivity(activity -> {
            activity.mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(new LatLng(0, 0)));
        });

        // 4. Assert the camera is now at the new position (0,0)
        scenario.onActivity(activity -> {
            CameraPosition cameraPosition = activity.mMap.getCameraPosition();
            LatLngSubject.assertThat(cameraPosition.target).isNear(new LatLng(0, 0));
        });
    }

    @Test
    public void testSetMinZoomClamp() {
        Espresso.onView(ViewMatchers.withText("MinZoom++")).perform(ViewActions.click());
        scenario.onActivity(activity -> {
            // Try to zoom out
            activity.mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomTo(1.0f));
            assertEquals(4.0f, activity.mMap.getCameraPosition().zoom, 1e-5);
        });
    }

    @Test
    public void testSetMaxZoomClamp() {
        Espresso.onView(ViewMatchers.withText("MaxZoom--")).perform(ViewActions.click());
        scenario.onActivity(activity -> {
            // Try to zoom in
            activity.mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomTo(22.0f));
            assertEquals(20.0f, activity.mMap.getCameraPosition().zoom, 1e-5);
        });
    }

    @Test
    public void testMinMaxZoomClampReset() {
        // Set a high min zoom and verify it's active.
        Espresso.onView(ViewMatchers.withText("MinZoom++")).perform(ViewActions.click()); // min zoom -> 4.0
        Espresso.onView(ViewMatchers.withText("MinZoom++")).perform(ViewActions.click()); // min zoom -> 6.0
        scenario.onActivity(activity -> {
            activity.mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomTo(5.0f));
        });
        scenario.onActivity(activity -> {
            assertEquals("Min zoom clamp should be 6.0", 6.0f, activity.mMap.getCameraPosition().zoom, 1e-5);
        });

        // Set a low max zoom and verify it's active.
        Espresso.onView(ViewMatchers.withText("MaxZoom--")).perform(ViewActions.click()); // max zoom -> 20.0
        Espresso.onView(ViewMatchers.withText("MaxZoom--")).perform(ViewActions.click()); // max zoom -> 18.0
        scenario.onActivity(activity -> {
            activity.mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomTo(19.0f));
        });
        scenario.onActivity(activity -> {
            assertEquals("Max zoom clamp should be 18.0", 18.0f, activity.mMap.getCameraPosition().zoom, 1e-5);
        });

        // Reset the zoom bounds.
        Espresso.onView(ViewMatchers.withText("Reset Zoom Bounds")).perform(ViewActions.click());

        // Verify the min clamp is gone.
        scenario.onActivity(activity -> {
            activity.mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomTo(5.0f));
        });
        scenario.onActivity(activity -> {
            assertEquals("Min zoom clamp should be gone", 5.0f, activity.mMap.getCameraPosition().zoom, 1e-5);
        });

        // Verify the max clamp is gone.
        scenario.onActivity(activity -> {
            activity.mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomTo(19.0f));
        });
        scenario.onActivity(activity -> {
            assertEquals("Max zoom clamp should be gone", 19.0f, activity.mMap.getCameraPosition().zoom, 1e-5);
        });
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        scenario.close();
    }
}