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

import static java.lang.Math.round;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//noinspection UnusedImport
import com.example.common_ui.R; // <-- Keep this import

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This sample showcases how to use the Data-driven styling for boundaries. For more information
 * on how the Data-driven styling for boundaries work, check out the following link:
 * https://developers.google.com/maps/documentation/android-sdk/dds-boundaries/overview
 */
// [START maps_android_data_driven_styling_boundaries]
public class DataDrivenBoundariesActivity extends SamplesBaseActivity implements OnMapReadyCallback,
        FeatureLayer.OnFeatureClickListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = DataDrivenBoundariesActivity.class.getName();

    private static final LatLng HANA_HAWAII = new LatLng(20.7522, -155.9877); // Hana, Hawaii
    private static final LatLng CENTER_US = new LatLng(39.8283, -98.5795); // Approximate geographical center of the contiguous US

    private GoogleMap map;

    private FeatureLayer localityLayer = null;
    private FeatureLayer areaLevel1Layer = null;
    private FeatureLayer countryLayer = null;

    private final FeatureLayer.StyleFactory localityStyleFactory = getLocalityStyleFactory();
    private final FeatureLayer.StyleFactory countryStyleFactory = getCountryStyleFactory();
    private final FeatureLayer.StyleFactory areaLevel1StyleFactory = getAreaLevel1StyleFactory();

    // Which layers are currently enabled
    private boolean localityEnabled = true;
    private boolean adminAreaEnabled = false;
    private boolean countryEnabled = false;

    private final Set<String> selectedPlaceIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.data_driven_boundaries_demo);

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
        mapOptions.mapId(mapId);
        // 3. Create SupportMapFragment instance with options
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);

        // 4. Add the fragment to your FrameLayout container using FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_container, mapFragment); // Use the container ID from XML
        fragmentTransaction.commit();
        // --- End Programmatic Creation ---

        mapFragment.getMapAsync(this);

        findViewById(R.id.button_hawaii).setOnClickListener(view -> centerMapOnLocation(HANA_HAWAII, 11f));
        findViewById(R.id.button_us).setOnClickListener(view -> centerMapOnLocation(CENTER_US, 1f));

        applyInsets(findViewById(R.id.map_container));

        setupBoundarySelectorButton();

        // [START_EXCLUDE silent]
        applyInsets(findViewById(R.id.map_container));
        // [END_EXCLUDE]
    }

    private void setupBoundarySelectorButton() {
        MaterialButton stylingTypeButton = findViewById(R.id.button_feature_type);
        stylingTypeButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(this, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.boundary_types_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(this);

                popupMenu.getMenu().findItem(R.id.boundary_type_locality).setChecked(localityEnabled);
                popupMenu.getMenu().findItem(R.id.boundary_type_administrative_area_level_1).setChecked(adminAreaEnabled);
                popupMenu.getMenu().findItem(R.id.boundary_type_country).setChecked(countryEnabled);
                popupMenu.show();
        });
    }
    // [END_EXCLUDE]

    private void centerMapOnLocation(LatLng location, float zoomLevel) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
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

        // Gets the LOCALITY feature layer.
        localityLayer = googleMap.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.LOCALITY)
                        .build()
        );

        // Gets the ADMINISTRATIVE_AREA_LEVEL_1 feature layer.
        areaLevel1Layer = googleMap.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.ADMINISTRATIVE_AREA_LEVEL_1)
                        .build()
        );

        // Gets the COUNTRY feature layer.
        countryLayer = googleMap.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.COUNTRY)
                        .build()
        );
        countryLayer.addOnFeatureClickListener(this);

        centerMapOnLocation(HANA_HAWAII, 11f);

        // Apply the current set of styles.
        updateStyles();
    }

    /**
     * Updates the styles of the locality, area level 1, and country layers based on the current
     * state of the `localityEnabled`, `adminAreaEnabled`, and `countryEnabled` flags.
     * <p>
     * For each layer, if the corresponding flag is true, the layer's features will be styled using
     * the layer specific style factory function.
     */
    private void updateStyles() {
        if (localityLayer != null && areaLevel1Layer != null && countryLayer != null) {
            localityLayer.setFeatureStyle(localityEnabled ? localityStyleFactory : null);
            areaLevel1Layer.setFeatureStyle(adminAreaEnabled ? areaLevel1StyleFactory : null);
            if (countryEnabled) {
                countryLayer.setFeatureStyle(countryStyleFactory);
            } else {
                countryLayer.setFeatureStyle(null);
            }
        }
    }

    /**
     * Creates a StyleFactory for a FeatureLayer that styles Hana, HI on its Place ID.
     * <p>
     * This method defines a style factory that checks if a given feature is a {@link PlaceFeature}.
     * and if that feature matches "ChIJ0zQtYiWsVHkRk8lRoB1RNPo" (Hana, HI) applies a specific style.
     * Otherwise, it returns null, indicating no specific styling is applied.
     *
     * @return A {@link FeatureLayer.StyleFactory} instance that can be used to style features in a FeatureLayer.
     *         The factory returns a {@link FeatureStyle} for Hana, HI, and null for other features.
     */
    private static FeatureLayer.StyleFactory getLocalityStyleFactory() {
        int purple = 0x810FCB;
        // Define a style with purple fill at 50% opacity and
        // solid purple border.
        int fillColor = setAlphaValueOnColor(purple, 0.5f);
        int strokeColor = setAlphaValueOnColor(purple, 1f);

        return feature -> {
            // Check if the feature is an instance of PlaceFeature,
            // which contains a place ID.
            if (feature instanceof PlaceFeature placeFeature) {

                // Determine if the place ID is for Hana, HI.
                if ("ChIJ0zQtYiWsVHkRk8lRoB1RNPo".equals(placeFeature.getPlaceId())) {
                    // Use FeatureStyle.Builder to configure the FeatureStyle object
                    // returned by the style factory function.
                    return new FeatureStyle.Builder()
                            .fillColor(fillColor)
                            .strokeColor(strokeColor)
                            .build();
                }
            }
            return null;
        };
    }

    /**
     * Creates a StyleFactory for area level 1 features (e.g., states, provinces).
     * <p>
     * This factory provides a semi-transparent fill color for each area level 1 feature.
     * <p>
     * @return A StyleFactory that can be used to style area level 1 features on a map.
     */
    private static FeatureLayer.StyleFactory getAreaLevel1StyleFactory() {
        int alpha = (int) (255 * 0.25);

        return feature -> {
            if (feature instanceof PlaceFeature placeFeature) {

                // Return a hueColor in the range [-299,299]. If the value is
                // negative, add 300 to make the value positive.
                int hueColor = placeFeature.getPlaceId().hashCode() % 300;
                if (hueColor < 0) {
                    hueColor += 300;
                }
                return new FeatureStyle.Builder()
                        // Set the fill color for the state based on the hashed hue color.
                        .fillColor(Color.HSVToColor(alpha, new float[]{hueColor, 1f, 1f}))
                        .build();
            }
            return null;
        };
    }

    /**
     * Creates a StyleFactory for styling country features on a FeatureLayer highlighting selected
     * countries. Selection is determined via the selectedPlaceIds set.
     * <p>
     * *Note:* If the set of selected countries changes, this function must be called to update the
     * styling.
     * <p>
     * @return A FeatureLayer.StyleFactory that can be used to style country features.
     */
    private FeatureLayer.StyleFactory getCountryStyleFactory() {
        int defaultFillColor = setAlphaValueOnColor(Color.BLACK, 0.1f);
        int selectedFillColor = setAlphaValueOnColor(Color.RED, 0.33f);
        return feature -> {
            if (feature instanceof PlaceFeature) {
                int fillColor = selectedPlaceIds.contains(((PlaceFeature) feature).getPlaceId()) ? selectedFillColor : defaultFillColor;
                FeatureStyle.Builder build = new FeatureStyle.Builder();
                return build.fillColor(fillColor).strokeColor(Color.BLACK).build();
            }
            return null;
        };
    }

    /**
     * Called when a feature is clicked on the map.  It is only applied to the country layer.
     * <p>
     * Each time a country is clicked, its place ID is added to the selectedPlaceIds set or removed
     * if it was already present.  Each time the set is
     * <p>
     */
    @Override
    public void onFeatureClick(@NonNull FeatureClickEvent event) {
        // Get the list of features affected by the click using
        // getPlaceIds() defined below.
        List<String> newSelectedPlaceIds = getPlaceIds(event.getFeatures());

        for (String placeId : newSelectedPlaceIds) {
            if (selectedPlaceIds.contains(placeId)) {
                selectedPlaceIds.remove(placeId);
            } else {
                selectedPlaceIds.add(placeId);
            }
        }

        // Reset the feature styling
        countryLayer.setFeatureStyle(countryStyleFactory);
    }

    // Gets a List of place IDs from the FeatureClickEvent object.
    private List<String> getPlaceIds(List<Feature> features) {
        List<String> placeIds = new ArrayList<>();
        for (Feature feature : features) {
            if (feature instanceof PlaceFeature) {
                placeIds.add(((PlaceFeature) feature).getPlaceId());
            }
        }
        return placeIds;
    }

    private static int setAlphaValueOnColor(int color, float alpha) {
        return (color & 0x00ffffff) | (round(alpha * 255) << 24);
    }

    /**
     * Handles the click events for menu items in the boundary type selection menu.
     * This method is called when a user selects a boundary type (locality, administrative area, or country) from the menu.
     * It toggles the checked state of the selected menu item and updates the corresponding boolean flags (localityEnabled, adminAreaEnabled, countryEnabled).
     * Finally, it calls the {@link #updateStyles()} method to reflect the changes in the map's display.
     *
     * @param item The MenuItem that was clicked.
     * @return True if the event was handled, false otherwise. In this case it always return true if one of the correct items was selected.
     */ // [START_EXCLUDE silent]
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: " + item.getItemId());
        int id = item.getItemId();
        boolean result = false;

        if (id == R.id.boundary_type_locality) {
            item.setChecked(!item.isChecked());
            localityEnabled = item.isChecked();
            result = true;
        } else if (id == R.id.boundary_type_administrative_area_level_1) {
            item.setChecked(!item.isChecked());
            adminAreaEnabled = item.isChecked();
            result = true;
        } else if (id == R.id.boundary_type_country) {
            item.setChecked(!item.isChecked());
            countryEnabled = item.isChecked();
            result = true;
        }

        updateStyles();

        return result;
    }
    // [END_EXCLUDE]
}
// [END maps_android_data_driven_styling_boundaries]