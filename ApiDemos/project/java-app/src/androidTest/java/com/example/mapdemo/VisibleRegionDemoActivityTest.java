package com.example.mapdemo;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class VisibleRegionDemoActivityTest {

    @Test
    public void testSetNoPadding() {
        ActivityScenario<VisibleRegionDemoActivity> scenario = ActivityScenario.launch(VisibleRegionDemoActivity.class);
        scenario.onActivity(activity -> {
            // Initial state
            assertEquals(150, activity.currentLeft);
            assertEquals(0, activity.currentTop);
            assertEquals(0, activity.currentRight);
            assertEquals(0, activity.currentBottom);
        });

        Espresso.onView(ViewMatchers.withText("No padding")).perform(ViewActions.click());

        scenario.onActivity(activity -> {
            assertEquals(150, activity.currentLeft);
            assertEquals(0, activity.currentTop);
            assertEquals(0, activity.currentRight);
            assertEquals(0, activity.currentBottom);
        });
    }
}
