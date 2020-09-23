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

package com.google.maps.example.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.example.R

internal class MapsObject : AppCompatActivity() {
    // [START maps_android_on_create_set_content_view]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
    }
    // [END maps_android_on_create_set_content_view]

    private fun mapFragment() {
        // [START maps_android_map_fragment]
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.my_container, mapFragment)
            .commit()
        // [END maps_android_map_fragment]
    }

    private fun mapType(map: GoogleMap) {
        // [START maps_android_map_type]
        // Sets the map type to be "hybrid"
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        // [END maps_android_map_type]
    }

    fun googleMapOptions() {
        // [START maps_android_google_map_options]
        val options = GoogleMapOptions()
        // [END maps_android_google_map_options]

        // [START maps_android_google_map_options_configure]
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
            .compassEnabled(false)
            .rotateGesturesEnabled(false)
            .tiltGesturesEnabled(false)
        // [END maps_android_google_map_options_configure]
    }
}

// [START maps_android_on_map_ready_callback]
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // [START_EXCLUDE]
    // [START maps_android_on_map_ready_add_marker]
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
    }
    // [END maps_android_on_map_ready_add_marker]

    private fun getMapAsync() {
        // [START maps_android_get_map_async]
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // [END maps_android_get_map_async]
    }
    // [END_EXCLUDE]
}
// [END maps_android_on_map_ready_callback]
