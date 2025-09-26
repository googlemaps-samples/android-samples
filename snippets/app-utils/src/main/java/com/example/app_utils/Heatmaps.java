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
import android.widget.Toast;

import androidx.annotation.RawRes;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.maps.example.utils.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Heatmaps {

    private Context context;
    private GoogleMap map;

    // [START maps_android_utils_heatmap_simple]
    private void addHeatMap() {
        List<LatLng> latLngs = new ArrayList<>();

        // Get the data: latitude/longitude positions of police stations.
        try {
            latLngs = readItems(R.raw.police_stations);
        } catch (JSONException e) {
            Toast.makeText(context, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
            .data(latLngs)
            .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }

    private List<LatLng> readItems(@RawRes int resource) throws JSONException {
        List<LatLng> result = new ArrayList<>();
        InputStream inputStream = context.getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            result.add(new LatLng(lat, lng));
        }
        return result;
    }
    // [END maps_android_utils_heatmap_simple]

    private void customizeHeatmap(List<LatLng> latLngs) {
        // [START maps_android_utils_heatmap_customize]
        // Create the gradient.
        int[] colors = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
            0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        // Create the tile provider.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
            .data(latLngs)
            .gradient(gradient)
            .build();

        // Add the tile overlay to the map.
        TileOverlay tileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        // [END maps_android_utils_heatmap_customize]

        assert tileOverlay != null;

        // [START maps_android_utils_heatmap_customize_opacity]
        provider.setOpacity(0.7);
        tileOverlay.clearTileCache();
        // [END maps_android_utils_heatmap_customize_opacity]

        // [START maps_android_utils_heatmap_customize_dataset]
        List<WeightedLatLng> data = new ArrayList<>();
        provider.updateData(data);
        tileOverlay.clearTileCache();
        // [END maps_android_utils_heatmap_customize_dataset]

        // [START maps_android_utils_heatmap_remove]
        tileOverlay.remove();
        // [END maps_android_utils_heatmap_remove]
    }
}
