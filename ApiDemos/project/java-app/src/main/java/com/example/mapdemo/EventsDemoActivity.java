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


package com.example.mapdemo;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.widget.TextView;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This shows how to listen to some {@link GoogleMap} events.
 */
// [START maps_android_sample_events]
public class EventsDemoActivity extends SamplesBaseActivity
        implements OnMapClickListener, OnMapLongClickListener, OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener, OnMapReadyCallback {

    private TextView tapTextView;
    private TextView cameraTextView;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.events_demo);

        tapTextView = findViewById(com.example.common_ui.R.id.tap_text);
        cameraTextView = findViewById(com.example.common_ui.R.id.camera_text);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        this.map.setOnMapClickListener(this);
        this.map.setOnMapLongClickListener(this);
        this.map.setOnCameraMoveListener(this);
        this.map.setOnCameraIdleListener(this);
        updateCameraPosition();
    }

    @Override
    public void onMapClick(LatLng point) {
        String lat = String.format(Locale.US, "%.6f", point.latitude);
        String lng = String.format(Locale.US, "%.6f", point.longitude);
        tapTextView.setText(getString(com.example.common_ui.R.string.events_tapped_format, lat, lng));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        String lat = String.format(Locale.US, "%.6f", point.latitude);
        String lng = String.format(Locale.US, "%.6f", point.longitude);
        tapTextView.setText(getString(com.example.common_ui.R.string.events_long_pressed_format, lat, lng));
    }

    @Override
    public void onCameraMove() {
        updateCameraPosition();
    }

    @Override
    public void onCameraIdle() {
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        if (map == null) return;
        com.google.android.gms.maps.model.CameraPosition pos = map.getCameraPosition();
        String lat = String.format(Locale.US, "%.6f", pos.target.latitude);
        String lng = String.format(Locale.US, "%.6f", pos.target.longitude);
        String zoom = String.format(Locale.US, "%.1f", pos.zoom);
        String tilt = String.format(Locale.US, "%.1f", pos.tilt);
        String bearing = String.format(Locale.US, "%.1f", pos.bearing);
        cameraTextView.setText(getString(
            com.example.common_ui.R.string.events_camera_position_format,
            lat,
            lng,
            zoom,
            tilt,
            bearing
        ));
    }
}
// [END maps_android_sample_events]