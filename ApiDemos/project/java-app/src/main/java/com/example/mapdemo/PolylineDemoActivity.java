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

import com.example.mapdemo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;

import java.util.Arrays;
import java.util.List;

/**
 * This shows how to draw polylines on a map.
 */
// [START maps_android_sample_polylines]
public class PolylineDemoActivity extends SamplesBaseActivity
        implements OnSeekBarChangeListener, OnItemSelectedListener, OnMapReadyCallback {

    // City locations for mutable polyline.
    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng DARWIN = new LatLng(-12.4258647, 130.7932231);
    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private static final LatLng PERTH = new LatLng(-31.95285, 115.85734);

    // Airport locations for geodesic polyline.
    private static final LatLng AKL = new LatLng(-37.006254, 174.783018);
    private static final LatLng JFK = new LatLng(40.641051, -73.777485);
    private static final LatLng LAX = new LatLng(33.936524, -118.377686);
    private static final LatLng LHR = new LatLng(51.471547, -0.460052);

    private static final int MAX_WIDTH_PX = 100;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;
    private static final int CUSTOM_CAP_IMAGE_REF_WIDTH_PX = 50;
    private static final int INITIAL_STROKE_WIDTH_PX = 5;

    private static final int PATTERN_DASH_LENGTH_PX = 50;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);

    private Polyline mutablePolyline;
    private SeekBar hueBar;
    private SeekBar alphaBar;
    private SeekBar widthBar;
    private Spinner startCapSpinner;
    private Spinner endCapSpinner;
    private Spinner jointTypeSpinner;
    private Spinner patternSpinner;
    private CheckBox clickabilityCheckbox;

    // These are the options for polyline caps, joints and patterns. We use their
    // string resource IDs as identifiers.

    private static final int[] CAP_TYPE_NAME_RESOURCE_IDS = {
            com.example.common_ui.R.string.cap_butt, // Default
            com.example.common_ui.R.string.cap_round,
            com.example.common_ui.R.string.cap_square,
            com.example.common_ui.R.string.cap_image,
    };

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
        setContentView(com.example.common_ui.R.layout.polyline_demo);

        hueBar = findViewById(com.example.common_ui.R.id.hueSeekBar);
        hueBar.setMax(MAX_HUE_DEGREES);
        hueBar.setProgress(0);

        alphaBar = findViewById(com.example.common_ui.R.id.alphaSeekBar);
        alphaBar.setMax(MAX_ALPHA);
        alphaBar.setProgress(MAX_ALPHA);

        widthBar = findViewById(com.example.common_ui.R.id.widthSeekBar);
        widthBar.setMax(MAX_WIDTH_PX);
        widthBar.setProgress(MAX_WIDTH_PX / 2);

        startCapSpinner = findViewById(com.example.common_ui.R.id.startCapSpinner);
        startCapSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(CAP_TYPE_NAME_RESOURCE_IDS)));

        endCapSpinner = findViewById(com.example.common_ui.R.id.endCapSpinner);
        endCapSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(CAP_TYPE_NAME_RESOURCE_IDS)));

        jointTypeSpinner = findViewById(com.example.common_ui.R.id.jointTypeSpinner);
        jointTypeSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(JOINT_TYPE_NAME_RESOURCE_IDS)));

        patternSpinner = findViewById(com.example.common_ui.R.id.patternSpinner);
        patternSpinner.setAdapter(new ArrayAdapter<>(
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
        map.setContentDescription(getString(com.example.common_ui.R.string.polyline_demo_description));

        // A geodesic polyline that goes around the world.
        map.addPolyline(new PolylineOptions()
                .add(LHR, AKL, LAX, JFK, LHR)
                .width(INITIAL_STROKE_WIDTH_PX)
                .color(Color.BLUE)
                .geodesic(true)
                .clickable(clickabilityCheckbox.isChecked()));

        // A simple polyline across Australia. This polyline will be mutable.
        int color = Color.HSVToColor(
                alphaBar.getProgress(), new float[]{hueBar.getProgress(), 1, 1});
        mutablePolyline = map.addPolyline(new PolylineOptions()
                .color(color)
                .width(widthBar.getProgress())
                .clickable(clickabilityCheckbox.isChecked())
                .add(MELBOURNE, ADELAIDE, PERTH, DARWIN));

        hueBar.setOnSeekBarChangeListener(this);
        alphaBar.setOnSeekBarChangeListener(this);
        widthBar.setOnSeekBarChangeListener(this);

        startCapSpinner.setOnItemSelectedListener(this);
        endCapSpinner.setOnItemSelectedListener(this);
        jointTypeSpinner.setOnItemSelectedListener(this);
        patternSpinner.setOnItemSelectedListener(this);

        mutablePolyline.setStartCap(getSelectedCap(startCapSpinner.getSelectedItemPosition()));
        mutablePolyline.setEndCap(getSelectedCap(endCapSpinner.getSelectedItemPosition()));
        mutablePolyline.setJointType(getSelectedJointType(jointTypeSpinner.getSelectedItemPosition()));
        mutablePolyline.setPattern(getSelectedPattern(patternSpinner.getSelectedItemPosition()));

        // Move the map so that it is centered on the mutable polyline.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MELBOURNE, 3));

        // Add a listener for polyline clicks that changes the clicked polyline's color.
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                // Flip the values of the red, green and blue components of the polyline's color.
                polyline.setColor(polyline.getColor() ^ 0x00ffffff);
            }
        });
    }

    // [START_EXCLUDE silent]
    private Cap getSelectedCap(int pos) {
        int id = CAP_TYPE_NAME_RESOURCE_IDS[pos];
        if (id == com.example.common_ui.R.string.cap_butt) {
            return new ButtCap();
        } else if (id == com.example.common_ui.R.string.cap_square) {
            return new SquareCap();
        } else if (id == com.example.common_ui.R.string.cap_round) {
            return new RoundCap();
        } else if (id == com.example.common_ui.R.string.cap_image) {
            return new CustomCap(
                    BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.chevron),
                    CUSTOM_CAP_IMAGE_REF_WIDTH_PX);
        }
        return null;
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
        if (parentId == com.example.common_ui.R.id.startCapSpinner) {
            mutablePolyline.setStartCap(getSelectedCap(pos));
        } else if (parentId == com.example.common_ui.R.id.endCapSpinner) {
            mutablePolyline.setEndCap(getSelectedCap(pos));
        } else if (parentId == com.example.common_ui.R.id.jointTypeSpinner) {
            mutablePolyline.setJointType(getSelectedJointType(pos));
        } else if (parentId == com.example.common_ui.R.id.patternSpinner) {
            mutablePolyline.setPattern(getSelectedPattern(pos));
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
        if (mutablePolyline == null) {
            return;
        }

        if (seekBar == hueBar) {
            mutablePolyline.setColor(Color.HSVToColor(
                    Color.alpha(mutablePolyline.getColor()), new float[]{progress, 1, 1}));
        } else if (seekBar == alphaBar) {
            float[] prevHSV = new float[3];
            Color.colorToHSV(mutablePolyline.getColor(), prevHSV);
            mutablePolyline.setColor(Color.HSVToColor(progress, prevHSV));
        } else if (seekBar == widthBar) {
            mutablePolyline.setWidth(progress);
        }
    }

    /**
     * Toggles the clickability of the polyline based on the state of the View that triggered this
     * call.
     * This callback is defined on the CheckBox in the layout for this Activity.
     */
    public void toggleClickability(View view) {
        if (mutablePolyline != null) {
            mutablePolyline.setClickable(((CheckBox) view).isChecked());
        }
    }
    // [END_EXCLUDE]
}
// [END maps_android_sample_polylines]