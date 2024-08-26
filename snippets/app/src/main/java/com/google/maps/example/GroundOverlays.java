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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

class GroundOverlays {

    private GoogleMap map;

    private void groundOverlays() {
        // [START maps_android_ground_overlays_add]
        LatLng newarkLatLng = new LatLng(40.714086, -74.228697);

        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
            .position(newarkLatLng, 8600f, 6500f);
        map.addGroundOverlay(newarkMap);
        // [END maps_android_ground_overlays_add]

        // [START maps_android_ground_overlays_retain]
        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
        GroundOverlay imageOverlay = map.addGroundOverlay(newarkMap);
        // [END maps_android_ground_overlays_retain]

        // [START maps_android_ground_overlays_remove]
        imageOverlay.remove();
        // [END maps_android_ground_overlays_remove]

        // [START maps_android_ground_overlays_change_image]
        // Update the GroundOverlay with a new image of the same dimension
        imageOverlay.setImage(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922));
        // [END maps_android_ground_overlays_change_image]

        // [START maps_android_ground_overlays_associate_data]
        GroundOverlay sydneyGroundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.harbour_bridge))
            .position(new LatLng(-33.873, 151.206), 100)
            .clickable(true));

        sydneyGroundOverlay.setTag("Sydney");
        // [END maps_android_ground_overlays_associate_data]
    }

    private void positionImageLocation() {
        // [START maps_android_ground_overlays_position_image_location]
        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
            .anchor(0, 1)
            .position(new LatLng(40.714086, -74.228697), 8600f, 6500f);
        // [END maps_android_ground_overlays_position_image_location]
    }

    private void positionImageBounds() {
        // [START maps_android_ground_overlays_position_image_bounds]
        LatLngBounds newarkBounds = new LatLngBounds(
            new LatLng(40.712216, -74.22655),       // South west corner
            new LatLng(40.773941, -74.12544));      // North east corner
        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
            .positionFromBounds(newarkBounds);
        // [END maps_android_ground_overlays_position_image_bounds]
    }
}
