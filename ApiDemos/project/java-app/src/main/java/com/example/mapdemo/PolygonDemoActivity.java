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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.Arrays;
import java.util.List;

/**
 * This shows how to draw polygons on a map.
 */
// [START maps_android_sample_polygons]
public class PolygonDemoActivity extends SamplesBaseActivity
        implements OnSeekBarChangeListener, OnItemSelectedListener, OnMapReadyCallback {

    private static final LatLng CENTER = new LatLng(-20, 130);
    private static final int MAX_WIDTH_PX = 100;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;

    private static final int PATTERN_DASH_LENGTH_PX = 50;
    private static final int PATTERN_GAP_LENGTH_PX = 10;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);

    private Polygon mutablePolygon;
    private SeekBar fillHueBar;
    private SeekBar fillAlphaBar;
    private SeekBar strokeWidthBar;
    private SeekBar strokeHueBar;
    private SeekBar strokeAlphaBar;
    private Spinner strokeJointTypeSpinner;
    private Spinner strokePatternSpinner;
    private CheckBox clickabilityCheckbox;

    // These are the options for polygon stroke joints and patterns. We use their
    // string resource IDs as identifiers.

    private static final int[] JOINT_TYPE_NAME_RESOURCE_IDS = {
            com.example.common_ui.R.string.joint_type_default, // Default
            com.example.common_ui.R.string.joint_type_bevel,
            com.example.common_ui.R.string.joint_type_round,
    };

    private static final int[] PATTERN_TYPE_NAME_RESOURCE_IDS = {
            com.example.common_ui.R.string.pattern_solid, // Default
            com.example.common_ui.R.string.pattern_dashed,
            com.example.common_ui.R.string.pattern_dotted,
            com.example.common_ui.R.string.pattern_mixed,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.polygon_demo);

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

        strokeJointTypeSpinner = findViewById(com.example.common_ui.R.id.strokeJointTypeSpinner);
        strokeJointTypeSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(JOINT_TYPE_NAME_RESOURCE_IDS)));

        strokePatternSpinner = findViewById(com.example.common_ui.R.id.strokePatternSpinner);
        strokePatternSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(PATTERN_TYPE_NAME_RESOURCE_IDS)));

        clickabilityCheckbox = findViewById(com.example.common_ui.R.id.toggleClickability);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);

        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }
    // [START_EXCLUDE silent]

    private String[] getResourceStrings(int[] resourceIds) {
        String[] strings = new String[resourceIds.length];
        for (int i = 0; i < resourceIds.length; i++) {
            strings[i] = getString(resourceIds[i]);
        }
        return strings;
    }
    // [END_EXCLUDE]

    @Override
    public void onMapReady(GoogleMap map) {
        // Override the default content description on the view, for accessibility mode.
        map.setContentDescription(getString(com.example.common_ui.R.string.polygon_demo_description));

        int fillColorArgb = Color.HSVToColor(
                fillAlphaBar.getProgress(), new float[]{fillHueBar.getProgress(), 1, 1});
        int strokeColorArgb = Color.HSVToColor(
                strokeAlphaBar.getProgress(), new float[]{strokeHueBar.getProgress(), 1, 1});

        // Create a rectangle with two rectangular holes.
        mutablePolygon = map.addPolygon(new PolygonOptions()
                .addAll(createRectangle(CENTER, 5, 5))
                .addHole(createRectangle(new LatLng(-22, 128), 1, 1))
                .addHole(createRectangle(new LatLng(-18, 133), 0.5, 1.5))
                .fillColor(fillColorArgb)
                .strokeColor(strokeColorArgb)
                .strokeWidth(strokeWidthBar.getProgress())
                .clickable(clickabilityCheckbox.isChecked()));

        fillHueBar.setOnSeekBarChangeListener(this);
        fillAlphaBar.setOnSeekBarChangeListener(this);

        strokeWidthBar.setOnSeekBarChangeListener(this);
        strokeHueBar.setOnSeekBarChangeListener(this);
        strokeAlphaBar.setOnSeekBarChangeListener(this);

        strokeJointTypeSpinner.setOnItemSelectedListener(this);
        strokePatternSpinner.setOnItemSelectedListener(this);

        mutablePolygon.setStrokeJointType(getSelectedJointType(strokeJointTypeSpinner.getSelectedItemPosition()));
        mutablePolygon.setStrokePattern(getSelectedPattern(strokePatternSpinner.getSelectedItemPosition()));

        // Move the map so that it is centered on the mutable polygon.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 4));

        // Add a listener for polygon clicks that changes the clicked polygon's stroke color.
        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                // Flip the red, green and blue components of the polygon's stroke color.
                polygon.setStrokeColor(polygon.getStrokeColor() ^ 0x00ffffff);
            }
        });
    }

    // [START_EXCLUDE silent]
    /**
     * Creates a List of LatLngs that form a rectangle with the given dimensions.
     */
    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
    }

    private int getSelectedJointType(int pos) {
        int id = JOINT_TYPE_NAME_RESOURCE_IDS[pos];
        if (id == com.example.common_ui.R.string.joint_type_bevel) {
            return JointType.BEVEL;
        } else if (id == com.example.common_ui.R.string.joint_type_round) {
            return JointType.ROUND;
        } else if (id == com.example.common_ui.R.string.joint_type_default) {
            return JointType.DEFAULT;
        }
        return 0;
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
        int parentId = parent.getId();
        if (parentId == com.example.common_ui.R.id.strokeJointTypeSpinner) {
            mutablePolygon.setStrokeJointType(getSelectedJointType(pos));
        } else if (parentId == com.example.common_ui.R.id.strokePatternSpinner) {
            mutablePolygon.setStrokePattern(getSelectedPattern(pos));
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
        if (mutablePolygon == null) {
            return;
        }

        if (seekBar == fillHueBar) {
            mutablePolygon.setFillColor(Color.HSVToColor(
                    Color.alpha(mutablePolygon.getFillColor()), new float[]{progress, 1, 1}));
        } else if (seekBar == fillAlphaBar) {
            int prevColor = mutablePolygon.getFillColor();
            mutablePolygon.setFillColor(Color.argb(
                    progress, Color.red(prevColor), Color.green(prevColor),
                    Color.blue(prevColor)));
        } else if (seekBar == strokeHueBar) {
            mutablePolygon.setStrokeColor(Color.HSVToColor(
                    Color.alpha(mutablePolygon.getStrokeColor()), new float[]{progress, 1, 1}));
        } else if (seekBar == strokeAlphaBar) {
            int prevColorArgb = mutablePolygon.getStrokeColor();
            mutablePolygon.setStrokeColor(Color.argb(
                    progress, Color.red(prevColorArgb), Color.green(prevColorArgb),
                    Color.blue(prevColorArgb)));
        } else if (seekBar == strokeWidthBar) {
            mutablePolygon.setStrokeWidth(progress);
        }
    }

    /**
     * Toggles the clickability of the polygon based on the state of the View that triggered this
     * call.
     * This callback is defined on the CheckBox in the layout for this Activity.
     */
    public void toggleClickability(View view) {
        if (mutablePolygon != null) {
            mutablePolygon.setClickable(((CheckBox) view).isChecked());
        }
    }
    // [END_EXCLUDE]
}
// [END maps_android_sample_polygons]