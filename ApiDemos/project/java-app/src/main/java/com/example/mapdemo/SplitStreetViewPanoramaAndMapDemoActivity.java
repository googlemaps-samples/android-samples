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
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This shows how to create a simple activity with streetview and a map
 */
public class SplitStreetViewPanoramaAndMapDemoActivity extends SamplesBaseActivity
        implements OnMarkerDragListener, OnStreetViewPanoramaChangeListener {

    private static final String MARKER_POSITION_KEY = "MarkerPosition";

    // George St, Sydney
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private StreetViewPanorama streetViewPanorama;

    private Marker marker;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.split_street_view_panorama_and_map_demo);

        final LatLng markerPosition;
        if (savedInstanceState == null) {
            markerPosition = SYDNEY;
        } else {
            markerPosition = savedInstanceState.getParcelable(MARKER_POSITION_KEY);
        }

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        streetViewPanorama = panorama;
                        streetViewPanorama.setOnStreetViewPanoramaChangeListener(
                                SplitStreetViewPanoramaAndMapDemoActivity.this);
                        // Only need to set the position once as the streetview fragment will maintain
                        // its state.
                        if (savedInstanceState == null) {
                            streetViewPanorama.setPosition(SYDNEY);
                        }
                    }
                });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.setOnMarkerDragListener(SplitStreetViewPanoramaAndMapDemoActivity.this);
                // Creates a draggable marker. Long press to drag.
                marker = map.addMarker(new MarkerOptions()
                        .position(markerPosition)
                        .icon(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.pegman))
                        .draggable(true));
            }
        });
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MARKER_POSITION_KEY, marker.getPosition());
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
        if (location != null) {
            marker.setPosition(location.position);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        streetViewPanorama.setPosition(marker.getPosition(), 150);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }
}
