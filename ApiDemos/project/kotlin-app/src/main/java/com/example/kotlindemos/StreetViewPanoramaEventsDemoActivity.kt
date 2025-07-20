// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.kotlindemos

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.common_ui.R

import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanorama.*
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import com.google.android.gms.maps.model.StreetViewPanoramaLocation
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation

/**
 * This shows how to listen to some [StreetViewPanorama] events.
 */
class StreetViewPanoramaEventsDemoActivity : SamplesBaseActivity(),
    OnStreetViewPanoramaChangeListener, OnStreetViewPanoramaCameraChangeListener,
    OnStreetViewPanoramaClickListener, OnStreetViewPanoramaLongClickListener {

    private lateinit var streetViewPanorama: StreetViewPanorama
    private lateinit var panoChangeTimesTextView: TextView
    private lateinit var panoCameraChangeTextView: TextView
    private lateinit var panoClickTextView: TextView
    private lateinit var panoLongClickTextView: TextView

    private var panoChangeTimes = 0
    private var panoCameraChangeTimes = 0
    private var panoClickTimes = 0
    private var panoLongClickTimes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.street_view_panorama_events_demo)

        panoChangeTimesTextView = findViewById(R.id.change_pano)
        panoCameraChangeTextView = findViewById(R.id.change_camera)
        panoClickTextView = findViewById(R.id.click_pano)
        panoLongClickTextView = findViewById(R.id.long_click_pano)

        val streetViewPanoramaFragment =
            supportFragmentManager.findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment?
        streetViewPanoramaFragment?.getStreetViewPanoramaAsync { panorama: StreetViewPanorama ->
            streetViewPanorama = panorama
            streetViewPanorama.setOnStreetViewPanoramaChangeListener(
                this@StreetViewPanoramaEventsDemoActivity
            )
            streetViewPanorama.setOnStreetViewPanoramaCameraChangeListener(
                this@StreetViewPanoramaEventsDemoActivity
            )
            streetViewPanorama.setOnStreetViewPanoramaClickListener(
                this@StreetViewPanoramaEventsDemoActivity
            )
            streetViewPanorama.setOnStreetViewPanoramaLongClickListener(
                this@StreetViewPanoramaEventsDemoActivity
            )

            // Only set the panorama to SYDNEY on startup (when no panoramas have been
            // loaded which is when the savedInstanceState is null).
            savedInstanceState ?: streetViewPanorama.setPosition(SYDNEY)
        }
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    override fun onStreetViewPanoramaChange(location: StreetViewPanoramaLocation) {
        panoChangeTimesTextView.text = "Times panorama changed=" + ++panoChangeTimes
    }

    override fun onStreetViewPanoramaCameraChange(camera: StreetViewPanoramaCamera) {
        panoCameraChangeTextView.text = "Times camera changed=" + ++panoCameraChangeTimes
    }

    override fun onStreetViewPanoramaClick(orientation: StreetViewPanoramaOrientation) {
        val point = streetViewPanorama.orientationToPoint(orientation)
        point?.let {
            panoClickTimes++
            panoClickTextView.text = "Times clicked=$panoClickTimes : $point"
            streetViewPanorama.animateTo(
                StreetViewPanoramaCamera.Builder()
                    .orientation(orientation)
                    .zoom(streetViewPanorama.panoramaCamera.zoom)
                    .build(), 1000
            )
        }
    }

    override fun onStreetViewPanoramaLongClick(orientation: StreetViewPanoramaOrientation) {
        val point = streetViewPanorama.orientationToPoint(orientation)
        if (point != null) {
            panoLongClickTimes++
            panoLongClickTextView.text = "Times long clicked=$panoLongClickTimes : $point"
        }
    }

    companion object {
        // George St, Sydney
        private val SYDNEY = LatLng(-33.87365, 151.20689)
    }
}