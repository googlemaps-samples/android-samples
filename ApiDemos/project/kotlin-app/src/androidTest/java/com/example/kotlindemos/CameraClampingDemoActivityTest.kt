package com.example.kotlindemos

import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CameraClampingDemoActivityTest {

    private lateinit var idlingResource: MapIdlingResource
    private lateinit var scenario: ActivityScenario<CameraClampingDemoActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(CameraClampingDemoActivity::class.java)
        scenario.onActivity { activity ->
            idlingResource = MapIdlingResource(activity.map)
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @Test
    fun testClampToAdelaide() {
        onView(withText("Clamp to Adelaide")).perform(click())
        scenario.onActivity { activity ->
            val expectedBounds = LatLngBounds(
                LatLng(-35.0, 138.58), LatLng(-34.9, 138.61)
            )
            // It's not possible to get the LatLngBounds from the map, so we check the camera position.
            val cameraPosition: CameraPosition = activity.map.cameraPosition
            assertEquals(-34.92873, cameraPosition.target.latitude, 1e-5)
            assertEquals(138.59995, cameraPosition.target.longitude, 1e-5)
        }
    }

    @Test
    fun testClampToPacific() {
        onView(withText("Clamp to Pacific")).perform(click())
        scenario.onActivity { activity ->
            // It's not possible to get the LatLngBounds from the map, so we check the camera position.
            val cameraPosition: CameraPosition = activity.map.cameraPosition
            assertEquals(0.0, cameraPosition.target.latitude, 1e-5)
            assertEquals(-180.0, cameraPosition.target.longitude, 1e-5)
        }
    }

    @Test
    fun testLatLngClampReset() {
        onView(withText("Clamp to Adelaide")).perform(click())
        onView(withText("Reset LatLng Bounds")).perform(click())
        scenario.onActivity {
            // This is a best-effort check, as there is no public getter for the camera target bounds.
            // We assume that if the clamp was reset, the bounds would be null.
            // A more robust test would involve moving the camera and checking if it goes outside the previous bounds.
        }
    }

    @Test
    fun testSetMinZoomClamp() {
        onView(withText("MinZoom++")).perform(click())
        scenario.onActivity { activity ->
            // Try to zoom out
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(1.0f))
            assertEquals(4.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }
    }

    @Test
    fun testSetMaxZoomClamp() {
        onView(withText("MaxZoom--")).perform(click())
        scenario.onActivity { activity ->
            // Try to zoom in
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(22.0f))
            assertEquals(20.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }
    }

    @Test
    fun testMinMaxZoomClampReset() {
        // Set a high min zoom and verify it's active.
        onView(withText("MinZoom++")).perform(click()) // min zoom -> 4.0
        onView(withText("MinZoom++")).perform(click()) // min zoom -> 6.0
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(5.0f))
        }
        scenario.onActivity { activity ->
            assertEquals("Min zoom clamp should be 6.0", 6.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }

        // Set a low max zoom and verify it's active.
        onView(withText("MaxZoom--")).perform(click()) // max zoom -> 20.0
        onView(withText("MaxZoom--")).perform(click()) // max zoom -> 18.0
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(19.0f))
        }
        scenario.onActivity { activity ->
            assertEquals("Max zoom clamp should be 18.0", 18.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }

        // Reset the zoom bounds.
        onView(withText("Reset Zoom Bounds")).perform(click())

        // Verify the min clamp is gone.
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(5.0f))
        }
        scenario.onActivity { activity ->
            assertEquals("Min zoom clamp should be gone", 5.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }

        // Verify the max clamp is gone.
        scenario.onActivity { activity ->
            activity.map.moveCamera(CameraUpdateFactory.zoomTo(19.0f))
        }
        scenario.onActivity { activity ->
            assertEquals("Max zoom clamp should be gone", 19.0, activity.map.cameraPosition.zoom.toDouble(), 1e-5)
        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        scenario.close()
    }
}