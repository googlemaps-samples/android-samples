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