/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.common_ui.R

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapColorScheme


class MapColorSchemeActivity :
    SamplesBaseActivity(), OnMapReadyCallback {

    /** This is ok to be lateinit as it is initialised in onMapReady */
    private lateinit var map: GoogleMap


    private lateinit var buttonLight: Button
    private lateinit var buttonDark: Button
    private lateinit var buttonFollowSystem: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_color_scheme_demo)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        buttonLight = findViewById(R.id.map_color_light_mode)
        buttonDark = findViewById(R.id.map_color_dark_mode)
        buttonFollowSystem = findViewById(R.id.map_color_follow_system_mode)
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapColorScheme = MapColorScheme.DARK

        buttonLight.setOnClickListener {
            map.mapColorScheme = MapColorScheme.LIGHT
        }

        buttonDark.setOnClickListener {
            map.mapColorScheme = MapColorScheme.DARK
        }

        buttonFollowSystem.setOnClickListener {
            map.mapColorScheme = MapColorScheme.FOLLOW_SYSTEM
        }
    }
}