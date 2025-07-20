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

import androidx.appcompat.app.AppCompatActivity;

/**
 * This shows how to listen to some {@link GoogleMap} events.
 */
// [START maps_android_sample_events]
public class EventsDemoActivity extends SamplesBaseActivity
        implements OnMapClickListener, OnMapLongClickListener, OnCameraIdleListener,
        OnMapReadyCallback {

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
        this.map.setOnCameraIdleListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        tapTextView.setText("tapped, point=" + point);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        tapTextView.setText("long pressed, point=" + point);
    }

    @Override
    public void onCameraIdle() {
        cameraTextView.setText(map.getCameraPosition().toString());
    }
}
// [END maps_android_sample_events]