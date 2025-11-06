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
package com.example.kotlindemos.utils

import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.Tapper
import androidx.test.espresso.matcher.ViewMatchers
import com.example.common_ui.R
import com.example.kotlindemos.MapIdlingResource
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import org.junit.After
import org.junit.Before

abstract class MapDemoActivityTest<T>(
    // Pass the class to the constructor as a property
    private val klass: Class<T>
)
// Use a 'where' clause for multiple type constraints
        where T : AppCompatActivity, T : MapProvider {
    // Made protected so children can access
    protected lateinit var mapView: View
    protected lateinit var map: GoogleMap
    protected lateinit var idlingResource: MapIdlingResource
    protected lateinit var scenario: ActivityScenario<T>

    @Before
    open fun setUp() { // Made open so it can be overridden
        scenario = ActivityScenario.launch(klass) // <-- Uses constructor property
        scenario.onActivity { activity -> // <-- 'activity' is now of type T
            // Wait for the map to be ready
            val startTime = System.currentTimeMillis()
            val endTime = startTime + 5000 // 5 second timeout

            // This works because T is constrained to MapProvider
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

            map = activity.map

            // This works because T is constrained to AppCompatActivity
            // Changed from MapView to View, as R.id.map points to the FragmentContainerView
            mapView = activity.findViewById(R.id.map)

            idlingResource = MapIdlingResource(activity.map)
            IdlingRegistry.getInstance().register(idlingResource)
        }
        Thread.sleep(2000)
    }

    @After
    open fun tearDown() { // Made open so it can be overridden
        IdlingRegistry.getInstance().unregister(idlingResource)
        scenario.close()
    }

    protected fun clickOnMapAt(
        latLng: LatLng,
        tapType: Tapper = Tap.SINGLE) {
        clickOnMapAt(latLng, map, mapView, tapType)
    }

    /**
     * Helper function to perform a click at a specific geographical location on the map.
     */
    protected fun clickOnMapAt(
        latLng: LatLng,
        map: GoogleMap,
        mapView: View,
        tapType: Tapper = Tap.SINGLE
    ) {
        val coordinates = FloatArray(2)
        scenario.onActivity { activity ->
            val mapLocation = IntArray(2)
            mapView.getLocationOnScreen(mapLocation)

            val projection = map.projection
            val screenPosition = projection.toScreenLocation(latLng)

            coordinates[0] = screenPosition.x.toFloat() + mapLocation[0]
            coordinates[1] = screenPosition.y.toFloat() + mapLocation[1]
        }

        val clickAction = GeneralClickAction(
            /* tapper = */ tapType,
            /* coordinatesProvider = */ { _ -> coordinates },
            /* precisionDescriber = */ Press.FINGER,
            /* inputDevice = */ InputDevice.SOURCE_UNKNOWN,
            /* buttonState = */ MotionEvent.BUTTON_PRIMARY
        )
        Espresso.onView(ViewMatchers.withId(R.id.map)).perform(clickAction)
    }
}
