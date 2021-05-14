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

package com.example.mapwithmarker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
// [START maps_marker_on_map_ready]
class MapsMarkerActivity : AppCompatActivity(), OnMapReadyCallback {

    // [START_EXCLUDE]
    // [START maps_marker_get_map_async]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }
    // [END maps_marker_get_map_async]
    // [END_EXCLUDE]

    // [START maps_marker_on_map_ready_add_marker]
    override fun onMapReady(googleMap: GoogleMap) {
      val sydney = LatLng(-33.852, 151.211)
      googleMap.addMarker(
        MarkerOptions()
          .position(sydney)
          .title("Marker in Sydney")
      )
      // [START_EXCLUDE silent]
      googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
      // [END_EXCLUDE]
    }
    // [END maps_marker_on_map_ready_add_marker]
}
// [END maps_marker_on_map_ready]
