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
import com.example.common_ui.R

import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.*

/**
 * This shows how to create a simple activity with streetview and a map
 */
class SplitStreetViewPanoramaAndMapDemoActivity : SamplesBaseActivity(),
    OnMarkerDragListener, OnStreetViewPanoramaChangeListener {
    var streetViewPanorama: StreetViewPanorama? = null
    var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.split_street_view_panorama_and_map_demo)
        val markerPosition = savedInstanceState?.getParcelable(MARKER_POSITION_KEY) ?: SYDNEY

        val streetViewPanoramaFragment =
            supportFragmentManager.findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment?
        streetViewPanoramaFragment?.getStreetViewPanoramaAsync { panorama ->
            streetViewPanorama = panorama
            streetViewPanorama?.setOnStreetViewPanoramaChangeListener(
                this@SplitStreetViewPanoramaAndMapDemoActivity
            )
            // Only need to set the position once as the streetview fragment will maintain
            // its state.
            savedInstanceState ?: streetViewPanorama?.setPosition(SYDNEY)
        }
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { map ->
            map.setOnMarkerDragListener(this@SplitStreetViewPanoramaAndMapDemoActivity)
            // Creates a draggable marker. Long press to drag.
            marker = map.addMarker(
                MarkerOptions()
                    .position(markerPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pegman))
                    .draggable(true)
            )
        }
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            MARKER_POSITION_KEY,
            marker?.position
        )
    }

    override fun onStreetViewPanoramaChange(location: StreetViewPanoramaLocation) {
        marker?.position = location.position
    }

    override fun onMarkerDragStart(marker: Marker) {}
    override fun onMarkerDragEnd(marker: Marker) {
        streetViewPanorama?.setPosition(marker.position, 150)
    }

    override fun onMarkerDrag(marker: Marker) {}

    companion object {
        private const val MARKER_POSITION_KEY = "MarkerPosition"

        // George St, Sydney
        private val SYDNEY = LatLng(-33.87365, 151.20689)
    }
}