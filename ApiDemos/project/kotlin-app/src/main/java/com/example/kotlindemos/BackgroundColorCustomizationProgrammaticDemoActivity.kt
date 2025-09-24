// Copyright 2021 Google LLC
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

import android.graphics.Color

import com.google.android.gms.maps.OnMapReadyCallback
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.GoogleMap
import android.widget.CheckBox
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.example.common_ui.R

/**
 * This shows how to to instantiate a SupportMapFragment programmatically with a custom background
 * color applied to the map, and add a marker on the map.
 */
class BackgroundColorCustomizationProgrammaticDemoActivity : SamplesBaseActivity(),
                                                             OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.background_color_customization_programmatic_demo)

        // It isn't possible to set a fragment's id programmatically so we set a tag instead and
        // search for it using that.
        var mapFragment =
            supportFragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG) as? SupportMapFragment

        // We only create a fragment if it doesn't already exist.
        if (mapFragment == null) {
            // To programmatically add the map, we first create a SupportMapFragment, with the
            // GoogleMapOptions to set the custom background color displayed before the map tiles load.
            mapFragment = SupportMapFragment.newInstance(
                GoogleMapOptions().backgroundColor(
                    LIGHT_PINK_COLOR
                )
            )

            // Then we add the fragment using a FragmentTransaction.
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.map, mapFragment, MAP_FRAGMENT_TAG)
            fragmentTransaction.commit()
        } else {
            mapFragment.getMapAsync(this)
        }
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    override fun onMapReady(map: GoogleMap) {
        map.mapType = GoogleMap.MAP_TYPE_NONE
        val mapTypeToggleCheckbox = findViewById<CheckBox>(R.id.map_type_toggle)
        mapTypeToggleCheckbox.setOnCheckedChangeListener { _, isChecked ->
            map.mapType = if (isChecked) GoogleMap.MAP_TYPE_NORMAL else GoogleMap.MAP_TYPE_NONE
        }
        map.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Marker"))
    }

    companion object {
        private const val MAP_FRAGMENT_TAG = "map"
        private val LIGHT_PINK_COLOR = Color.argb(153, 240, 178, 221)
    }
}