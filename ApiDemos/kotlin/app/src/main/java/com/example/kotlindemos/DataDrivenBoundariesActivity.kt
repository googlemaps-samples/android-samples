// Copyright 2025 Google LLC
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
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Feature
import com.google.android.gms.maps.model.FeatureClickEvent
import com.google.android.gms.maps.model.FeatureLayer
import com.google.android.gms.maps.model.FeatureLayerOptions
import com.google.android.gms.maps.model.FeatureStyle
import com.google.android.gms.maps.model.FeatureType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapCapabilities
import com.google.android.gms.maps.model.PlaceFeature

/**
 * This sample showcases how to use the Data-driven styling for boundaries. For more information
 * on how the Data-driven styling for boundaries work, check out the following link:
 * https://developers.google.com/maps/documentation/android-sdk/dds-boundaries/overview
 */
class DataDrivenBoundariesActivity : AppCompatActivity(), OnMapReadyCallback,
    FeatureLayer.OnFeatureClickListener {


    private lateinit var map: GoogleMap

    private var localityLayer: FeatureLayer? = null

    private var areaLevel1Layer: FeatureLayer? = null

    private var countryLayer: FeatureLayer? = null

    private val HANA_HAWAII = LatLng(20.7522, -155.9877) // Hana, Hawaii
    private val CENTER_US = LatLng(39.8283, -98.5795) // Approximate geographical center of the contiguous US


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_driven_boundaries_demo)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        findViewById<Button>(R.id.button_hawaii).setOnClickListener {
            centerMapOnLocation(HANA_HAWAII, 13.5f) // Seattle coordinates
        }

        findViewById<Button>(R.id.button_us).setOnClickListener {
            centerMapOnLocation(CENTER_US, 1f) // New York coordinates
        }
    }

    private fun centerMapOnLocation(location: LatLng, zoomLevel: Float) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        val capabilities: MapCapabilities = map.mapCapabilities
        println("Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable)

        // Get the LOCALITY feature layer.
        localityLayer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.LOCALITY)
                .build()
        )

        // Apply style factory function to LOCALITY layer.
        styleLocalityLayer()

        // Get the ADMINISTRATIVE_AREA_LEVEL_1 feature layer.
        areaLevel1Layer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.ADMINISTRATIVE_AREA_LEVEL_1)
                .build()
        )

        // Apply style factory function to ADMINISTRATIVE_AREA_LEVEL_1 layer.
        styleAreaLevel1Layer()

        // Get the COUNTRY feature layer.
        countryLayer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.COUNTRY)
                .build()
        )

        // Register the click event handler for the Country layer.
        countryLayer?.addOnFeatureClickListener(this)

        // Apply default style to all countries on load to enable clicking.
        styleCountryLayer()
    }


    private fun styleLocalityLayer() {

        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Check if the feature is an instance of PlaceFeature,
            // which contains a place ID.
            if (feature is PlaceFeature) {
                val placeFeature: PlaceFeature = feature

                // Determine if the place ID is for Hana, HI.
                if (placeFeature.placeId == "ChIJ0zQtYiWsVHkRk8lRoB1RNPo") {
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
        localityLayer?.featureStyle = styleFactory
    }

    private fun styleAreaLevel1Layer() {
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->
            if (feature is PlaceFeature) {
                val placeFeature: PlaceFeature = feature

                // Return a hueColor in the range [-299,299]. If the value is
                // negative, add 300 to make the value positive.
                var hueColor: Int = placeFeature.placeId.hashCode() % 300
                if (hueColor < 0) {
                    hueColor += 300
                }
                return@StyleFactory FeatureStyle.Builder()
                    // Set the fill color for the state based on the hashed hue color.
                    .fillColor(Color.HSVToColor(150, floatArrayOf(hueColor.toFloat(), 1f, 1f)))
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the feature layer.
        areaLevel1Layer?.featureStyle = styleFactory
    }

    // Set default fill and border for all countries to ensure that they respond
    // to click events.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun styleCountryLayer() {
        val styleFactory = FeatureLayer.StyleFactory { _: Feature ->
            return@StyleFactory FeatureStyle.Builder()
                // Set the fill color for the country as white with a 10% opacity.
                // This requires minApi 26
                .fillColor(Color.argb(0.1f, 0f, 0f, 0f))
                // Set border color to solid black.
                .strokeColor(Color.BLACK)
                .build()
        }

        // Apply the style factory function to the country feature layer.
        countryLayer?.featureStyle = styleFactory
    }

    // Define the click event handler.
    override fun onFeatureClick(event: FeatureClickEvent) {

        // Get the list of features affected by the click using
        // getPlaceIds() defined below.
        val selectedPlaceIds = getPlaceIds(event.features)
        if (selectedPlaceIds.isNotEmpty()) {
            val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->
                // Use PlaceFeature to get the placeID of the country.
                if (feature is PlaceFeature) {
                    if (selectedPlaceIds.contains(feature.placeId)) {
                        return@StyleFactory FeatureStyle.Builder()
                            // Set the fill color to red.
                            .fillColor(Color.RED)
                            .build()
                    }
                }
                return@StyleFactory null
            }

            // Apply the style factory function to the feature layer.
            countryLayer?.featureStyle = styleFactory
        }
    }

    // Get a List of place IDs from the FeatureClickEvent object.
    private fun getPlaceIds(features: List<Feature>): List<String> {
        val placeIds: MutableList<String> = ArrayList()
        for (feature in features) {
            if (feature is PlaceFeature) {
                placeIds.add(feature.placeId)
            }
        }
        return placeIds
    }
}