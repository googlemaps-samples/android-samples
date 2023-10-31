// Copyright 2020 Google LLC
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

package com.example.app_utils_ktx

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.annotation.RawRes
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import com.example.app_utils_ktx.R
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.jvm.Throws

internal class Heatmaps {

    private lateinit var context: Context
    private lateinit var map: GoogleMap

    // [START maps_android_utils_heatmap_simple]
    private fun addHeatMap() {
        var latLngs: List<LatLng?>? = null

        // Get the data: latitude/longitude positions of police stations.
        try {
            latLngs = readItems(R.raw.police_stations)
        } catch (e: JSONException) {
            Toast.makeText(context, "Problem reading list of locations.", Toast.LENGTH_LONG)
                .show()
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        val provider = HeatmapTileProvider.Builder()
            .data(latLngs)
            .build()

        // Add a tile overlay to the map, using the heat map tile provider.
        val overlay = map.addTileOverlay(TileOverlayOptions().tileProvider(provider))
    }

    @Throws(JSONException::class)
    private fun readItems(@RawRes resource: Int): List<LatLng?> {
        val result: MutableList<LatLng?> = ArrayList()
        val inputStream = context.resources.openRawResource(resource)
        val json = Scanner(inputStream).useDelimiter("\\A").next()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val `object` = array.getJSONObject(i)
            val lat = `object`.getDouble("lat")
            val lng = `object`.getDouble("lng")
            result.add(LatLng(lat, lng))
        }
        return result
    }
    // [END maps_android_utils_heatmap_simple]

    private fun customizeHeatmap(latLngs: List<LatLng>) {
        // [START maps_android_utils_heatmap_customize]
        // Create the gradient.
        val colors = intArrayOf(
            Color.rgb(102, 225, 0),  // green
            Color.rgb(255, 0, 0) // red
        )
        val startPoints = floatArrayOf(0.2f, 1f)
        val gradient = Gradient(colors, startPoints)

        // Create the tile provider.
        val provider = HeatmapTileProvider.Builder()
            .data(latLngs)
            .gradient(gradient)
            .build()

        // Add the tile overlay to the map.
        val tileOverlay = map.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(provider)
        )
        // [END maps_android_utils_heatmap_customize]

        // [START maps_android_utils_heatmap_customize_opacity]
        provider.setOpacity(0.7)
        tileOverlay?.clearTileCache()
        // [END maps_android_utils_heatmap_customize_opacity]

        // [START maps_android_utils_heatmap_customize_dataset]
        val data: List<WeightedLatLng> = ArrayList()
        provider.setWeightedData(data)
        tileOverlay?.clearTileCache()
        // [END maps_android_utils_heatmap_customize_dataset]

        // [START maps_android_utils_heatmap_remove]
        tileOverlay?.remove()
        // [END maps_android_utils_heatmap_remove]
    }
}
