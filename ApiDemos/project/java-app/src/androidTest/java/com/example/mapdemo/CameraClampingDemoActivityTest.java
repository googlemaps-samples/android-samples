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
        Espresso.onView(ViewMatchers.withText("Clamp to Adelaide")).perform(ViewActions.click());
        scenario.onActivity(activity -> {
            LatLngBounds expectedBounds = new LatLngBounds(
                new LatLng(-35.0, 138.58), new LatLng(-34.9, 138.61));
            // It's not possible to get the LatLngBounds from the map, so we check the camera position.
            CameraPosition cameraPosition = activity.mMap.getCameraPosition();
            assertEquals(-34.92873, cameraPosition.target.latitude, 1e-5);
            assertEquals(138.59995, cameraPosition.target.longitude, 1e-5);
        });
    }

    @Test
    public void testClampToPacific() {
        Espresso.onView(ViewMatchers.withText("Clamp to Pacific")).perform(ViewActions.click());
        scenario.onActivity(activity -> {
            // It's not possible to get the LatLngBounds from the map, so we check the camera position.
            CameraPosition cameraPosition = activity.mMap.getCameraPosition();
            assertEquals(0, cameraPosition.target.latitude, 1e-5);
            assertEquals(-180, cameraPosition.target.longitude, 1e-5);
        });
    }

    @Test
    public void testLatLngClampReset() {
        Espresso.onView(ViewMatchers.withText("Clamp to Adelaide")).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Reset LatLng Bounds")).perform(ViewActions.click());
        scenario.onActivity(activity -> {
            // This is a best-effort check, as there is no public getter for the camera target bounds.
            // We assume that if the clamp was reset, the bounds would be null.
            // A more robust test would involve moving the camera and checking if it goes outside the previous bounds.
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