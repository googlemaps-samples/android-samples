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
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import com.example.app_utils_ktx.R
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws

internal class KML {
    private val map: GoogleMap? = null

    @Throws(IOException::class, XmlPullParserException::class)
    private fun addKmlLayerFile(context: Context) {
        // [START maps_android_utils_kml_add_file]
        val layer = KmlLayer(map, R.raw.geojson_file, context)
        // [END maps_android_utils_kml_add_file]
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun addKmlLayerFileInputStream(context: Context) {
        // [START maps_android_utils_kml_add_input_stream]
        val inputStream: InputStream? =  // InputStream containing KML data
            // [START_EXCLUDE silent]
            null
            // [END_EXCLUDE]
        val layer = KmlLayer(map, inputStream, context)
        // [END maps_android_utils_kml_add_input_stream]

        // [START maps_android_utils_kml_add_layer]
        layer.addLayerToMap()
        // [END maps_android_utils_kml_add_layer]

        // [START maps_android_utils_kml_remove_layer]
        layer.removeLayerFromMap()
        // [END maps_android_utils_kml_remove_layer]

        // [START maps_android_utils_kml_access_containers]
        for (containers in layer.containers) {
            // Do something to container
        }
        // [END maps_android_utils_kml_access_containers]

        // [START maps_android_utils_kml_access_placemarks]
        for (placemark in layer.placemarks) {
            // Do something to Placemark
        }
        // [END maps_android_utils_kml_access_placemarks]

        // [START maps_android_utils_kml_access_properties]
        for (container in layer.containers) {
            if (container.hasProperty("name")) {
                Log.i("KML", container.getProperty("name"))
            }
        }
        // [END maps_android_utils_kml_access_properties]

        // [START maps_android_utils_kml_click_listener]
        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener { feature ->
            Log.i(
                "KML",
                "Feature clicked: " + feature.id
            )
        }
        // [END maps_android_utils_kml_click_listener]
    }

    // [START maps_android_utils_kml_access_containers_nested]
    fun accessContainers(containers: Iterable<KmlContainer>) {
        for (container in containers) {
            if (container.hasContainers()) {
                accessContainers(container.containers)
            }
        }
    } // [END maps_android_utils_kml_access_containers_nested]
}
