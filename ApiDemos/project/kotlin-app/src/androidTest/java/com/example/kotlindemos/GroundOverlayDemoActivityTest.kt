package com.example.kotlindemos

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.GroundOverlay
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroundOverlayDemoActivityTest {

    private lateinit var idlingResource: MapIdlingResource
    private lateinit var scenario: ActivityScenario<GroundOverlayDemoActivity>
    private lateinit var groundOverlay: GroundOverlay

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(GroundOverlayDemoActivity::class.java)
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
            groundOverlay = activity.groundOverlayRotated!!
        }
    }

    @Test
    fun testToggleClickability() {
        var initialTransparency = 0f
        scenario.onActivity {
            initialTransparency = groundOverlay.transparency
        }

        onView(withId(com.example.common_ui.R.id.map)).perform(click())

        // Let the map idle before checking the transparency.
        idlingResource.waitForIdle()

        scenario.onActivity {
            assertThat(groundOverlay.transparency).isNotEqualTo(initialTransparency)
        }

        // Disable clickability.
        onView(withId(com.example.common_ui.R.id.toggleClickability)).perform(click())

        var transparencyAfterToggle = 0f
        scenario.onActivity {
            transparencyAfterToggle = groundOverlay.transparency
        }

        // Now, clicking the map should not change the transparency.
        onView(withId(com.example.common_ui.R.id.map)).perform(click())

        // Let the map idle before checking the transparency.
        idlingResource.waitForIdle()

        scenario.onActivity {
            assertThat(groundOverlay.transparency).isEqualTo(transparencyAfterToggle)
        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        scenario.close()
    }
}