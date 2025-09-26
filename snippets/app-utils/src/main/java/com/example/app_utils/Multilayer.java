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
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.GroundOverlayManager;
import com.google.maps.android.collections.MarkerManager;
import com.google.maps.android.collections.PolygonManager;
import com.google.maps.android.collections.PolylineManager;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.example.utils.R;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

class Multilayer {
    private GoogleMap map;
    private Context context;

    private void init() throws IOException, JSONException, XmlPullParserException {
        // [START maps_android_utils_multilayer_init]
        MarkerManager markerManager = new MarkerManager(map);
        GroundOverlayManager groundOverlayManager = new GroundOverlayManager(map);
        PolygonManager polygonManager = new PolygonManager(map);
        PolylineManager polylineManager = new PolylineManager(map);
        // [END maps_android_utils_multilayer_init]

        // [START maps_android_utils_multilayer_manager]
        ClusterManager<MyItem> clusterManager = new ClusterManager<>(context, map, markerManager);
        GeoJsonLayer geoJsonLineLayer = new GeoJsonLayer(map, R.raw.geojson_file, context, markerManager, polygonManager, polylineManager, groundOverlayManager);
        KmlLayer kmlPolylineLayer = new KmlLayer(map, R.raw.kml_file, context, markerManager, polygonManager, polylineManager, groundOverlayManager, null);
        // [END maps_android_utils_multilayer_manager]

        // [START maps_android_utils_multilayer_unclustered_marker]
        MarkerManager.Collection markerCollection = markerManager.newCollection();
        markerCollection.addMarker(new MarkerOptions()
            .position(new LatLng(51.150000, -0.150032))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            .title("Unclustered marker"));
        // [END maps_android_utils_multilayer_unclustered_marker]

        // [START maps_android_utils_multilayer_kml_click_events]
        kmlPolylineLayer.addLayerToMap();
        kmlPolylineLayer.setOnFeatureClickListener(feature -> Toast.makeText(context,
            "KML polyline clicked: " + feature.getProperty("name"),
            Toast.LENGTH_SHORT).show());
        // [END maps_android_utils_multilayer_kml_click_events]

        // [START maps_android_utils_multilayer_marker_click_events]
        markerCollection.setOnMarkerClickListener(marker -> { Toast.makeText(context,
            "Marker clicked: " + marker.getTitle(),
                Toast.LENGTH_SHORT).show();
            return false;
        });
        // [END maps_android_utils_multilayer_marker_click_events]
    }

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
}
