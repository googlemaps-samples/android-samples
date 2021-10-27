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

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.OnMapReadyCallback
import android.os.Bundle
import android.view.View
import com.example.kotlindemos.R
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import android.widget.CheckBox
import android.widget.CompoundButton
import com.google.android.gms.common.internal.Preconditions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng

/**
 * This shows how to create a simple activity with a custom background color appiled to the map, and
 * add a marker on the map.
 */
class BackgroundColorCustomizationDemoActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.background_color_customization_demo)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     */
    override fun onMapReady(map: GoogleMap) {
        map.mapType = GoogleMap.MAP_TYPE_NONE

        val mapTypeToggleCheckbox = findViewById<CheckBox>(R.id.map_type_toggle)
        mapTypeToggleCheckbox.setOnCheckedChangeListener { _, isChecked ->
            map.mapType = if (isChecked) GoogleMap.MAP_TYPE_NORMAL else GoogleMap.MAP_TYPE_NONE
        }
        map.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Marker"))
    }
}