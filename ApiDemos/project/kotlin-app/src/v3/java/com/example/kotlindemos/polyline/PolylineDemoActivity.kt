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
package com.example.kotlindemos.polyline

import android.graphics.Color
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.kotlindemos.R
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.GoogleMap.OnPolylineClickListener
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.Dash
import com.google.android.libraries.maps.model.Dot
import com.google.android.libraries.maps.model.Gap
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.Polyline
import com.google.android.libraries.maps.model.PolylineOptions
import java.util.ArrayList
import java.util.Arrays

/**
 * This shows how to apply multiple colors on a polyline.
 */
class PolylineDemoActivity : AppCompatActivity(), OnMapReadyCallback, OnPageChangeListener, OnCheckedChangeListener, OnPolylineClickListener {
    private var australiaPolyline: Polyline? = null
    private var melbournePolyline: Polyline? = null
    private var sydneyPolyline: Polyline? = null
    private var worldPolyline: Polyline? = null
    private var selectedPolyline: Polyline? = null
    private lateinit var pagerAdapter: PolylineControlFragmentPagerAdapter
    private val spanResetPolylines = mutableSetOf<Polyline>()

    private lateinit var polylineRadio: RadioGroup
    private lateinit var pager: ViewPager // TODO use ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.common_ui.R.layout.multicolor_polyline_demo)
        pagerAdapter = PolylineControlFragmentPagerAdapter(
            supportFragmentManager,
            /* isLiteMode= */false
        )
        pager = findViewById(com.example.common_ui.R.id.pager)
        pager.adapter = pagerAdapter

        // onPageSelected(0) isn't invoked once views are ready, so post a Runnable to
        // refreshControlPanel() for the first time instead...
        pager.post { refreshControlPanel() }
        polylineRadio = findViewById(com.example.common_ui.R.id.polyline_radio)
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(com.example.common_ui.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        // For accessibility mode. Ideally this string would be localised.
        map.setContentDescription("Google Map with polylines.")

        // Non-loop polyline that goes past Australian cities. Added before sydneyPolyline and would
        // normally be underneath, but increase Z-Index so that this line is on top.
        australiaPolyline = map.addPolyline(
            PolylineOptions()
                .add(PERTH, ADELAIDE, SYDNEY, MELBOURNE)
                .pattern(Arrays.asList(Dot(), Gap(20.0f)))
                .color(Color.MAGENTA)
                .zIndex(1F))

        // Geodesic polyline that goes around the world.
        worldPolyline = map.addPolyline(
            PolylineOptions()
                .add(LONDON, AUCKLAND, LOS_ANGELES, NEW_YORK, LONDON)
                .width(5F)
                .color(Color.BLUE)
                .geodesic(true)
                .clickable(true))

        // Loop polyline centered at Sydney.
        val radius = 4
        sydneyPolyline = map.addPolyline(
            PolylineOptions()
                .add(LatLng(SYDNEY.latitude + radius, SYDNEY.longitude + radius))
                .add(LatLng(SYDNEY.latitude + radius, SYDNEY.longitude - radius))
                .add(LatLng(SYDNEY.latitude - radius, SYDNEY.longitude - radius))
                .add(LatLng(SYDNEY.latitude - radius, SYDNEY.longitude))
                .add(LatLng(SYDNEY.latitude - radius, SYDNEY.longitude + radius))
                .add(LatLng(SYDNEY.latitude + radius, SYDNEY.longitude + radius))
                .pattern(listOf(Dash(45.0f), Gap(10.0f)))
                .color(Color.RED)
                .width(5F)
                .clickable(true))

        // Create Melbourne polyline to show layering of polylines with same Z-Index. This is added
        // second so it will be layered on top of the Sydney polyline (both have Z-Index == 0).
        melbournePolyline = map.addPolyline(
            PolylineOptions()
                .add(LatLng(MELBOURNE.latitude + radius, MELBOURNE.longitude + radius))
                .add(LatLng(MELBOURNE.latitude + radius, MELBOURNE.longitude - radius))
                .add(LatLng(MELBOURNE.latitude - radius, MELBOURNE.longitude - radius))
                .add(LatLng(MELBOURNE.latitude - radius, MELBOURNE.longitude))
                .add(LatLng(MELBOURNE.latitude - radius, MELBOURNE.longitude + radius))
                .add(LatLng(MELBOURNE.latitude + radius, MELBOURNE.longitude + radius))
                .color(Color.GREEN)
                .width(5F)
                .clickable(true))
        map.moveCamera(CameraUpdateFactory.newLatLng(SYDNEY))
        selectedPolyline = australiaPolyline
        polylineRadio.check(com.example.common_ui.R.id.polyline_radio_australia)
        pager.addOnPageChangeListener(this)
        polylineRadio.setOnCheckedChangeListener(this)
        map.setOnPolylineClickListener(this)
    }

    override fun onPolylineClick(polyline: Polyline) {
        // Flip the values of the r, g and b components of the polyline's color.
        val strokeColor: Int = polyline.color xor 0x00ffffff
        polyline.color = strokeColor
        polyline.setSpans(ArrayList())
        spanResetPolylines.add(polyline)
        refreshControlPanel()
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            com.example.common_ui.R.id.polyline_radio_australia -> {
                selectedPolyline = australiaPolyline
            }
            com.example.common_ui.R.id.polyline_radio_sydney -> {
                selectedPolyline = sydneyPolyline
            }
            com.example.common_ui.R.id.polyline_radio_melbourne -> {
                selectedPolyline = melbournePolyline
            }
            com.example.common_ui.R.id.polyline_radio_world -> {
                selectedPolyline = worldPolyline
            }
        }
        refreshControlPanel()
    }

    override fun onPageSelected(position: Int) {
        refreshControlPanel()
    }

    private fun refreshControlPanel() {
        val fragment: PolylineControlFragment? = pagerAdapter.getFragmentAtPosition(pager.getCurrentItem())
        if (fragment != null) {
            if (fragment is PolylineSpansControlFragment
                && spanResetPolylines.contains(selectedPolyline)) {
                val spansControlFragment: PolylineSpansControlFragment = fragment as PolylineSpansControlFragment
                spansControlFragment.resetSpanState(selectedPolyline)
                spanResetPolylines.remove(selectedPolyline)
            }
            fragment.polyline = selectedPolyline
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        // Don't do anything here.
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        // Don't do anything here.
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {
    }

    companion object {
        val TAG: String = PolylineDemoActivity::class.java.getSimpleName()
        private val MELBOURNE: LatLng = LatLng(-37.81319, 144.96298)
        private val SYDNEY: LatLng = LatLng(-33.87365, 151.20689)
        private val ADELAIDE: LatLng = LatLng(-34.92873, 138.59995)
        private val PERTH: LatLng = LatLng(-31.95285, 115.85734)
        private val LONDON: LatLng = LatLng(51.471547, -0.460052)
        private val LOS_ANGELES: LatLng = LatLng(33.936524, -118.377686)
        private val NEW_YORK: LatLng = LatLng(40.641051, -73.777485)
        private val AUCKLAND: LatLng = LatLng(-37.006254, 174.783018)
    }
}