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

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewSource;

// [START maps_street_view_on_street_view_panorama_ready]
class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {
    // [START_EXCLUDE]
    // [START maps_street_view_on_create]
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        // [START maps_street_view_find_fragment]
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
            (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                .findFragmentById(R.id.street_view_panorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        // [END maps_street_view_find_fragment]
    }
    // [END maps_street_view_on_create]

    // [START maps_street_view_on_street_view_panorama_ready_callback]
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        LatLng sanFrancisco = new LatLng(37.754130, -122.447129);
        streetViewPanorama.setPosition(sanFrancisco);
    }
    // [END maps_street_view_on_street_view_panorama_ready_callback]

    private void newView() {
        // [START maps_street_view_new_panorama_view]
        LatLng sanFrancisco = new LatLng(37.754130, -122.447129);
        StreetViewPanoramaView view = new StreetViewPanoramaView(this,
            new StreetViewPanoramaOptions().position(sanFrancisco));
        // [END maps_street_view_new_panorama_view]
    }

    private void setLocationOfThePanorama(StreetViewPanorama streetViewPanorama) {
        // [START maps_street_view_panorama_set_location]
        LatLng sanFrancisco = new LatLng(37.754130, -122.447129);

        // Set position with LatLng only.
        streetViewPanorama.setPosition(sanFrancisco);

        // Set position with LatLng and radius.
        streetViewPanorama.setPosition(sanFrancisco, 20);

        // Set position with LatLng and source.
        streetViewPanorama.setPosition(sanFrancisco, StreetViewSource.OUTDOOR);

        // Set position with LaLng, radius and source.
        streetViewPanorama.setPosition(sanFrancisco, 20, StreetViewSource.OUTDOOR);
        // [END maps_street_view_panorama_set_location]

        // [START maps_street_view_panorama_set_location_2]
        StreetViewPanoramaLocation location = streetViewPanorama.getLocation();
        if (location != null && location.links != null) {
            streetViewPanorama.setPosition(location.links[0].panoId);
        }
        // [END maps_street_view_panorama_set_location_2]
    }

    private void zoom(StreetViewPanorama streetViewPanorama) {
        // [START maps_street_view_panorama_zoom]
        float zoomBy = 0.5f;
        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
            .zoom(streetViewPanorama.getPanoramaCamera().zoom + zoomBy)
            .tilt(streetViewPanorama.getPanoramaCamera().tilt)
            .bearing(streetViewPanorama.getPanoramaCamera().bearing)
            .build();
        // [END maps_street_view_panorama_zoom]
    }

    private void pan(StreetViewPanorama streetViewPanorama) {
        // [START maps_street_view_panorama_pan]
        float panBy = 30;
        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
            .zoom(streetViewPanorama.getPanoramaCamera().zoom)
            .tilt(streetViewPanorama.getPanoramaCamera().tilt)
            .bearing(streetViewPanorama.getPanoramaCamera().bearing - panBy)
            .build();
        // [END maps_street_view_panorama_pan]
    }

    private void tilt(StreetViewPanorama streetViewPanorama) {
        // [START maps_street_view_panorama_tilt]
        float tilt = streetViewPanorama.getPanoramaCamera().tilt + 30;
        tilt = (tilt > 90) ? 90 : tilt;

        StreetViewPanoramaCamera previous = streetViewPanorama.getPanoramaCamera();

        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder(previous)
            .tilt(tilt)
            .build();
        // [END maps_street_view_panorama_tilt]
    }

    private void animate(StreetViewPanorama streetViewPanorama) {
        // [START maps_street_view_panorama_animate]
        // Keeping the zoom and tilt. Animate bearing by 60 degrees in 1000 milliseconds.
        long duration = 1000;
        StreetViewPanoramaCamera camera =
            new StreetViewPanoramaCamera.Builder()
                .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                .tilt(streetViewPanorama.getPanoramaCamera().tilt)
                .bearing(streetViewPanorama.getPanoramaCamera().bearing - 60)
                .build();
        streetViewPanorama.animateTo(camera, duration);
        // [END maps_street_view_panorama_animate]
    }
    // [END_EXCLUDE]
}
// [END maps_street_view_on_street_view_panorama_ready]
