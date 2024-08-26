// Copyright 2024 Google LLC
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
package com.example.mapdemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.FeatureLayer;
import com.google.android.gms.maps.model.FeatureLayerOptions;
import com.google.android.gms.maps.model.FeatureStyle;
import com.google.android.gms.maps.model.FeatureType;
import com.google.android.gms.maps.model.MapCapabilities;
import com.google.android.gms.maps.model.PlaceFeature;

/**
 * This sample showcases how to use the Data-driven styling for boundaries. For more information
 * on how the Data-driven styling for boundaries work, check out the following link:
 * https://developers.google.com/maps/documentation/android-sdk/dds-boundaries/overview
 */
public class DataDrivenBoundariesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FeatureLayer localityLayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_driven_styling_demo);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        MapCapabilities capabilities = map.getMapCapabilities();
        System.out.println("Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable());

        // Get the LOCALITY feature layer.
        localityLayer = googleMap.getFeatureLayer(new FeatureLayerOptions.Builder()
                .featureType(FeatureType.LOCALITY)
                .build());

        // Apply style factory function to LOCALITY layer.
        styleLocalityLayer();
    }

    private void styleLocalityLayer() {

        // Create the style factory function.
        FeatureLayer.StyleFactory styleFactory = feature -> {
            // Check if the feature is an instance of PlaceFeature,
            // which contains a place ID.
            if (feature instanceof PlaceFeature placeFeature) {

                // Determine if the place ID is for Hana, HI.
                if ("ChIJ0zQtYiWsVHkRk8lRoB1RNPo".equals(placeFeature.getPlaceId())) {
                    // Use FeatureStyle.Builder to configure the FeatureStyle object
                    // returned by the style factory function.
                    return new FeatureStyle.Builder()
                            // Define a style with purple fill at 50% opacity and
                            // solid purple border.
                            .fillColor(0x80810FCB)
                            .strokeColor(0xFF810FCB)
                            .build();
                }
            }
            return null;
        };

        // Apply the style factory function to the feature layer.
        if (localityLayer != null) {
            localityLayer.setFeatureStyle(styleFactory);
        }
    }
}
