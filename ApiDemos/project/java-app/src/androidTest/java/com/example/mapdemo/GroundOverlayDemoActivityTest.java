package com.example.mapdemo;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlay;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

import com.example.common_ui.R;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class GroundOverlayDemoActivityTest {

    private MapIdlingResource idlingResource;
    private ActivityScenario<GroundOverlayDemoActivity> scenario;
    private GroundOverlay groundOverlay;

    // A coordinate known to be on the rotated overlay
    private final LatLng OVERLAY_COORDINATE = new LatLng(40.714904490859396, -74.23857238143682);

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(GroundOverlayDemoActivity.class);
        scenario.onActivity(activity -> {            long startTime = System.currentTimeMillis();
            long endTime = startTime + 5000; // 5 seconds

            while (activity.mMap == null && System.currentTimeMillis() < endTime) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (activity.mMap == null) {
                throw new RuntimeException("Map did not become available within 5 seconds.");
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            idlingResource = new MapIdlingResource(activity.mMap);
            IdlingRegistry.getInstance().register(idlingResource);
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

    private Point getPointAtCoordinate(LatLng latLng) {
        final Point[] screenPoint = new Point[1];

        scenario.onActivity(activity -> {
            // Get the top-left screen coordinate of the MapView
            int[] mapViewLocation = new int[2];
            activity.mMap.getUiSettings().setScrollGesturesEnabled(false); // Disable gestures to prevent map movement during test
            activity.findViewById(R.id.map).getLocationOnScreen(mapViewLocation);
            int mapViewLeft = mapViewLocation[0];
            int mapViewTop = mapViewLocation[1];

            // Convert the LatLng to screen pixels relative to the MapView
            screenPoint[0] = activity.mMap.getProjection().toScreenLocation(latLng);

            // Now calculate the overall location
            screenPoint[0].x += mapViewLeft;
            screenPoint[0].y += mapViewTop;
        });

        return screenPoint[0];
    }

    private boolean clickOnMapAt(UiDevice device, LatLng latLng) {
        Point screenPoint = getPointAtCoordinate(latLng);
        return device.click(screenPoint.x, screenPoint.y);
    }

    @Test
    public void testToggleClickability_DisablesClicks() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // 1. VERIFY CLICKABLE (WHEN ENABLED)
        scenario.onActivity(activity -> {
            // Ensure it's clickable for the first part of the test
            activity.groundOverlayRotated.setClickable(true);
        });

        clickOnMapAt(device, OVERLAY_COORDINATE);

        idlingResource.waitForIdle(); // Wait for listener to fire

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlayRotatedClickCount).isEqualTo(1);
        });

        // 2. DISABLE CLICKABILITY
        Espresso.onView(ViewMatchers.withId(R.id.toggleClickability))
                .perform(ViewActions.click());

        // 3. VERIFY NOT CLICKABLE (WHEN DISABLED)

        // Click the *exact same spot* again
        clickOnMapAt(device, OVERLAY_COORDINATE);

        idlingResource.waitForIdle(); // Give it time (listener shouldn't fire)

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Assert the count did not change
        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlayRotatedClickCount).isEqualTo(1);
        });

        // And back to clickability
        Espresso.onView(ViewMatchers.withId(R.id.toggleClickability))
                .perform(ViewActions.click());

        // Click the *exact same spot* again
        clickOnMapAt(device, OVERLAY_COORDINATE);

        idlingResource.waitForIdle(); // Give it time (listener shouldn't fire)

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idlingResource.waitForIdle();

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlay.getTransparency()).isEqualTo(initialTransparency[0]);
        });
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        scenario.close();
    }
}