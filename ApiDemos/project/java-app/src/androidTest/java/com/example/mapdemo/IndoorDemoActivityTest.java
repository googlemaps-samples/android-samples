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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.common_ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IndoorDemoActivityTest {

    private MapIdlingResource idlingResource;
    private ActivityScenario<IndoorDemoActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(IndoorDemoActivity.class);
        scenario.onActivity(activity -> {
            idlingResource = new MapIdlingResource(activity.map);
            IdlingRegistry.getInstance().register(idlingResource);
        });
    }

    @Test
    public void testToggleLevelPicker() {
        // By default, the level picker is enabled.
        scenario.onActivity(activity -> {
            assertTrue(activity.map.getUiSettings().isIndoorLevelPickerEnabled());
        });

        // Click the toggle button and verify the level picker is disabled.
        onView(withId(R.id.toggle_level_picker_button)).perform(click());
        scenario.onActivity(activity -> {
            assertFalse(activity.map.getUiSettings().isIndoorLevelPickerEnabled());
        });

        // Click it again and verify it's re-enabled.
        onView(withId(R.id.toggle_level_picker_button)).perform(click());
        scenario.onActivity(activity -> {
            assertTrue(activity.map.getUiSettings().isIndoorLevelPickerEnabled());
        });
    }

    @Test
    public void testHigherLevel() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.higher_level_button)).perform(click());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.top_text)).check(matches(withText("Activating level 3")));
    }

    @Test
    public void testVisibleLevelInfo() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.focused_level_info_button)).perform(click());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.top_text)).check(matches(withText("1")));
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.higher_level_button)).perform(click());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.focused_level_info_button)).perform(click());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.top_text)).check(matches(withText("3")));
    }

    @Test
    public void testFocusedBuildingInfo() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.focused_bulding_info_button)).perform(click());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.top_text)).check(matches(withText("3 1 ")));
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        scenario.close();
    }
}
