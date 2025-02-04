/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.wearosmap.kt

import androidx.appcompat.app.AppCompatActivity
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.maps.SupportMapFragment
import android.os.Bundle
import android.util.Log
import com.example.wearosmap.R

/**
 * Sample that shows how to set up a basic Google Map on Wear OS.
 */
// [START maps_wear_os_ambient_mode_support]
class AmbientActivity : AppCompatActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private lateinit var mapFragment: SupportMapFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout. It only contains a SupportMapFragment and a DismissOverlay.
        setContentView(R.layout.activity_main)

        // Enable ambient support, so the map remains visible in simplified, low-color display
        // when the user is no longer actively using the app but the app is still visible on the
        // watch face.
        val controller = AmbientModeSupport.attach(this)
        Log.d(AmbientActivity::class.java.simpleName, "Is ambient enabled: " + controller.isAmbient)

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return object : AmbientModeSupport.AmbientCallback() {
            /**
             * Starts ambient mode on the map.
             * The API swaps to a non-interactive and low-color rendering of the map when the user is no
             * longer actively using the app.
             */
            override fun onEnterAmbient(ambientDetails: Bundle) {
                super.onEnterAmbient(ambientDetails)
                mapFragment.onEnterAmbient(ambientDetails)
            }

            /**
             * Exits ambient mode on the map.
             * The API swaps to the normal rendering of the map when the user starts actively using the app.
             */
            override fun onExitAmbient() {
                super.onExitAmbient()
                mapFragment.onExitAmbient()
            }
        }
    }
}
// [END maps_wear_os_ambient_mode_support]
