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
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.IndoorBuilding
import com.google.maps.example.R

internal class EventsActivity : AppCompatActivity() {
    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
    }

    private fun mapViewDisableClickEvent() {
        // [START maps_android_events_disable_clicks_mapview]
        val mapView = findViewById<MapView>(R.id.mapView)
        mapView.isClickable = false
        // [END maps_android_events_disable_clicks_mapview]
    }

    private fun mapFragmentDisableClickEvent() {
        // [START maps_android_events_disable_clicks_mapfragment]
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        val view = mapFragment.view
        view?.isClickable = false
        // [END maps_android_events_disable_clicks_mapfragment]
    }

    private fun focusedBuilding() {
        // [START maps_android_events_active_level]
        map.focusedBuilding?.let { building: IndoorBuilding ->
            val activeLevelIndex = building.activeLevelIndex
            val activeLevel = building.levels[activeLevelIndex]
        }
        // [END maps_android_events_active_level]
    }
}
