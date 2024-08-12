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
import android.widget.Button
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


private val SINGAPORE = LatLng(1.3521, 103.8198)

private const val ZOOM_LEVEL = 13.5f

private val TAG = DataDrivenDatasetStylingActivity::class.java.name

private var datasetLayer: FeatureLayer? = null

private lateinit var map: GoogleMap

class DataDrivenDatasetStylingActivity : AppCompatActivity(), OnMapReadyCallback, FeatureLayer.OnFeatureClickListener {

    // The globalid of the clicked dataset feature.
    private var lastGlobalId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_driven_styling_demo)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        findViewById<Button>(R.id.button_seattle).setOnClickListener {
            centerMapOnLocation(LatLng(47.6062, -122.3321)) // Seattle coordinates
        }

        findViewById<Button>(R.id.button_ny).setOnClickListener {
            centerMapOnLocation(LatLng(40.7128, -74.0060)) // New York coordinates
        }

        findViewById<Button>(R.id.button_south_africa).setOnClickListener {
            centerMapOnLocation(LatLng(-30.5595, 22.9375)) // South Africa coordinates
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(SINGAPORE, ZOOM_LEVEL))
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
                // Determine CombinationofPrimaryandHighlightColor attribute.
                val furColor = furColors["CombinationofPrimaryandHighlightColor"]
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
                val typeCategory = typeCategories["typecategory"]
                // Set default colors to green.
                val fillColor: Int
                val strokeColor: Int
                when (typeCategory) {
                    "Undeveloped" -> {
                        // Color undeveloped areas blue.
                        fillColor = Color.BLUE
                        strokeColor = Color.BLUE
                    }

                    "Parkway" -> {
                        // Color parkway areas red.
                        fillColor = Color.RED
                        strokeColor = Color.RED
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

        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Check if the feature is an instance of DatasetFeature.
            if (feature is DatasetFeature) {

                return@StyleFactory FeatureStyle.Builder()
                    // Define a style with green stroke with a width of 4.
                    .strokeColor(0xff00ff00.toInt())
                    .strokeWidth(4F)
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
            lastGlobalId = ((clickFeatures[0] as DatasetFeature).datasetAttributes.get("globalid"))
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
