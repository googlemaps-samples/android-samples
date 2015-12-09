/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.Arrays;
import java.util.List;

/**
 * This shows how to draw polygons on a map.
 */
public class PolygonDemoActivity extends AppCompatActivity
        implements OnSeekBarChangeListener, OnMapReadyCallback {

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private static final int WIDTH_MAX = 50;

    private static final int HUE_MAX = 360;

    private static final int ALPHA_MAX = 255;

    private Polygon mMutablePolygon;

    private Polygon mClickablePolygonWithHoles;

    private SeekBar mColorBar;

    private SeekBar mAlphaBar;

    private SeekBar mWidthBar;

    private CheckBox mClickabilityCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polygon_demo);

        mColorBar = (SeekBar) findViewById(R.id.hueSeekBar);
        mColorBar.setMax(HUE_MAX);
        mColorBar.setProgress(0);

        mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
        mAlphaBar.setMax(ALPHA_MAX);
        mAlphaBar.setProgress(127);

        mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
        mWidthBar.setMax(WIDTH_MAX);
        mWidthBar.setProgress(10);

        mClickabilityCheckbox = (CheckBox) findViewById(R.id.toggleClickability);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with polygons.");

        // Create a rectangle with two rectangular holes.
        mClickablePolygonWithHoles = map.addPolygon(new PolygonOptions()
                .addAll(createRectangle(new LatLng(-20, 130), 5, 5))
                .addHole(createRectangle(new LatLng(-22, 128), 1, 1))
                .addHole(createRectangle(new LatLng(-18, 133), 0.5, 1.5))
                .fillColor(Color.CYAN)
                .strokeColor(Color.BLUE)
                .strokeWidth(5)
                .clickable(mClickabilityCheckbox.isChecked()));

        // Create a rectangle centered at Sydney.
        PolygonOptions options = new PolygonOptions()
                .addAll(createRectangle(SYDNEY, 5, 8))
                .clickable(mClickabilityCheckbox.isChecked());

        int fillColor = Color.HSVToColor(
                mAlphaBar.getProgress(), new float[]{mColorBar.getProgress(), 1, 1});
        mMutablePolygon = map.addPolygon(options
                .strokeWidth(mWidthBar.getProgress())
                .strokeColor(Color.BLACK)
                .fillColor(fillColor));

        // Create another polygon that overlaps the previous two.
        // Clickability defaults to false, so this one won't accept clicks.
        map.addPolygon(new PolygonOptions()
                .addAll(createRectangle(new LatLng(-27, 140), 10, 7))
                .fillColor(Color.WHITE)
                .strokeColor(Color.BLACK));

        mColorBar.setOnSeekBarChangeListener(this);
        mAlphaBar.setOnSeekBarChangeListener(this);
        mWidthBar.setOnSeekBarChangeListener(this);

        // Move the map so that it is centered on the mutable polygon.
        map.moveCamera(CameraUpdateFactory.newLatLng(SYDNEY));

        // Add a listener for polygon clicks that changes the clicked polygon's stroke color.
        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                // Flip the r, g and b components of the polygon's stroke color.
                int strokeColor = polygon.getStrokeColor() ^ 0x00ffffff;
                polygon.setStrokeColor(strokeColor);
            }
        });
    }

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
        if (mMutablePolygon == null) {
            return;
        }

        if (seekBar == mColorBar) {
            mMutablePolygon.setFillColor(Color.HSVToColor(
                    Color.alpha(mMutablePolygon.getFillColor()), new float[]{progress, 1, 1}));
        } else if (seekBar == mAlphaBar) {
            int prevColor = mMutablePolygon.getFillColor();
            mMutablePolygon.setFillColor(Color.argb(
                    progress, Color.red(prevColor), Color.green(prevColor),
                    Color.blue(prevColor)));
        } else if (seekBar == mWidthBar) {
            mMutablePolygon.setStrokeWidth(progress);
        }
    }

    /**
     * Toggles the clickability of two polygons based on the state of the View that triggered this
     * call.
     * This callback is defined on the CheckBox in the layout for this Activity.
     */
    public void toggleClickability(View view) {
        if (mClickablePolygonWithHoles != null) {
            mClickablePolygonWithHoles.setClickable(((CheckBox) view).isChecked());
        }
        if (mMutablePolygon != null) {
            mMutablePolygon.setClickable(((CheckBox) view).isChecked());
        }
    }
}