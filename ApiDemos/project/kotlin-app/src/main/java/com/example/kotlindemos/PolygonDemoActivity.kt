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

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.CheckBox
import android.widget.ArrayAdapter
import com.example.common_ui.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.SupportMapFragment

import java.util.Arrays

/**
 * This shows how to draw polygons on a map.
 */
class PolygonDemoActivity :
        SamplesBaseActivity(),
        OnMapReadyCallback,
        SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener {

    private val center = LatLng(-20.0, 130.0)
    private val MAX_WIDTH_PX = 100
    private val MAX_HUE_DEGREES = 360
    private val MAX_ALPHA = 255
    private val PATTERN_DASH_LENGTH_PX = 50
    private val PATTERN_GAP_LENGTH_PX = 10
    private val dot = Dot()
    private val dash = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
    private val gap = Gap(PATTERN_GAP_LENGTH_PX.toFloat())
    private val patternDotted = Arrays.asList(dot, gap)
    private val patternDashed = Arrays.asList(dash, gap)
    private val patternMixed = Arrays.asList(dot, gap, dot, dash, gap)

    private lateinit var mutablePolygon: Polygon
    private lateinit var fillHueBar: SeekBar
    private lateinit var fillAlphaBar: SeekBar
    private lateinit var strokeWidthBar: SeekBar
    private lateinit var strokeHueBar: SeekBar
    private lateinit var strokeAlphaBar: SeekBar
    private lateinit var strokeJointTypeSpinner: Spinner
    private lateinit var strokePatternSpinner: Spinner
    private lateinit var clickabilityCheckbox: CheckBox

    // These are the options for polygon stroke joints and patterns. We use their
    // string resource IDs as identifiers.

    private val jointTypeNameResourceIds = intArrayOf(
        R.string.joint_type_default, // Default
            R.string.joint_type_bevel, R.string.joint_type_round)

    private val patternTypeNameResourceIds = intArrayOf(
        R.string.pattern_solid, // Default
            R.string.pattern_dashed, R.string.pattern_dotted, R.string.pattern_mixed)

    // [START maps_android_sample_polygons]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.polygon_demo)

        fillHueBar = findViewById<SeekBar>(R.id.fillHueSeekBar).apply {
            max = MAX_HUE_DEGREES
            progress = MAX_HUE_DEGREES / 2
        }

        fillAlphaBar = findViewById<SeekBar>(R.id.fillAlphaSeekBar).apply {
            max = MAX_ALPHA
            progress = MAX_ALPHA / 2
        }

        strokeWidthBar = findViewById<SeekBar>(R.id.strokeWidthSeekBar).apply {
            max = MAX_WIDTH_PX
            progress = MAX_WIDTH_PX / 3
        }

        strokeHueBar = findViewById<SeekBar>(R.id.strokeHueSeekBar).apply {
            max = MAX_HUE_DEGREES
            progress = 0
        }

        strokeAlphaBar = findViewById<SeekBar>(R.id.strokeAlphaSeekBar).apply {
            max = MAX_ALPHA
            progress = MAX_ALPHA
        }

        strokeJointTypeSpinner = findViewById<Spinner>(R.id.strokeJointTypeSpinner).apply {
            adapter = ArrayAdapter(
                    this@PolygonDemoActivity, android.R.layout.simple_spinner_item,
                    getResourceStrings(jointTypeNameResourceIds))
        }

        strokePatternSpinner = findViewById<Spinner>(R.id.strokePatternSpinner).apply {
            adapter = ArrayAdapter(
                    this@PolygonDemoActivity, android.R.layout.simple_spinner_item,
                    getResourceStrings(patternTypeNameResourceIds))
        }

        clickabilityCheckbox = findViewById(R.id.toggleClickability)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        applyInsets(findViewById<View?>(R.id.map_container))
    }
    // [START_EXCLUDE silent]

    private fun getResourceStrings(resourceIds: IntArray): List<String> {
        return resourceIds.map { getString(it) }
    }
    // [END_EXCLUDE]

    override fun onMapReady(googleMap: GoogleMap) {
        val fillColorArgb = Color.HSVToColor(
                fillAlphaBar.progress, floatArrayOf(fillHueBar.progress.toFloat(), 1f, 1f))
        val strokeColorArgb = Color.HSVToColor(
                strokeAlphaBar.progress, floatArrayOf(strokeHueBar.progress.toFloat(), 1f, 1f))

        with(googleMap) {
            // Override the default content description on the view, for accessibility mode.
            setContentDescription(getString(R.string.polygon_demo_description))
            // Move the googleMap so that it is centered on the mutable polygon.
            moveCamera(CameraUpdateFactory.newLatLngZoom(center, 4f))

            // Create a rectangle with two rectangular holes.
            mutablePolygon = addPolygon(PolygonOptions().apply {
                addAll(createRectangle(center, 5.0, 5.0))
                addHole(createRectangle(LatLng(-22.0, 128.0), 1.0, 1.0))
                addHole(createRectangle(LatLng(-18.0, 133.0), 0.5, 1.5))
                fillColor(fillColorArgb)
                strokeColor(strokeColorArgb)
                strokeWidth(strokeWidthBar.progress.toFloat())
                clickable(clickabilityCheckbox.isChecked)
            })

            // Add a listener for polygon clicks that changes the clicked polygon's stroke color.
            setOnPolygonClickListener { polygon ->
                // Flip the red, green and blue components of the polygon's stroke color.
                polygon.strokeColor = polygon.strokeColor xor 0x00ffffff
            }
        }

        // set listeners on seekBars
        arrayOf(fillHueBar, fillAlphaBar, strokeWidthBar, strokeHueBar, strokeAlphaBar).map {
            it.setOnSeekBarChangeListener(this)
        }

        // set listeners on spinners
        arrayOf(strokeJointTypeSpinner, strokePatternSpinner).map {
            it.onItemSelectedListener = this
        }

        // set line pattern and joint type based on current spinner position
        with(mutablePolygon) {
            strokeJointType = getSelectedJointType(strokeJointTypeSpinner.selectedItemPosition)
            strokePattern = getSelectedPattern(strokePatternSpinner.selectedItemPosition)
        }

    }
    // [END maps_android_sample_polygons]

    /**
     * Creates a List of LatLngs that form a rectangle with the given dimensions.
     */
    private fun createRectangle(
            center: LatLng,
            halfWidth: Double,
            halfHeight: Double
    ): List<LatLng> {
        return Arrays.asList(
                LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
                LatLng(center.latitude - halfHeight, center.longitude - halfWidth))
    }

    private fun getSelectedJointType(pos: Int): Int {
        return when (jointTypeNameResourceIds[pos]) {
            R.string.joint_type_bevel -> JointType.BEVEL
            R.string.joint_type_round -> JointType.ROUND
            R.string.joint_type_default -> JointType.DEFAULT
            else -> 0
        }
    }

    private fun getSelectedPattern(pos: Int): List<PatternItem>? {
        return when (patternTypeNameResourceIds[pos]) {
            R.string.pattern_solid -> null
            R.string.pattern_dotted -> patternDotted
            R.string.pattern_dashed -> patternDashed
            R.string.pattern_mixed -> patternMixed
            else -> null
        }
    }

    /**
     * Toggles the clickability of the polygon based on the state of the View that triggered this
     * call.
     * This callback is defined on the CheckBox in the layout for this Activity.
     */
    fun toggleClickability(view: View) {
        if (view is CheckBox) {
            mutablePolygon.isClickable = view.isChecked
        }
    }


    /**
     * Listener that is called when a seek bar is moved.
     * Can change polygon fill color/transparency, stroke color/transparency and stroke width.
     */
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int,
                                   fromUser: Boolean) {

        mutablePolygon.fillColor = when (seekBar) {
            fillHueBar -> Color.HSVToColor(Color.alpha(mutablePolygon.fillColor),
                    floatArrayOf(progress.toFloat(), 1f, 1f))
            fillAlphaBar -> {
                val prevColor = mutablePolygon.fillColor
                Color.argb(progress, Color.red(prevColor), Color.green(prevColor),
                        Color.blue(prevColor))
            }
            else -> mutablePolygon.fillColor
        }

        mutablePolygon.strokeColor = when (seekBar) {
            strokeHueBar -> Color.HSVToColor(
                    Color.alpha(mutablePolygon.strokeColor),
                    floatArrayOf(progress.toFloat(), 1f, 1f))
            strokeAlphaBar -> {
                val prevColorArgb = mutablePolygon.strokeColor
                Color.argb(progress, Color.red(prevColorArgb),
                        Color.green(prevColorArgb), Color.blue(prevColorArgb))
            }
            else -> mutablePolygon.strokeColor
        }

        if (seekBar == strokeWidthBar) mutablePolygon.strokeWidth = progress.toFloat()

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // do nothing
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // do nothing
    }

    /**
     * Listener for when an item is selected using a spinner.
     * Can change line pattern and joint type.
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int,
                                id: Long) {
        when (parent?.id) {
            R.id.strokeJointTypeSpinner ->
                mutablePolygon.strokeJointType = getSelectedJointType(pos)
            R.id.strokePatternSpinner ->
                mutablePolygon.strokePattern = getSelectedPattern(pos)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // don't do anything here
    }

}