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

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class VisibleRegionDemoActivityTest {

    @Rule
    public ActivityScenarioRule<VisibleRegionDemoActivity> activityRule =
            new ActivityScenarioRule<>(VisibleRegionDemoActivity.class);

    @Test
    public void testActionsMenuDisplayed() {
        onView(withId(com.example.common_ui.R.id.camera_actions_button)).check(matches(isDisplayed()));
        onView(withId(com.example.common_ui.R.id.camera_target_text)).check(matches(isDisplayed()));
        onView(withId(com.example.common_ui.R.id.camera_details_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testNoPaddingAction() {
        onView(withId(com.example.common_ui.R.id.camera_actions_button)).perform(click());
        onView(withText("Default Padding (None)")).perform(click());
        onView(withId(com.example.common_ui.R.id.camera_target_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testMorePaddingAction() {
        onView(withId(com.example.common_ui.R.id.camera_actions_button)).perform(click());
        onView(withText("Asymmetric Padding (Right + Bottom)")).perform(click());
        onView(withId(com.example.common_ui.R.id.camera_target_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testOperaHouseAction() {
        onView(withId(com.example.common_ui.R.id.camera_actions_button)).perform(click());
        onView(withText("Move to Sydney Opera House")).perform(click());
        onView(withId(com.example.common_ui.R.id.camera_target_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testSfoAction() {
        onView(withId(com.example.common_ui.R.id.camera_actions_button)).perform(click());
        onView(withText("Move to San Francisco Airport (SFO)")).perform(click());
        onView(withId(com.example.common_ui.R.id.camera_target_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testAusAction() {
        onView(withId(com.example.common_ui.R.id.camera_actions_button)).perform(click());
        onView(withText("Fit Australia Bounds")).perform(click());
        onView(withId(com.example.common_ui.R.id.camera_target_text)).check(matches(isDisplayed()));
    }
}