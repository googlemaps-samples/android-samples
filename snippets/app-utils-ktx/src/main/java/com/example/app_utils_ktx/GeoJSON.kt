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
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.*
import com.example.app_utils_ktx.R
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.jvm.Throws

internal class GeoJSON {
    private lateinit var map: GoogleMap
    private fun addGeoJsonLayerJsonObject() {
        // [START maps_android_util_geojson_add_jsonobject]
        val geoJsonData: JSONObject? = // JSONObject containing the GeoJSON data
            // [START_EXCLUDE silent]
            null
            // [END_EXCLUDE]
        val layer = GeoJsonLayer(map, geoJsonData)
        // [END maps_android_util_geojson_add_jsonobject]
    }

    @Throws(IOException::class, JSONException::class)
    private fun addGeoJsonLayerFile(context: Context) {
        // [START maps_android_util_geojson_add_file]
        val layer = GeoJsonLayer(map, R.raw.geojson_file, context)
        // [END maps_android_util_geojson_add_file]

        // [START maps_android_util_geojson_add_layer_to_map]
        layer.addLayerToMap()
        // [END maps_android_util_geojson_add_layer_to_map]

        // [START maps_android_util_geojson_remove_layer]
        layer.removeLayerFromMap()
        // [END maps_android_util_geojson_remove_layer]
    }

    private fun geoJsonFeature(layer: GeoJsonLayer) {
        // [START maps_android_util_geojson_point_feature]
        val point = GeoJsonPoint(LatLng(0.0, 0.0))
        val properties = hashMapOf("Ocean" to "South Atlantic")
        val pointFeature = GeoJsonFeature(point, "Origin", properties, null)
        // [END maps_android_util_geojson_point_feature]

        // [START maps_android_util_geojson_point_feature_add]
        layer.addFeature(pointFeature)
        // [END maps_android_util_geojson_point_feature_add]

        // [START maps_android_util_geojson_point_feature_remove]
        layer.removeFeature(pointFeature)
        // [END maps_android_util_geojson_point_feature_remove]

        // [START maps_android_util_geojson_point_feature_access]
        for (feature in layer.features) {
            // Do something to the feature
            // [START_EXCLUDE silent]
            // [START maps_android_util_geojson_point_feature_has_property]
            if (feature.hasProperty("Ocean")) {
                val oceanProperty = feature.getProperty("Ocean")
            }
            // [END maps_android_util_geojson_point_feature_has_property]
            // [END_EXCLUDE]
        }
        // [END maps_android_util_geojson_point_feature_access]

        // [START maps_android_util_geojson_geometry_click_events]
        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener { feature ->
            Log.i("GeoJsonClick", "Feature clicked: ${feature.getProperty("title")}")
        }
        // [END maps_android_util_geojson_geometry_click_events]

        // [START maps_android_util_geojson_style]
        val pointStyle = layer.defaultPointStyle
        pointStyle.isDraggable = true
        pointStyle.title = "Hello, World!"
        pointStyle.snippet = "I am a draggable marker"
        // [END maps_android_util_geojson_style]

        // [START maps_android_util_geojson_style_specific]
        // Create a new feature containing a linestring
        val lineStringArray: MutableList<LatLng> = ArrayList()
        lineStringArray.add(LatLng(0.0, 0.0))
        lineStringArray.add(LatLng(50.0, 50.0))
        val lineString = GeoJsonLineString(lineStringArray)
        val lineStringFeature = GeoJsonFeature(lineString, null, null, null)

        // Set the color of the linestring to red
        val lineStringStyle = GeoJsonLineStringStyle()
        lineStringStyle.color = Color.RED

        // Set the style of the feature
        lineStringFeature.lineStringStyle = lineStringStyle
        // [END maps_android_util_geojson_style_specific]
    }
}
