/*
 * Copyright 2026 Google LLC
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

package com.example.snippets.kotlin.snippets

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import com.example.snippets.kotlin.BuildConfig
import com.example.snippets.kotlin.TrackedMap
import com.example.snippets.common.R
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.DatasetFeature
import com.google.android.gms.maps.model.FeatureLayer
import com.google.android.gms.maps.model.FeatureLayerOptions
import com.google.android.gms.maps.model.FeatureStyle
import com.google.android.gms.maps.model.FeatureType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope

@SnippetGroup(
    title = "Custom Geospatial Datasets",
    description = "Snippets demonstrating custom Cloud geospatial dataset feature layers, attribute styling, and click events."
)
class DatasetLayerSnippets(private val context: Context, private val map: TrackedMap) {

    private val TAG = DatasetLayerSnippets::class.java.simpleName

    @SnippetItem(
        title = "1. Dataset - Boulder Trails",
        description = "What it does: Fetches a Cloud OSMP Boulder Trails dataset layer and dynamically styles trail lines by difficulty or dog regulations.\nHow to see the effect: Trail lines render in green (Easy), blue (Moderate), or red (Difficult), with line thickness indicating dog restrictions.",
    )
    fun styleBoulderTrails() {
        val datasetId = BuildConfig.BOULDER_DATASET_ID
        if (datasetId.isEmpty() || datasetId == "YOUR_DATASET_ID") {
            Toast.makeText(context, "Please configure BOULDER_DATASET_ID in secrets.properties", Toast.LENGTH_LONG).show()
            return
        }

        Log.d(TAG, "styleBoulderTrails started. Dataset ID: $datasetId")
        val capabilities = map.delegate.mapCapabilities
        Log.d(TAG, "isDataDrivenStylingAvailable: ${capabilities.isDataDrivenStylingAvailable}")

        // 1. Get the dataset feature layer from the map
        val layer = map.delegate.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.DATASET)
                .datasetId(datasetId)
                .build()
        )
        Log.d(TAG, "Dataset layer retrieved: $layer")

        // 2. Center the camera over Boulder OSMP Trails
        map.delegate.moveCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(40.0150, -105.2705), 13.0f)
        )

        // 3. Define toggle state and custom styling application function
        var colorByDifficulty = true

        val applyBoulderStyling = {
            layer?.featureStyle = FeatureLayer.StyleFactory { feature ->
                if (feature is DatasetFeature) {
                    val attributes = feature.datasetAttributes
                    val difficulty = attributes["OSMPTrailsOSMPDIFFICULTY"]
                    val dogsAllowed = attributes["OSMPTrailsOSMPDOGREGGEN"]

                    var strokeColor: Int
                    var strokeWidth = 3f

                    if (colorByDifficulty) {
                        // Mode 1: Color by difficulty
                        strokeColor = when (difficulty) {
                            "Easy" -> Color.GREEN
                            "Moderate" -> Color.BLUE
                            "Difficult" -> Color.RED
                            else -> Color.MAGENTA
                        }
                        // Thicken and fade if dogs are prohibited
                        if (dogsAllowed == "No Dogs") {
                            strokeColor = ColorUtils.setAlphaComponent(strokeColor, 120)
                            strokeWidth = 6f
                        }
                    } else {
                        // Mode 2: Color by dog regulations
                        strokeColor = when (dogsAllowed) {
                            "No Dogs" -> Color.RED          // Prohibited
                            "LR", "LVS" -> Color.YELLOW      // Leash Required / Conditional
                            "RV" -> Color.GREEN             // Off Leash / Voice & Sight
                            else -> Color.LTGRAY            // Default / Unknown
                        }
                        strokeWidth = 4f
                    }

                    FeatureStyle.Builder()
                        .strokeColor(strokeColor)
                        .strokeWidth(strokeWidth)
                        .build()
                } else {
                    null
                }
            }
        }

        // 4. Create and add the toggle button to the activity controls container
        val activity = context as? android.app.Activity
        if (activity != null) {
            val container = activity.findViewById<android.widget.LinearLayout>(R.id.custom_controls_container)
            if (container != null) {
                val toggleButton = android.widget.Button(context).apply {
                    text = "Mode: Difficulty"
                    textSize = 11f
                    alpha = 0.85f
                    val scale = resources.displayMetrics.density
                    val paddingPx = (8 * scale + 0.5f).toInt()
                    val paddingTopBottomPx = (4 * scale + 0.5f).toInt()
                    setPadding(paddingPx, paddingTopBottomPx, paddingPx, paddingTopBottomPx)
                    setOnClickListener {
                        colorByDifficulty = !colorByDifficulty
                        text = if (colorByDifficulty) "Mode: Difficulty" else "Mode: Dog Leash"
                        applyBoulderStyling()
                    }
                }
                container.addView(toggleButton)
                container.visibility = android.view.View.VISIBLE
            }
        }

        // Apply initial styling
        applyBoulderStyling()
        // [END maps_android_dds_boulder_trails]
        // [END maps_android_dds_boulder_trails]
    }

    @SnippetItem(
        title = "2. Dataset - NYC Squirrels",
        description = "What it does: Imports a Cloud Central Park Squirrel Sightings dataset layer and styles point features by fur color.\nHow to see the effect: Point markers display across Central Park colored by squirrel fur color (black, cinnamon, gray).",
    )
    fun styleNycSquirrels() {
        val datasetId = BuildConfig.NEW_YORK_DATASET_ID
        if (datasetId.isEmpty() || datasetId == "YOUR_DATASET_ID") {
            Toast.makeText(context, "Please configure NEW_YORK_DATASET_ID in secrets.properties", Toast.LENGTH_LONG).show()
            return
        }

        Log.d(TAG, "styleNycSquirrels started. Dataset ID: $datasetId")
        // [START maps_android_dds_nyc_squirrels]
        // 1. Get the dataset feature layer
        val layer = map.delegate.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.DATASET)
                .datasetId(datasetId)
                .build()
        )
        Log.d(TAG, "NYC Squirrels layer retrieved: $layer")

        // 2. Center the camera over Central Park, New York
        map.delegate.moveCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(40.786244, -73.962684), 14.0f)
        )

        // 3. Define styling rules based on squirrel fur color attributes
        layer?.featureStyle = FeatureLayer.StyleFactory { feature ->
            if (feature is DatasetFeature) {
                val attributes = feature.datasetAttributes
                val furColor = attributes["Color"]

                val color = when (furColor) {
                    "Black+" -> Color.BLACK
                    "Cinnamon+" -> Color.rgb(210, 105, 30) // Chocolate brown
                    "Gray+" -> Color.GRAY
                    else -> Color.GREEN // default
                }

                FeatureStyle.Builder()
                    .fillColor(color)
                    .strokeColor(Color.WHITE)
                    .pointRadius(8f)
                    .build()
            } else {
                null
            }
        }
        // [END maps_android_dds_nyc_squirrels]
    }

    @SnippetItem(
        title = "3. Dataset - Kyoto Temples (Clickable)",
        description = "What it does: Renders Cloud Kyoto temple boundary polygons and updates clicked polygon styling to highlight yellow.\nHow to see the effect: Temple grounds render in semi-transparent blue; tap any temple polygon to highlight it in yellow.",
    )
    fun styleKyotoTemples() {
        val datasetId = BuildConfig.KYOTO_DATASET_ID
        if (datasetId.isEmpty() || datasetId == "YOUR_DATASET_ID") {
            Toast.makeText(context, "Please configure KYOTO_DATASET_ID in secrets.properties", Toast.LENGTH_LONG).show()
            return
        }

        Log.d(TAG, "styleKyotoTemples started. Dataset ID: $datasetId")
        // [START maps_android_dds_kyoto_temples]
        // 1. Get the dataset feature layer
        val layer = map.delegate.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.DATASET)
                .datasetId(datasetId)
                .build()
        )
        Log.d(TAG, "Kyoto Temples layer retrieved: $layer")

        // 2. Center the camera over Kyoto, Japan
        map.delegate.moveCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(35.005081, 135.764385), 13.5f)
        )

        // Track the ID of the clicked temple area
        var clickedGlobalId: String? = null

        // Helper function to reapply style factory based on selection
        fun applyStyles() {
            layer?.featureStyle = FeatureLayer.StyleFactory { feature ->
                if (feature is DatasetFeature) {
                    val attributes = feature.datasetAttributes
                    val type = attributes["type"]
                    val globalId = attributes["globalid"]

                    var fillColor = if (type == "temple") {
                        ColorUtils.setAlphaComponent(Color.BLUE, 100)
                    } else {
                        ColorUtils.setAlphaComponent(Color.GREEN, 80)
                    }

                    // Highlight clicked temple area in Yellow
                    if (globalId != null && globalId == clickedGlobalId) {
                        fillColor = ColorUtils.setAlphaComponent(Color.YELLOW, 180)
                    }

                    FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(Color.BLACK)
                        .strokeWidth(2f)
                        .build()
                } else {
                    null
                }
            }
        }

        // 3. Register click handler to select temple feature and toast its name
        layer?.addOnFeatureClickListener { event ->
            val clickedFeature = event.features.filterIsInstance<DatasetFeature>().firstOrNull()
            if (clickedFeature != null) {
                val templeName = clickedFeature.datasetAttributes["name"] ?: "Unknown Temple"
                clickedGlobalId = clickedFeature.datasetAttributes["globalid"]
                Toast.makeText(context, "Temple: $templeName", Toast.LENGTH_SHORT).show()
                applyStyles() // Refresh styles to apply highlight color
            }
        }

        // Apply initial styles
        applyStyles()
        // [END maps_android_dds_kyoto_temples]
    }
}
