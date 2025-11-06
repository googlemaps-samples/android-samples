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

import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCircleClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;

import com.example.mapdemo.utils.MapProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * This shows how to draw circles on a map.
 */
public class CircleDemoActivity extends SamplesBaseActivity
    implements OnSeekBarChangeListener, OnMarkerDragListener, OnMapLongClickListener,
    OnItemSelectedListener, OnMapReadyCallback, MapProvider {

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final double DEFAULT_RADIUS_METERS = 1000000;
    private static final double RADIUS_OF_EARTH_METERS = 6371009;

    private static final int MAX_WIDTH_PX = 50;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;

    private static final int PATTERN_DASH_LENGTH_PX = 100;
    private static final int PATTERN_GAP_LENGTH_PX = 200;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);

    private GoogleMap map;
    private boolean mapReady = false;

    final List<DraggableCircle> circles = new ArrayList<>(1);

    private int fillColorArgb;
    private int strokeColorArgb;

    com.example.common_ui.databinding.CircleDemoBinding binding;

    // These are the options for stroke patterns. We use their
    // string resource IDs as identifiers.

    private static final int[] PATTERN_TYPE_NAME_RESOURCE_IDS = {
        com.example.common_ui.R.string.pattern_solid, // Default
        com.example.common_ui.R.string.pattern_dashed,
        com.example.common_ui.R.string.pattern_dotted,
        com.example.common_ui.R.string.pattern_mixed,
    };

    @Override
    public GoogleMap getMap() {
        return map;
    }

    @Override
    public boolean isMapReady() {
        return mapReady;
    }

    class DraggableCircle {
        private final Marker centerMarker;
        private final Marker radiusMarker;
        public final Circle circle;
        private double radiusMeters;

        public DraggableCircle(LatLng center, double radiusMeters) {
            this.radiusMeters = radiusMeters;
            centerMarker = map.addMarker(new MarkerOptions()
                .position(center)
                .draggable(true));
            radiusMarker = map.addMarker(new MarkerOptions()
                .position(toRadiusLatLng(center, radiusMeters))
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE)));
            circle = map.addCircle(new CircleOptions()
                .center(center)
                .radius(radiusMeters)
                .strokeWidth(binding.strokeWidthSeekBar.getProgress())
                .strokeColor(strokeColorArgb)
                .fillColor(fillColorArgb)
                .clickable(binding.toggleClickability.isChecked()));
        }

        public boolean onMarkerMoved(Marker marker) {
            if (marker.equals(centerMarker)) {
                circle.setCenter(marker.getPosition());
                radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radiusMeters));
                return true;
            }
            if (marker.equals(radiusMarker)) {
                radiusMeters =
                    toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
                circle.setRadius(radiusMeters);
                return true;
            }
            return false;
        }

        public void onStyleChange() {
            circle.setStrokeWidth(binding.strokeWidthSeekBar.getProgress());
            circle.setStrokeColor(strokeColorArgb);
            circle.setFillColor(fillColorArgb);
        }

        public void setStrokePattern(List<PatternItem> pattern) {
            circle.setStrokePattern(pattern);
        }

        public void setClickable(boolean clickable) {
            circle.setClickable(clickable);
        }
    }

    /** Generate LatLng of radius marker */
    private static LatLng toRadiusLatLng(LatLng center, double radiusMeters) {
        double radiusAngle = Math.toDegrees(radiusMeters / RADIUS_OF_EARTH_METERS) /
            Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
            radius.latitude, radius.longitude, result);
        return result[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.common_ui.databinding.CircleDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fillHueSeekBar.setMax(MAX_HUE_DEGREES);
        binding.fillHueSeekBar.setProgress(MAX_HUE_DEGREES / 2);

        binding.fillAlphaSeekBar.setMax(MAX_ALPHA);
        binding.fillAlphaSeekBar.setProgress(MAX_ALPHA / 2);

        binding.strokeWidthSeekBar.setMax(MAX_WIDTH_PX);
        binding.strokeWidthSeekBar.setProgress(MAX_WIDTH_PX / 3);

        binding.strokeHueSeekBar.setMax(MAX_HUE_DEGREES);
        binding.strokeHueSeekBar.setProgress(0);

        binding.strokeAlphaSeekBar.setMax(MAX_ALPHA);
        binding.strokeAlphaSeekBar.setProgress(MAX_ALPHA);

        binding.strokePatternSpinner.setAdapter(new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item,
            getResourceStrings(PATTERN_TYPE_NAME_RESOURCE_IDS)));

        binding.toggleClickability.setOnClickListener(v -> toggleClickability());

        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        applyInsets(binding.mapContainer);
    }

    private String[] getResourceStrings(int[] resourceIds) {
        String[] strings = new String[resourceIds.length];
        for (int i = 0; i < resourceIds.length; i++) {
            strings[i] = getString(resourceIds[i]);
        }
        return strings;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Override the default content description on the view, for accessibility mode.
        googleMap.setContentDescription(getString(com.example.common_ui.R.string.map_circle_description));

        map = googleMap;
        map.setOnMarkerDragListener(this);
        map.setOnMapLongClickListener(this);

        fillColorArgb = Color.HSVToColor(
            binding.fillAlphaSeekBar.getProgress(), new float[]{binding.fillHueSeekBar.getProgress(), 1, 1});
        strokeColorArgb = Color.HSVToColor(
            binding.strokeAlphaSeekBar.getProgress(), new float[]{binding.strokeHueSeekBar.getProgress(), 1, 1});

        binding.fillHueSeekBar.setOnSeekBarChangeListener(this);
        binding.fillAlphaSeekBar.setOnSeekBarChangeListener(this);

        binding.strokeWidthSeekBar.setOnSeekBarChangeListener(this);
        binding.strokeHueSeekBar.setOnSeekBarChangeListener(this);
        binding.strokeAlphaSeekBar.setOnSeekBarChangeListener(this);

        binding.strokePatternSpinner.setOnItemSelectedListener(this);

        DraggableCircle circle = new DraggableCircle(SYDNEY, DEFAULT_RADIUS_METERS);
        circles.add(circle);

        // Move the map so that it is centered on the initial circle
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 4.0f));

        // Set up the click listener for the circle.
        googleMap.setOnCircleClickListener(new OnCircleClickListener() {
            @Override
            public void onCircleClick(@NonNull Circle circle) {
                // Flip the red, green and blue components of the circle's stroke color.
                circle.setStrokeColor(circle.getStrokeColor() ^ 0x00ffffff);
            }
        });

        List<PatternItem> pattern = getSelectedPattern(binding.strokePatternSpinner.getSelectedItemPosition());
        for (DraggableCircle draggableCircle : circles) {
            draggableCircle.setStrokePattern(pattern);
        }
        mapReady = true;
    }

    private List<PatternItem> getSelectedPattern(int pos) {
        int id = PATTERN_TYPE_NAME_RESOURCE_IDS[pos];
        if (id == com.example.common_ui.R.string.pattern_solid) {
            return null;
        } else if (id == com.example.common_ui.R.string.pattern_dotted) {
            return PATTERN_DOTTED;
        } else if (id == com.example.common_ui.R.string.pattern_dashed) {
            return PATTERN_DASHED;
        } else if (id == com.example.common_ui.R.string.pattern_mixed) {
            return PATTERN_MIXED;
        } else {
            return null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getId() == com.example.common_ui.R.id.strokePatternSpinner) {
            for (DraggableCircle draggableCircle : circles) {
                draggableCircle.setStrokePattern(getSelectedPattern(pos));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Don't do anything here.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == binding.fillHueSeekBar) {
            fillColorArgb =
                Color.HSVToColor(Color.alpha(fillColorArgb), new float[]{progress, 1, 1});
        } else if (seekBar == binding.fillAlphaSeekBar) {
            fillColorArgb = Color.argb(progress, Color.red(fillColorArgb),
                Color.green(fillColorArgb), Color.blue(fillColorArgb));
        } else if (seekBar == binding.strokeHueSeekBar) {
            strokeColorArgb =
                Color.HSVToColor(Color.alpha(strokeColorArgb), new float[]{progress, 1, 1});
        } else if (seekBar == binding.strokeAlphaSeekBar) {
            strokeColorArgb = Color.argb(progress, Color.red(strokeColorArgb),
                Color.green(strokeColorArgb), Color.blue(strokeColorArgb));
        }

        for (DraggableCircle draggableCircle : circles) {
            draggableCircle.onStyleChange();
        }
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        onMarkerMoved(marker);
    }

    private void onMarkerMoved(Marker marker) {
        for (DraggableCircle draggableCircle : circles) {
            if (draggableCircle.onMarkerMoved(marker)) {
                break;
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng point) {
        // We know the center, let's place the outline at a point 3/4 along the view.
        View view = Objects.requireNonNull(getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map)).getView();
        assert view != null;
        LatLng radiusLatLng = map.getProjection().fromScreenLocation(new Point(
            view.getHeight() * 3 / 4, view.getWidth() * 3 / 4));

        // Create the circle.
        DraggableCircle circle = new DraggableCircle(point, toRadiusMeters(point, radiusLatLng));
        circles.add(circle);
    }

    private void toggleClickability() {
        boolean clickable = binding.toggleClickability.isChecked();
        // Set each of the circles to be clickable or not, based on the
        // state of the checkbox.
        for (DraggableCircle draggableCircle : circles) {
            draggableCircle.setClickable(clickable);
        }
    }
}