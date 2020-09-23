// Copyright 2020 Google LLC
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

package com.google.maps.example;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

class CameraAndView {

    // [START maps_android_camera_and_view_zoom_level]
    private GoogleMap map;
    // [START_EXCLUDE silent]
    private void zoomLevel() {
    // [END_EXCLUDE]
        map.setMinZoomPreference(6.0f);
        map.setMaxZoomPreference(14.0f);
    // [START_EXCLUDE silent]
    }
    // [END_EXCLUDE]
    // [END maps_android_camera_and_view_zoom_level]

    private void settingBoundaries() {
        // [START maps_android_camera_and_view_setting_boundaries]
        LatLngBounds australiaBounds = new LatLngBounds(
            new LatLng(-44, 113), // SW bounds
            new LatLng(-10, 154)  // NE bounds
        );
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0));
        // [END maps_android_camera_and_view_setting_boundaries]
    }

    private void centeringMapWithinAnArea() {
        // [START maps_android_camera_and_view_centering_within_area]
        LatLngBounds australiaBounds = new LatLngBounds(
            new LatLng(-44, 113), // SW bounds
            new LatLng(-10, 154)  // NE bounds
        );
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(australiaBounds.getCenter(), 10));
        // [END maps_android_camera_and_view_centering_within_area]
    }

    private void panningRestrictions() {
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

    private void commonMapMovements() {
        // [START maps_android_camera_and_view_common_map_movements]
        LatLng sydney = new LatLng(-33.88,151.21);
        LatLng mountainView = new LatLng(37.4, -122.1);

        // Move the camera instantly to Sydney with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
            .target(mountainView )      // Sets the center of the map to Mountain View
            .zoom(17)                   // Sets the zoom
            .bearing(90)                // Sets the orientation of the camera to east
            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
            .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        // [END maps_android_camera_and_view_common_map_movements]
    }
}
