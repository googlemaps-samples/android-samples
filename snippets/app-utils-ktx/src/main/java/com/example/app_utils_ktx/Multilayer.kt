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
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.Feature
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.kml.KmlLayer
import org.json.JSONException
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal class Multilayer {
    private val map: GoogleMap? = null
    private val context: Context? = null

    @Suppress("IndexOutOfBoundsException")
    @Throws(IOException::class, JSONException::class, XmlPullParserException::class)
    private fun init() {
        // [START maps_android_utils_multilayer_init]
        val markerManager = MarkerManager(map)
        val groundOverlayManager = GroundOverlayManager(map!!)
        val polygonManager = PolygonManager(map)
        val polylineManager = PolylineManager(map)
        // [END maps_android_utils_multilayer_init]

        // [START maps_android_utils_multilayer_manager]
        val clusterManager =
            ClusterManager<MyItem>(context, map, markerManager)
        val geoJsonLineLayer = GeoJsonLayer(
            map,
            R.raw.geojson_file,
            context,
            markerManager,
            polygonManager,
            polylineManager,
            groundOverlayManager
        )
        val kmlPolylineLayer = KmlLayer(
            map,
            R.raw.kml_file,
            context,
            markerManager,
            polygonManager,
            polylineManager,
            groundOverlayManager,
            null
        )
        // [END maps_android_utils_multilayer_manager]

        // [START maps_android_utils_multilayer_unclustered_marker]
        val markerCollection =
            markerManager.newCollection()
        markerCollection.addMarker(
            MarkerOptions()
                .position(LatLng(51.150000, -0.150032))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Unclustered marker")
        )
        // [END maps_android_utils_multilayer_unclustered_marker]

        // [START maps_android_utils_multilayer_kml_click_events]
        kmlPolylineLayer.addLayerToMap()
        kmlPolylineLayer.setOnFeatureClickListener { feature: Feature ->
            Toast.makeText(context,
                "KML polyline clicked: ${feature.getProperty("name")}",
                Toast.LENGTH_SHORT
            ).show()
        }
        // [END maps_android_utils_multilayer_kml_click_events]

        // [START maps_android_utils_multilayer_marker_click_events]
        markerCollection.setOnMarkerClickListener { marker: Marker ->
            Toast.makeText(
                context,
                "Marker clicked: ${marker.title}",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
        // [END maps_android_utils_multilayer_marker_click_events]
    }

    inner class MyItem(
        lat: Double,
        lng: Double,
        title: String,
        snippet: String
    ) :
        ClusterItem {
        private val position: LatLng
        private val title: String
        private val snippet: String
        override fun getPosition(): LatLng {
            return position
        }

        override fun getTitle(): String {
            return title
        }

        override fun getSnippet(): String {
            return snippet
        }

        override fun getZIndex(): Float {
            return 0f
        }

        init {
            position = LatLng(lat, lng)
            this.title = title
            this.snippet = snippet
        }
    }
}
