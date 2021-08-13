/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
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

package com.example.wearosmap;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.wear.ambient.AmbientModeSupport;
import androidx.wear.ambient.AmbientModeSupport.AmbientCallback;
import androidx.wear.widget.SwipeDismissFrameLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Sample that shows how to set up a basic Google Map on Wear OS.
 */
// [START maps_wear_os_swipe_dismiss_callback]
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
    AmbientModeSupport.AmbientCallbackProvider {

    // [START_EXCLUDE silent]
    private SupportMapFragment mapFragment;
    // [END_EXCLUDE]

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Set the layout. It only contains a SupportMapFragment and a DismissOverlay.
        setContentView(R.layout.activity_main);

        // Enable ambient support, so the map remains visible in simplified, low-color display
        // when the user is no longer actively using the app but the app is still visible on the
        // watch face.
        // [START maps_wear_os_ambient_mode_support]
        AmbientModeSupport.AmbientController controller = AmbientModeSupport.attach(this);
        // [END maps_wear_os_ambient_mode_support]
        Log.d(MainActivity.class.getSimpleName(), "Is ambient enabled: " + controller.isAmbient());

        // Retrieve the containers for the root of the layout and the map. Margins will need to be
        // set on them to account for the system window insets.
        final SwipeDismissFrameLayout mapFrameLayout = (SwipeDismissFrameLayout) findViewById(
            R.id.map_container);
        mapFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                onBackPressed();
            }
        });

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // [START_EXCLUDE]
    // [START maps_wear_os_on_map_ready]
    private static final LatLng SYDNEY = new LatLng(-33.85704, 151.21522);

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Add a marker with a title that is shown in its info window.
        googleMap.addMarker(new MarkerOptions().position(SYDNEY)
            .title("Sydney Opera House"));

        // Move the camera to show the marker.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 10));
    }
    // [END maps_wear_os_on_map_ready]

    @Override
    public AmbientCallback getAmbientCallback() {
        return new AmbientCallback() {
            /**
             * Starts ambient mode on the map.
             * The API swaps to a non-interactive and low-color rendering of the map when the user is no
             * longer actively using the app.
             */
            @Override
            public void onEnterAmbient(Bundle ambientDetails) {
                super.onEnterAmbient(ambientDetails);
                mapFragment.onEnterAmbient(ambientDetails);
            }

            /**
             * Exits ambient mode on the map.
             * The API swaps to the normal rendering of the map when the user starts actively using the app.
             */
            @Override
            public void onExitAmbient() {
                super.onExitAmbient();
                mapFragment.onExitAmbient();
            }
        };
    }
    // [END_EXCLUDE]
}
// [END maps_wear_os_swipe_dismiss_callback]
