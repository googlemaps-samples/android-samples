package com.example.kotlindemos

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.LatLng
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
        Thread.sleep(2000)

        // Get the initial transparency and position of the overlay on the UI thread.
        var initialTransparency = 0f
        scenario.onActivity { activity ->
            initialTransparency = groundOverlay.transparency
        }

        val clickPosition = LatLng(40.71398657613997, -74.24413025379181)

        // Perform a click inside the ground overlay.
        clickOnMapAt(clickPosition)

        // This is not ideal, but it's a simple way to make the test reliable.
        Thread.sleep(200)

        // Verify that the transparency has changed, indicating the click was successful.
        scenario.onActivity {
            assertThat(groundOverlay.transparency).isNotEqualTo(initialTransparency)
        }

        // Disable clickability by clicking the checkbox.
        onView(withId(com.example.common_ui.R.id.toggleClickability)).perform(click())

        // Get the transparency after the first click.
        var transparencyAfterToggle = 0f
        scenario.onActivity {
            transparencyAfterToggle = groundOverlay.transparency
        }

        // Perform another click at the same location.
        clickOnMapAt(clickPosition)

        // Verify that the transparency has NOT changed, because the overlay is no longer clickable.
        scenario.onActivity {
            assertThat(groundOverlay.transparency).isEqualTo(transparencyAfterToggle)
        }
    }

    /**
     * Helper function to perform a click at a specific geographical location on the map.
     */
    private fun clickOnMapAt(latLng: LatLng) {
        val coordinates = FloatArray(2)
        scenario.onActivity { activity ->
            val projection = activity.map.projection
            val screenPosition = projection.toScreenLocation(latLng)
            coordinates[0] = screenPosition.x.toFloat()
            coordinates[1] = screenPosition.y.toFloat()
        }

        val clickAction = androidx.test.espresso.action.GeneralClickAction(
            androidx.test.espresso.action.Tap.SINGLE,
            { _ -> coordinates },
            androidx.test.espresso.action.Press.FINGER,
            android.view.InputDevice.SOURCE_UNKNOWN,
            android.view.MotionEvent.BUTTON_PRIMARY
        )
        onView(withId(com.example.common_ui.R.id.map)).perform(clickAction)
    }

    @Test
    fun testTransparencySeekBar() {
        var initialTransparency = 0f
        scenario.onActivity { activity ->
            initialTransparency = activity.groundOverlay!!.transparency
        }

        onView(withId(com.example.common_ui.R.id.transparencySeekBar)).perform(click())

        scenario.onActivity { activity ->
            assertThat(activity.groundOverlay!!.transparency).isNotEqualTo(initialTransparency)
            assertThat(activity.groundOverlayRotated!!.transparency).isEqualTo(initialTransparency)
        }
    }

    @Test
    fun testSwitchImageButton() {
        var initialBitmapDescriptor: BitmapDescriptor? = null
        scenario.onActivity { activity ->
            initialBitmapDescriptor = activity.groundOverlay!!.tag as BitmapDescriptor
        }

        onView(withId(com.example.common_ui.R.id.switchImage)).perform(click())

        scenario.onActivity { activity ->
            assertThat(activity.groundOverlay!!.tag as BitmapDescriptor).isNotEqualTo(initialBitmapDescriptor)
        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        scenario.close()
    }
}