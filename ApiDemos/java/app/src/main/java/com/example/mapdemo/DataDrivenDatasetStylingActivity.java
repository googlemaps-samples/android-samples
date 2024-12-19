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
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.DatasetFeature;
import com.google.android.gms.maps.model.Feature;
import com.google.android.gms.maps.model.FeatureClickEvent;
import com.google.android.gms.maps.model.FeatureLayer;
import com.google.android.gms.maps.model.FeatureLayerOptions;
import com.google.android.gms.maps.model.FeatureStyle;
import com.google.android.gms.maps.model.FeatureType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapCapabilities;

import java.util.List;
import java.util.Map;

/**
 * This sample showcases how to use the Data-driven styling for datasets. For more information
 * on how the Data-driven styling for boundaries work, check out the following link:
 * https://developers.google.com/maps/documentation/android-sdk/dds-datasets/overview
 */
// [START maps_android_data_driven_styling_datasets]
public class DataDrivenDatasetStylingActivity extends AppCompatActivity implements OnMapReadyCallback, FeatureLayer.OnFeatureClickListener {

    private static final LatLng SEATTLE = new LatLng(47.6062, -122.3321);
    private static final LatLng NEW_YORK = new LatLng(40.7128, -74.0060);
    private static final LatLng FALSE_BAY_CAPE_TOWN = new LatLng(-34.1476, 18.6722);

    private static final float ZOOM_LEVEL = 13.5f;
    private static final String TAG = DataDrivenDatasetStylingActivity.class.getName();
    private static FeatureLayer datasetLayer = null;
    private GoogleMap map;
    private String lastGlobalId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_driven_styling_demo);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.button_seattle).setOnClickListener(v -> centerMapOnLocation(SEATTLE)); // Seattle coordinates
        findViewById(R.id.button_ny).setOnClickListener(v -> centerMapOnLocation(NEW_YORK)); // New York coordinates
        findViewById(R.id.button_south_africa).setOnClickListener(v -> centerMapOnLocation(FALSE_BAY_CAPE_TOWN)); // False Bay, Cape Town coordinates

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEATTLE, ZOOM_LEVEL));

        MapCapabilities capabilities = map.getMapCapabilities();
        Log.d(TAG, "Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable());

        datasetLayer = map.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.DATASET)
                        .datasetId("YOUR-DATASET-ID")
                        .build()
        );

        datasetLayer.addOnFeatureClickListener(this);

        styleDatasetsLayer();

        // Uncommenting these lines will style the polygons or polylines.
        // styleDatasetsLayerPolygon();
        // styleDatasetsLayerPolyline();
    }

    private void styleDatasetsLayer() {
        FeatureLayer.StyleFactory styleFactory = feature -> {
            int fillColor = Color.GREEN;
            int strokeColor = Color.YELLOW;
            float pointRadius = 8F;

            if (feature instanceof DatasetFeature) {
                Map<String, String> furColors = ((DatasetFeature) feature).getDatasetAttributes();
                String furColor = furColors.get("CombinationofPrimaryandHighlightColor");

                if (furColor != null) {
                    switch (furColor) {
                        case "Black+":
                            fillColor = Color.BLACK;
                            strokeColor = Color.BLACK;
                            break;
                        case "Cinnamon+":
                            fillColor = 0xFF8B0000; // dark red color
                            strokeColor = 0xFF8B0000;
                            break;
                        case "Cinnamon+Gray":
                            fillColor = 0xFF8B0000; // dark red color
                            strokeColor = 0xFF8B0000;
                            pointRadius = 6F;
                            break;
                        case "Cinnamon+White":
                            fillColor = 0xFF8B0000; // dark red color
                            strokeColor = Color.WHITE;
                            pointRadius = 6F;
                            break;
                        case "Gray+":
                            fillColor = Color.GRAY;
                            break;
                        case "Gray+Cinnamon":
                            fillColor = Color.GRAY;
                            strokeColor = 0xFF8B0000; // dark red color
                            pointRadius = 6F;
                            break;
                        case "Gray+Cinnamon, White":
                            fillColor = Color.LTGRAY;
                            strokeColor = 0xFF8B0000; // dark red color
                            pointRadius = 6F;
                            break;
                        case "Gray+White":
                            fillColor = Color.GRAY;
                            strokeColor = Color.WHITE;
                            pointRadius = 6F;
                            break;
                    }
                }

                return new FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .pointRadius(pointRadius)
                        .build();
            }
            return null;
        };

        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }

    private void styleDatasetsLayerPolygon() {
        FeatureLayer.StyleFactory styleFactory = feature -> {
            if (feature instanceof DatasetFeature) {
                Map<String, String> typeCategories = ((DatasetFeature) feature).getDatasetAttributes();
                String typeCategory = typeCategories.get("typecategory");

                int fillColor;
                int strokeColor;

                if ("Undeveloped".equals(typeCategory)) {
                    fillColor = Color.BLUE;
                    strokeColor = Color.BLUE;
                } else if ("Parkway".equals(typeCategory)) {
                    fillColor = Color.RED;
                    strokeColor = Color.RED;
                } else {
                    fillColor = Color.GREEN;
                    strokeColor = Color.GREEN;
                }

                return new FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .strokeWidth(2F)
                        .build();
            }
            return null;
        };

        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }

    private void styleDatasetsLayerPolyline() {
        FeatureLayer.StyleFactory styleFactory = feature -> {
            if (feature instanceof DatasetFeature) {
                return new FeatureStyle.Builder()
                        .strokeColor(0xff00ff00)
                        .strokeWidth(4F)
                        .build();
            }
            return null;
        };

        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }

    private void centerMapOnLocation(LatLng location) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL));
    }

    @Override
    public void onFeatureClick(FeatureClickEvent event) {
        List<Feature> clickFeatures = event.getFeatures();
        lastGlobalId = null;
        if (clickFeatures.get(0) instanceof DatasetFeature) {
            lastGlobalId = ((DatasetFeature) clickFeatures.get(0)).getDatasetAttributes().get("globalid");
            styleDatasetsLayerClickEvent();
        }
    }

    private void styleDatasetsLayerClickEvent() {
        FeatureLayer.StyleFactory styleFactory = feature -> {
            if (feature instanceof DatasetFeature) {
                Map<String, String> globalIDs = ((DatasetFeature) feature).getDatasetAttributes();
                String globalID = globalIDs.get("globalid");
                int fillColor = Color.GREEN;
                int strokeColor = Color.GREEN;

                if (globalID != null && globalID.equals(lastGlobalId)) {
                    fillColor = Color.BLUE;
                    strokeColor = Color.BLUE;
                }

                return new FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .build();
            }
            return null;
        };

        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }
}
// [END maps_android_data_driven_styling_datasets]