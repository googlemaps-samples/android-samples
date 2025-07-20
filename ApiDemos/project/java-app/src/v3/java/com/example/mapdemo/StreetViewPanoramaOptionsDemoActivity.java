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
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.libraries.maps.StreetViewPanorama;
import com.google.android.libraries.maps.SupportStreetViewPanoramaFragment;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.StreetViewSource;

/**
 * This shows how to create an activity with static streetview (all options have been switched off)
 */
public class StreetViewPanoramaOptionsDemoActivity extends AppCompatActivity {

    // Cole St, San Fran
    private static final LatLng SAN_FRAN = new LatLng(37.765927, -122.449972);

    private static int RADIUS = 20;

    private StreetViewPanorama streetViewPanorama;

    private CheckBox streetNameCheckbox;

    private CheckBox navigationCheckbox;

    private CheckBox zoomCheckbox;

    private CheckBox panningCheckbox;

    private CheckBox outdoorCheckbox;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.street_view_panorama_options_demo);

        streetNameCheckbox = findViewById(com.example.common_ui.R.id.streetnames);
        navigationCheckbox = findViewById(com.example.common_ui.R.id.navigation);
        zoomCheckbox = findViewById(com.example.common_ui.R.id.zoom);
        panningCheckbox = findViewById(com.example.common_ui.R.id.panning);
        outdoorCheckbox = findViewById(com.example.common_ui.R.id.outdoor);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                panorama -> {
                    streetViewPanorama = panorama;
                    panorama.setStreetNamesEnabled(streetNameCheckbox.isChecked());
                    panorama.setUserNavigationEnabled(navigationCheckbox.isChecked());
                    panorama.setZoomGesturesEnabled(zoomCheckbox.isChecked());
                    panorama.setPanningGesturesEnabled(panningCheckbox.isChecked());

                    // Only set the panorama to SAN_FRAN on startup (when no panoramas have been
                    // loaded which is when the savedInstanceState is null).
                    if (savedInstanceState == null) {
                        setPosition();
                    }
                });
    }

    private void setPosition() {
        streetViewPanorama.setPosition(
                SAN_FRAN,
                RADIUS,
                outdoorCheckbox.isChecked() ? StreetViewSource.OUTDOOR : StreetViewSource.DEFAULT
        );
    }

    private boolean checkReady() {
        if (streetViewPanorama == null) {
            Toast.makeText(this, com.example.common_ui.R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onStreetNamesToggled(View view) {
        if (!checkReady()) {
            return;
        }
        streetViewPanorama.setStreetNamesEnabled(streetNameCheckbox.isChecked());
    }

    public void onNavigationToggled(View view) {
        if (!checkReady()) {
            return;
        }
        streetViewPanorama.setUserNavigationEnabled(navigationCheckbox.isChecked());
    }

    public void onZoomToggled(View view) {
        if (!checkReady()) {
            return;
        }
        streetViewPanorama.setZoomGesturesEnabled(zoomCheckbox.isChecked());
    }

    public void onPanningToggled(View view) {
        if (!checkReady()) {
            return;
        }
        streetViewPanorama.setPanningGesturesEnabled(panningCheckbox.isChecked());
    }

    public void onOutdoorToggled(View view) {
        if (!checkReady()) {
            return;
        }
        setPosition();
    }
}
