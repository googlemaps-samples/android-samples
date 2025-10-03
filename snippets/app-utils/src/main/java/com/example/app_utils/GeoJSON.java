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
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;
import com.google.maps.example.utils.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class GeoJSON {
    private GoogleMap map;

    private void addGeoJsonLayerJsonObject() {
        // [START maps_android_util_geojson_add_jsonobject]
        JSONObject geoJsonData = // JSONObject containing the GeoJSON data
        // [START_EXCLUDE silent]
            null;
        // [END_EXCLUDE]
        GeoJsonLayer layer = new GeoJsonLayer(map, geoJsonData);
        // [END maps_android_util_geojson_add_jsonobject]
    }

    private void addGeoJsonLayerFile(Context context) throws IOException, JSONException {
        // [START maps_android_util_geojson_add_file]
        GeoJsonLayer layer = new GeoJsonLayer(map, R.raw.geojson_file, context);
        // [END maps_android_util_geojson_add_file]

        // [START maps_android_util_geojson_add_layer_to_map]
        layer.addLayerToMap();
        // [END maps_android_util_geojson_add_layer_to_map]

        // [START maps_android_util_geojson_remove_layer]
        layer.removeLayerFromMap();
        // [END maps_android_util_geojson_remove_layer]
    }

    private void geoJsonFeature(GeoJsonLayer layer) {
        // [START maps_android_util_geojson_point_feature]
        GeoJsonPoint point = new GeoJsonPoint(new LatLng(0, 0));
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Ocean", "South Atlantic");
        GeoJsonFeature pointFeature = new GeoJsonFeature(point, "Origin", properties, null);
        // [END maps_android_util_geojson_point_feature]

        // [START maps_android_util_geojson_point_feature_add]
        layer.addFeature(pointFeature);
        // [END maps_android_util_geojson_point_feature_add]

        // [START maps_android_util_geojson_point_feature_remove]
        layer.removeFeature(pointFeature);
        // [END maps_android_util_geojson_point_feature_remove]

        // [START maps_android_util_geojson_point_feature_access]
        for (GeoJsonFeature feature : layer.getFeatures()) {
            // Do something to the feature
            // [START_EXCLUDE silent]
            // [START maps_android_util_geojson_point_feature_has_property]
            if (feature.hasProperty("Ocean")) {
                String oceanProperty = feature.getProperty("Ocean");
            }
            // [END maps_android_util_geojson_point_feature_has_property]
            // [END_EXCLUDE]
        }
        // [END maps_android_util_geojson_point_feature_access]

        // [START maps_android_util_geojson_geometry_click_events]
        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener(new Layer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Log.i("GeoJsonClick", "Feature clicked: " + feature.getProperty("title"));
            }
        });
        // [END maps_android_util_geojson_geometry_click_events]

        // [START maps_android_util_geojson_style]
        GeoJsonPointStyle pointStyle = layer.getDefaultPointStyle();
        pointStyle.setDraggable(true);
        pointStyle.setTitle("Hello, World!");
        pointStyle.setSnippet("I am a draggable marker");
        // [END maps_android_util_geojson_style]

        // [START maps_android_util_geojson_style_specific]
        // Create a new feature containing a linestring
        List<LatLng> lineStringArray = new ArrayList<LatLng>();
        lineStringArray.add(new LatLng(0, 0));
        lineStringArray.add(new LatLng(50, 50));
        GeoJsonLineString lineString = new GeoJsonLineString(lineStringArray);
        GeoJsonFeature lineStringFeature = new GeoJsonFeature(lineString, null, null, null);

        // Set the color of the linestring to red
        GeoJsonLineStringStyle lineStringStyle = new GeoJsonLineStringStyle();
        lineStringStyle.setColor(Color.RED);

        // Set the style of the feature
        lineStringFeature.setLineStringStyle(lineStringStyle);
        // [END maps_android_util_geojson_style_specific]
    }
}
