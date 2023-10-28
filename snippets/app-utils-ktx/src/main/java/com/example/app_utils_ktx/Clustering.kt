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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager

internal class Clustering {
    private lateinit var map: GoogleMap
    private lateinit var context: Context

    // [START maps_android_utils_clustering_cluster_item]
    inner class MyItem(
        lat: Double,
        lng: Double,
        title: String,
        snippet: String
    ) : ClusterItem {

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
    // [END maps_android_utils_clustering_cluster_item]

    // [START maps_android_utils_clustering_cluster_manager]
    // Declare a variable for the cluster manager.
    private lateinit var clusterManager: ClusterManager<MyItem>

    private fun setUpClusterer() {
        // Position the map.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.503186, -0.126446), 10f))

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(context, map)

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        // Add cluster items (markers) to the cluster manager.
        addItems()
    }

    private fun addItems() {

        // Set some lat/lng coordinates to start with.
        var lat = 51.5145160
        var lng = -0.1270060

        // Add ten cluster items in close proximity, for purposes of this example.
        for (i in 0..9) {
            val offset = i / 60.0
            lat += offset
            lng += offset
            val offsetItem =
                MyItem(lat, lng, "Title $i", "Snippet $i")
            clusterManager.addItem(offsetItem)
        }
    }
    // [END maps_android_utils_clustering_cluster_manager]

    private fun clusterAnimation() {
        // [START maps_android_utils_clustering_animation_off]
        clusterManager.setAnimation(false)
        // [END maps_android_utils_clustering_animation_off]
    }

    private fun infoWindow() {
        // [START maps_android_utils_clustering_info_window]
        // Set the lat/long coordinates for the marker.
        val lat = 51.5009
        val lng = -0.122

        // Set the title and snippet strings.
        val title = "This is the title"
        val snippet = "and this is the snippet."

        // Create a cluster item for the marker and set the title and snippet using the constructor.
        val infoWindowItem = MyItem(lat, lng, title, snippet)

        // Add the cluster item (marker) to the cluster manager.
        clusterManager.addItem(infoWindowItem)
        // [END maps_android_utils_clustering_info_window]
    }
}
