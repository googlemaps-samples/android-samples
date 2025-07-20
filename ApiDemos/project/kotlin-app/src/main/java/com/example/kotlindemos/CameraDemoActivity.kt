/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Toast
import com.example.common_ui.R

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveCanceledListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

/**
 * This shows how to change the camera position for the map.
 */
// [START maps_camera_events]
class CameraDemoActivity :
        SamplesBaseActivity(),
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback {
    // [START_EXCLUDE silent]
    /**
     * The amount by which to scroll the camera. Note that this amount is in raw pixels, not dp
     * (density-independent pixels).
     */
    private val SCROLL_BY_PX = 100
    private val TAG = CameraDemoActivity::class.java.name
    private val sydneyLatLng = LatLng(-33.87365, 151.20689)
    private val bondiLocation: CameraPosition = CameraPosition.Builder()
            .target(LatLng(-33.891614, 151.276417))
            .zoom(15.5f)
            .bearing(300f)
            .tilt(50f)
            .build()

    private val sydneyLocation: CameraPosition = CameraPosition.Builder().
            target(LatLng(-33.87365, 151.20689))
            .zoom(15.5f)
            .bearing(0f)
            .tilt(25f)
            .build()
    // [END_EXCLUDE]

    private lateinit var map: GoogleMap
    // [START_EXCLUDE silent]
    private lateinit var animateToggle: CompoundButton
    private lateinit var customDurationToggle: CompoundButton
    private lateinit var customDurationBar: SeekBar
    private var currPolylineOptions: PolylineOptions? = null
    private var isCanceled = false
    // [END_EXCLUDE]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_demo)
        // [START_EXCLUDE silent]
        animateToggle = findViewById(R.id.animate)
        customDurationToggle = findViewById(R.id.duration_toggle)
        customDurationBar = findViewById(R.id.duration_bar)

        updateEnabledState()
        // [END_EXCLUDE]

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    // [START_EXCLUDE silent]
    override fun onResume() {
        super.onResume()
        updateEnabledState()
    }
    // [END_EXCLUDE]

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // return early if the map was not initialised properly
        with(googleMap) {
            setOnCameraIdleListener(this@CameraDemoActivity)
            setOnCameraMoveStartedListener(this@CameraDemoActivity)
            setOnCameraMoveListener(this@CameraDemoActivity)
            setOnCameraMoveCanceledListener(this@CameraDemoActivity)
            // [START_EXCLUDE silent]
            // We will provide our own zoom controls.
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isMyLocationButtonEnabled = true
            // [END_EXCLUDE]

            // Show Sydney
            moveCamera(CameraUpdateFactory.newLatLngZoom(sydneyLatLng, 10f))
        }
    }

    // [START_EXCLUDE silent]
    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be used to wrap
     * all entry points that call methods on the Google Maps API.
     *
     * @param stuffToDo the code to be executed if the map is initialised
     */
    private fun checkReadyThen(stuffToDo: () -> Unit) {
        if (!::map.isInitialized) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show()
        } else {
            stuffToDo()
        }
    }

    /**
     * Called when the Go To Bondi button is clicked.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onGoToBondi(view: View) {
        checkReadyThen {
            changeCamera(CameraUpdateFactory.newCameraPosition(bondiLocation))
        }
    }

    /**
     * Called when the Animate To Sydney button is clicked.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onGoToSydney(view: View) {
        checkReadyThen {
            changeCamera(CameraUpdateFactory.newCameraPosition(sydneyLocation),
                    object : CancelableCallback {
                        override fun onFinish() {
                            Toast.makeText(baseContext, "Animation to Sydney complete",
                                    Toast.LENGTH_SHORT).show()
                        }

                        override fun onCancel() {
                            Toast.makeText(baseContext, "Animation to Sydney canceled",
                                    Toast.LENGTH_SHORT).show()
                        }
                    })
        }
    }

    /**
     * Called when the stop button is clicked.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onStopAnimation(view: View) = checkReadyThen { map.stopAnimation() }

    /**
     * Called when the zoom in button (the one with the +) is clicked.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onZoomIn(view: View) = checkReadyThen { changeCamera(CameraUpdateFactory.zoomIn()) }

    /**
     * Called when the zoom out button (the one with the -) is clicked.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onZoomOut(view: View) = checkReadyThen { changeCamera(CameraUpdateFactory.zoomOut()) }

    /**
     * Called when the tilt more button (the one with the /) is clicked.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onTiltMore(view: View) {
        checkReadyThen {

            val newTilt = Math.min(map.cameraPosition.tilt + 10, 90F)
            val cameraPosition = CameraPosition.Builder(map.cameraPosition).tilt(newTilt).build()

            changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    /**
     * Called when the tilt less button (the one with the \) is clicked.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onTiltLess(view: View) {
        checkReadyThen {

            val newTilt = Math.max(map.cameraPosition.tilt - 10, 0F)
            val cameraPosition = CameraPosition.Builder(map.cameraPosition).tilt(newTilt).build()

            changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    /**
     * Called when the left arrow button is clicked. This causes the camera to move to the left
     */
    @Suppress("UNUSED_PARAMETER")
    fun onScrollLeft(view: View) {
        checkReadyThen {
            changeCamera(CameraUpdateFactory.scrollBy((-SCROLL_BY_PX).toFloat(),0f))
        }
    }

    /**
     * Called when the right arrow button is clicked. This causes the camera to move to the right.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onScrollRight(view: View) {
        checkReadyThen {
            changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX.toFloat(), 0f))
        }
    }

    /**
     * Called when the up arrow button is clicked. The causes the camera to move up.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onScrollUp(view: View) {
        checkReadyThen {
            changeCamera(CameraUpdateFactory.scrollBy(0f, (-SCROLL_BY_PX).toFloat()))
        }
    }

    /**
     * Called when the down arrow button is clicked. This causes the camera to move down.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onScrollDown(view: View) {
        checkReadyThen {
            changeCamera(CameraUpdateFactory.scrollBy(0f, SCROLL_BY_PX.toFloat()))
        }
    }

    /**
     * Called when the animate button is toggled
     */
    @Suppress("UNUSED_PARAMETER")
    fun onToggleAnimate(view: View) = updateEnabledState()

    /**
     * Called when the custom duration checkbox is toggled
     */
    @Suppress("UNUSED_PARAMETER")
    fun onToggleCustomDuration(view: View) = updateEnabledState()

    /**
     * Update the enabled state of the custom duration controls.
     */
    private fun updateEnabledState() {
        customDurationToggle.isEnabled = animateToggle.isChecked
        customDurationBar.isEnabled = animateToggle.isChecked && customDurationToggle.isChecked
    }

    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
    private fun changeCamera(update: CameraUpdate, callback: CancelableCallback? = null) {
        if (animateToggle.isChecked) {
            if (customDurationToggle.isChecked) {
                // The duration must be strictly positive so we make it at least 1.
                map.animateCamera(update, Math.max(customDurationBar.progress, 1), callback)
            } else {
                map.animateCamera(update, callback)
            }
        } else {
            map.moveCamera(update)
        }
    }
    // [END_EXCLUDE]

    override fun onCameraMoveStarted(reason: Int) {
        // [START_EXCLUDE silent]
        if (!isCanceled) map.clear()
        // [END_EXCLUDE]

        var reasonText = "UNKNOWN_REASON"
        // [START_EXCLUDE silent]
        currPolylineOptions = PolylineOptions().width(5f)
        // [END_EXCLUDE]
        when (reason) {
            OnCameraMoveStartedListener.REASON_GESTURE -> {
                // [START_EXCLUDE silent]
                currPolylineOptions?.color(Color.BLUE)
                // [END_EXCLUDE]
                reasonText = "GESTURE"
            }
            OnCameraMoveStartedListener.REASON_API_ANIMATION -> {
                // [START_EXCLUDE silent]
                currPolylineOptions?.color(Color.RED)
                // [END_EXCLUDE]
                reasonText = "API_ANIMATION"
            }
            OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> {
                // [START_EXCLUDE silent]
                currPolylineOptions?.color(Color.GREEN)
                // [END_EXCLUDE]
                reasonText = "DEVELOPER_ANIMATION"
            }
        }
        Log.d(TAG, "onCameraMoveStarted($reasonText)")
        // [START_EXCLUDE silent]
        addCameraTargetToPath()
        // [END_EXCLUDE]
    }

    // [START_EXCLUDE silent]
    /**
     * Ensures that currPolyLine options is not null before accessing it
     *
     * @param stuffToDo the code to be executed if currPolylineOptions is not null
     */
    private fun checkPolylineThen(stuffToDo: () -> Unit) {
        if (currPolylineOptions != null) stuffToDo()
    }
    // [END_EXCLUDE]

    override fun onCameraMove() {
        Log.d(TAG, "onCameraMove")
        // [START_EXCLUDE silent]
        // When the camera is moving, add its target to the current path we'll draw on the map.
        checkPolylineThen { addCameraTargetToPath() }
        // [END_EXCLUDE]
    }

    override fun onCameraMoveCanceled() {
        // [START_EXCLUDE silent]
        // When the camera stops moving, add its target to the current path, and draw it on the map.
        checkPolylineThen {
            addCameraTargetToPath()
            map.addPolyline(currPolylineOptions!!)
        }

        isCanceled = true  // Set to clear the map when dragging starts again.
        currPolylineOptions = null
        // [END_EXCLUDE]
        Log.d(TAG, "onCameraMoveCancelled")
    }

    override fun onCameraIdle() {
        // [START_EXCLUDE silent]
        checkPolylineThen {
            addCameraTargetToPath()
            map.addPolyline(currPolylineOptions!!)
        }

        currPolylineOptions = null
        isCanceled = false  // Set to *not* clear the map when dragging starts again.
        // [END_EXCLUDE]
        Log.d(TAG, "onCameraIdle")
    }
    // [START_EXCLUDE silent]
    private fun addCameraTargetToPath() {
        currPolylineOptions?.add(map.cameraPosition.target)
    }
    // [END_EXCLUDE]
}
// [END maps_camera_events]