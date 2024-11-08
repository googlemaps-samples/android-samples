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

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.OnMapReadyCallback
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.graphics.ColorUtils
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.DatasetFeature
import com.google.android.gms.maps.model.Feature
import com.google.android.gms.maps.model.FeatureClickEvent
import com.google.android.gms.maps.model.FeatureLayer
import com.google.android.gms.maps.model.FeatureLayerOptions
import com.google.android.gms.maps.model.FeatureStyle
import com.google.android.gms.maps.model.FeatureType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapCapabilities


private val KYOTO = LatLng(35.0016, 135.7681)
private val NEW_YORK = LatLng(40.7826, -73.9656)
private val BOULDER = LatLng(40.0150, -105.2705)

private const val ZOOM_LEVEL = 13.5f

private val TAG = DataDrivenDatasetStylingActivity::class.java.name

private var datasetLayer: FeatureLayer? = null

private lateinit var map: GoogleMap

/**
 * This sample showcases how to use the Data-driven styling for datasets. For more information
 * on how the Data-driven styling for boundaries work, check out the following link:
 * https://developers.google.com/maps/documentation/android-sdk/dds-datasets/overview
 */
class DataDrivenDatasetStylingActivity : AppCompatActivity(), OnMapReadyCallback, FeatureLayer.OnFeatureClickListener {

    // The globalid of the clicked dataset feature.
    private var lastGlobalId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_driven_styling_demo)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        findViewById<Button>(R.id.button_kyoto).setOnClickListener {
            centerMapOnLocation(KYOTO) // Seattle coordinates
        }

        findViewById<Button>(R.id.button_ny).setOnClickListener {
            centerMapOnLocation(NEW_YORK) // New York coordinates
        }

        findViewById<Button>(R.id.button_boulder).setOnClickListener {
            centerMapOnLocation(BOULDER) // New York coordinates
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(KYOTO, ZOOM_LEVEL))
        }

        val capabilities: MapCapabilities = map.mapCapabilities
        println("Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable)

        // Get the DATASET feature layer.
        datasetLayer = map.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.DATASET)
                // Specify the dataset ID.
                .datasetId("YOUR-DATASET-ID")
                .build()
        )

        // Register the click event handler for the Datasets layer.
        datasetLayer?.addOnFeatureClickListener(this)

        styleDatasetsLayer()

        // Uncommenting this line will style the polygons.
        // styleDatasetsLayerPolygon()

        // Uncommenting this line will style the polylines.
        // styleDatasetsLayerPolyline()
    }

    private fun styleDatasetsLayer() {

        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Set default colors to to yellow and point radius to 8.
            var fillColor = Color.GREEN
            var strokeColor = Color.YELLOW
            var pointRadius = 8F
            // Check if the feature is an instance of DatasetFeature.
            if (feature is DatasetFeature) {

                val furColors: MutableMap<String, String> = feature.datasetAttributes
                // Determine Color attribute.
                val furColor = furColors["Color"]
                when (furColor) {
                    "Black+" -> {
                        fillColor = Color.BLACK
                        strokeColor = Color.BLACK
                    }

                    "Cinnamon+" -> {
                        fillColor = -0x750000
                        strokeColor = -0x750000
                    }

                    "Cinnamon+Gray" -> {
                        fillColor = -0x750000
                        strokeColor = -0x750000
                        pointRadius = 6F
                    }

                    "Cinnamon+White" -> {
                        fillColor = -0x750000
                        strokeColor = Color.WHITE
                        pointRadius = 6F
                    }

                    "Gray+" -> fillColor = Color.GRAY
                    "Gray+Cinnamon" -> {
                        fillColor = Color.GRAY
                        strokeColor = -0x750000
                        pointRadius = 6F
                    }

                    "Gray+Cinnamon, White" -> {
                        fillColor = Color.LTGRAY
                        strokeColor = -0x750000
                        pointRadius = 6F
                    }

                    "Gray+White" -> {
                        fillColor = Color.GRAY
                        strokeColor = Color.WHITE
                        pointRadius = 6F
                    }
                }
                return@StyleFactory FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .pointRadius(pointRadius)
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the feature layer.
        datasetLayer?.featureStyle = styleFactory
    }

    private fun styleDatasetsLayerPolygon() {

        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Check if the feature is an instance of DatasetFeature.
            if (feature is DatasetFeature) {
                // Determine the value of the typecategory attribute.
                val typeCategories: MutableMap<String, String> = feature.datasetAttributes
                val typeCategory = typeCategories["type"]
                // Set default colors to green.
                val fillColor: Int
                val strokeColor: Int
                when (typeCategory) {
                    "temple" -> {
                        // Color temples areas blue.
                        fillColor = Color.BLUE
                        strokeColor = Color.BLUE
                    }

                    else -> {
                        // Color all other areas green.
                        fillColor = Color.GREEN
                        strokeColor = Color.GREEN
                    }
                }
                return@StyleFactory FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .strokeWidth(2F)
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the feature layer.
        datasetLayer?.featureStyle = styleFactory
    }

    private fun styleDatasetsLayerPolyline() {
        val EASY = Color.GREEN
        val MODERATE = Color.BLUE
        val DIFFICULT = Color.RED

        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Set default colors to to yellow and point radius to 8.
            var fillColor = Color.GREEN
            var strokeColor = Color.YELLOW
            var pointRadius = 8F
            var strokeWidth = 3F
            // Check if the feature is an instance of DatasetFeature.
            if (feature is DatasetFeature) {

                val attributes: MutableMap<String, String> = feature.datasetAttributes
                val difficulty = attributes["OSMPTrailsOSMPDIFFICULTY"]
                val name = attributes["OSMPTrailsOSMPTRAILNAME"]
                val dogsAllowed = attributes["OSMPTrailsOSMPDOGREGGEN"]

                when (difficulty) {
                    "Easy" -> {
                        fillColor = EASY
                    }
                    "Moderate" -> {
                        fillColor = MODERATE
                    }
                    "Difficult" -> {
                        fillColor = DIFFICULT
                    }
                    else -> {
                        Log.w(TAG, "$name -> Unknown difficulty: $difficulty")
                        fillColor = Color.MAGENTA
                    }
                }

                when (dogsAllowed) {
                    "No Dogs" -> {
                        fillColor = ColorUtils.setAlphaComponent(fillColor, 66)
                        strokeWidth = 5f
                    }
                    "LVS" -> {
                    }
                    "LR", "RV" -> {
                        fillColor = ColorUtils.setAlphaComponent(fillColor, 75)
                    }
                    else -> {
                        Log.w(TAG, "$name -> Unknown dogs reg: $dogsAllowed")
                    }
                }

                strokeColor = fillColor

                return@StyleFactory FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .pointRadius(pointRadius)
                    .strokeWidth(strokeWidth)
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the feature layer.
        datasetLayer?.featureStyle = styleFactory
    }


    private fun centerMapOnLocation(location: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL))
    }

    // Define the click event handler to set lastGlobalId to globalid of selected feature.
    override fun onFeatureClick(event: FeatureClickEvent) {
        // Get the dataset feature affected by the click.
        val clickFeatures: MutableList<Feature> = event.features
        lastGlobalId = null
        if (clickFeatures[0] is DatasetFeature) {
            lastGlobalId = ((clickFeatures[0] as DatasetFeature).datasetAttributes["globalid"])
            // Remember to reset the Style Factory.
            styleDatasetsLayerClickEvent()
        }
    }

    // Set fill and border for all features.
    private fun styleDatasetsLayerClickEvent() {
        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Check if the feature is an instance of DatasetFeature.
            if (feature is DatasetFeature) {
                val globalIDs: MutableMap<String, String> = feature.datasetAttributes
                // Determine globalid attribute.
                val globalID = globalIDs["globalid"]
                // Set default colors to to green.
                var fillColor = Color.GREEN
                var strokeColor = Color.GREEN
                if (globalID == lastGlobalId) {
                    // Color selected area blue.
                    fillColor = Color.BLUE
                    strokeColor = Color.BLUE
                }
                return@StyleFactory FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the dataset feature layer.
        datasetLayer?.featureStyle = styleFactory
    }
}
