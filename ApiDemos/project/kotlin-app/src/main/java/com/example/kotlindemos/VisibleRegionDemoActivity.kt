/*
 * Copyright 2026 Google LLC
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

import com.example.common_ui.catalog.Sample
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.R

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.view.animation.OvershootInterpolator
import com.example.common_ui.databinding.VisibleRegionDemoBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

import androidx.appcompat.widget.PopupMenu
import java.util.Locale

/**
 * This shows how to use setPadding to allow overlays that obscure part of the map without
 * obscuring the map UI or copyright notices.
 */
// [START maps_android_sample_visible_region]
@Sample(
    id = "com.example.kotlindemos.VisibleRegionDemoActivity",
    title = "Visible Region & Projection",
    description = "Querying current viewport bounding coordinates via GoogleMap.projection.",
    category = "Camera Controls",
    complexity = Complexity.SIMPLE,
    tags = ["#camera", "#projection", "#visibleregion", "#latlngbounds"],
    apiCalls = [
        "GoogleMap.setPadding(int, int, int, int)",
        "GoogleMap.moveCamera(CameraUpdate)",
        "GoogleMap.cameraPosition",
        "GoogleMap.setOnCameraIdleListener(OnCameraIdleListener)"
    ],
    purpose = "Demonstrates reading GoogleMap.projection.visibleRegion and calculating viewport bounds dynamically.",
    successCriteria = "Bounding coordinates update live in the UI as the camera pans and zooms.",
    failureIndicators = "Projection returns null or stale LatLng bounds after camera idle.",
    framework = Framework.KOTLIN_VIEWS
)
class VisibleRegionDemoActivity :
    SamplesBaseActivity(),
    OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    private val operaHouseLatLng = LatLng(-33.85704, 151.21522)
    private val sfoLatLng = LatLng(37.614631, -122.385153)
    private val australiaBounds = LatLngBounds(LatLng(-44.0, 113.0),
        LatLng(-10.0, 154.0))

    private lateinit var map: GoogleMap
    private lateinit var binding: VisibleRegionDemoBinding

    /** Keep track of current values for padding, so we can animate from them.  */
    private var currentLeft = 0
    private var currentTop = 0
    private var currentRight = 0
    private var currentBottom = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VisibleRegionDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraActionsButton.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.visible_region_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_action_no_padding -> { setNoPadding(); true }
                    R.id.menu_action_more_padding -> { setMorePadding(); true }
                    R.id.menu_action_opera_house -> { moveToOperaHouse(); true }
                    R.id.menu_action_sfo -> { moveToSFO(); true }
                    R.id.menu_action_australia -> { moveToAUS(); true }
                    else -> false
                }
            }
            popup.show()
        }

        binding.vrNormalButton.setOnClickListener { setNoPadding() }
        binding.vrMorePaddedButton.setOnClickListener { setMorePadding() }
        binding.vrSohButton.setOnClickListener { moveToOperaHouse() }
        binding.vrSfoButton.setOnClickListener { moveToSFO() }
        binding.vrAusButton.setOnClickListener { moveToAUS() }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)
        
        applyInsets(binding.mapContainer)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // exit early if the map was not initialised properly
        map = googleMap ?: return

        map.apply {
            setPadding(currentLeft, currentTop, currentRight, currentBottom)
            moveCamera(CameraUpdateFactory.newLatLngZoom(sfoLatLng, 18f))
            addMarker(MarkerOptions().position(operaHouseLatLng).title("Sydney Opera House"))
            setOnCameraIdleListener {
                updateCameraDisplay()
            }
        }
        updateCameraDisplay()
    }

    private fun updateCameraDisplay() {
        if (!::map.isInitialized) return
        val pos = map.cameraPosition
        binding.cameraTargetText.text = String.format(
            Locale.US,
            "Lat: %.4f°, Lng: %.4f°",
            pos.target.latitude,
            pos.target.longitude
        )
        binding.cameraDetailsText.text = String.format(
            Locale.US,
            "Zoom: %.1fx  •  Tilt: %.1f°  •  Bearing: %.1f°",
            pos.zoom,
            pos.tilt,
            pos.bearing
        )
    }

    private fun setNoPadding() {
        if (!::map.isInitialized) return
        animatePadding(0, 0, 0, 0)
    }

    private fun setMorePadding() {
        if (!::map.isInitialized) return
        val mapView: View? = supportFragmentManager.findFragmentById(R.id.map)?.view
        animatePadding(0, 0, (mapView?.width ?: 0) / 3,
            (mapView?.height ?: 0)/ 4)
    }

    private fun moveToOperaHouse() {
        if (!::map.isInitialized) return
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(operaHouseLatLng, 16f))
    }

    private fun moveToSFO() {
        if (!::map.isInitialized) return
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sfoLatLng, 18f))
    }

    private fun moveToAUS() {
        if (!::map.isInitialized) return
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0))
    }

    // this function smoothly changes the amount of padding over a period of time
    private fun animatePadding(toLeft: Int, toTop: Int, toRight: Int, toBottom: Int) {

        val handler = Handler(Looper.getMainLooper())
        val start = SystemClock.uptimeMillis()
        val duration: Long = 1000

        val interpolator = OvershootInterpolator()

        val startLeft: Int = currentLeft
        val startTop: Int = currentTop
        val startRight: Int = currentRight
        val startBottom: Int = currentBottom

        currentLeft = toLeft
        currentTop = toTop
        currentRight = toRight
        currentBottom = toBottom

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(elapsed.toFloat() / duration)

                val leftDiff = ((toLeft - startLeft) * t).toInt()
                val topDiff = ((toTop - startTop) * t).toInt()
                val rightDiff = ((toRight - startRight) * t).toInt()
                val bottomDiff = ((toBottom - startBottom) * t).toInt()

                val left = startLeft + leftDiff
                val top = startTop + topDiff
                val right = startRight + rightDiff
                val bottom = startBottom + bottomDiff

                map.setPadding(left, top, right, bottom)

                // Post again 16ms later.
                if (elapsed < duration) { handler.postDelayed(this, 16) }
            }
        })
    }
}
// [END maps_android_sample_visible_region]
