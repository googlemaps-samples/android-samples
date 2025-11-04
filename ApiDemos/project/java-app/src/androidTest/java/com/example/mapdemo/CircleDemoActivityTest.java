package com.example.mapdemo;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.gms.maps.model.Circle;
import com.example.common_ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CircleDemoActivityTest {

    private MapIdlingResource idlingResource;
    private ActivityScenario<CircleDemoActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(CircleDemoActivity.class);
        scenario.onActivity(activity -> {
            idlingResource = new MapIdlingResource(activity.map);
            IdlingRegistry.getInstance().register(idlingResource);
        });
    }

    @Test
    public void testMapIsDisplayed() {
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testInitialCircleIsDrawn() {
        scenario.onActivity(activity -> {
            assertEquals(1, activity.circles.size());
        });
    }

    @Test
    public void testFillColorChanges() {
        scenario.onActivity(activity -> {
            Circle circle = activity.circles.get(0).circle;
            int initialColor = circle.getFillColor();

            activity.binding.fillHueSeekBar.setProgress(120);

            assertNotEquals(initialColor, circle.getFillColor());
        });
    }

    @Test
    public void testStrokeColorChanges() {
        scenario.onActivity(activity -> {
            Circle circle = activity.circles.get(0).circle;
            int initialColor = circle.getStrokeColor();

            activity.binding.strokeHueSeekBar.setProgress(120);

            assertNotEquals(initialColor, circle.getStrokeColor());
        });
    }

    @Test
    public void testStrokeWidthChanges() {
        scenario.onActivity(activity -> {
            Circle circle = activity.circles.get(0).circle;
            float initialWidth = circle.getStrokeWidth();

            activity.binding.strokeWidthSeekBar.setProgress(20);

            assertNotEquals(initialWidth, circle.getStrokeWidth());
        });
    }

    @Test
    public void testToggleClickability() {
        scenario.onActivity(activity -> {
            Circle circle = activity.circles.get(0).circle;
            boolean initialClickable = circle.isClickable();

            onView(withId(R.id.toggleClickability)).perform(click());

            assertEquals(!initialClickable, circle.isClickable());
        });
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        scenario.close();
    }
}
