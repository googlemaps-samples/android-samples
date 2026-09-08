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

import android.os.Bundle;

import com.example.common_ui.catalog.Complexity;
import com.example.common_ui.catalog.Framework;
import com.example.common_ui.catalog.Sample;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Demonstrates rendering and animating multiple independent GoogleMap instances concurrently.
 * Each quadrant showcases a UNESCO World Heritage Site with simultaneous smooth zoom animations.
 */
// [START maps_android_sample_multimap]
@Sample(
    id = "com.example.kotlindemos.MultiMapDemoActivity",
    title = "Multi-Map View",
    description = "Rendering multiple independent GoogleMap instances in a single activity layout.",
    category = "Map Initialization",
    complexity = Complexity.ADVANCED,
    tags = {"#multimap", "#multiple", "#layout", "#rendering"},
    apiCalls = {
        "SupportMapFragment.getMapAsync(OnMapReadyCallback)",
        "GoogleMap.animateCamera(CameraUpdate, int, CancelableCallback)",
        "GoogleMap.addMarker(MarkerOptions)"
    },
    purpose = "Shows how to render and control multiple independent GoogleMap instances concurrently in one screen.",
    successCriteria = "All 4 map fragments render distinct geographic locations simultaneously with smooth scrolling.",
    failureIndicators = "GL context collision, thread locking, or tile stuttering when dragging multiple maps.",
    framework = Framework.JAVA_VIEWS
)
public class MultiMapDemoActivity extends SamplesBaseActivity {

    private static final LatLng COMMON_START = new LatLng(20.0, 0.0);
    private static final LatLng GIZA = new LatLng(29.9792, 31.1342);
    private static final LatLng MACHU_PICCHU = new LatLng(-13.1631, -72.5450);
    private static final LatLng TAJ_MAHAL = new LatLng(27.1751, 78.0421);
    private static final LatLng COLOSSEUM = new LatLng(41.8902, 12.4922);
    private static final float INITIAL_ZOOM = 1.5f;
    private static final float TARGET_ZOOM = 15.5f;
    private static final int ANIM_DURATION_MS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.multimap_demo);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));

        setupMap(com.example.common_ui.R.id.map1, GIZA, "Pyramids of Giza");
        setupMap(com.example.common_ui.R.id.map2, MACHU_PICCHU, "Machu Picchu");
        setupMap(com.example.common_ui.R.id.map3, TAJ_MAHAL, "Taj Mahal");
        setupMap(com.example.common_ui.R.id.map4, COLOSSEUM, "Colosseum");
    }

    private void setupMap(int fragmentId, LatLng location, String title) {
        SupportMapFragment fragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(fragmentId);
        if (fragment != null) {
            fragment.getMapAsync(map -> {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(COMMON_START, INITIAL_ZOOM));
                map.addMarker(new MarkerOptions().position(location).title(title));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, TARGET_ZOOM), ANIM_DURATION_MS, null);
            });
        }
    }
}
// [END maps_android_sample_multimap]
