// Copyright 2026 Google LLC
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;

/**
 * Demonstrates capturing a bitmap screenshot of a {@link GoogleMap} view using {@link GoogleMap#snapshot}.
 * <p>
 * Key Concepts:
 * 1. <b>Live Map Capture</b>: {@link GoogleMap#snapshot} takes an asynchronous render of the current
 *    map viewport and delivers it as an Android {@link Bitmap} via {@link SnapshotReadyCallback}.
 * 2. <b>Tile Readiness Synchronization</b>: If the "Wait for Map Load" option is selected,
 *    {@link GoogleMap#setOnMapLoadedCallback} is invoked first to ensure all vector tiles, labels,
 *    and overlays are fully rendered before capturing the bitmap.
 * 3. <b>Material 3 Split View</b>: Displays the interactive map in a top card and the captured
 *    preview in a bottom card with empty-state placeholder handling.
 */
@Sample(
    id = "snapshot_demo",
    title = "Map Snapshot & Image Capture",
    description = "Asynchronous bitmap frame capture using GoogleMap.snapshot() rendered in Material 3 preview cards.",
    category = "Snapshots & Sharing",
    complexity = Complexity.SIMPLE,
    tags = {"#snapshot", "#bitmap", "#export", "#material3", "#capture"},
    purpose = "Demonstrates taking asynchronous high-resolution bitmap snapshots of the map with snapshot ready callbacks.",
    successCriteria = "Tapping 'Take Snapshot' captures the current map frame and displays it in the Material 3 preview card.",
    failureIndicators = "Snapshot returns blank bitmap or blocks UI thread during GL readback.",
    framework = Framework.JAVA_VIEWS
)
public class SnapshotDemoActivity extends SamplesBaseActivity implements OnMapReadyCallback {

    // Venice, Italy (Grand Canal & Rialto)
    static final LatLng VENICE = new LatLng(45.4380, 12.3350);

    private GoogleMap mMap;
    private com.example.common_ui.databinding.SnapshotDemoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.common_ui.databinding.SnapshotDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.screenshotButton.setOnClickListener(v -> takeSnapshot());
        binding.clearButton.setOnClickListener(v -> clearSnapshot());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        applyInsets(binding.mapContainer);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.mMap = map;
        // Center on Venice, Italy — a visually rich standard vector map showing the Grand Canal and Rialto
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VENICE, 14.5f));
    }

    private void takeSnapshot() {
        if (mMap == null) {
            return;
        }

        final ImageView snapshotHolder = binding.snapshotHolder;

        final SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // Callback is called from the main thread, so we can modify the ImageView and cards safely.
                snapshotHolder.setImageBitmap(snapshot);
                binding.snapshotPlaceholder.setVisibility(View.GONE);
                binding.snapshotLabel.setVisibility(View.VISIBLE);
            }
        };

        if (((CheckBox) binding.waitForMapLoad).isChecked()) {
            mMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.snapshot(callback);
                }
            });
        } else {
            mMap.snapshot(callback);
        }
    }

    private void clearSnapshot() {
        binding.snapshotHolder.setImageDrawable(null);
        binding.snapshotPlaceholder.setVisibility(View.VISIBLE);
        binding.snapshotLabel.setVisibility(View.GONE);
    }
}
