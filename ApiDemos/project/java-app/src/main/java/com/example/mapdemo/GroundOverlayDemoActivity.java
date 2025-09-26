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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * This shows how to add a ground overlay to a map.
 */
public class GroundOverlayDemoActivity extends SamplesBaseActivity
        implements OnSeekBarChangeListener, OnMapReadyCallback,
        GoogleMap.OnGroundOverlayClickListener {

    private static final int TRANSPARENCY_MAX = 100;

    private static final LatLng NEWARK = new LatLng(40.714086, -74.228697);

    private static final LatLng NEAR_NEWARK =
            new LatLng(NEWARK.latitude - 0.001, NEWARK.longitude - 0.025);

    private final List<BitmapDescriptor> images = new ArrayList<>();

    private GroundOverlay groundOverlay;

    private GroundOverlay groundOverlayRotated;

    private SeekBar transparencyBar;

    private int currentEntry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.ground_overlay_demo);

        transparencyBar = findViewById(com.example.common_ui.R.id.transparencySeekBar);
        transparencyBar.setMax(TRANSPARENCY_MAX);
        transparencyBar.setProgress(0);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Register a listener to respond to clicks on GroundOverlays.
        map.setOnGroundOverlayClickListener(this);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NEWARK, 11));

        images.clear();
        images.add(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.newark_nj_1922));
        images.add(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.newark_prudential_sunny));

        // Add a small, rotated overlay that is clickable by default
        // (set by the initial state of the checkbox.)
        groundOverlayRotated = map.addGroundOverlay(new GroundOverlayOptions()
                .image(images.get(1)).anchor(0, 1)
                .position(NEAR_NEWARK, 4300f, 3025f)
                .bearing(30)
                .clickable(((CheckBox) findViewById(com.example.common_ui.R.id.toggleClickability)).isChecked()));

        // Add a large overlay at Newark on top of the smaller overlay.
        groundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
                .image(images.get(currentEntry)).anchor(0, 1)
                .position(NEWARK, 8600f, 6500f));

        transparencyBar.setOnSeekBarChangeListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with ground overlay.");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (groundOverlay != null) {
            groundOverlay.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
        }
    }

    public void switchImage(View view) {
        currentEntry = (currentEntry + 1) % images.size();
        groundOverlay.setImage(images.get(currentEntry));
    }

    /**
     * Toggles the visibility between 100% and 50% when a {@link GroundOverlay} is clicked.
     */
    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        // Toggle transparency value between 0.0f and 0.5f. Initial default value is 0.0f.
        groundOverlayRotated.setTransparency(0.5f - groundOverlayRotated.getTransparency());
    }

    /**
     * Toggles the clickability of the smaller, rotated overlay based on the state of the View that
     * triggered this call.
     * This callback is defined on the CheckBox in the layout for this Activity.
     */
    public void toggleClickability(View view) {
        if (groundOverlayRotated != null) {
            groundOverlayRotated.setClickable(((CheckBox) view).isChecked());
        }
    }
}
