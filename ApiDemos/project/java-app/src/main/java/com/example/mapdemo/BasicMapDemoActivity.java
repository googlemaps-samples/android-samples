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

import com.example.common_ui.catalog.Sample;
import com.example.common_ui.catalog.Complexity;
import com.example.common_ui.catalog.Framework;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
// [START maps_android_sample_basic_map]
@Sample(
    id = "basic_map",
    title = "Basic Map",
    description = "Fundamental map instantiation, lifecycle binding, and default camera centering.",
    category = "Map Initialization",
    complexity = Complexity.SNIPPET,
    tags = {"#map", "#init", "#lifecycle", "#quickstart"},
    purpose = "Demonstrates clean, minimal map instantiation using SupportMapFragment.",
    successCriteria = "The map loads default vector tiles cleanly centered at the initial coordinates with working gestures.",
    failureIndicators = "Grey tiles (missing API key or auth mismatch), crash on back navigation, or map failing to unpause.",
    framework = Framework.JAVA_VIEWS
)
public class BasicMapDemoActivity extends SamplesBaseActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.basic_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
// [END maps_android_sample_basic_map]