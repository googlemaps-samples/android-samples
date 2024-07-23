/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.AdvancedMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * This shows how to set collision behavior for the marker.
 */
public class MarkerCollisionDemoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private GoogleMap map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_collision_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        addMarkersToMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 3));
    }

    private void addMarkersToMap() {
        AdvancedMarkerOptions defaultMarkerOptions = new AdvancedMarkerOptions();

        // Add 100 markers to the map.
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                defaultMarkerOptions
                        .position(new LatLng(SYDNEY.latitude + i, SYDNEY.longitude - j))
                        .zIndex(i * 10 + j)
                        .title("zIndex:" + (i * 10 + j))
                        .draggable(true);

                if ((i + j) % 3 == 0) {
                    defaultMarkerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    defaultMarkerOptions.collisionBehavior(
                            AdvancedMarkerOptions.CollisionBehavior.OPTIONAL_AND_HIDES_LOWER_PRIORITY);
                } else if ((i + j) % 3 == 1) {
                    defaultMarkerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    defaultMarkerOptions.collisionBehavior(AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL);
                } else {
                    defaultMarkerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    defaultMarkerOptions.collisionBehavior(AdvancedMarkerOptions.CollisionBehavior.REQUIRED);
                }
                map.addMarker(defaultMarkerOptions);
            }
        }
    }
}
