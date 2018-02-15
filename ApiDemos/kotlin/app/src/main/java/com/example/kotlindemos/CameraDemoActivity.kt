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
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Toast
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
class CameraDemoActivity :
        AppCompatActivity(),
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback {

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


    private lateinit var map: GoogleMap

    private lateinit var animateToggle: CompoundButton
    private lateinit var customDurationToggle: CompoundButton
    private lateinit var customDurationBar: SeekBar
    private var currPolylineOptions: PolylineOptions? = null
    private var isCanceled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_demo)

        animateToggle = findViewById(R.id.animate)
        customDurationToggle = findViewById(R.id.duration_toggle)
        customDurationBar = findViewById(R.id.duration_bar)

        updateEnabledState()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        updateEnabledState()
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        // return early if the map was not initialised properly
        map = googleMap ?: return

        with(googleMap) {
            setOnCameraIdleListener(this@CameraDemoActivity)
            setOnCameraMoveStartedListener(this@CameraDemoActivity)
            setOnCameraMoveListener(this@CameraDemoActivity)
            setOnCameraMoveCanceledListener(this@CameraDemoActivity)

            // We will provide our own zoom controls.
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isMyLocationButtonEnabled = true

            // Show Sydney
            moveCamera(CameraUpdateFactory.newLatLngZoom(sydneyLatLng, 10f))
        }
    }

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

    override fun onCameraMoveStarted(reason: Int) {
        if (!isCanceled) map.clear()


        var reasonText = "UNKNOWN_REASON"
        currPolylineOptions = PolylineOptions().width(5f)
        when (reason) {
            OnCameraMoveStartedListener.REASON_GESTURE -> {
                currPolylineOptions?.color(Color.BLUE)
                reasonText = "GESTURE"
            }
            OnCameraMoveStartedListener.REASON_API_ANIMATION -> {
                currPolylineOptions?.color(Color.RED)
                reasonText = "API_ANIMATION"
            }
            OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> {
                currPolylineOptions?.color(Color.GREEN)
                reasonText = "DEVELOPER_ANIMATION"
            }
        }
        Log.i(TAG, "onCameraMoveStarted($reasonText)")
        addCameraTargetToPath()
    }

    /**
     * Ensures that currPolyLine options is not null before accessing it
     *
     * @param stuffToDo the code to be executed if currPolylineOptions is not null
     */
    private fun checkPolylineThen(stuffToDo: () -> Unit) {
        if (currPolylineOptions != null) stuffToDo()
    }


    override fun onCameraMove() {
        Log.i(TAG, "onCameraMove")
        // When the camera is moving, add its target to the current path we'll draw on the map.
        checkPolylineThen { addCameraTargetToPath() }
    }

    override fun onCameraMoveCanceled() {
        // When the camera stops moving, add its target to the current path, and draw it on the map.
        checkPolylineThen {
            addCameraTargetToPath()
            map.addPolyline(currPolylineOptions)
        }

        isCanceled = true  // Set to clear the map when dragging starts again.
        currPolylineOptions = null
        Log.i(TAG, "onCameraMoveCancelled")
    }

    override fun onCameraIdle() {
        checkPolylineThen {
            addCameraTargetToPath()
            map.addPolyline(currPolylineOptions)
        }

        currPolylineOptions = null
        isCanceled = false  // Set to *not* clear the map when dragging starts again.
        Log.i(TAG, "onCameraIdle")
    }

    private fun addCameraTargetToPath() {
        currPolylineOptions?.add(map.cameraPosition.target)
    }
}