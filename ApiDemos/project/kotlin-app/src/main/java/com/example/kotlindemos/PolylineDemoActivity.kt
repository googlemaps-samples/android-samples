/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.Cap
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.SquareCap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Spinner

import java.util.Arrays

/**
 * This shows how to draw polylines on a map.
 */
class PolylineDemoActivity :
        SamplesBaseActivity(),
        OnMapReadyCallback,
        SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener {

    private val CUSTOM_CAP_IMAGE_REF_WIDTH_PX = 50
    private val INITIAL_STROKE_WIDTH_PX = 5
    private val MAX_WIDTH_PX = 100
    private val MAX_HUE_DEGREES = 360
    private val MAX_ALPHA = 255
    private val PATTERN_DASH_LENGTH_PX = 50
    private val PATTERN_GAP_LENGTH_PX = 20

    // City locations for mutable polyline.
    private val adelaideLatLng = LatLng(-34.92873, 138.59995)
    private val darwinLatLng = LatLng(-12.4258647, 130.7932231)
    private val melbourneLatLng = LatLng(-37.81319, 144.96298)
    private val perthLatLng = LatLng(-31.95285, 115.85734)

    // Airport locations for geodesic polyline.
    private val aklLatLng = LatLng(-37.006254, 174.783018)
    private val jfkLatLng = LatLng(40.641051, -73.777485)
    private val laxLatLng = LatLng(33.936524, -118.377686)
    private val lhrLatLng = LatLng(51.471547, -0.460052)

    private val dot = Dot()
    private val dash = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
    private val gap = Gap(PATTERN_GAP_LENGTH_PX.toFloat())
    private val patternDotted = Arrays.asList(dot, gap)
    private val patternDashed = Arrays.asList(dash, gap)
    private val patternMixed = Arrays.asList(dot, gap, dot, dash, gap)

    private lateinit var mutablePolyline: Polyline
    private lateinit var hueBar: SeekBar
    private lateinit var alphaBar: SeekBar
    private lateinit var widthBar: SeekBar
    private lateinit var startCapSpinner: Spinner
    private lateinit var endCapSpinner: Spinner
    private lateinit var jointTypeSpinner: Spinner
    private lateinit var patternSpinner: Spinner
    private lateinit var clickabilityCheckbox: CheckBox

    // These are the options for polyline caps, joints and patterns. We use their
    // string resource IDs as identifiers.
    private val capTypeNameResourceIds = intArrayOf(com.example.common_ui.R.string.cap_butt,
                                                    com.example.common_ui.R.string.cap_round,
                                                    com.example.common_ui.R.string.cap_square,
                                                    com.example.common_ui.R.string.cap_image)

    private val jointTypeNameResourceIds = intArrayOf(com.example.common_ui.R.string.joint_type_default,
                                                      com.example.common_ui.R.string.joint_type_bevel,
                                                      com.example.common_ui.R.string.joint_type_round)

    private val patternTypeNameResourceIds = intArrayOf(com.example.common_ui.R.string.pattern_solid,
                                                        com.example.common_ui.R.string.pattern_dashed,
                                                        com.example.common_ui.R.string.pattern_dotted,
                                                        com.example.common_ui.R.string.pattern_mixed)

    // [START maps_android_sample_polylines]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.common_ui.R.layout.polyline_demo)

        hueBar = findViewById<SeekBar>(com.example.common_ui.R.id.hueSeekBar).apply {
            max = MAX_HUE_DEGREES
            progress = 0
        }

        alphaBar = findViewById<SeekBar>(com.example.common_ui.R.id.alphaSeekBar).apply {
            max = MAX_ALPHA
            progress = MAX_ALPHA
        }

        widthBar = findViewById<SeekBar>(com.example.common_ui.R.id.widthSeekBar).apply {
            max = MAX_WIDTH_PX
            progress = MAX_WIDTH_PX / 2
        }

        startCapSpinner = findViewById<Spinner>(com.example.common_ui.R.id.startCapSpinner).apply {
            adapter = ArrayAdapter(this@PolylineDemoActivity,
                    android.R.layout.simple_spinner_item,
                    getResourceStrings(capTypeNameResourceIds))
        }

        endCapSpinner = findViewById<Spinner>(com.example.common_ui.R.id.endCapSpinner).apply {
            adapter = ArrayAdapter(this@PolylineDemoActivity,
                    android.R.layout.simple_spinner_item,
                    getResourceStrings(capTypeNameResourceIds))
        }

        jointTypeSpinner = findViewById<Spinner>(com.example.common_ui.R.id.jointTypeSpinner).apply {
            adapter = ArrayAdapter(this@PolylineDemoActivity,
                    android.R.layout.simple_spinner_item,
                    getResourceStrings(jointTypeNameResourceIds))
        }

        patternSpinner = findViewById<Spinner>(com.example.common_ui.R.id.patternSpinner).apply {
            adapter = ArrayAdapter(
                    this@PolylineDemoActivity, android.R.layout.simple_spinner_item,
                    getResourceStrings(patternTypeNameResourceIds))
        }

        clickabilityCheckbox = findViewById<CheckBox>(com.example.common_ui.R.id.toggleClickability)

        val mapFragment = supportFragmentManager.findFragmentById(com.example.common_ui.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        applyInsets(findViewById<View?>(com.example.common_ui.R.id.map_container))
    }
    // [START_EXCLUDE silent]

    private fun getResourceStrings(resourceIds: IntArray): List<String> {
        return resourceIds.map { getString(it) }
    }
    // [END_EXCLUDE]

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap

        with(googleMap) {
            // Override the default content description on the view, for accessibility mode.
            setContentDescription(getString(com.example.common_ui.R.string.polyline_demo_description))

            // A geodesic polyline that goes around the world.
            addPolyline(PolylineOptions().apply {
                add(lhrLatLng, aklLatLng, laxLatLng, jfkLatLng, lhrLatLng)
                width(INITIAL_STROKE_WIDTH_PX.toFloat())
                color(Color.BLUE)
                geodesic(true)
                clickable(clickabilityCheckbox.isChecked)
            })

            // Move the googleMap so that it is centered on the mutable polyline.
            moveCamera(CameraUpdateFactory.newLatLngZoom(melbourneLatLng, 3f))

            // Add a listener for polyline clicks that changes the clicked polyline's color.
            setOnPolylineClickListener { polyline ->
                // Flip the values of the red, green and blue components of the polyline's color.
                polyline.color = polyline.color xor 0x00ffffff
            }
        }

        // A simple polyline across Australia. This polyline will be mutable.
        mutablePolyline = googleMap.addPolyline(PolylineOptions().apply{
            color(Color.HSVToColor(
                    alphaBar.progress, floatArrayOf(hueBar.progress.toFloat(), 1f, 1f)))
            width(widthBar.progress.toFloat())
            clickable(clickabilityCheckbox.isChecked)
            add(melbourneLatLng, adelaideLatLng, perthLatLng, darwinLatLng)
        })

        arrayOf(hueBar, alphaBar, widthBar).map {
            it.setOnSeekBarChangeListener(this)
        }

        arrayOf(startCapSpinner, endCapSpinner, jointTypeSpinner, patternSpinner).map {
            it.onItemSelectedListener = this
        }

        with(mutablePolyline) {
            startCap = getSelectedCap(startCapSpinner.selectedItemPosition) ?: ButtCap()
            endCap = getSelectedCap(endCapSpinner.selectedItemPosition) ?: ButtCap()
            jointType = getSelectedJointType(jointTypeSpinner.selectedItemPosition)
            pattern = getSelectedPattern(patternSpinner.selectedItemPosition)
        }

        clickabilityCheckbox.setOnClickListener {
            view -> mutablePolyline.isClickable = (view as CheckBox).isChecked
        }
    }
    // [END maps_android_sample_polylines]

    private fun getSelectedCap(pos: Int): Cap? {
        return when (capTypeNameResourceIds[pos]) {
            com.example.common_ui.R.string.cap_butt -> ButtCap()
            com.example.common_ui.R.string.cap_square -> SquareCap()
            com.example.common_ui.R.string.cap_round -> RoundCap()
            com.example.common_ui.R.string.cap_image -> CustomCap(
              BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.chevron),
              CUSTOM_CAP_IMAGE_REF_WIDTH_PX.toFloat())
            else -> null
        }
    }

    private fun getSelectedJointType(pos: Int): Int {
        return when (jointTypeNameResourceIds[pos]) {
            com.example.common_ui.R.string.joint_type_bevel -> JointType.BEVEL
            com.example.common_ui.R.string.joint_type_round -> JointType.ROUND
            com.example.common_ui.R.string.joint_type_default -> JointType.DEFAULT
            else -> 0
        }
    }

    private fun getSelectedPattern(pos: Int): List<PatternItem>? {
        return when (patternTypeNameResourceIds[pos]) {
            com.example.common_ui.R.string.pattern_solid -> null
            com.example.common_ui.R.string.pattern_dotted -> patternDotted
            com.example.common_ui.R.string.pattern_dashed -> patternDashed
            com.example.common_ui.R.string.pattern_mixed -> patternMixed
            else -> null
        }
    }

    /**
     * Listener for changes in a seekbar's position.
     * Can change polyline color, width and transparency.
     */
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

        when(seekBar) {
            hueBar -> mutablePolyline.color = Color.HSVToColor(
                    Color.alpha(mutablePolyline.color), floatArrayOf(progress.toFloat(), 1f, 1f))
            alphaBar -> {
                val prevHSV = FloatArray(3)
                Color.colorToHSV(mutablePolyline.color, prevHSV)
                mutablePolyline.color = Color.HSVToColor(progress, prevHSV)
            }
            widthBar -> mutablePolyline.width = progress.toFloat()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // Don't do anything here.
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // Don't do anything here.
    }

    /**
     * Listener for changes in a spinner's position.
     * Can change the polyline's start and end caps, pattern and joint type.
     */
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        when (parent.id) {
            com.example.common_ui.R.id.startCapSpinner -> mutablePolyline.startCap = getSelectedCap(pos) ?: ButtCap()
            com.example.common_ui.R.id.endCapSpinner -> mutablePolyline.endCap = getSelectedCap(pos) ?: ButtCap()
            com.example.common_ui.R.id.jointTypeSpinner -> mutablePolyline.jointType = getSelectedJointType(pos)
            com.example.common_ui.R.id.patternSpinner -> mutablePolyline.pattern = getSelectedPattern(pos)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Don't do anything here.
    }

}