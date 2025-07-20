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
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.GoogleMap.OnCircleClickListener;
import com.google.android.libraries.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.libraries.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.Circle;
import com.google.android.libraries.maps.model.CircleOptions;
import com.google.android.libraries.maps.model.Dash;
import com.google.android.libraries.maps.model.Dot;
import com.google.android.libraries.maps.model.Gap;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.libraries.maps.model.PatternItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This shows how to draw circles on a map.
 */
public class CircleDemoActivity extends AppCompatActivity
        implements OnSeekBarChangeListener, OnMarkerDragListener, OnMapLongClickListener,
        OnItemSelectedListener, OnMapReadyCallback {

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

    private List<DraggableCircle> circles = new ArrayList<>(1);

    private int fillColorArgb;
    private int strokeColorArgb;

    private SeekBar fillHueBar;
    private SeekBar fillAlphaBar;
    private SeekBar strokeWidthBar;
    private SeekBar strokeHueBar;
    private SeekBar strokeAlphaBar;
    private Spinner strokePatternSpinner;
    private CheckBox clickabilityCheckbox;

    // These are the options for stroke patterns. We use their
    // string resource IDs as identifiers.

    private static final int[] PATTERN_TYPE_NAME_RESOURCE_IDS = {
            com.example.common_ui.R.string.pattern_solid, // Default
            com.example.common_ui.R.string.pattern_dashed,
            com.example.common_ui.R.string.pattern_dotted,
            com.example.common_ui.R.string.pattern_mixed,
    };

    private class DraggableCircle {
        private final Marker centerMarker;
        private final Marker radiusMarker;
        private final Circle circle;
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
                    .strokeWidth(strokeWidthBar.getProgress())
                    .strokeColor(strokeColorArgb)
                    .fillColor(fillColorArgb)
                    .clickable(clickabilityCheckbox.isChecked()));
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
            circle.setStrokeWidth(strokeWidthBar.getProgress());
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
        setContentView(com.example.common_ui.R.layout.circle_demo);

        fillHueBar = findViewById(com.example.common_ui.R.id.fillHueSeekBar);
        fillHueBar.setMax(MAX_HUE_DEGREES);
        fillHueBar.setProgress(MAX_HUE_DEGREES / 2);

        fillAlphaBar = findViewById(com.example.common_ui.R.id.fillAlphaSeekBar);
        fillAlphaBar.setMax(MAX_ALPHA);
        fillAlphaBar.setProgress(MAX_ALPHA / 2);

        strokeWidthBar = findViewById(com.example.common_ui.R.id.strokeWidthSeekBar);
        strokeWidthBar.setMax(MAX_WIDTH_PX);
        strokeWidthBar.setProgress(MAX_WIDTH_PX / 3);

        strokeHueBar = findViewById(com.example.common_ui.R.id.strokeHueSeekBar);
        strokeHueBar.setMax(MAX_HUE_DEGREES);
        strokeHueBar.setProgress(0);

        strokeAlphaBar = findViewById(com.example.common_ui.R.id.strokeAlphaSeekBar);
        strokeAlphaBar.setMax(MAX_ALPHA);
        strokeAlphaBar.setProgress(MAX_ALPHA);

        strokePatternSpinner = findViewById(com.example.common_ui.R.id.strokePatternSpinner);
        strokePatternSpinner.setAdapter(new ArrayAdapter<>(
                this, android.com.example.common_ui.R.layout.simple_spinner_item,
                getResourceStrings(PATTERN_TYPE_NAME_RESOURCE_IDS)));

        clickabilityCheckbox = findViewById(com.example.common_ui.R.id.toggleClickability);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
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
                fillAlphaBar.getProgress(), new float[]{fillHueBar.getProgress(), 1, 1});
        strokeColorArgb = Color.HSVToColor(
                strokeAlphaBar.getProgress(), new float[]{strokeHueBar.getProgress(), 1, 1});

        fillHueBar.setOnSeekBarChangeListener(this);
        fillAlphaBar.setOnSeekBarChangeListener(this);

        strokeWidthBar.setOnSeekBarChangeListener(this);
        strokeHueBar.setOnSeekBarChangeListener(this);
        strokeAlphaBar.setOnSeekBarChangeListener(this);

        strokePatternSpinner.setOnItemSelectedListener(this);

        DraggableCircle circle = new DraggableCircle(SYDNEY, DEFAULT_RADIUS_METERS);
        circles.add(circle);

        // Move the map so that it is centered on the initial circle
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 4.0f));

        // Set up the click listener for the circle.
        googleMap.setOnCircleClickListener(new OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                // Flip the red, green and blue components of the circle's stroke color.
                circle.setStrokeColor(circle.getStrokeColor() ^ 0x00ffffff);
            }
        });

        List<PatternItem> pattern = getSelectedPattern(strokePatternSpinner.getSelectedItemPosition());
        for (DraggableCircle draggableCircle : circles) {
            draggableCircle.setStrokePattern(pattern);
        }
    }

    private List<PatternItem> getSelectedPattern(int pos) {
        switch (PATTERN_TYPE_NAME_RESOURCE_IDS[pos]) {
            case com.example.common_ui.R.string.pattern_solid:
                return null;
            case com.example.common_ui.R.string.pattern_dotted:
                return PATTERN_DOTTED;
            case com.example.common_ui.R.string.pattern_dashed:
                return PATTERN_DASHED;
            case com.example.common_ui.R.string.pattern_mixed:
                return PATTERN_MIXED;
            default:
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
        if (seekBar == fillHueBar) {
            fillColorArgb =
                    Color.HSVToColor(Color.alpha(fillColorArgb), new float[]{progress, 1, 1});
        } else if (seekBar == fillAlphaBar) {
            fillColorArgb = Color.argb(progress, Color.red(fillColorArgb),
                    Color.green(fillColorArgb), Color.blue(fillColorArgb));
        } else if (seekBar == strokeHueBar) {
            strokeColorArgb =
                    Color.HSVToColor(Color.alpha(strokeColorArgb), new float[]{progress, 1, 1});
        } else if (seekBar == strokeAlphaBar) {
            strokeColorArgb = Color.argb(progress, Color.red(strokeColorArgb),
                    Color.green(strokeColorArgb), Color.blue(strokeColorArgb));
        }

        for (DraggableCircle draggableCircle : circles) {
            draggableCircle.onStyleChange();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
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
    public void onMapLongClick(LatLng point) {
        // We know the center, let's place the outline at a point 3/4 along the view.
        View view = getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map).getView();
        LatLng radiusLatLng = map.getProjection().fromScreenLocation(new Point(
                view.getHeight() * 3 / 4, view.getWidth() * 3 / 4));

        // Create the circle.
        DraggableCircle circle = new DraggableCircle(point, toRadiusMeters(point, radiusLatLng));
        circles.add(circle);
    }

    public void toggleClickability(View view) {
        boolean clickable = ((CheckBox) view).isChecked();
        // Set each of the circles to be clickable or not, based on the
        // state of the checkbox.
        for (DraggableCircle draggableCircle : circles) {
            draggableCircle.setClickable(clickable);
        }
    }
}