/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo.polyline;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.example.mapdemo.R;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.GoogleMap.OnPolylineClickListener;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.Dash;
import com.google.android.libraries.maps.model.Dot;
import com.google.android.libraries.maps.model.Gap;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.Polyline;
import com.google.android.libraries.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This shows how to apply multiple colors on a polyline.
 */
public class PolylineDemoActivity extends AppCompatActivity implements
    OnMapReadyCallback, OnPageChangeListener, OnCheckedChangeListener, OnPolylineClickListener {

    public static final String TAG = PolylineDemoActivity.class.getSimpleName();

    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng PERTH = new LatLng(-31.95285, 115.85734);
    private static final LatLng LONDON = new LatLng(51.471547, -0.460052);
    private static final LatLng LOS_ANGELES = new LatLng(33.936524, -118.377686);
    private static final LatLng NEW_YORK = new LatLng(40.641051, -73.777485);
    private static final LatLng AUCKLAND = new LatLng(-37.006254, 174.783018);

    private Polyline australiaPolyline;
    private Polyline melbournePolyline;
    private Polyline sydneyPolyline;
    private Polyline worldPolyline;
    private Polyline selectedPolyline;

    private ViewPager pager; // TODO use ViewPager2
    private PolylineControlFragmentPagerAdapter pagerAdapter;
    private RadioGroup polylineRadio;

    private final Set<Polyline> spanResetPolylines = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.polyline_demo);

        pagerAdapter =
            new PolylineControlFragmentPagerAdapter(
                getSupportFragmentManager(), /* isLiteMode= */ false);
        pager = findViewById(com.example.common_ui.R.id.pager);
        pager.setAdapter(pagerAdapter);

        // onPageSelected(0) isn't invoked once views are ready, so post a Runnable to
        // refreshControlPanel() for the first time instead...
        pager.post(this::refreshControlPanel);

        polylineRadio = findViewById(com.example.common_ui.R.id.polyline_radio);

        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // For accessibility mode. Ideally this string would be localised.
        map.setContentDescription("Google Map with polylines.");

        // Non-loop polyline that goes past Australian cities. Added before sydneyPolyline and would
        // normally be underneath, but increase Z-Index so that this line is on top.
        australiaPolyline =
            map.addPolyline(
                new PolylineOptions()
                    .add(PERTH, ADELAIDE, SYDNEY, MELBOURNE)
                    .pattern(Arrays.asList(new Dot(), new Gap(20.0f)))
                    .color(Color.MAGENTA)
                    .zIndex(1));

        // Geodesic polyline that goes around the world.
        worldPolyline =
            map.addPolyline(
                new PolylineOptions()
                    .add(LONDON, AUCKLAND, LOS_ANGELES, NEW_YORK, LONDON)
                    .width(5)
                    .color(Color.BLUE)
                    .geodesic(true)
                    .clickable(true));

        // Loop polyline centered at Sydney.
        int radius = 4;
        sydneyPolyline =
            map.addPolyline(
                new PolylineOptions()
                    .add(new LatLng(SYDNEY.latitude + radius, SYDNEY.longitude + radius))
                    .add(new LatLng(SYDNEY.latitude + radius, SYDNEY.longitude - radius))
                    .add(new LatLng(SYDNEY.latitude - radius, SYDNEY.longitude - radius))
                    .add(new LatLng(SYDNEY.latitude - radius, SYDNEY.longitude))
                    .add(new LatLng(SYDNEY.latitude - radius, SYDNEY.longitude + radius))
                    .add(new LatLng(SYDNEY.latitude + radius, SYDNEY.longitude + radius))
                    .pattern(Arrays.asList(new Dash(45.0f), new Gap(10.0f)))
                    .color(Color.RED)
                    .width(5)
                    .clickable(true));

        // Create Melbourne polyline to show layering of polylines with same Z-Index. This is added
        // second so it will be layered on top of the Sydney polyline (both have Z-Index == 0).
        melbournePolyline =
            map.addPolyline(
                new PolylineOptions()
                    .add(new LatLng(MELBOURNE.latitude + radius, MELBOURNE.longitude + radius))
                    .add(new LatLng(MELBOURNE.latitude + radius, MELBOURNE.longitude - radius))
                    .add(new LatLng(MELBOURNE.latitude - radius, MELBOURNE.longitude - radius))
                    .add(new LatLng(MELBOURNE.latitude - radius, MELBOURNE.longitude))
                    .add(new LatLng(MELBOURNE.latitude - radius, MELBOURNE.longitude + radius))
                    .add(new LatLng(MELBOURNE.latitude + radius, MELBOURNE.longitude + radius))
                    .color(Color.GREEN)
                    .width(5)
                    .clickable(true));

        map.moveCamera(CameraUpdateFactory.newLatLng(SYDNEY));
        selectedPolyline = australiaPolyline;
        polylineRadio.check(com.example.common_ui.R.id.polyline_radio_australia);

        pager.addOnPageChangeListener(this);
        polylineRadio.setOnCheckedChangeListener(this);
        map.setOnPolylineClickListener(this);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip the values of the r, g and b components of the polyline's color.
        int strokeColor = polyline.getColor() ^ 0x00ffffff;
        polyline.setColor(strokeColor);
        polyline.setSpans(new ArrayList<>());
        spanResetPolylines.add(polyline);
        refreshControlPanel();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == com.example.common_ui.R.id.polyline_radio_australia) {
            selectedPolyline = australiaPolyline;
        } else if (checkedId == com.example.common_ui.R.id.polyline_radio_sydney) {
            selectedPolyline = sydneyPolyline;
        } else if (checkedId == com.example.common_ui.R.id.polyline_radio_melbourne) {
            selectedPolyline = melbournePolyline;
        } else if (checkedId == com.example.common_ui.R.id.polyline_radio_world) {
            selectedPolyline = worldPolyline;
        }
        refreshControlPanel();
    }

    @Override
    public void onPageSelected(int position) {
        refreshControlPanel();
    }

    private void refreshControlPanel() {
        PolylineControlFragment fragment = pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
        if (fragment != null) {
            if (fragment instanceof PolylineSpansControlFragment
                && spanResetPolylines.contains(selectedPolyline)) {
                PolylineSpansControlFragment spansControlFragment = (PolylineSpansControlFragment) fragment;
                spansControlFragment.resetSpanState(selectedPolyline);
                spanResetPolylines.remove(selectedPolyline);
            }
            fragment.setPolyline(selectedPolyline);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Don't do anything here.
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Don't do anything here.
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
