// Copyright 2025 Google LLC
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
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//noinspection UnusedImport
import com.example.common_ui.R; // <-- Keep this import

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
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
 * <p>
 * This is meant to work with the datasets in the res/raw directory.
 */
// [START maps_android_data_driven_styling_datasets]
public class DataDrivenDatasetStylingActivity extends SamplesBaseActivity implements OnMapReadyCallback, FeatureLayer.OnFeatureClickListener {
    private record DataSet(
            String label,
            String datasetId,
            LatLng location,
            float zoomLevel,
            DataDrivenDatasetStylingActivity.DataSet.StylingCallback callback) {
            public interface StylingCallback {
                void styleDatasetLayer();
            }
    }

    /**
     * An array of DataSet objects representing different geographic locations and their associated data.
     * Each DataSet contains:
     *  - A human-readable name (e.g., "Boulder", "New York").
     *  - A unique Dataset ID, which should correspond to a dataset id in the Datasets console tab.
     *  - The central latitude and longitude coordinates (LatLng) of the location.
     *  - A styling function (method reference) that defines how to style the data from that dataset on a map.
     * <p>
     * This array is used to configure which datasets are available for display and how they should be presented.
     * Each element of the array should be a new DataSet object.
     * Modify the constructor arguments of each DataSet to define your specific data and styles.
     * The styling function will receive a `Layer` to which it can add elements.
     * <p>
     * Example:
     *   - `new DataSet("Boulder", "Boulder-DataSet-Id", new LatLng(40.0150, -105.2705), this::styleBoulderDatasetLayer)`
     *     This creates a DataSet for Boulder, identified by "Boulder-DataSet-Id", centered on the given coordinates,
     *     and styled using the `styleBoulderDatasetLayer` method.
     * <p>
     * Note: We have use the secrets plugin to allow us to configure the Dataset IDs in our secrets.properties file.
     */
    private final DataSet[] dataSets = new DataSet[] {
            new DataSet("Boulder", BuildConfig.BOULDER_DATASET_ID, new LatLng(40.0150, -105.2705), 11f, this::styleBoulderDatasetLayer),
            new DataSet("New York", BuildConfig.NEW_YORK_DATASET_ID, new LatLng(40.786244, -73.962684), 14f, this::styleNYCDatasetLayer),
            new DataSet("Kyoto", BuildConfig.KYOTO_DATASET_ID, new LatLng(35.005081, 135.764385), 13.5f, this::styleKyotoDatasetsLayer),
    };

    private DataSet findDataSetByLabel(String label) {
        for (DataSet dataSet : dataSets) {
            if (dataSet.label().equalsIgnoreCase(label)) { // Case-insensitive comparison
                return dataSet;
            }
        }
        return null; // Return null if no match is found
    }

    private static final String TAG = DataDrivenDatasetStylingActivity.class.getName();
    private static FeatureLayer datasetLayer = null;
    private GoogleMap map;
    private String lastGlobalId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_driven_styling_demo);

        // [START_EXCLUDE silent]
        // 1. Get the Application instance and cast it
        ApiDemoApplication app = (ApiDemoApplication) getApplication();

        // 2. Call the getMapId() method
        String mapId = app.getMapId();

        if (mapId == null) {
            finish();
            return;
        }
        // [END_EXCLUDE]

        // --- Programmatically Create and Add Map Fragment ---
        // 1. Create GoogleMapOptions
        GoogleMapOptions mapOptions = new GoogleMapOptions();

        // 2. Set the mapId from the secrets.properties file
        mapOptions.mapId(BuildConfig.MAP_ID); // Use the mapId retrieved earlier

        // 3. Create SupportMapFragment instance with options
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);
        mapFragment.getMapAsync(this);

        // 4. Add the fragment to your FrameLayout container using FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_container, mapFragment); // Use the container ID from XML
        fragmentTransaction.commit();
        // --- End Programmatic Creation ---


        int[] buttonIds = {R.id.button_boulder, R.id.button_ny, R.id.button_kyoto};
        for (int buttonId : buttonIds) {
            findViewById(buttonId).setOnClickListener(view -> switchDataSet(((Button) view).getText().toString()));
        }

        applyInsets(findViewById(R.id.map_container));
    }

    /**
     * Switches the currently displayed dataset to the one specified by the provided label.
     * <p>
     * This method attempts to find a DataSet object associated with the given label.
     * If a matching DataSet is found, it updates the map's feature layer to display the
     * data from that dataset. It also applies styling to the new dataset layer and centers
     * the map on the dataset's specified location.
     * <p>
     * If no matching DataSet is found, a toast message is displayed indicating the failure.
     * <p>
     * @param label The label of the dataset to switch to. This label should correspond to a
     *              dataset that has been previously added or loaded.
     */
    private void switchDataSet(String label) {
        DataSet dataSet = findDataSetByLabel(label);
        if (dataSet == null) {
            Toast.makeText(this, "Failed to find dataset" + label, Toast.LENGTH_SHORT).show();
        } else {
            datasetLayer = map.getFeatureLayer(
                    new FeatureLayerOptions.Builder()
                            .featureType(FeatureType.DATASET)
                            .datasetId(dataSet.datasetId())
                            .build()
            );
            dataSet.callback.styleDatasetLayer();
            centerMapOnLocation(dataSet.location(), dataSet.zoomLevel());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;

        MapCapabilities capabilities = map.getMapCapabilities();
        Log.d(TAG, "Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable());
        if (!capabilities.isDataDrivenStylingAvailable()) {
            Toast.makeText(
                    this,
                    "Data-driven Styling is not available.  See README.md for instructions.",
                    Toast.LENGTH_LONG
            ).show();
        }

        // Switch to the default dataset which must happen before adding the feature click listener
        switchDataSet("Boulder");

        datasetLayer.addOnFeatureClickListener(this);
    }

    private void styleNYCDatasetLayer() {
        FeatureLayer.StyleFactory styleFactory = feature -> {
            int fillColor = Color.GREEN;
            int strokeColor = Color.YELLOW;
            float pointRadius = 12F;

            if (feature instanceof DatasetFeature) {
                Map<String, String> furColors = ((DatasetFeature) feature).getDatasetAttributes();
                String furColor = furColors.get("Color");

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
                            pointRadius = 10F;
                            break;
                        case "Cinnamon+White":
                            fillColor = 0xFF8B0000; // dark red color
                            strokeColor = Color.WHITE;
                            pointRadius = 10F;
                            break;
                        case "Gray+":
                            fillColor = Color.GRAY;
                            break;
                        case "Gray+Cinnamon":
                            fillColor = Color.GRAY;
                            strokeColor = 0xFF8B0000; // dark red color
                            pointRadius = 10F;
                            break;
                        case "Gray+Cinnamon, White":
                            fillColor = Color.LTGRAY;
                            strokeColor = 0xFF8B0000; // dark red color
                            pointRadius = 10F;
                            break;
                        case "Gray+White":
                            fillColor = Color.GRAY;
                            strokeColor = Color.WHITE;
                            pointRadius = 10F;
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

    private void styleKyotoDatasetsLayer() {
        // Create the style factory function.
        FeatureLayer.StyleFactory styleFactory = feature -> {
            // Check if the feature is an instance of DatasetFeature.
            if (feature instanceof DatasetFeature datasetFeature) {
                // Determine the value of the typecategory attribute.
                Map<String, String> typeCategories = datasetFeature.getDatasetAttributes();
                String typeCategory = typeCategories.get("type");

                // Set default colors to green.
                int fillColor;
                int strokeColor;

                if ("temple".equals(typeCategory)) {
                    // Color temples areas blue.
                    fillColor = Color.BLUE;
                    strokeColor = Color.BLUE;
                } else {
                    // Color all other areas green.
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

        // Apply the style factory function to the feature layer.
        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }

    private void styleBoulderDatasetLayer() {
        final int EASY = Color.GREEN;
        final int MODERATE = Color.BLUE;
        final int DIFFICULT = Color.RED;

        // Create the style factory function.
        FeatureLayer.StyleFactory styleFactory = feature -> {
            // Set default colors to yellow and point radius to 8.
            int fillColor;
            int strokeColor;
            float pointRadius = 8F;
            float strokeWidth = 3F;

            // Check if the feature is an instance of DatasetFeature.
            if (feature instanceof DatasetFeature datasetFeature) {
                Map<String, String> attributes = datasetFeature.getDatasetAttributes();
                String difficulty = attributes.get("OSMPTrailsOSMPDIFFICULTY");
                String name = attributes.get("OSMPTrailsOSMPTRAILNAME");
                String dogsAllowed = attributes.get("OSMPTrailsOSMPDOGREGGEN");

                if ("Easy".equals(difficulty)) {
                    fillColor = EASY;
                } else if ("Moderate".equals(difficulty)) {
                    fillColor = MODERATE;
                } else if ("Difficult".equals(difficulty)) {
                    fillColor = DIFFICULT;
                } else {
                    Log.w(TAG, name + " -> Unknown difficulty: " + difficulty);
                    fillColor = Color.MAGENTA;
                }

                if ("No Dogs".equals(dogsAllowed)) {
                    fillColor = ColorUtils.setAlphaComponent(fillColor, 66);
                    strokeWidth = 5f;
                } else if ("LVS".equals(dogsAllowed)) {
                    // No change needed
                } else if ("LR".equals(dogsAllowed) || "RV".equals(dogsAllowed)) {
                    fillColor = ColorUtils.setAlphaComponent(fillColor, 75);
                } else {
                    Log.w(TAG, name + " -> Unknown dogs reg: " + dogsAllowed);
                }

                strokeColor = fillColor;

                return new FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .pointRadius(pointRadius)
                        .strokeWidth(strokeWidth)
                        .build();
            }
            return null;
        };

        // Apply the style factory function to the feature layer.
        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }


    private void centerMapOnLocation(LatLng location, float zoomLevel) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
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

