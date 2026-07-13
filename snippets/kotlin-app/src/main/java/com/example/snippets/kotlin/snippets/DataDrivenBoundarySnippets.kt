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
import com.example.snippets.kotlin.TrackedMap
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.FeatureLayer
import com.google.android.gms.maps.model.FeatureLayerOptions
import com.google.android.gms.maps.model.FeatureStyle
import com.google.android.gms.maps.model.FeatureType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PlaceFeature
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt

@SnippetGroup(
    title = "Data-Driven Boundary Styling",
    description = "Snippets demonstrating administrative boundary feature layers, polygon styling, and click events."
)
class DataDrivenBoundarySnippets(private val context: Context, private val map: TrackedMap) {

    private val TAG = DataDrivenBoundarySnippets::class.java.simpleName

    @SnippetItem(
        title = "1. Boundaries - Localities (Hana, HI)",
        description = "Loads LOCALITY layer. Styles Hana, Hawaii (Place ID: ChIJ0zQtYiWsVHkRk8lRoB1RNPo) with purple fill and border. Centers camera."
    )
    fun styleLocalityBoundary() {
        // [START maps_android_dds_locality_boundary]
        // 1. Get the LOCALITY feature layer
        val layer = map.delegate.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.LOCALITY)
                .build()
        )

        // 2. Center the camera over Hana, Hawaii
        map.delegate.moveCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(20.7522, -155.9877), 11.0f)
        )

        // 3. Define styling: 50% opacity purple fill, opaque purple stroke
        val purple = 0x810FCB
        val fillColor = ColorUtils.setAlphaComponent(purple, (0.5f * 255).roundToInt())
        val strokeColor = ColorUtils.setAlphaComponent(purple, 255)

        layer?.featureStyle = FeatureLayer.StyleFactory { feature ->
            if (feature is PlaceFeature && feature.placeId == "ChIJ0zQtYiWsVHkRk8lRoB1RNPo") {
                FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .strokeWidth(3f)
                    .build()
            } else {
                null
            }
        }
        // [END maps_android_dds_locality_boundary]
    }

    @SnippetItem(
        title = "2. Boundaries - Admin Area 1 (States)",
        description = "Loads ADMINISTRATIVE_AREA_LEVEL_1 layer. Styles state/provincial boundaries with random colors based on Place ID hashes. Centers over US."
    )
    fun styleStateBoundaries() {
        // [START maps_android_dds_state_boundaries]
        // 1. Get the administrative area level 1 feature layer
        val layer = map.delegate.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.ADMINISTRATIVE_AREA_LEVEL_1)
                .build()
        )

        // 2. Center the camera over the USA
        map.delegate.moveCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(39.8283, -98.5795), 4.0f)
        )

        // 3. Define styling: Assign pseudo-random hue fills dynamically based on place ID
        val alpha = (255 * 0.25).roundToInt() // 25% opacity
        layer?.featureStyle = FeatureLayer.StyleFactory { feature ->
            if (feature is PlaceFeature) {
                var hueColor = feature.placeId.hashCode() % 300
                if (hueColor < 0) hueColor += 300
                FeatureStyle.Builder()
                    .fillColor(Color.HSVToColor(alpha, floatArrayOf(hueColor.toFloat(), 1f, 1f)))
                    .strokeColor(Color.DKGRAY)
                    .strokeWidth(1.5f)
                    .build()
            } else {
                null
            }
        }
        // [END maps_android_dds_state_boundaries]
    }

    @SnippetItem(
        title = "3. Boundaries - Countries (Interactive)",
        description = "Loads COUNTRY layer. Renders countries with 10% black fill. Taps toggle country coloring between light black and 33% opaque red."
    )
    fun styleCountryInteractive() {
        // [START maps_android_dds_country_interactive]
        // 1. Get the COUNTRY feature layer
        val layer = map.delegate.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.COUNTRY)
                .build()
        )

        // 2. Center the camera globally
        map.delegate.moveCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 2.0f)
        )

        // Track selected country place IDs
        val selectedPlaceIds = mutableSetOf<String>()

        val defaultFillColor = ColorUtils.setAlphaComponent(Color.BLACK, (0.1f * 255).roundToInt())
        val selectedFillColor = ColorUtils.setAlphaComponent(Color.RED, (0.33f * 255).roundToInt())

        // Helper function to reapply style factory based on selection
        fun applyStyles() {
            layer?.featureStyle = FeatureLayer.StyleFactory { feature ->
                if (feature is PlaceFeature) {
                    val fillColor = if (selectedPlaceIds.contains(feature.placeId)) {
                        selectedFillColor
                    } else {
                        defaultFillColor
                    }
                    FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(Color.BLACK)
                        .strokeWidth(1f)
                        .build()
                } else {
                    null
                }
            }
        }

        // 3. Register feature click listener to toggle selections and toast Place ID
        layer?.addOnFeatureClickListener { event ->
            val placeFeatures = event.features.filterIsInstance<PlaceFeature>()
            var changed = false
            placeFeatures.forEach { feature ->
                if (selectedPlaceIds.contains(feature.placeId)) {
                    selectedPlaceIds.remove(feature.placeId)
                } else {
                    selectedPlaceIds.add(feature.placeId)
                }
                changed = true
                Toast.makeText(context, "Country: ${feature.placeId}", Toast.LENGTH_SHORT).show()
            }
            if (changed) {
                applyStyles() // Refresh styles
            }
        }

        // Apply initial styles
        applyStyles()
        // [END maps_android_dds_country_interactive]
    }
}
