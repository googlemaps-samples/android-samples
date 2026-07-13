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
import com.example.snippets.java.R;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;

@SnippetGroup(
    title = "Cloud Customization",
    description = "Snippets demonstrating Google Cloud Console map customization capabilities loaded via Map ID."
)
public class CloudCustomizationSnippets {

    private final Context context;
    private final TrackedMap map;

    public CloudCustomizationSnippets(Context context, TrackedMap map) {
        this.context = context;
        this.map = map;
    }

    @SnippetItem(
        title = "1. Reusable Map Style",
        description = "Demonstrates loading a reusable, cross-platform map style created in Google Cloud Console."
    )
    public void loadReusableMapStyle() {
        // [START maps_android_cloud_reusable_style]
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(context.getString(R.string.map_id))
        );
        // [END maps_android_cloud_reusable_style]
    }

    @SnippetItem(
        title = "2. Style Roads and Polygons",
        description = "Loads a Map ID configured with custom road network and geometry polygon styles."
    )
    public void loadRoadAndPolygonStyling() {
        // [START maps_android_cloud_style_roads]
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(context.getString(R.string.map_id))
        );
        // [END maps_android_cloud_style_roads]
    }

    @SnippetItem(
        title = "3. Feature Visibility Toggling",
        description = "Loads a Map ID configured in Cloud Console to display or hide specific base map feature layers."
    )
    public void loadFeatureVisibilityStyling() {
        // [START maps_android_cloud_feature_visibility]
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(context.getString(R.string.map_id))
        );
        // [END maps_android_cloud_feature_visibility]
    }

    @SnippetItem(
        title = "4. Style Icons and Text Labels",
        description = "Loads a Map ID configured with custom typography, label colors, and POI icon styles."
    )
    public void loadIconAndLabelStyling() {
        // [START maps_android_cloud_style_labels]
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(context.getString(R.string.map_id))
        );
        // [END maps_android_cloud_style_labels]
    }

    @SnippetItem(
        title = "5. Zoom-Level Styling",
        description = "Loads a Map ID configured to apply distinct map styles dynamically across zoom levels."
    )
    public void loadZoomLevelStyling() {
        // [START maps_android_cloud_zoom_styling]
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(context.getString(R.string.map_id))
        );
        // [END maps_android_cloud_zoom_styling]
    }

    @SnippetItem(
        title = "6. POI Density Filtering",
        description = "Loads a Map ID configured with adjusted business and point-of-interest display density."
    )
    public void loadPoiDensityFiltering() {
        // [START maps_android_cloud_poi_density]
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(context.getString(R.string.map_id))
        );
        // [END maps_android_cloud_poi_density]
    }

    @SnippetItem(
        title = "7. Style Buildings",
        description = "Loads a Map ID configured with customized 2D and 3D building footprint styles."
    )
    public void loadBuildingStyling() {
        // [START maps_android_cloud_style_buildings]
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(context.getString(R.string.map_id))
        );
        // [END maps_android_cloud_style_buildings]
    }

    @SnippetItem(
        title = "8. Style Landmarks",
        description = "Loads a Map ID configured with specialized styling for prominent natural and urban landmarks."
    )
    public void loadLandmarkStyling() {
        // [START maps_android_cloud_style_landmarks]
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(context.getString(R.string.map_id))
        );
        // [END maps_android_cloud_style_landmarks]
    }
}
