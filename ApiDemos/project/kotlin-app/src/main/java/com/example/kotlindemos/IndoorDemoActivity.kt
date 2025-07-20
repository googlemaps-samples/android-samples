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

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

/**
 * A demo activity showing how to use indoor.
 */
class IndoorDemoActivity : SamplesBaseActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var showLevelPicker = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.indoor_demo)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.614631, -122.385153), 18f))
    }

    /**
     * Called when the toggle level picker button is clicked.
     */
    fun onToggleLevelPicker(view: View?) {
        if (!::map.isInitialized) return

        showLevelPicker = !showLevelPicker
        map.uiSettings.isIndoorLevelPickerEnabled = showLevelPicker
    }

    /**
     * Called when the focused building info is clicked.
     */
    fun onFocusedBuildingInfo(view: View?) {
        if (!::map.isInitialized) return

        val building = map.focusedBuilding
        if (building != null) {
            val s = StringBuilder()
            for (level in building.levels) {
                s.append(level.name).append(" ")
            }
            if (building.isUnderground) {
                s.append("is underground")
            }
            setText(s.toString())
        } else {
            setText("No visible building")
        }
    }

    /**
     * Called when the focused level info is clicked.
     */
    fun onVisibleLevelInfo(view: View?) {
        if (!::map.isInitialized) return

        val building = map.focusedBuilding
        if (building != null) {
            val level = building.levels[building.activeLevelIndex]
            if (level != null) {
                setText(level.name)
            } else {
                setText("No visible level")
            }
        } else {
            setText("No visible building")
        }
    }

    /**
     * Called when the activate higher level is clicked.
     */
    fun onHigherLevel(view: View?) {
        if (!::map.isInitialized) return

        val building = map.focusedBuilding
        if (building != null) {
            val levels = building.levels
            if (!levels.isEmpty()) {
                val currentLevel = building.activeLevelIndex
                // The levels are in 'display order' from top to bottom,
                // i.e. higher levels in the building are lower numbered in the array.
                var newLevel = currentLevel - 1
                if (newLevel == -1) {
                    newLevel = levels.size - 1
                }
                val level = levels[newLevel]
                setText("Activating level " + level.name)
                level.activate()
            } else {
                setText("No levels in building")
            }
        } else {
            setText("No visible building")
        }
    }

    private fun setText(message: String) {
        val text = findViewById<TextView>(R.id.top_text)
        text.text = message
    }
}