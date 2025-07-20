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

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

/**
 * This shows how to listen to some [GoogleMap] events.
 */
// [START maps_android_sample_events]
class EventsDemoActivity : SamplesBaseActivity(), OnMapClickListener,
    OnMapLongClickListener, OnCameraIdleListener, OnMapReadyCallback {

    private lateinit var tapTextView: TextView
    private lateinit var cameraTextView: TextView
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events_demo)
        tapTextView = findViewById(R.id.tap_text)
        cameraTextView = findViewById(R.id.camera_text)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // return early if the map was not initialised properly
        map = googleMap
        map.setOnMapClickListener(this)
        map.setOnMapLongClickListener(this)
        map.setOnCameraIdleListener(this)
    }

    override fun onMapClick(point: LatLng) {
        tapTextView.text = "tapped, point=$point"
    }

    override fun onMapLongClick(point: LatLng) {
        tapTextView.text = "long pressed, point=$point"
    }

    override fun onCameraIdle() {
        if (!::map.isInitialized) return
        cameraTextView.text = map.cameraPosition.toString()
    }
}
// [END maps_android_sample_events]