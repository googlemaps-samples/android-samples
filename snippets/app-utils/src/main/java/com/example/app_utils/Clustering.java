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

package com.example.app_utils;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

class Clustering {

    private GoogleMap map;
    private Context context;

    // [START maps_android_utils_clustering_cluster_item]
    public class MyItem implements ClusterItem {
        private final LatLng position;
        private final String title;
        private final String snippet;

        public MyItem(double lat, double lng, String title, String snippet) {
            position = new LatLng(lat, lng);
            this.title = title;
            this.snippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSnippet() {
            return snippet;
        }

        @Nullable
        @Override
        public Float getZIndex() {
            return 0f;
        }
    }
    // [END maps_android_utils_clustering_cluster_item]

    // [START maps_android_utils_clustering_cluster_manager]
    // Declare a variable for the cluster manager.
    private ClusterManager<MyItem> clusterManager;

    private void setUpClusterer() {
        // Position the map.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<MyItem>(context, map);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = 51.5145160;
        double lng = -0.1270060;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng, "Title " + i, "Snippet " + i);
            clusterManager.addItem(offsetItem);
        }
    }
    // [END maps_android_utils_clustering_cluster_manager]

    private void clusterAnimation() {
        // [START maps_android_utils_clustering_animation_off]
        clusterManager.setAnimation(false);
        // [END maps_android_utils_clustering_animation_off]
    }

    private void infoWindow() {
        // [START maps_android_utils_clustering_info_window]
        // Set the lat/long coordinates for the marker.
        double lat = 51.5009;
        double lng = -0.122;

        // Set the title and snippet strings.
        String title = "This is the title";
        String snippet = "and this is the snippet.";

        // Create a cluster item for the marker and set the title and snippet using the constructor.
        MyItem infoWindowItem = new MyItem(lat, lng, title, snippet);

        // Add the cluster item (marker) to the cluster manager.
        clusterManager.addItem(infoWindowItem);
        // [END maps_android_utils_clustering_info_window]
    }
}
