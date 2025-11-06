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
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This shows how to take a snapshot of the map.
 */
public class SnapshotDemoActivity extends SamplesBaseActivity implements OnMapReadyCallback {

    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
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
        mapFragment.getMapAsync(this);

        applyInsets(binding.mapContainer);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
    }

    private void takeSnapshot() {
        if (mMap == null) {
            return;
        }

        final ImageView snapshotHolder = binding.snapshotHolder;

        final SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // Callback is called from the main thread, so we can modify the ImageView safely.
                snapshotHolder.setImageBitmap(snapshot);
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
    }
}
