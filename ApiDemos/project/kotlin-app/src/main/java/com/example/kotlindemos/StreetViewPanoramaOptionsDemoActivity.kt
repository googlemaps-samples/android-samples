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
import android.widget.CheckBox
import android.widget.Toast
import com.example.common_ui.R

import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewSource


/**
 * This shows how to create an activity with static streetview (all options have been switched off)
 */
class StreetViewPanoramaOptionsDemoActivity : SamplesBaseActivity() {
    private var streetViewPanorama: StreetViewPanorama? = null
    private lateinit var streetNameCheckbox: CheckBox
    private lateinit var navigationCheckbox: CheckBox
    private lateinit var zoomCheckbox: CheckBox
    private lateinit var panningCheckbox: CheckBox
    private lateinit var outdoorCheckbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.street_view_panorama_options_demo)
        streetNameCheckbox = findViewById(R.id.streetnames)
        navigationCheckbox = findViewById(R.id.navigation)
        zoomCheckbox = findViewById(R.id.zoom)
        panningCheckbox = findViewById(R.id.panning)
        outdoorCheckbox = findViewById(R.id.outdoor)
        val streetViewPanoramaFragment =
            supportFragmentManager.findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment?
        streetViewPanoramaFragment?.getStreetViewPanoramaAsync { panorama: StreetViewPanorama ->
            streetViewPanorama = panorama
            panorama.isStreetNamesEnabled = streetNameCheckbox.isChecked
            panorama.isUserNavigationEnabled = navigationCheckbox.isChecked
            panorama.isZoomGesturesEnabled = zoomCheckbox.isChecked
            panorama.isPanningGesturesEnabled = panningCheckbox.isChecked

            // Only set the panorama to SAN_FRAN on startup (when no panoramas have been
            // loaded which is when the savedInstanceState is null).
            savedInstanceState ?: setPosition()
        }
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    private fun setPosition() {
        streetViewPanorama?.setPosition(
            SAN_FRAN,
            RADIUS,
            if (outdoorCheckbox.isChecked) StreetViewSource.OUTDOOR else StreetViewSource.DEFAULT
        )
    }

    private fun checkReady(): Boolean {
        if (streetViewPanorama == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun onStreetNamesToggled(view: View?) {
        if (!checkReady()) {
            return
        }
        streetViewPanorama?.isStreetNamesEnabled = streetNameCheckbox.isChecked
    }

    fun onNavigationToggled(view: View?) {
        if (!checkReady()) {
            return
        }
        streetViewPanorama?.isUserNavigationEnabled = navigationCheckbox.isChecked
    }

    fun onZoomToggled(view: View?) {
        if (!checkReady()) {
            return
        }
        streetViewPanorama?.isZoomGesturesEnabled = zoomCheckbox.isChecked
    }

    fun onPanningToggled(view: View?) {
        if (!checkReady()) {
            return
        }
        streetViewPanorama?.isPanningGesturesEnabled = panningCheckbox.isChecked
    }

    fun onOutdoorToggled(view: View?) {
        if (!checkReady()) {
            return
        }
        setPosition()
    }

    companion object {
        // Cole St, San Fran
        private val SAN_FRAN = LatLng(37.765927, -122.449972)
        private const val RADIUS = 20
    }
}