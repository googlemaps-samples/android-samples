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
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import com.google.android.gms.maps.model.StreetViewPanoramaLink
import com.google.android.gms.maps.model.StreetViewSource
import com.google.maps.example.R

// [START maps_street_view_on_street_view_panorama_ready]
class StreetViewActivity : AppCompatActivity(), OnStreetViewPanoramaReadyCallback {
    // [START_EXCLUDE]
    // [START maps_street_view_on_create]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_view)
        // [START maps_street_view_find_fragment]
        val streetViewPanoramaFragment =
            supportFragmentManager
                .findFragmentById(R.id.street_view_panorama) as SupportStreetViewPanoramaFragment
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this)
        // [END maps_street_view_find_fragment]
    }
    // [END maps_street_view_on_create]

    // [START maps_street_view_on_street_view_panorama_ready_callback]
    override fun onStreetViewPanoramaReady(streetViewPanorama: StreetViewPanorama) {
        val sanFrancisco = LatLng(37.754130, -122.447129)
        streetViewPanorama.setPosition(sanFrancisco)
    }
    // [END maps_street_view_on_street_view_panorama_ready_callback]

    private fun newView() {
        // [START maps_street_view_new_panorama_view]
        val sanFrancisco = LatLng(37.754130, -122.447129)
        val view = StreetViewPanoramaView(
            this,
            StreetViewPanoramaOptions().position(sanFrancisco)
        )
        // [END maps_street_view_new_panorama_view]
    }

    private fun setLocationOfThePanorama(streetViewPanorama: StreetViewPanorama) {
        // [START maps_street_view_panorama_set_location]
        val sanFrancisco = LatLng(37.754130, -122.447129)

        // Set position with LatLng only.
        streetViewPanorama.setPosition(sanFrancisco)

        // Set position with LatLng and radius.
        streetViewPanorama.setPosition(sanFrancisco, 20)

        // Set position with LatLng and source.
        streetViewPanorama.setPosition(sanFrancisco, StreetViewSource.OUTDOOR)

        // Set position with LaLng, radius and source.
        streetViewPanorama.setPosition(sanFrancisco, 20, StreetViewSource.OUTDOOR)
        // [END maps_street_view_panorama_set_location]

        // [START maps_street_view_panorama_set_location_2]
        streetViewPanorama.location.links.firstOrNull()?.let { link: StreetViewPanoramaLink ->
            streetViewPanorama.setPosition(link.panoId)
        }
        // [END maps_street_view_panorama_set_location_2]
    }

    private fun zoom(streetViewPanorama: StreetViewPanorama) {
        // [START maps_street_view_panorama_zoom]
        val zoomBy = 0.5f
        val camera = StreetViewPanoramaCamera.Builder()
            .zoom(streetViewPanorama.panoramaCamera.zoom + zoomBy)
            .tilt(streetViewPanorama.panoramaCamera.tilt)
            .bearing(streetViewPanorama.panoramaCamera.bearing)
            .build()
        // [END maps_street_view_panorama_zoom]
    }

    private fun pan(streetViewPanorama: StreetViewPanorama) {
        // [START maps_street_view_panorama_pan]
        val panBy = 30f
        val camera = StreetViewPanoramaCamera.Builder()
            .zoom(streetViewPanorama.panoramaCamera.zoom)
            .tilt(streetViewPanorama.panoramaCamera.tilt)
            .bearing(streetViewPanorama.panoramaCamera.bearing - panBy)
            .build()
        // [END maps_street_view_panorama_pan]
    }

    private fun tilt(streetViewPanorama: StreetViewPanorama) {
        // [START maps_street_view_panorama_tilt]
        var tilt = streetViewPanorama.panoramaCamera.tilt + 30
        tilt = if (tilt > 90) 90f else tilt
        val previous = streetViewPanorama.panoramaCamera
        val camera = StreetViewPanoramaCamera.Builder(previous)
            .tilt(tilt)
            .build()
        // [END maps_street_view_panorama_tilt]
    }

    private fun animate(streetViewPanorama: StreetViewPanorama) {
        // [START maps_street_view_panorama_animate]
        // Keeping the zoom and tilt. Animate bearing by 60 degrees in 1000 milliseconds.
        val duration: Long = 1000
        val camera = StreetViewPanoramaCamera.Builder()
            .zoom(streetViewPanorama.panoramaCamera.zoom)
            .tilt(streetViewPanorama.panoramaCamera.tilt)
            .bearing(streetViewPanorama.panoramaCamera.bearing - 60)
            .build()
        streetViewPanorama.animateTo(camera, duration)
        // [END maps_street_view_panorama_animate]
    }
    // [END_EXCLUDE]
}
// [END maps_street_view_on_street_view_panorama_ready]
