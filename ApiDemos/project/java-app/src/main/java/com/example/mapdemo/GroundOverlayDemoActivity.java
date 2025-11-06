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

import android.graphics.Point;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.mapdemo.utils.MapProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * This demo shows how to add a ground overlay to a map.
 * <p>
 * A ground overlay is an image that is fixed to a map. Unlike markers, ground overlays are
 * oriented against the Earth's surface rather than the screen. Rotating, tilting, or zooming the
 * map changes the orientation of the camera, but not the overlay.
 */
public class GroundOverlayDemoActivity extends SamplesBaseActivity
    implements OnSeekBarChangeListener, OnMapReadyCallback,
    GoogleMap.OnGroundOverlayClickListener, MapProvider {

    private static final String TAG = GroundOverlayDemoActivity.class.getName();

    private static final int TRANSPARENCY_MAX = 100;
    private static final LatLng NEWARK = new LatLng(40.714086, -74.228697);
    private static final LatLng NEAR_NEWARK =
        new LatLng(NEWARK.latitude - 0.001, NEWARK.longitude - 0.025);

    private final List<BitmapDescriptor> images = new ArrayList<>();

    // These are public for testing purposes only.
    GoogleMap mMap;
    GroundOverlay groundOverlay;
    GroundOverlay groundOverlayRotated;

    int groundOverlayRotatedClickCount = 0;
    int mapClickCount = 0;

    com.example.common_ui.databinding.GroundOverlayDemoBinding binding;
    private int currentEntry = 0;

    boolean mapReady = false;

    @Override
    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public boolean isMapReady() {
        return mapReady;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.common_ui.databinding.GroundOverlayDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.transparencySeekBar.setMax(TRANSPARENCY_MAX);
        binding.transparencySeekBar.setProgress(0);

        // Set up programmatic click listeners for the buttons.
        binding.switchImage.setOnClickListener(v -> switchImage());
        binding.toggleClickability.setOnClickListener(v -> toggleClickability());

        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        applyInsets(binding.mapContainer);
    }

    /**
     * This is called when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;
        // Register a listener to respond to clicks on GroundOverlays.
        map.setOnGroundOverlayClickListener(this);

        // Move the camera to the Newark area.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NEWARK, 11));

        map.moveCamera(CameraUpdateFactory.scrollBy(100f, 100f));

        map.setOnMapClickListener(ll -> {
            Point point = mMap.getProjection().toScreenLocation(ll);
            mapClickCount += 1;
        });

        // Prepare the BitmapDescriptor objects. Using a BitmapDescriptorFactory is the most
        // memory-efficient way to create the images that will be used for the overlays.
        images.clear();
        images.add(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.newark_nj_1922));
        images.add(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.newark_prudential_sunny));

        // Add a small, rotated overlay that is clickable by default.
        // The initial state of the "Clickable" checkbox determines its clickability.
        groundOverlayRotated = map.addGroundOverlay(new GroundOverlayOptions()
            // The image to use for the overlay.
            .image(images.get(1))
            // The anchor point for the overlay. By default, the anchor is the center of the
            // image. Here, we set it to the top-left corner.
            .anchor(0, 1)
            // The location of the anchor point on the map, the width of the overlay in meters,
            // and the height of the overlay in meters.
            .position(NEAR_NEWARK, 4300f, 3025f)
            // The bearing of the overlay in degrees, clockwise from north.
            .bearing(30)
            // The initial clickability of the overlay.
            .clickable(binding.toggleClickability.isChecked()));

        // Add a large overlay at Newark on top of the smaller overlay.
        groundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
                .image(images.get(currentEntry)).anchor(0, 1)
                .position(NEWARK, 8600f, 6500f));
        groundOverlay.setTag(images.get(currentEntry));

        binding.transparencySeekBar.setOnSeekBarChangeListener(this);

        // Override the default content description on the view for accessibility mode.
        // Ideally this string would be localized.
        map.setContentDescription("Google Map with ground overlay.");

        mapReady = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /**
     * This is called when the transparency SeekBar is changed.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Update the transparency of the main ground overlay. The transparency ranges from
        // 0 (completely opaque) to 1 (completely transparent).
        if (groundOverlay != null) {
            groundOverlay.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
        }
    }

    private void switchImage() {
        currentEntry = (currentEntry + 1) % images.size();
        groundOverlay.setImage(images.get(currentEntry));
        groundOverlay.setTag(images.get(currentEntry));
    }

    /**
     * This is called when a ground overlay is clicked.
     */
    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        groundOverlayRotatedClickCount += 1;
        // In this demo, we only toggle the transparency of the smaller, rotated overlay.
        // The transparency is toggled between 0.0f (opaque) and 0.5f (semi-transparent).
        groundOverlayRotated.setTransparency(0.5f - groundOverlayRotated.getTransparency());
    }

    /**
     * Toggles the clickability of the smaller, rotated ground overlay.
     */
    private void toggleClickability() {
        if (groundOverlayRotated != null) {
            // The clickability of an overlay can be changed at any time.
            groundOverlayRotated.setClickable(binding.toggleClickability.isChecked());
        }
    }
}
