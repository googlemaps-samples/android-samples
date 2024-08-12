// Copyright 2024 Google LLC
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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Feature
import com.google.android.gms.maps.model.FeatureLayer
import com.google.android.gms.maps.model.FeatureLayerOptions
import com.google.android.gms.maps.model.FeatureStyle
import com.google.android.gms.maps.model.FeatureType
import com.google.android.gms.maps.model.MapCapabilities
import com.google.android.gms.maps.model.PlaceFeature

class DataDrivenBoundariesActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var map: GoogleMap

    private var localityLayer: FeatureLayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_driven_styling_demo)

    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        val capabilities: MapCapabilities = map.mapCapabilities
        println("Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable)

        // Get the LOCALITY feature layer.
        localityLayer = googleMap.getFeatureLayer(FeatureLayerOptions.Builder()
            .featureType(FeatureType.LOCALITY)
            .build())

        // Apply style factory function to LOCALITY layer.
        styleLocalityLayer()
    }


    private fun styleLocalityLayer() {

        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Check if the feature is an instance of PlaceFeature,
            // which contains a place ID.
            if (feature is PlaceFeature) {
                val placeFeature: PlaceFeature = feature as PlaceFeature

                // Determine if the place ID is for Hana, HI.
                if (placeFeature.getPlaceId().equals("ChIJ0zQtYiWsVHkRk8lRoB1RNPo")) {
                    // Use FeatureStyle.Builder to configure the FeatureStyle object
                    // returned by the style factory function.
                    return@StyleFactory FeatureStyle.Builder()
                        // Define a style with purple fill at 50% opacity and
                        // solid purple border.
                        .fillColor(0x80810FCB.toInt())
                        .strokeColor(0xFF810FCB.toInt())
                        .build()
                }
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the feature layer.
        localityLayer?.setFeatureStyle(styleFactory)
    }
}