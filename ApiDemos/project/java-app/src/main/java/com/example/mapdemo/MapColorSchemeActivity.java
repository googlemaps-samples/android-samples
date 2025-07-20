/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapColorScheme;

public class MapColorSchemeActivity extends SamplesBaseActivity implements OnMapReadyCallback {

    private Button buttonLight;
    private Button buttonDark;
    private Button buttonFollowSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.map_color_scheme_demo);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        buttonLight = findViewById(com.example.common_ui.R.id.map_color_light_mode);
        buttonDark = findViewById(com.example.common_ui.R.id.map_color_dark_mode);
        buttonFollowSystem = findViewById(com.example.common_ui.R.id.map_color_follow_system_mode);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        
        googleMap.setMapColorScheme(MapColorScheme.DARK);

        buttonLight.setOnClickListener(view -> googleMap.setMapColorScheme(MapColorScheme.LIGHT));

        buttonDark.setOnClickListener(view -> googleMap.setMapColorScheme(MapColorScheme.DARK));

        buttonFollowSystem.setOnClickListener(view -> googleMap.setMapColorScheme(MapColorScheme.FOLLOW_SYSTEM));
    }
}
