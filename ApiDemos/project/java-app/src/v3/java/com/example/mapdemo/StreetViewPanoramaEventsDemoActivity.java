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

import android.graphics.Point;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.maps.StreetViewPanorama;
import com.google.android.libraries.maps.StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener;
import com.google.android.libraries.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener;
import com.google.android.libraries.maps.StreetViewPanorama.OnStreetViewPanoramaClickListener;
import com.google.android.libraries.maps.StreetViewPanorama.OnStreetViewPanoramaLongClickListener;
import com.google.android.libraries.maps.SupportStreetViewPanoramaFragment;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.StreetViewPanoramaCamera;
import com.google.android.libraries.maps.model.StreetViewPanoramaLocation;
import com.google.android.libraries.maps.model.StreetViewPanoramaOrientation;

/**
 * This shows how to listen to some {@link StreetViewPanorama} events.
 */
public class StreetViewPanoramaEventsDemoActivity extends AppCompatActivity
        implements OnStreetViewPanoramaChangeListener, OnStreetViewPanoramaCameraChangeListener,
        OnStreetViewPanoramaClickListener, OnStreetViewPanoramaLongClickListener {

    // George St, Sydney
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private StreetViewPanorama streetViewPanorama;

    private TextView panoChangeTimesTextView;

    private TextView panoCameraChangeTextView;

    private TextView panoClickTextView;

    private TextView panoLongClickTextView;

    private int panoChangeTimes = 0;

    private int panoCameraChangeTimes = 0;

    private int panoClickTimes = 0;

    private int panoLongClickTimes = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.street_view_panorama_events_demo);

        panoChangeTimesTextView = findViewById(com.example.common_ui.R.id.change_pano);
        panoCameraChangeTextView = findViewById(com.example.common_ui.R.id.change_camera);
        panoClickTextView = findViewById(com.example.common_ui.R.id.click_pano);
        panoLongClickTextView = findViewById(com.example.common_ui.R.id.long_click_pano);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                panorama -> {
                    streetViewPanorama = panorama;
                    streetViewPanorama.setOnStreetViewPanoramaChangeListener(
                            StreetViewPanoramaEventsDemoActivity.this);
                    streetViewPanorama.setOnStreetViewPanoramaCameraChangeListener(
                            StreetViewPanoramaEventsDemoActivity.this);
                    streetViewPanorama.setOnStreetViewPanoramaClickListener(
                            StreetViewPanoramaEventsDemoActivity.this);
                    streetViewPanorama.setOnStreetViewPanoramaLongClickListener(
                            StreetViewPanoramaEventsDemoActivity.this);

                    // Only set the panorama to SYDNEY on startup (when no panoramas have been
                    // loaded which is when the savedInstanceState is null).
                    if (savedInstanceState == null) {
                        streetViewPanorama.setPosition(SYDNEY);
                    }
                });
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
        if (location != null) {
            panoChangeTimesTextView.setText("Times panorama changed=" + ++panoChangeTimes);
        }
    }

    @Override
    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera camera) {
        panoCameraChangeTextView.setText("Times camera changed=" + ++panoCameraChangeTimes);
    }

    @Override
    public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation orientation) {
        Point point = streetViewPanorama.orientationToPoint(orientation);
        if (point != null) {
            panoClickTimes++;
            panoClickTextView.setText(
                    "Times clicked=" + panoClickTimes + " : " + point.toString());
            streetViewPanorama.animateTo(
                    new StreetViewPanoramaCamera.Builder()
                            .orientation(orientation)
                            .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                            .build(), 1000);
        }
    }

    @Override
    public void onStreetViewPanoramaLongClick(StreetViewPanoramaOrientation orientation) {
        Point point = streetViewPanorama.orientationToPoint(orientation);
        if (point != null) {
            panoLongClickTimes++;
            panoLongClickTextView.setText(
                    "Times long clicked=" + panoLongClickTimes + " : " + point.toString());
        }
    }
}
