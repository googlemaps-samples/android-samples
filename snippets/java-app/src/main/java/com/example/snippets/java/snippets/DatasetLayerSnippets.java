/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snippets.java.snippets;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import com.example.snippets.java.BuildConfig;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.common.R;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.DatasetFeature;
import com.google.android.gms.maps.model.FeatureLayer;
import com.google.android.gms.maps.model.FeatureLayerOptions;
import com.google.android.gms.maps.model.FeatureStyle;
import com.google.android.gms.maps.model.FeatureType;
import com.google.android.gms.maps.model.LatLng;
import java.util.Map;

@SnippetGroup(
    title = "Custom Geospatial Datasets",
    description = "Snippets demonstrating custom Cloud geospatial dataset feature layers, attribute styling, and click events."
)
public class DatasetLayerSnippets {

    private static final String TAG = DatasetLayerSnippets.class.getSimpleName();
    private final Context context;
    private final TrackedMap map;

    // Track Kyoto selected globalid state across clicks inside this snippet context
    private String kyotoClickedGlobalId = null;

    // Track Boulder styling toggle state
    private boolean boulderColorByDifficulty = true;

    public DatasetLayerSnippets(Context context, TrackedMap map) {
        this.context = context;
        this.map = map;
    }

    @SnippetItem(
        title = "1. Dataset - Boulder Trails",
        description = "What it does: Fetches a Cloud OSMP Boulder Trails dataset layer and dynamically styles trail lines by difficulty or dog regulations.\nHow to see the effect: Trail lines render in green (Easy), blue (Moderate), or red (Difficult), with line thickness indicating dog restrictions."
    )
    public void styleBoulderTrails() {
        String datasetId = BuildConfig.BOULDER_DATASET_ID;
        if (datasetId == null || datasetId.isEmpty() || datasetId.equals("YOUR_DATASET_ID")) {
            Toast.makeText(context, "Please configure BOULDER_DATASET_ID in secrets.properties", Toast.LENGTH_LONG).show();
            return;
        }

        // [START maps_android_dds_boulder_trails_java]
        // 1. Get the dataset feature layer
        FeatureLayer layer = map.getDelegate().getFeatureLayer(
            new FeatureLayerOptions.Builder()
                .featureType(FeatureType.DATASET)
                .datasetId(datasetId)
                .build()
        );

        // 2. Center the camera over Boulder OSMP Trails
        map.getDelegate().moveCamera(
            CameraUpdateFactory.newLatLngZoom(new LatLng(40.0150, -105.2705), 13.0f)
        );

        // 3. Setup toggle button inside the activity controls container
        boulderColorByDifficulty = true;
        android.app.Activity activity = (android.app.Activity) context;
        if (activity != null) {
            android.widget.LinearLayout container = activity.findViewById(R.id.custom_controls_container);
            if (container != null) {
                android.widget.Button toggleButton = new android.widget.Button(context);
                toggleButton.setText("Mode: Difficulty");
                toggleButton.setTextSize(11f);
                toggleButton.setAlpha(0.85f);
                float scale = context.getResources().getDisplayMetrics().density;
                int paddingPx = (int) (8 * scale + 0.5f);
                int paddingTopBottomPx = (int) (4 * scale + 0.5f);
                toggleButton.setPadding(paddingPx, paddingTopBottomPx, paddingPx, paddingTopBottomPx);
                toggleButton.setOnClickListener(v -> {
                    boulderColorByDifficulty = !boulderColorByDifficulty;
                    toggleButton.setText(boulderColorByDifficulty ? "Mode: Difficulty" : "Mode: Dog Leash");
                    applyBoulderStyling(layer);
                });
                container.addView(toggleButton);
                container.setVisibility(android.view.View.VISIBLE);
            }
        }

        // Apply initial styling
        applyBoulderStyling(layer);
        // [END maps_android_dds_boulder_trails_java]
    }

    private void applyBoulderStyling(FeatureLayer layer) {
        if (layer != null) {
            layer.setFeatureStyle(feature -> {
                if (feature instanceof DatasetFeature) {
                    DatasetFeature datasetFeature = (DatasetFeature) feature;
                    Map<String, String> attributes = datasetFeature.getDatasetAttributes();
                    String difficulty = attributes.get("OSMPTrailsOSMPDIFFICULTY");
                    String dogsAllowed = attributes.get("OSMPTrailsOSMPDOGREGGEN");

                    int strokeColor;
                    float strokeWidth = 3.0f;

                    if (boulderColorByDifficulty) {
                        // Mode 1: Color by difficulty
                        if ("Easy".equals(difficulty)) {
                            strokeColor = Color.GREEN;
                        } else if ("Moderate".equals(difficulty)) {
                            strokeColor = Color.BLUE;
                        } else if ("Difficult".equals(difficulty)) {
                            strokeColor = Color.RED;
                        } else {
                            strokeColor = Color.MAGENTA;
                        }

                        // If dogs are not allowed, make the line wider and slightly transparent
                        if ("No Dogs".equals(dogsAllowed)) {
                            strokeColor = ColorUtils.setAlphaComponent(strokeColor, 120);
                            strokeWidth = 6.0f;
                        }
                    } else {
                        // Mode 2: Color by dog regulations
                        if ("No Dogs".equals(dogsAllowed)) {
                            strokeColor = Color.RED;              // Prohibited
                        } else if ("LR".equals(dogsAllowed) || "LVS".equals(dogsAllowed)) {
                            strokeColor = Color.YELLOW;           // Leash Required / Conditional
                        } else if ("RV".equals(dogsAllowed)) {
                            strokeColor = Color.GREEN;            // Off Leash
                        } else {
                            strokeColor = Color.LTGRAY;           // Unknown
                        }
                        strokeWidth = 4.0f;
                    }

                    return new FeatureStyle.Builder()
                        .strokeColor(strokeColor)
                        .strokeWidth(strokeWidth)
                        .build();
                }
                return null;
            });
        }
    }

    @SnippetItem(
        title = "2. Dataset - NYC Squirrels",
        description = "What it does: Imports a Cloud Central Park Squirrel Sightings dataset layer and styles point features by fur color.\nHow to see the effect: Point markers display across Central Park colored by squirrel fur color (black, cinnamon, gray)."
    )
    public void styleNycSquirrels() {
        String datasetId = BuildConfig.NEW_YORK_DATASET_ID;
        if (datasetId == null || datasetId.isEmpty() || datasetId.equals("YOUR_DATASET_ID")) {
            Toast.makeText(context, "Please configure NEW_YORK_DATASET_ID in secrets.properties", Toast.LENGTH_LONG).show();
            return;
        }

        // [START maps_android_dds_nyc_squirrels_java]
        // 1. Get the dataset feature layer
        FeatureLayer layer = map.getDelegate().getFeatureLayer(
            new FeatureLayerOptions.Builder()
                .featureType(FeatureType.DATASET)
                .datasetId(datasetId)
                .build()
        );

        // 2. Center the camera over Central Park, New York
        map.getDelegate().moveCamera(
            CameraUpdateFactory.newLatLngZoom(new LatLng(40.786244, -73.962684), 14.0f)
        );

        // 3. Define styling rules based on squirrel fur color attributes
        if (layer != null) {
            layer.setFeatureStyle(feature -> {
                if (feature instanceof DatasetFeature) {
                    DatasetFeature datasetFeature = (DatasetFeature) feature;
                    Map<String, String> attributes = datasetFeature.getDatasetAttributes();
                    String furColor = attributes.get("Color");

                    int color;
                    if ("Black+".equals(furColor)) {
                        color = Color.BLACK;
                    } else if ("Cinnamon+".equals(furColor)) {
                        color = Color.rgb(210, 105, 30); // Chocolate brown
                    } else if ("Gray+".equals(furColor)) {
                        color = Color.GRAY;
                    } else {
                        color = Color.GREEN; // default
                    }

                    return new FeatureStyle.Builder()
                        .fillColor(color)
                        .strokeColor(Color.WHITE)
                        .pointRadius(8.0f)
                        .build();
                }
                return null;
            });
        }
        // [END maps_android_dds_nyc_squirrels_java]
    }

    @SnippetItem(
        title = "3. Dataset - Kyoto Temples (Clickable)",
        description = "What it does: Renders Cloud Kyoto temple boundary polygons and updates clicked polygon styling to highlight yellow.\nHow to see the effect: Temple grounds render in semi-transparent blue; tap any temple polygon to highlight it in yellow."
    )
    public void styleKyotoTemples() {
        String datasetId = BuildConfig.KYOTO_DATASET_ID;
        if (datasetId == null || datasetId.isEmpty() || datasetId.equals("YOUR_DATASET_ID")) {
            Toast.makeText(context, "Please configure KYOTO_DATASET_ID in secrets.properties", Toast.LENGTH_LONG).show();
            return;
        }

        // Reset click state
        kyotoClickedGlobalId = null;

        // [START maps_android_dds_kyoto_temples_java]
        // 1. Get the dataset feature layer
        FeatureLayer layer = map.getDelegate().getFeatureLayer(
            new FeatureLayerOptions.Builder()
                .featureType(FeatureType.DATASET)
                .datasetId(datasetId)
                .build()
        );

        // 2. Center the camera over Kyoto, Japan
        map.getDelegate().moveCamera(
            CameraUpdateFactory.newLatLngZoom(new LatLng(35.005081, 135.764385), 13.5f)
        );

        // Define styling application function
        Runnable applyStyles = new Runnable() {
            @Override
            public void run() {
                if (layer != null) {
                    layer.setFeatureStyle(feature -> {
                        if (feature instanceof DatasetFeature) {
                            DatasetFeature datasetFeature = (DatasetFeature) feature;
                            Map<String, String> attributes = datasetFeature.getDatasetAttributes();
                            String type = attributes.get("type");
                            String globalId = attributes.get("globalid");

                            int fillColor;
                            if ("temple".equals(type)) {
                                fillColor = ColorUtils.setAlphaComponent(Color.BLUE, 100);
                            } else {
                                fillColor = ColorUtils.setAlphaComponent(Color.GREEN, 80);
                            }

                            // Highlight clicked temple area in Yellow
                            if (globalId != null && globalId.equals(kyotoClickedGlobalId)) {
                                fillColor = ColorUtils.setAlphaComponent(Color.YELLOW, 180);
                            }

                            return new FeatureStyle.Builder()
                                .fillColor(fillColor)
                                .strokeColor(Color.BLACK)
                                .strokeWidth(2.0f)
                                .build();
                        }
                        return null;
                    });
                }
            }
        };

        // 3. Register click handler to select temple feature and toast its name
        if (layer != null) {
            layer.addOnFeatureClickListener(event -> {
                DatasetFeature clickedFeature = null;
                for (com.google.android.gms.maps.model.Feature f : event.getFeatures()) {
                    if (f instanceof DatasetFeature) {
                        clickedFeature = (DatasetFeature) f;
                        break;
                    }
                }
                if (clickedFeature != null) {
                    Map<String, String> attributes = clickedFeature.getDatasetAttributes();
                    String templeName = attributes.containsKey("name") ? attributes.get("name") : "Unknown Temple";
                    kyotoClickedGlobalId = attributes.get("globalid");
                    Toast.makeText(context, "Temple: " + templeName, Toast.LENGTH_SHORT).show();
                    applyStyles.run(); // Refresh styling state
                }
            });
        }

        // Apply initial styles
        applyStyles.run();
        // [END maps_android_dds_kyoto_temples_java]
    }
}
