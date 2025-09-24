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

import com.google.android.gms.maps.OnMapReadyCallback
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
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
import androidx.core.view.WindowCompat
import com.google.android.gms.maps.GoogleMapOptions


private val TAG = DataDrivenDatasetStylingActivity::class.java.name

/**
 * This sample showcases how to use the Data-driven styling for datasets. For more information
 * on how the Data-driven styling for boundaries work, check out the following link:
 * https://developers.google.com/maps/documentation/android-sdk/dds-datasets/overview
 */
class DataDrivenDatasetStylingActivity : SamplesBaseActivity(), OnMapReadyCallback, FeatureLayer.OnFeatureClickListener {
    private lateinit var mapContainer: ViewGroup

    private lateinit var map: GoogleMap
    private val zoomLevel = 13.5f

    private var datasetLayer: FeatureLayer? = null

    // The global id of the clicked dataset feature.
    private var lastGlobalId: String? = null

    private data class DataSet(
        val datasetId: String,
        val location: LatLng,
        val callback: DataDrivenDatasetStylingActivity.() -> Unit
    )

    private val dataSets = mutableMapOf<String, DataSet>()

    private lateinit var buttonLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val mapId = (application as ApiDemoApplication).mapId

        // --- Map ID Check ---
        if (mapId == null) {
            finish()
            return // Exit early if no valid Map ID
        }

        if (dataSets.isEmpty()) {
            with(dataSets) {
                put(getString(com.example.common_ui.R.string.boulder), DataSet(BuildConfig.BOULDER_DATASET_ID, LatLng(40.0150, -105.2705)) { styleBoulderDataset() })
                put(getString(com.example.common_ui.R.string.new_york), DataSet(BuildConfig.NEW_YORK_DATASET_ID, LatLng(40.786244, -73.962684)) { styleNYCDataset() })
                put(getString(com.example.common_ui.R.string.kyoto), DataSet(BuildConfig.KYOTO_DATASET_ID, LatLng(35.005081, 135.764385)) { styleKyotoDataset() })
            }
        }

        setContentView(com.example.common_ui.R.layout.data_driven_styling_demo)

        mapContainer = findViewById(com.example.common_ui.R.id.map_container)

        // --- Programmatically create and add the map fragment ---
        // 1. Create GoogleMapOptions
        val mapOptions = GoogleMapOptions().apply {
            // 2. Set the mapId using your BuildConfig field
            mapId(mapId)
        }

        // 3. Create SupportMapFragment instance with options
        val mapFragment = SupportMapFragment.newInstance(mapOptions)

        // 4. Add the fragment to your FrameLayout container
        supportFragmentManager.beginTransaction()
            .replace(com.example.common_ui.R.id.map_fragment_container, mapFragment) // Use the container ID from XML
            .commit()
        // --- End of programmatic creation ---

        mapFragment.getMapAsync(this)

        // Set the click listener for each of the buttons
        listOf(com.example.common_ui.R.id.button_kyoto, com.example.common_ui.R.id.button_ny, com.example.common_ui.R.id.button_boulder).forEach { viewId ->
            findViewById<Button>(viewId).setOnClickListener { view ->
                switchToDataset((view as Button).text.toString())
            }
        }

        buttonLayout = findViewById<View>(com.example.common_ui.R.id.button_kyoto).parent as LinearLayout

        handleCutout()
        applyInsets(findViewById<View?>(com.example.common_ui.R.id.map_container))
    }

    private fun handleCutout() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.setOnApplyWindowInsetsListener { _, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsets.Type.systemBars())
                val topInset = insets.top
                mapContainer.setPadding(0, topInset, 0, 0)
                windowInsets
            }
        } else {
            window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
                val topInset = windowInsets.systemWindowInsetTop
                mapContainer.setPadding(0, topInset, 0, 0)
                windowInsets
            }
        }
    }

    /**
     * Switches the currently displayed dataset on the map to the one identified by the given label.
     *
     * This function retrieves a DataSetInfo object from the `dataSets` map using the provided `label`.
     * If a dataset with the given label exists, it does the following:
     *   1. Creates a new FeatureLayer for the specified dataset.
     *   2. Sets the `datasetLayer` property to the newly created FeatureLayer.
     *   3. Executes the callback function associated with the dataset, passing the current activity instance (this).
     *   4. Centers the map on the location associated with the dataset.
     * If no dataset with the given label is found, it displays a Toast message indicating an unknown dataset.
     *
     * @param label The label identifying the dataset to switch to. This label should correspond to a key in the `dataSets` map.
     * @throws IllegalStateException if `map` is not initialized.
     */
    private fun switchToDataset(label: String) {
        dataSets[label]?.let { dataSet ->
            datasetLayer = map.getFeatureLayer(
                with(FeatureLayerOptions.Builder()) {
                    featureType(FeatureType.DATASET)
                    // Specify the dataset ID.
                    datasetId(dataSet.datasetId)
                }.build()
            )
            dataSet.callback(this)
            centerMapOnLocation(dataSet.location)
        } ?: run {
            Toast.makeText(this, "Unknown dataset: $label", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val capabilities: MapCapabilities = map.mapCapabilities
        println("Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable)

        switchToDataset("Boulder")

        // Register the click event handler for the Datasets layer.
        datasetLayer?.addOnFeatureClickListener(this)
    }

    private fun styleNYCDataset() {
        data class Style(
            @ColorInt val fillColor: Int,
            @ColorInt val strokeColor: Int,
            val pointRadius: Float,
        )

        val largePointRadius = 8F
        val smallPointRadius = 6F
        val darkRedBrown = resources.getColor(R.color.darkRedBrown)

        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->
            if (feature is DatasetFeature) {
                val furColors: MutableMap<String, String> = feature.datasetAttributes
                val furColor = furColors["Color"]

                val style = when (furColor) {
                    "Black+" -> Style(Color.BLACK, Color.BLACK, largePointRadius)
                    "Cinnamon+" -> Style(darkRedBrown, darkRedBrown, largePointRadius)
                    "Cinnamon+Gray" -> Style(darkRedBrown, darkRedBrown, smallPointRadius)
                    "Cinnamon+White" -> Style(darkRedBrown, Color.WHITE, smallPointRadius)
                    "Gray+" -> Style(Color.GRAY, Color.YELLOW, largePointRadius) // Default stroke
                    "Gray+Cinnamon" -> Style(Color.GRAY, darkRedBrown, smallPointRadius)
                    "Gray+Cinnamon, White" -> Style(Color.LTGRAY, darkRedBrown, smallPointRadius)
                    "Gray+White" -> Style(Color.GRAY, Color.WHITE, smallPointRadius)
                    else -> Style(Color.GREEN, Color.YELLOW, largePointRadius) // Default style if furColor is null or doesn't match
                }

                return@StyleFactory FeatureStyle.Builder()
                    .fillColor(style.fillColor)
                    .strokeColor(style.strokeColor)
                    .pointRadius(style.pointRadius)
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the feature layer.
        datasetLayer?.featureStyle = styleFactory
    }

    private fun styleKyotoDataset() {

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

    private fun styleBoulderDataset() {
        val EASY = Color.GREEN
        val MODERATE = Color.BLUE
        val DIFFICULT = Color.RED

        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Set default colors to to yellow and point radius to 8.
            var fillColor: Int
            var strokeColor: Int
            val pointRadius = 8F
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
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
