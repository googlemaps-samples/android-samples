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
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.example.snippets.java.R;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

@SnippetGroup(
        title = "Map Initialization",
        description = "Snippets showing how to initialize, configure map options, types, and renderers."
)
public class MapInitSnippets {

    private final Context context;
    private final TrackedMap map;

    public MapInitSnippets(Context context, TrackedMap map) {
        this.context = context;
        this.map = map;
    }

    @SnippetItem(
            title = "1. Basic Map Activity",
            description = "What it does: Initializes an interactive map centered on Sydney, Australia and adds a title marker.\nHow to see the effect: The viewport opens directly on Sydney with a default red marker at LatLng(-34.0, 151.0)."
    )
    public void basicMap() {
        //[START maps_android_mapsactivity]
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //[END maps_android_mapsactivity]
    }

    @SnippetItem(
            title = "2. Map Fragment Transaction",
            description = "What it does: Dynamically adds a SupportMapFragment into the Activity view hierarchy programmatically.\nHow to see the effect: A new map fragment view is instantiated and rendered into the container layout frame."
    )
    public void mapFragment() {
        // [START maps_android_map_fragment]
        if (context instanceof FragmentActivity) {
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            ((FragmentActivity) context).getSupportFragmentManager()
                .beginTransaction()
                .add(com.example.snippets.common.R.id.map_container, mapFragment)
                .commit();
        }
        // [END maps_android_map_fragment]
    }

    @SnippetItem(
            title = "3. Set Map Type",
            description = "What it does: Sets the map type to Hybrid (satellite imagery overlaid with road and landmark labels).\nHow to see the effect: Map visual tiles switch from vector lines to satellite imagery with label overlays."
    )
    public void mapType() {
        // [START maps_android_map_type]
        // Sets the map type to be "hybrid"
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setIndoorEnabled(true);
        boolean isIndoor = map.isIndoorEnabled();
        // [END maps_android_map_type]
    }

    @SnippetItem(
            title = "4. Google Map Options",
            description = "What it does: Configures initial map options programmatically (Satellite map type, disabled compass, tilt, and rotation gestures).\nHow to see the effect: The map opens in satellite view with rotation and tilt touch gestures disabled."
    )
    public void googleMapOptions() {
        // [START maps_android_google_map_options]
        GoogleMapOptions options = new GoogleMapOptions();
        // [END maps_android_google_map_options]

        // [START maps_android_google_map_options_configure]
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
            .compassEnabled(false)
            .rotateGesturesEnabled(false)
            .tiltGesturesEnabled(false);
        // [END maps_android_google_map_options_configure]
    }

    @SnippetItem(
            title = "5. Support Map Fragment Map ID",
            description = "What it does: Configures a SupportMapFragment with a Cloud Map ID for cloud-based map styling.\nHow to see the effect: The map fragment binds to Google Cloud Console custom styling rules associated with the Map ID."
    )
    public void fragmentMapId() {
        // [START maps_android_support_map_fragment_map_id]
        GoogleMapOptions options = new GoogleMapOptions()
            .mapId("YOUR_MAP_ID");
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        // [END maps_android_support_map_fragment_map_id]
    }

    @SnippetItem(
            title = "6. MapView Map ID",
            description = "What it does: Instantiates a programmatic MapView bound to a Cloud Map ID.\nHow to see the effect: The custom MapView renders with cloud styling and feature visibility configured in Cloud Console."
    )
    public void mapViewMapId() {
        // [START maps_android_mapview_map_id]
        GoogleMapOptions options = new GoogleMapOptions()
            .mapId("YOUR_MAP_ID");
        MapView mapView = new MapView(context, options);
        // [END maps_android_mapview_map_id]
    }

    @SnippetItem(
            title = "7. Lite Mode Options",
            description = "What it does: Enables Lite Mode on GoogleMapOptions to render a lightweight static map image.\nHow to see the effect: The map renders as a low-memory static image optimized for performance-constrained lists."
    )
    public void liteMode() {
        // [START maps_android_lite_mode_options]
        GoogleMapOptions options = new GoogleMapOptions()
            .liteMode(true);
        // [END maps_android_lite_mode_options]
    }

    @SnippetItem(
            title = "8. Cloud-based Map Styling",
            description = "What it does: Loads a MapFragment initialized with a Map ID stored in app resources.\nHow to see the effect: Custom styling, feature visibility, and color rules configured in Cloud Console are applied to the map."
    )
    public void cloudBasedMapStyling() {
        // [START maps_android_cloud_based_map_styling]
        MapFragment mapFragment = MapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(context.getResources().getString(R.string.map_id)));
        // [END maps_android_cloud_based_map_styling]
    }

    @SnippetItem(
            title = "9. Renderer Opt-In",
            description = "What it does: Requests the latest Maps SDK vector map renderer version via MapsInitializer.\nHow to see the effect: Inspect Android Logcat ('MapsDemo') to confirm whether Renderer.LATEST or Renderer.LEGACY is active."
    )
    public void rendererOptIn() {
        // [START maps_android_renderer_opt_in]
        MapsInitializer.initialize(context.getApplicationContext(), MapsInitializer.Renderer.LATEST, renderer -> {
            switch (renderer) {
                case LATEST:
                    Log.d("MapsDemo", "The latest version of the renderer is used.");
                    break;
                case LEGACY:
                    Log.d("MapsDemo", "The legacy version of the renderer is used.");
                    break;
            }
        });
        // [END maps_android_renderer_opt_in]
    }

    @SnippetItem(
            title = "10. Set Map Color Scheme",
            description = "What it does: Configures the map color scheme (Dark Mode / Light Mode / Follow System).\nHow to see the effect: Map visual colors shift to dark palette styling matching dark mode application preferences."
    )
    public void setMapColorScheme() {
        // [START maps_android_map_color_scheme]
        GoogleMapOptions options = new GoogleMapOptions()
            .mapColorScheme(com.google.android.gms.maps.model.MapColorScheme.DARK);
        // [END maps_android_map_color_scheme]
    }

    @SnippetItem(
            title = "11. Enable Traffic Layer",
            description = "What it does: Enables the real-time traffic overlay layer on the map surface.\nHow to see the effect: Colored congestion lines (green/yellow/red) overlay major highways displaying live traffic conditions."
    )
    public void enableTrafficLayer() {
        // [START maps_android_traffic_layer]
        map.getDelegate().setTrafficEnabled(true);
        // [END maps_android_traffic_layer]
    }
}
