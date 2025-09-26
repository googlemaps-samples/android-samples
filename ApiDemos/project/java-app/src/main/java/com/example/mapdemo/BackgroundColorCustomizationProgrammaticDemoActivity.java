// Copyright 2021 Google LLC
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

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * This shows how to to instantiate a SupportMapFragment programmatically with a custom background
 * color applied to the map, and add a marker on the map.
 */
public class BackgroundColorCustomizationProgrammaticDemoActivity extends SamplesBaseActivity
    implements OnMapReadyCallback {

    private static final String MAP_FRAGMENT_TAG = "map";

    private static final Integer LIGHT_PINK_COLOR = Color.argb(153, 240, 178, 221);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.background_color_customization_programmatic_demo);

        // It isn't possible to set a fragment's id programmatically so we set a tag instead and
        // search for it using that.
        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);

        // We only create a fragment if it doesn't already exist.
        if (mapFragment == null) {
            // To programmatically add the map, we first create a SupportMapFragment, with the
            // GoogleMapOptions to set the custom background color displayed before the map tiles load.
            mapFragment =
                SupportMapFragment.newInstance(new GoogleMapOptions().backgroundColor(LIGHT_PINK_COLOR));

            // Then we add the fragment using a FragmentTransaction.
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(com.example.common_ui.R.id.map, mapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        mapFragment.getMapAsync(this);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NONE);

        SwitchMaterial mapTypeToggleCheckbox = findViewById(com.example.common_ui.R.id.map_type_toggle);
        mapTypeToggleCheckbox.setOnCheckedChangeListener(
            (view, isChecked) -> map.setMapType(isChecked ? GoogleMap.MAP_TYPE_NORMAL : GoogleMap.MAP_TYPE_NONE));

        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}