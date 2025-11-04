package com.example.kotlindemos

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.common_ui.R
import com.example.kotlindemos.truth.LatLngSubject.Companion.assertThat
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CircleDemoActivityTest {

    private lateinit var idlingResource: MapIdlingResource
    private lateinit var scenario: ActivityScenario<CircleDemoActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(CircleDemoActivity::class.java)
        scenario.onActivity { activity ->
            idlingResource = MapIdlingResource(activity.map)
            androidx.test.espresso.IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @Test
    fun testMapIsDisplayed() {
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun testInitialCircleProperties() {
        scenario.onActivity { activity ->
            assertThat(activity.circles).hasSize(1)
            val circle = activity.circles[0].circle
            assertThat(circle.center).isNear(LatLng(-33.87365, 151.20689))
            assertThat(circle.radius).isWithin(1e-6).of(1000000.0)
            // Initial colors are set based on the initial state of the seekbars
            // Let's check if they are not transparent
            assertThat(circle.fillColor).isNotEqualTo(0)
            assertThat(circle.strokeColor).isNotEqualTo(0)
        }
    }

    @Test
    fun testFillColorChanges() {
        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            val initialColor = circle.fillColor

            activity.binding.fillHueSeekBar.progress = 120

            assertThat(circle.fillColor).isNotEqualTo(initialColor)
        }
    }

    @Test
    fun testStrokeColorChanges() {
        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            val initialColor = circle.strokeColor

            activity.binding.strokeHueSeekBar.progress = 120

            assertThat(circle.strokeColor).isNotEqualTo(initialColor)
        }
    }

    @Test
    fun testStrokeWidthChanges() {
        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            val initialWidth = circle.strokeWidth

            activity.binding.strokeWidthSeekBar.progress = 20

            assertThat(circle.strokeWidth).isNotEqualTo(initialWidth)
        }
    }

    @Test
    fun testToggleClickability() {
        var initialClickable = false
        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            initialClickable = circle.isClickable
        }

        onView(withId(R.id.toggleClickability)).perform(click())

        scenario.onActivity { activity ->
            val circle = activity.circles[0].circle
            assertThat(circle.isClickable).isEqualTo(!initialClickable)
        }
    }

    @Test
    fun testLongClickAddsNewCircle() {
        scenario.onActivity { activity ->
            assertThat(activity.circles).hasSize(1)
        }

        // Perform a long click at a location far from Sydney
//        onView(withId(R.id.map)).perform(longClick(LatLng(0.0, 0.0)))

        scenario.onActivity { activity ->
            assertThat(activity.circles).hasSize(2)
            val newCircle = activity.circles[1].circle
            assertThat(newCircle.center).isNear(LatLng(0.0, 0.0))
            assertThat(newCircle.radius).isNotEqualTo(0.0)
        }
    }

    @After
    fun tearDown() {
        androidx.test.espresso.IdlingRegistry.getInstance().unregister(idlingResource)
        scenario.close()
    }
}
