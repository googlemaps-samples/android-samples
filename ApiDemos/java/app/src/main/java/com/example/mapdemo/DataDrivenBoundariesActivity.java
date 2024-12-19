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

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Feature;
import com.google.android.gms.maps.model.FeatureClickEvent;
import com.google.android.gms.maps.model.FeatureLayer;
import com.google.android.gms.maps.model.FeatureLayerOptions;
import com.google.android.gms.maps.model.FeatureStyle;
import com.google.android.gms.maps.model.FeatureType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapCapabilities;
import com.google.android.gms.maps.model.PlaceFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * This sample showcases how to use the Data-driven styling for boundaries. For more information
 * on how the Data-driven styling for boundaries work, check out the following link:
 * https://developers.google.com/maps/documentation/android-sdk/dds-boundaries/overview
 */
// [START maps_android_data_driven_styling_boundaries]
public class DataDrivenBoundariesActivity extends AppCompatActivity implements OnMapReadyCallback,
        FeatureLayer.OnFeatureClickListener {

    private GoogleMap map;

    private FeatureLayer localityLayer = null;
    private FeatureLayer areaLevel1Layer = null;
    private FeatureLayer countryLayer = null;

    private static final String TAG = DataDrivenBoundariesActivity.class.getName();

    private static final LatLng HANA_HAWAII = new LatLng(20.7522, -155.9877); // Hana, Hawaii
    private static final LatLng CENTER_US = new LatLng(39.8283, -98.5795); // Approximate geographical center of the contiguous US

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_driven_boundaries_demo);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.button_hawaii).setOnClickListener(view -> centerMapOnLocation(HANA_HAWAII, 13.5f));
        findViewById(R.id.button_us).setOnClickListener(view -> centerMapOnLocation(CENTER_US, 1f));
    }

    private void centerMapOnLocation(LatLng location, float zoomLevel) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        MapCapabilities capabilities = map.getMapCapabilities();
        Log.d(TAG, "Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable());

        // Get the LOCALITY feature layer.
        localityLayer = googleMap.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.LOCALITY)
                        .build()
        );

        // Apply style factory function to LOCALITY layer.
        styleLocalityLayer();

        // Get the ADMINISTRATIVE_AREA_LEVEL_1 feature layer.
        areaLevel1Layer = googleMap.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.ADMINISTRATIVE_AREA_LEVEL_1)
                        .build()
        );

        // Apply style factory function to ADMINISTRATIVE_AREA_LEVEL_1 layer.
        styleAreaLevel1Layer();

        // Get the COUNTRY feature layer.
        countryLayer = googleMap.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.COUNTRY)
                        .build()
        );

        // Register the click event handler for the Country layer.
        countryLayer.addOnFeatureClickListener(this);

        // Apply default style to all countries on load to enable clicking.
        styleCountryLayer();
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

    private void styleAreaLevel1Layer() {
        FeatureLayer.StyleFactory styleFactory = feature -> {
            if (feature instanceof PlaceFeature placeFeature) {

                // Return a hueColor in the range [-299,299]. If the value is
                // negative, add 300 to make the value positive.
                int hueColor = placeFeature.getPlaceId().hashCode() % 300;
                if (hueColor < 0) {
                    hueColor += 300;
                }
                return new FeatureStyle.Builder()
                        // Set the fill color for the state based on the hashed hue color.
                        .fillColor(Color.HSVToColor(150, new float[]{hueColor, 1f, 1f}))
                        .build();
            }
            return null;
        };

        // Apply the style factory function to the feature layer.
        if (areaLevel1Layer != null) {
            areaLevel1Layer.setFeatureStyle(styleFactory);
        }
    }

    // Set default fill and border for all countries to ensure that they respond
    // to click events.
    @RequiresApi(Build.VERSION_CODES.O)
    private void styleCountryLayer() {
        FeatureLayer.StyleFactory styleFactory = feature -> new FeatureStyle.Builder()
                // Set the fill color for the country as white with a 10% opacity.
                // This requires minApi 26
                .fillColor(Color.argb(0.1f, 0f, 0f, 0f))
                // Set border color to solid black.
                .strokeColor(Color.BLACK)
                .build();

        // Apply the style factory function to the country feature layer.
        if (countryLayer != null) {
            countryLayer.setFeatureStyle(styleFactory);
        }
    }

    // Define the click event handler.
    @Override
    public void onFeatureClick(FeatureClickEvent event) {
        // Get the list of features affected by the click using
        // getPlaceIds() defined below.
        List<String> selectedPlaceIds = getPlaceIds(event.getFeatures());
        if (!selectedPlaceIds.isEmpty()) {
            FeatureLayer.StyleFactory styleFactory = feature -> {
                // Use PlaceFeature to get the placeID of the country.
                if (feature instanceof PlaceFeature) {
                    if (selectedPlaceIds.contains(((PlaceFeature) feature).getPlaceId())) {
                        return new FeatureStyle.Builder()
                                // Set the fill color to red.
                                .fillColor(Color.RED)
                                .build();
                    }
                }
                return null;
            };

            // Apply the style factory function to the feature layer.
            if (countryLayer != null) {
                countryLayer.setFeatureStyle(styleFactory);
            }
        }
    }

    // Get a List of place IDs from the FeatureClickEvent object.
    private List<String> getPlaceIds(List<Feature> features) {
        List<String> placeIds = new ArrayList<>();
        for (Feature feature : features) {
            if (feature instanceof PlaceFeature) {
                placeIds.add(((PlaceFeature) feature).getPlaceId());
            }
        }
        return placeIds;
    }
}
// [END maps_android_data_driven_styling_boundaries]