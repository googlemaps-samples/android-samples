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

import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

@SnippetGroup(
        title = "Camera",
        description = "Snippets demonstrating camera controls, zoom constraints, bounds, and animations."
)
public class CameraControlSnippets {

    private final TrackedMap map;

    public CameraControlSnippets(TrackedMap map) {
        this.map = map;
    }

    @SnippetItem(
            title = "1. Zoom Level Constraints",
            description = "Sets minimum and maximum zoom preference bounds on the camera."
    )
    public void zoomLevel() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.88, 151.21), 8.0f));
        // [START maps_android_camera_and_view_zoom_level]
        map.setMinZoomPreference(6.0f);
        map.setMaxZoomPreference(14.0f);
        // [END maps_android_camera_and_view_zoom_level]
    }

    @SnippetItem(
            title = "2. Fit Camera To Bounds (Australia)",
            description = "Moves the camera once to fit geographic boundaries (Australia) within the viewport. Note: This frames the map initially, but does not restrict subsequent user panning."
    )
    public void settingBoundaries() {
        // [START maps_android_camera_and_view_setting_boundaries]
        LatLngBounds australiaBounds = new LatLngBounds(
                new LatLng(-44, 113), // SW bounds
                new LatLng(-10, 154)  // NE bounds
        );
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0));
        // [END maps_android_camera_and_view_setting_boundaries]
    }

    @SnippetItem(
            title = "3. Centering Map Within An Area",
            description = "Centers the camera on the center point of geographic bounds (Australia) at a zoom level of 10."
    )
    public void centeringMapWithinAnArea() {
        // [START maps_android_camera_and_view_centering_within_area]
        LatLngBounds australiaBounds = new LatLngBounds(
                new LatLng(-44, 113), // SW bounds
                new LatLng(-10, 154)  // NE bounds
        );
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(australiaBounds.getCenter(), 10));
        // [END maps_android_camera_and_view_centering_within_area]
    }

    @SnippetItem(
            title = "4. Panning Restrictions",
            description = "Restricts the camera target to specified geographic boundaries (Adelaide)."
    )
    public void panningRestrictions() {
        // [START maps_android_camera_and_view_panning_restrictions]
        // Create a LatLngBounds that includes the city of Adelaide in Australia.
        LatLngBounds adelaideBounds = new LatLngBounds(
                new LatLng(-35.0, 138.58), // SW bounds
                new LatLng(-34.9, 138.61)  // NE bounds
        );

        // Constrain the camera target to the Adelaide bounds.
        map.setLatLngBoundsForCameraTarget(adelaideBounds);
        // [END maps_android_camera_and_view_panning_restrictions]
    }

    @SnippetItem(
            title = "5. Common Map Movements",
            description = "Demonstrates camera movement, animation, zoom, and CameraPosition builder."
    )
    public void commonMapMovements() {
        // [START maps_android_camera_and_view_common_map_movements]
        LatLng sydney = new LatLng(-33.88, 151.21);
        LatLng mountainView = new LatLng(37.4, -122.1);

        // Move the camera instantly to Sydney with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mountainView)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        // [END maps_android_camera_and_view_common_map_movements]
    }
}
