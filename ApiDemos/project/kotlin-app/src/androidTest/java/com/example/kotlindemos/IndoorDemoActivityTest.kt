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

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.common_ui.R
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IndoorDemoActivityTest {
    private lateinit var idlingResource: MapIdlingResource
    private lateinit var scenario: ActivityScenario<IndoorDemoActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(IndoorDemoActivity::class.java)
        scenario.onActivity { activity ->
            // Wait for the map to be ready
            val startTime = System.currentTimeMillis()
            val endTime = startTime + 5000 // 5 second timeout
            while (!activity.mapReady && System.currentTimeMillis() < endTime) {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            if (!activity.mapReady) {
                throw RuntimeException("Map is not ready after 5 seconds")
            }

            idlingResource = MapIdlingResource(activity.map)
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @Test
    fun testToggleLevelPicker() {
        // By default, the level picker is enabled.
        scenario.onActivity { activity ->
            assertTrue(activity.map.uiSettings.isIndoorLevelPickerEnabled)
        }

        // Click the toggle button and verify the level picker is disabled.
        onView(withId(R.id.toggle_level_picker_button)).perform(click())
        scenario.onActivity { activity ->
            assertFalse(activity.map.uiSettings.isIndoorLevelPickerEnabled)
        }

        // Click it again and verify it's re-enabled.
        onView(withId(R.id.toggle_level_picker_button)).perform(click())
        scenario.onActivity { activity ->
            assertTrue(activity.map.uiSettings.isIndoorLevelPickerEnabled)
        }
    }

    @Test
    fun testHigherLevel() {
        Thread.sleep(5000)
        onView(withId(R.id.higher_level_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.top_text)).check(matches(withText("Activating level 3")))
    }

    @Test
    fun testVisibleLevelInfo() {
        Thread.sleep(200)
        onView(withId(R.id.focused_level_info_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.top_text)).check(matches(withText("1")))
        Thread.sleep(200)
        onView(withId(R.id.higher_level_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.focused_level_info_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.top_text)).check(matches(withText("3")))
    }

    @Test
    fun testFocusedBuildingInfo() {
        Thread.sleep(5000)
        onView(withId(R.id.focused_bulding_info_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.top_text)).check(matches(withText("3 1 ")))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        scenario.close()
    }
}
