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
        description = "What it does: Loads a reusable map style created in Google Cloud Console using a designated Map ID.\nHow to see the effect: The map view renders with custom color schemes and branding configured in Cloud Console."
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
        description = "What it does: Applies custom fill colors, stroke widths, and geometry styles to road networks and land polygons.\nHow to see the effect: Highways and arterial roads render using custom colors and geometry widths defined in Cloud Console."
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
        description = "What it does: Toggles the visibility of specific base map feature layers (such as transit lines or water bodies).\nHow to see the effect: Target base map feature elements appear hidden or visible according to cloud style configuration."
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
        description = "What it does: Customizes typography, text label colors, and POI icon artwork across base map elements.\nHow to see the effect: Place names and POI icons display with customized colors and typography styling."
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
        description = "What it does: Configures scale-dependent visual styles that change dynamically across zoom levels.\nHow to see the effect: Zooming in or out dynamically shifts feature colors, label visibility, and geometry detail density."
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
        description = "What it does: Adjusts point-of-interest display density rules configured in Google Cloud Console.\nHow to see the effect: The map displays a higher or lower concentration of commercial and tourist POI icons."
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
        description = "What it does: Enables and styles 2D building footprints and 3D extruded building models.\nHow to see the effect: 3D architectural building shapes extrude visually from the map surface in dense urban areas."
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
        description = "What it does: Applies custom styling and highlight pin colors to major natural and urban landmarks.\nHow to see the effect: Famous landmarks (such as museums or monuments) display custom highlight pin icons."
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
