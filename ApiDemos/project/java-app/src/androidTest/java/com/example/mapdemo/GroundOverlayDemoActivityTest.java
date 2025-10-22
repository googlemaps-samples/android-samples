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

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class GroundOverlayDemoActivityTest {

    private MapIdlingResource idlingResource;
    private ActivityScenario<GroundOverlayDemoActivity> scenario;
    private GroundOverlay groundOverlay;

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
            idlingResource = new MapIdlingResource(activity.mMap);
            IdlingRegistry.getInstance().register(idlingResource);
            groundOverlay = activity.groundOverlayRotated;
        });
    }

    @Test
    public void testToggleClickability() {
        // By default, the overlay is clickable.
        final float[] initialTransparency = new float[1];
        scenario.onActivity(activity -> {
            initialTransparency[0] = groundOverlay.getTransparency();
        });

        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.map)).perform(ViewActions.click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idlingResource.waitForIdle();

        scenario.onActivity(activity -> {
            assertThat(groundOverlay.getTransparency()).isNotEqualTo(initialTransparency[0]);
        });

        // Disable clickability.
        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.toggleClickability)).perform(ViewActions.click());

        // Now, clicking the map should not change the transparency.
        final float[] transparencyAfterToggle = new float[1];
        scenario.onActivity(activity -> {
            transparencyAfterToggle[0] = groundOverlay.getTransparency();
        });

        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.map)).perform(ViewActions.click());
        idlingResource.waitForIdle();

        scenario.onActivity(activity -> {
            assertThat(groundOverlay.getTransparency()).isEqualTo(transparencyAfterToggle[0]);
        });
    }

    @Test
    public void testTransparencySeekBar() {
        final float[] initialTransparency = new float[1];
        scenario.onActivity(activity -> {
            initialTransparency[0] = activity.groundOverlay.getTransparency();
        });

        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.transparencySeekBar)).perform(ViewActions.swipeRight());

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

        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.switchImage)).perform(ViewActions.click());

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlay.getTag()).isNotEqualTo(initialBitmapDescriptor[0]);
        });
    }

    @Test
    public void testToggleClickability_DisablesClicks() {
        // Verify the overlay is initially clickable.
        final float[] initialTransparency = new float[1];
        scenario.onActivity(activity -> {
            initialTransparency[0] = activity.groundOverlayRotated.getTransparency();
        });

        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.map)).perform(ViewActions.click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idlingResource.waitForIdle();

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlayRotated.getTransparency()).isNotEqualTo(initialTransparency[0]);
        });

        // Disable clickability.
        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.toggleClickability)).perform(ViewActions.click());

        // Now, clicking the map should not change the transparency.
        final float[] transparencyAfterToggle = new float[1];
        scenario.onActivity(activity -> {
            transparencyAfterToggle[0] = activity.groundOverlayRotated.getTransparency();
        });

        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.map)).perform(ViewActions.click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idlingResource.waitForIdle();

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlayRotated.getTransparency()).isEqualTo(transparencyAfterToggle[0]);
        });
    }

    @Test
    public void testToggleClickability_ReEnablesClicks() {
        // Disable and then re-enable clickability.
        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.toggleClickability)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.toggleClickability)).perform(ViewActions.click());

        final float[] initialTransparency = new float[1];
        scenario.onActivity(activity -> {
            initialTransparency[0] = activity.groundOverlayRotated.getTransparency();
        });

        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.map)).perform(ViewActions.click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idlingResource.waitForIdle();

        scenario.onActivity(activity -> {
            assertThat(activity.groundOverlayRotated.getTransparency()).isNotEqualTo(initialTransparency[0]);
        });
    }

    @Test
    public void testMainOverlay_IsNotClickable() {
        final float[] initialTransparency = new float[1];
        scenario.onActivity(activity -> {
            initialTransparency[0] = activity.groundOverlay.getTransparency();
        });

        Espresso.onView(ViewMatchers.withId(com.example.common_ui.R.id.map)).perform(ViewActions.click());
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