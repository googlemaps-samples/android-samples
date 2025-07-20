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
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.ColorUtils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.FeatureClickEvent
import com.google.android.gms.maps.model.FeatureLayer
import com.google.android.gms.maps.model.FeatureLayerOptions
import com.google.android.gms.maps.model.FeatureStyle
import com.google.android.gms.maps.model.FeatureType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapCapabilities
import com.google.android.gms.maps.model.PlaceFeature
import com.google.android.material.button.MaterialButton
import kotlin.math.roundToInt
import com.example.common_ui.R


private val TAG = DataDrivenBoundariesActivity::class.java.simpleName

/**
 * This sample showcases how to use the Data-driven styling for boundaries. For more information
 * on how the Data-driven styling for boundaries work, check out the following link:
 * https://developers.google.com/maps/documentation/android-sdk/dds-boundaries/overview
 */
// Add PopupMenu.OnMenuItemClickListener interface
class DataDrivenBoundariesActivity : SamplesBaseActivity(), OnMapReadyCallback,
    FeatureLayer.OnFeatureClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var map: GoogleMap

    private var localityLayer: FeatureLayer? = null
    private var areaLevel1Layer: FeatureLayer? = null
    private var countryLayer: FeatureLayer? = null

    private val HANA_HAWAII = LatLng(20.7522, -155.9877) // Hana, Hawaii
    private val CENTER_US = LatLng(39.8283, -98.5795) // Approx center US

    // --- State Variables ---
    private var localityEnabled = true // Default enabled
    private var adminAreaEnabled = false
    private var countryEnabled = false
    private val selectedPlaceIds = mutableSetOf<String>() // For selected countries

    // --- Style Factories (defined once) ---
    private val localityStyleFactory: FeatureLayer.StyleFactory = createLocalityStyleFactory()
    private val areaLevel1StyleFactory: FeatureLayer.StyleFactory = createAreaLevel1StyleFactory()
    // Country factory references selectedPlaceIds, needs to be instance property or re-created if needed
    private val countryStyleFactory: FeatureLayer.StyleFactory = createCountryStyleFactory()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Assumes layout is in common_ui module
        setContentView(R.layout.data_driven_boundaries_demo)

        val mapId = (application as ApiDemoApplication).mapId

        // --- Map ID Check ---
        if (mapId == null) {
            finish()
            return // Exit early if no valid Map ID
        }

        // --- Programmatically create and add the map fragment ---
        val mapOptions = GoogleMapOptions().apply {
            mapId(mapId)
        }
        val mapFragment = SupportMapFragment.newInstance(mapOptions)
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, mapFragment) // Use the container ID
            .commit()
        mapFragment.getMapAsync(this)

        // --- Setup Buttons ---
        findViewById<MaterialButton>(R.id.button_hawaii).setOnClickListener {
            centerMapOnLocation(HANA_HAWAII, 11f) // Adjusted zoom from Java
        }
        findViewById<MaterialButton>(R.id.button_us).setOnClickListener {
            centerMapOnLocation(CENTER_US, 1f) // Adjusted zoom from Java
        }
        setupBoundarySelectorButton() // Setup the new selector button

        // --- Insets ---
        applyInsets(findViewById<View?>(R.id.map_container)) // Apply insets if needed
    }

    private fun setupBoundarySelectorButton() {
        val stylingTypeButton: MaterialButton = findViewById(R.id.button_feature_type) // Find the button
        stylingTypeButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.boundary_types_menu, popupMenu.menu) // Inflate your menu

            popupMenu.setOnMenuItemClickListener(this) // Set listener to this Activity

            // Set initial check states based on current flags
            popupMenu.menu.findItem(R.id.boundary_type_locality)?.isChecked = localityEnabled
            popupMenu.menu.findItem(R.id.boundary_type_administrative_area_level_1)?.isChecked = adminAreaEnabled
            popupMenu.menu.findItem(R.id.boundary_type_country)?.isChecked = countryEnabled

            popupMenu.show()
        }
    }

    private fun centerMapOnLocation(location: LatLng, zoomLevel: Float) {
        if (::map.isInitialized) { // Check if map is ready
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
        } else {
            Log.w(TAG, "Map not initialized, cannot center map.")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val capabilities: MapCapabilities = map.mapCapabilities
        Log.d(TAG, "Data-driven Styling is available: ${capabilities.isDataDrivenStylingAvailable}")

        if (!capabilities.isDataDrivenStylingAvailable) {
            Toast.makeText(
                this,
                "Data-driven Styling is not available. See README.md for instructions.",
                Toast.LENGTH_LONG
            ).show()
        }

        // Get feature layers
        localityLayer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.LOCALITY)
                .build()
        )
        areaLevel1Layer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.ADMINISTRATIVE_AREA_LEVEL_1)
                .build()
        )
        countryLayer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.COUNTRY)
                .build()
        ).also {
            it.addOnFeatureClickListener(this)
        }

        // Apply initial styles based on default flags
        updateStyles()

        // Center map initially
        centerMapOnLocation(HANA_HAWAII, 11f)
    }

    /**
     * Updates the styles based on the enabled flags.
     */
    private fun updateStyles() {
        Log.d(TAG, "Updating Styles: Locality=$localityEnabled, Admin1=$adminAreaEnabled, Country=$countryEnabled")
        localityLayer?.featureStyle = if (localityEnabled) localityStyleFactory else null
        areaLevel1Layer?.featureStyle = if (adminAreaEnabled) areaLevel1StyleFactory else null
        countryLayer?.featureStyle = if (countryEnabled) countryStyleFactory else null
    }

    // --- Style Factory Creation Methods ---

    private fun createLocalityStyleFactory(): FeatureLayer.StyleFactory {
        val purple = 0x810FCB
        // Define a style with purple fill at 50% opacity and solid purple border.
        val fillColor = ColorUtils.setAlphaComponent(purple, (0.5f * 255).roundToInt())
        val strokeColor = ColorUtils.setAlphaComponent(purple, 255) // Fully opaque

        return FeatureLayer.StyleFactory { feature ->
            if (feature is PlaceFeature && feature.placeId == "ChIJ0zQtYiWsVHkRk8lRoB1RNPo") { // Hana, HI
                FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .build()
            } else {
                null // No style for other localities
            }
        }
    }

    private fun createAreaLevel1StyleFactory(): FeatureLayer.StyleFactory {
        val alpha = (255 * 0.25).roundToInt() // 25% opacity

        return FeatureLayer.StyleFactory { feature ->
            if (feature is PlaceFeature) {
                // Generate a hue based on placeId hash
                var hueColor = feature.placeId.hashCode() % 300
                if (hueColor < 0) hueColor += 300
                FeatureStyle.Builder()
                    .fillColor(Color.HSVToColor(alpha, floatArrayOf(hueColor.toFloat(), 1f, 1f)))
                    .build()
            } else {
                null
            }
        }
    }

    private fun createCountryStyleFactory(): FeatureLayer.StyleFactory {
        val defaultFillColor = ColorUtils.setAlphaComponent(Color.BLACK, (0.1f * 255).roundToInt()) // 10% Black
        val selectedFillColor = ColorUtils.setAlphaComponent(Color.RED, (0.33f * 255).roundToInt()) // 33% Red

        return FeatureLayer.StyleFactory { feature ->
            if (feature is PlaceFeature) {
                // Check if this country's place ID is in our selected set
                val fillColor = if (selectedPlaceIds.contains(feature.placeId)) {
                    selectedFillColor
                } else {
                    defaultFillColor
                }
                FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(Color.BLACK) // Solid black border
                    .build()
            } else {
                null
            }
        }
    }

    // --- Listener Implementations ---

    /**
     * Handles clicks on the Country Layer features.
     */
    override fun onFeatureClick(event: FeatureClickEvent) {
        val clickedPlaceIds = event.features
            .filterIsInstance<PlaceFeature>() // Get only PlaceFeatures
            .map { it.placeId } // Extract their place IDs

        var changed = false
        clickedPlaceIds.forEach { placeId ->
            if (selectedPlaceIds.contains(placeId)) {
                selectedPlaceIds.remove(placeId)
                changed = true
            } else {
                selectedPlaceIds.add(placeId)
                changed = true
            }
        }

        // If the selection changed and the country layer is enabled, re-apply its style
        if (changed && countryEnabled) {
            Log.d(TAG, "Country selection changed. Selected IDs: $selectedPlaceIds")
            countryLayer?.featureStyle = countryStyleFactory // Re-apply the factory
        } else if (!countryEnabled) {
            Log.d(TAG, "Country clicked but layer not enabled.")
            // Optional: Show a toast? "Enable country layer to select"
        }
    }


    /**
     * Handles clicks on the PopupMenu items.
     */
    override fun onMenuItemClick(item: MenuItem): Boolean {
        val id = item.itemId
        item.isChecked = !item.isChecked // Toggle the checkmark

        when (id) {
            R.id.boundary_type_locality -> {
                localityEnabled = item.isChecked
            }
            R.id.boundary_type_administrative_area_level_1 -> {
                adminAreaEnabled = item.isChecked
            }
            R.id.boundary_type_country -> {
                countryEnabled = item.isChecked
                // If disabling country layer, clear selection visually (optional)
                // if (!countryEnabled) selectedPlaceIds.clear()
            }
            else -> return false // Unknown item
        }

        updateStyles() // Apply changes to map layers
        return true
    }
}