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

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.TextView
import com.example.common_ui.R


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

/**
 * This shows how to use setPadding to allow overlays that obscure part of the map without
 * obscuring the map UI or copyright notices.
 */
class VisibleRegionDemoActivity :
        SamplesBaseActivity(),
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    private val operaHouseLatLng = LatLng(-33.85704, 151.21522)
    private val sfoLatLng = LatLng(37.614631, -122.385153)
    private val australiaBounds = LatLngBounds(LatLng(-44.0, 113.0),
            LatLng(-10.0, 154.0))

    private lateinit var map: GoogleMap

    private lateinit var messageView: TextView
    private lateinit var normalButton: Button
    private lateinit var morePaddedButton: Button
    private lateinit var operaHouseButton: Button
    private lateinit var sfoButton: Button
    private lateinit var australiaButton: Button

    /** Keep track of current values for padding, so we can animate from them.  */
    private var currentLeft = 150
    private var currentTop = 0
    private var currentRight = 0
    private var currentBottom = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.visible_region_demo)
        messageView = findViewById(R.id.message_text)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)

        normalButton = findViewById(R.id.vr_normal_button)
        morePaddedButton = findViewById(R.id.vr_more_padded_button)
        operaHouseButton = findViewById(R.id.vr_soh_button)
        sfoButton = findViewById(R.id.vr_sfo_button)
        australiaButton = findViewById(R.id.vr_aus_button)
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        // exit early if the map was not initialised properly
        map = googleMap ?: return

        map.apply{
            // Set padding for the current camera view
            setPadding(currentLeft, currentTop, currentRight, currentBottom)
            // Move to a place with indoor (sfoLatLng airport).
            moveCamera(CameraUpdateFactory.newLatLngZoom(sfoLatLng, 18f))
            // Add a marker to the Opera House.
            addMarker(MarkerOptions().position(operaHouseLatLng).title("Sydney Opera House"))
            // Add a camera idle listener that displays the current camera position in a TextView
            setOnCameraIdleListener {
                messageView.text = getString(
                    R.string.camera_change_message,
                        this@VisibleRegionDemoActivity.map.cameraPosition)
            }
        }

        normalButton.setOnClickListener {
            animatePadding(150, 0, 0, 0)
        }

        // listener for when the 'more' padding button is clicked
        // increases the amount of padding along the right and bottom of the map
        morePaddedButton.setOnClickListener {
            // get the view that contains the map
            val mapView: View? = supportFragmentManager.findFragmentById(R.id.map)?.view
            animatePadding(150, 0, (mapView?.width ?: 0) / 3,
                    (mapView?.height ?: 0)/ 4)
        }

        operaHouseButton.setOnClickListener {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(operaHouseLatLng, 16f))
        }

        sfoButton.setOnClickListener {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sfoLatLng, 18f))
        }

        australiaButton.setOnClickListener {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0))
        }
    }

    // this function smoothly changes the amount of padding over a period of time
    private fun animatePadding(toLeft: Int, toTop: Int, toRight: Int, toBottom: Int) {

        val handler = Handler()
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
