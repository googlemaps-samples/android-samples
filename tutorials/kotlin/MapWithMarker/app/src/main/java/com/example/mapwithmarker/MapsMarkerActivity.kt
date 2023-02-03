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
import android.util.Log
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
// [START maps_marker_on_map_ready]
class MapsMarkerActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mapFragment : SupportMapFragment? = null

    // [START_EXCLUDE]
    // [START maps_marker_get_map_async]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps)

        // Change this to Latest vs Legacy and see the difference
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) { renderer -> Log.d("gmap", "rendererSelected:${renderer.name}") }

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
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
                .title("Sydney")
        )
        val melbourne = LatLng(-37.869403, 145.149128)
        googleMap.addMarker(
            MarkerOptions()
                .position(melbourne)
                .title("Melbourne")
        )
        googleMap.setPadding(256, 512, 256, 512)
        val bounds = LatLngBounds.builder().include(sydney).include(melbourne).build()
        mapFragment?.view?.viewTreeObserver?.addOnPreDrawListener(object: OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mapFragment?.view?.viewTreeObserver?.removeOnPreDrawListener(this)
                animateCamera(googleMap, bounds)
                return true
            }
        })

    }
    // [END maps_marker_on_map_ready_add_marker]
    private fun animateCamera(googleMap: GoogleMap, bounds: LatLngBounds) {
        Log.d("gmap", "width:${mapFragment?.view?.width}")
        Log.d("gmap", "width:${mapFragment?.view?.height}")
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
            mapFragment?.view?.width ?: 0, mapFragment?.view?.height ?: 0, 0))
    }
}
// [END maps_marker_on_map_ready]
