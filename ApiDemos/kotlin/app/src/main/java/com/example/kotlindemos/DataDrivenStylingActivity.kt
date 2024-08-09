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
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.DatasetFeature
import com.google.android.gms.maps.model.Feature
import com.google.android.gms.maps.model.FeatureLayer
import com.google.android.gms.maps.model.FeatureLayerOptions
import com.google.android.gms.maps.model.FeatureStyle
import com.google.android.gms.maps.model.FeatureType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapCapabilities


private val SINGAPORE = LatLng(1.3521, 103.8198)

private const val ZOOM_LEVEL = 3.5f

private val TAG = DataDrivenStylingActivity::class.java.name

private var datasetLayer: FeatureLayer? = null

/**
 * The following sample showcases how to create Advanced Markers, and use all their customization
 * possibilities.
 */

class DataDrivenStylingActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advanced_markers_demo)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {

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

        // Apply style factory function to DATASET layer.
        styleDatasetsLayer()
        //styleDatasetsLayerPolygon()
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
                var fillColor = 0x4000ff00
                var strokeColor = Color.YELLOW
                when (typeCategory) {
                    "Undeveloped" -> {
                        // Color undeveloped areas blue.
                        fillColor = 0x400000ff
                        strokeColor = Color.BLUE
                    }

                    "Parkway" -> {
                        // Color parkway areas red.
                        fillColor = 0x40ff0000
                        strokeColor = Color.RED
                    }

                    else -> {
                        // Color all other areas green.
                        fillColor = 0x4000ff00
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
}
