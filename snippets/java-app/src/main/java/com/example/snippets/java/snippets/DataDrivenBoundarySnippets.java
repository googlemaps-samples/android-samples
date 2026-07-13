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
import androidx.core.graphics.ColorUtils;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.FeatureLayer;
import com.google.android.gms.maps.model.FeatureLayerOptions;
import com.google.android.gms.maps.model.FeatureStyle;
import com.google.android.gms.maps.model.FeatureType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PlaceFeature;
import java.util.HashSet;
import java.util.Set;

@SnippetGroup(
    title = "Data-Driven Boundary Styling",
    description = "Snippets demonstrating administrative boundary feature layers, polygon styling, and click events."
)
public class DataDrivenBoundarySnippets {

    private static final String TAG = DataDrivenBoundarySnippets.class.getSimpleName();
    private final Context context;
    private final TrackedMap map;

    // Track selected country place IDs for the interactive country selection snippet
    private final Set<String> selectedCountryPlaceIds = new HashSet<>();

    public DataDrivenBoundarySnippets(Context context, TrackedMap map) {
        this.context = context;
        this.map = map;
    }

    @SnippetItem(
        title = "1. Boundaries - Localities (Hana, HI)",
        description = "Loads LOCALITY layer. Styles Hana, Hawaii (Place ID: ChIJ0zQtYiWsVHkRk8lRoB1RNPo) with purple fill and border. Centers camera."
    )
    public void styleLocalityBoundary() {
        // [START maps_android_dds_locality_boundary_java]
        // 1. Get the LOCALITY feature layer
        FeatureLayer layer = map.getDelegate().getFeatureLayer(
            new FeatureLayerOptions.Builder()
                .featureType(FeatureType.LOCALITY)
                .build()
        );

        // 2. Center the camera over Hana, Hawaii
        map.getDelegate().moveCamera(
            CameraUpdateFactory.newLatLngZoom(new LatLng(20.7522, -155.9877), 11.0f)
        );

        // 3. Define styling: 50% opacity purple fill, opaque purple stroke
        int purple = 0x810FCB;
        int fillColor = ColorUtils.setAlphaComponent(purple, (int) (0.5f * 255));
        int strokeColor = ColorUtils.setAlphaComponent(purple, 255);

        if (layer != null) {
            layer.setFeatureStyle(feature -> {
                if (feature instanceof PlaceFeature && "ChIJ0zQtYiWsVHkRk8lRoB1RNPo".equals(((PlaceFeature) feature).getPlaceId())) {
                    return new FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .strokeWidth(3.0f)
                        .build();
                }
                return null;
            });
        }
        // [END maps_android_dds_locality_boundary_java]
    }

    @SnippetItem(
        title = "2. Boundaries - Admin Area 1 (States)",
        description = "Loads ADMINISTRATIVE_AREA_LEVEL_1 layer. Styles state/provincial boundaries with random colors based on Place ID hashes. Centers over US."
    )
    public void styleStateBoundaries() {
        // [START maps_android_dds_state_boundaries_java]
        // 1. Get the administrative area level 1 feature layer
        FeatureLayer layer = map.getDelegate().getFeatureLayer(
            new FeatureLayerOptions.Builder()
                .featureType(FeatureType.ADMINISTRATIVE_AREA_LEVEL_1)
                .build()
        );

        // 2. Center the camera over the USA
        map.getDelegate().moveCamera(
            CameraUpdateFactory.newLatLngZoom(new LatLng(39.8283, -98.5795), 4.0f)
        );

        // 3. Define styling: Assign pseudo-random hue fills dynamically based on place ID
        int alpha = (int) (255 * 0.25); // 25% opacity
        if (layer != null) {
            layer.setFeatureStyle(feature -> {
                if (feature instanceof PlaceFeature) {
                    PlaceFeature placeFeature = (PlaceFeature) feature;
                    int hueColor = placeFeature.getPlaceId().hashCode() % 300;
                    if (hueColor < 0) hueColor += 300;
                    return new FeatureStyle.Builder()
                        .fillColor(Color.HSVToColor(alpha, new float[]{hueColor, 1.0f, 1.0f}))
                        .strokeColor(Color.DKGRAY)
                        .strokeWidth(1.5f)
                        .build();
                }
                return null;
            });
        }
        // [END maps_android_dds_state_boundaries_java]
    }

    @SnippetItem(
        title = "3. Boundaries - Countries (Interactive)",
        description = "Loads COUNTRY layer. Renders countries with 10% black fill. Taps toggle country coloring between light black and 33% opaque red."
    )
    public void styleCountryInteractive() {
        // [START maps_android_dds_country_interactive_java]
        // 1. Get the COUNTRY feature layer
        FeatureLayer layer = map.getDelegate().getFeatureLayer(
            new FeatureLayerOptions.Builder()
                .featureType(FeatureType.COUNTRY)
                .build()
        );

        // 2. Center the camera globally
        map.getDelegate().moveCamera(
            CameraUpdateFactory.newLatLngZoom(new LatLng(0.0, 0.0), 2.0f)
        );

        // Clear tracking set
        selectedCountryPlaceIds.clear();

        int defaultFillColor = ColorUtils.setAlphaComponent(Color.BLACK, (int) (0.1f * 255));
        int selectedFillColor = ColorUtils.setAlphaComponent(Color.RED, (int) (0.33f * 255));

        // Define styling application function
        Runnable applyStyles = new Runnable() {
            @Override
            public void run() {
                if (layer != null) {
                    layer.setFeatureStyle(feature -> {
                        if (feature instanceof PlaceFeature) {
                            PlaceFeature placeFeature = (PlaceFeature) feature;
                            int fillColor = selectedCountryPlaceIds.contains(placeFeature.getPlaceId())
                                ? selectedFillColor : defaultFillColor;
                            return new FeatureStyle.Builder()
                                .fillColor(fillColor)
                                .strokeColor(Color.BLACK)
                                .strokeWidth(1.0f)
                                .build();
                        }
                        return null;
                    });
                }
            }
        };

        // 3. Register feature click listener to toggle selections and toast Place ID
        if (layer != null) {
            layer.addOnFeatureClickListener(event -> {
                boolean changed = false;
                for (com.google.android.gms.maps.model.Feature f : event.getFeatures()) {
                    if (f instanceof PlaceFeature) {
                        PlaceFeature placeFeature = (PlaceFeature) f;
                        String placeId = placeFeature.getPlaceId();
                        if (selectedCountryPlaceIds.contains(placeId)) {
                            selectedCountryPlaceIds.remove(placeId);
                        } else {
                            selectedCountryPlaceIds.add(placeId);
                        }
                        changed = true;
                        Toast.makeText(context, "Country: " + placeId, Toast.LENGTH_SHORT).show();
                    }
                }
                if (changed) {
                    applyStyles.run(); // Refresh styling state
                }
            });
        }

        // Apply initial styles
        applyStyles.run();
        // [END maps_android_dds_country_interactive_java]
    }
}
