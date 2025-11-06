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
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewSource;

/**
 * This shows how to create an activity with static streetview (all options have been switched off)
 */
public class StreetViewPanoramaOptionsDemoActivity extends SamplesBaseActivity {

    // Cole St, San Fran
    private static final LatLng SAN_FRAN = new LatLng(37.765927, -122.449972);

    private static final int RADIUS = 20;

    private StreetViewPanorama streetViewPanorama;

    private com.example.common_ui.databinding.StreetViewPanoramaOptionsDemoBinding binding;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.common_ui.databinding.StreetViewPanoramaOptionsDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.streetnames.setOnClickListener(v -> onStreetNamesToggled());
        binding.navigation.setOnClickListener(v -> onNavigationToggled());
        binding.zoom.setOnClickListener(v -> onZoomToggled());
        binding.panning.setOnClickListener(v -> onPanningToggled());
        binding.outdoor.setOnClickListener(v -> onOutdoorToggled());

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                panorama -> {
                    streetViewPanorama = panorama;
                    panorama.setStreetNamesEnabled(binding.streetnames.isChecked());
                    panorama.setUserNavigationEnabled(binding.navigation.isChecked());
                    panorama.setZoomGesturesEnabled(binding.zoom.isChecked());
                    panorama.setPanningGesturesEnabled(binding.panning.isChecked());

                    // Only set the panorama to SAN_FRAN on startup (when no panoramas have been
                    // loaded which is when the savedInstanceState is null).
                    if (savedInstanceState == null) {
                        setPosition();
                    }
                });

        applyInsets(binding.mapContainer);
    }

    private void setPosition() {
        streetViewPanorama.setPosition(
                SAN_FRAN,
                RADIUS,
                binding.outdoor.isChecked() ? StreetViewSource.OUTDOOR : StreetViewSource.DEFAULT
        );
    }

    private boolean checkReady() {
        if (streetViewPanorama == null) {
            Toast.makeText(this, com.example.common_ui.R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void onStreetNamesToggled() {
        if (!checkReady()) {
            return;
        }
        streetViewPanorama.setStreetNamesEnabled(binding.streetnames.isChecked());
    }

    private void onNavigationToggled() {
        if (!checkReady()) {
            return;
        }
        streetViewPanorama.setUserNavigationEnabled(binding.navigation.isChecked());
    }

    private void onZoomToggled() {
        if (!checkReady()) {
            return;
        }
        streetViewPanorama.setZoomGesturesEnabled(binding.zoom.isChecked());
    }

    private void onPanningToggled() {
        if (!checkReady()) {
            return;
        }
        streetViewPanorama.setPanningGesturesEnabled(binding.panning.isChecked());
    }

    private void onOutdoorToggled() {
        if (!checkReady()) {
            return;
        }
        setPosition();
    }
}
