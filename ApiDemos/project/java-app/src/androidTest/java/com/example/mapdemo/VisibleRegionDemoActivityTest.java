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
    public void testNoPaddingButton() {
        onView(withId(com.example.common_ui.R.id.vr_normal_button)).perform(click());
        onView(withId(com.example.common_ui.R.id.message_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testMorePaddingButton() {
        onView(withId(com.example.common_ui.R.id.vr_more_padded_button)).perform(click());
        onView(withId(com.example.common_ui.R.id.message_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testOperaHouseButton() {
        onView(withId(com.example.common_ui.R.id.vr_soh_button)).perform(click());
        onView(withId(com.example.common_ui.R.id.message_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testSfoButton() {
        onView(withId(com.example.common_ui.R.id.vr_sfo_button)).perform(click());
        onView(withId(com.example.common_ui.R.id.message_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testAusButton() {
        onView(withId(com.example.common_ui.R.id.vr_aus_button)).perform(click());
        onView(withId(com.example.common_ui.R.id.message_text)).check(matches(isDisplayed()));
    }
}