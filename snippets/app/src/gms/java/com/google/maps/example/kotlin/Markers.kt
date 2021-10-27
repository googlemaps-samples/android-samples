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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.example.R

internal class Markers : OnMapReadyCallback {
    // [START maps_android_markers_add_a_marker]
    override fun onMapReady(googleMap: GoogleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        val sydney = LatLng(-33.852, 151.211)
        googleMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    // [END maps_android_markers_add_a_marker]

    private fun markerDraggable(map: GoogleMap) {
        // [START maps_android_markers_draggable]
        val perthLocation = LatLng(-31.90, 115.86)
        val perth = map.addMarker(
            MarkerOptions()
                .position(perthLocation)
                .draggable(true)
        )
        // [END maps_android_markers_draggable]
    }

    private fun defaultIcon(map: GoogleMap) {
        // [START maps_android_markers_default_icon]
        val melbourneLocation = LatLng(-37.813, 144.962)
        val melbourne = map.addMarker(
            MarkerOptions()
                .position(melbourneLocation)
        )
        // [END maps_android_markers_default_icon]
    }

    private fun customMarkerColor(map: GoogleMap) {
        // [START maps_android_markers_custom_marker_color]
        val melbourneLocation = LatLng(-37.813, 144.962)
        val melbourne = map.addMarker(
            MarkerOptions()
                .position(melbourneLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
        // [END maps_android_markers_custom_marker_color]
    }

    private fun markerOpacity(map: GoogleMap) {
        // [START maps_android_markers_opacity]
        val melbourneLocation = LatLng(-37.813, 144.962)
        val melbourne = map.addMarker(
            MarkerOptions()
                .position(melbourneLocation)
                .alpha(0.7f)
        )
        // [END maps_android_markers_opacity]
    }

    private fun markerImage(map: GoogleMap) {
        // [START maps_android_markers_image]
        val melbourneLocation = LatLng(-37.813, 144.962)
        val melbourne = map.addMarker(
            MarkerOptions()
                .position(melbourneLocation)
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
        )
        // [END maps_android_markers_image]
    }

    private fun markerFlatten(map: GoogleMap) {
        // [START maps_android_markers_flatten]
        val perthLocation = LatLng(-31.90, 115.86)
        val perth = map.addMarker(
            MarkerOptions()
                .position(perthLocation)
                .flat(true)
        )
        // [END maps_android_markers_flatten]
    }

    private fun markerRotate(map: GoogleMap) {
        // [START maps_android_markers_rotate]
        val perthLocation = LatLng(-31.90, 115.86)
        val perth = map.addMarker(
            MarkerOptions()
                .position(perthLocation)
                .anchor(0.5f, 0.5f)
                .rotation(90.0f)
        )
        // [END maps_android_markers_rotate]
    }

    private fun markerZIndex(map: GoogleMap) {
        // [START maps_android_markers_z_index]
        map.addMarker(
            MarkerOptions()
                .position(LatLng(10.0, 10.0))
                .title("Marker z1")
                .zIndex(1.0f)
        )
        // [END maps_android_markers_z_index]
    }
}

// [START maps_android_markers_tag_sample]
/**
 * A demo class that stores and retrieves data objects with each marker.
 */
class MarkerDemoActivity : AppCompatActivity(),
    OnMarkerClickListener, OnMapReadyCallback {
    private val PERTH = LatLng(-31.952854, 115.857342)
    private val SYDNEY = LatLng(-33.87365, 151.20689)
    private val BRISBANE = LatLng(-27.47093, 153.0235)

    private var markerPerth: Marker? = null
    private var markerSydney: Marker? = null
    private var markerBrisbane: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markers)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    /** Called when the map is ready.  */
    override fun onMapReady(map: GoogleMap) {
        // Add some markers to the map, and add a data object to each marker.
        markerPerth = map.addMarker(
            MarkerOptions()
                .position(PERTH)
                .title("Perth")
        )
        markerPerth?.tag = 0
        markerSydney = map.addMarker(
            MarkerOptions()
                .position(SYDNEY)
                .title("Sydney")
        )
        markerSydney?.tag = 0
        markerBrisbane = map.addMarker(
            MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane")
        )
        markerBrisbane?.tag = 0

        // Set a listener for marker click.
        map.setOnMarkerClickListener(this)
    }

    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        val clickCount = marker.tag as? Int

        // Check if a click count was set, then display the click count.
        clickCount?.let {
            val newClickCount = it + 1
            marker.tag = newClickCount
            Toast.makeText(
                this,
                "${marker.title} has been clicked $newClickCount times.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }
}
// [END maps_android_markers_tag_sample]
