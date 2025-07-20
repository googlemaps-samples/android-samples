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
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Spinner
import com.example.common_ui.R


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PatternItem

import java.util.ArrayList
import java.util.Arrays

/**
 * This shows how to draw circles on a map.
 */
class CircleDemoActivity :
        SamplesBaseActivity(),
        SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener,
        OnMapReadyCallback {

    private val DEFAULT_RADIUS_METERS = 1000000.0

    private val MAX_WIDTH_PX = 50
    private val MAX_HUE_DEGREE = 360

    private val MAX_ALPHA = 255
    private val PATTERN_DASH_LENGTH = 100
    private val PATTERN_GAP_LENGTH = 200

    private val sydney = LatLng(-33.87365, 151.20689)

    private val dot = Dot()
    private val dash = Dash(PATTERN_DASH_LENGTH.toFloat())
    private val gap = Gap(PATTERN_GAP_LENGTH.toFloat())
    private val patternDotted = Arrays.asList(dot, gap)
    private val patternDashed = Arrays.asList(dash, gap)
    private val patternMixed = Arrays.asList(dot, gap, dot, dash, gap)

    // These are the options for stroke patterns
    private val patterns: List<Pair<Int, List<PatternItem>?>> = listOf(
            Pair(R.string.pattern_solid, null),
            Pair(R.string.pattern_dashed, patternDashed),
            Pair(R.string.pattern_dotted, patternDotted),
            Pair(R.string.pattern_mixed, patternMixed)
    )

    private lateinit var map: GoogleMap

    private val circles = ArrayList<DraggableCircle>(1)

    private var fillColorArgb : Int = 0
    private var strokeColorArgb: Int = 0

    private lateinit var fillHueBar: SeekBar
    private lateinit var fillAlphaBar: SeekBar
    private lateinit var strokeWidthBar: SeekBar
    private lateinit var strokeHueBar: SeekBar
    private lateinit var strokeAlphaBar: SeekBar
    private lateinit var strokePatternSpinner: Spinner
    private lateinit var clickabilityCheckbox: CheckBox

    /**
     * This class contains information about a circle, including its markers
     */
    private inner class DraggableCircle(center: LatLng, private var radiusMeters: Double) {
        private val centerMarker: Marker? = map.addMarker(MarkerOptions().apply {
            position(center)
            draggable(true)
        })

        private val radiusMarker: Marker? = map.addMarker(
                MarkerOptions().apply {
                    position(center.getPointAtDistance(radiusMeters))
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    draggable(true)
                })

        private val circle: Circle = map.addCircle(
                CircleOptions().apply {
                    center(center)
                    radius(radiusMeters)
                    strokeWidth(strokeWidthBar.progress.toFloat())
                    strokeColor(strokeColorArgb)
                    fillColor(fillColorArgb)
                    clickable(clickabilityCheckbox.isChecked)
                    strokePattern(getSelectedPattern(strokePatternSpinner.selectedItemPosition))
                })

        fun onMarkerMoved(marker: Marker): Boolean {
            when (marker) {
                centerMarker -> {
                    circle.center = marker.position
                    radiusMarker?.position = marker.position.getPointAtDistance(radiusMeters)
                }
                radiusMarker -> {
                    radiusMeters = centerMarker?.position?.distanceFrom(radiusMarker.position)!!
                    circle.radius = radiusMeters
                }
                else -> return false
            }
            return true
        }

        fun onStyleChange() {
            // [circle] is treated as implicit this inside the with block
            with(circle) {
                strokeWidth = strokeWidthBar.progress.toFloat()
                strokeColor = strokeColorArgb
                fillColor = fillColorArgb
            }
        }

        fun setStrokePattern(pattern: List<PatternItem>?) {
            circle.strokePattern = pattern
        }

        fun setClickable(clickable: Boolean) {
            circle.isClickable = clickable
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.circle_demo)

        // Set all the SeekBars
        fillHueBar = findViewById<SeekBar>(R.id.fillHueSeekBar).apply {
            max = MAX_HUE_DEGREE
            progress = MAX_HUE_DEGREE / 2
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
            max = MAX_HUE_DEGREE
            progress = 0
        }
        strokeAlphaBar = findViewById<SeekBar>(R.id.strokeAlphaSeekBar).apply {
            max = MAX_ALPHA
            progress = MAX_ALPHA
        }

        strokePatternSpinner = findViewById<Spinner>(R.id.strokePatternSpinner).apply {
            adapter = ArrayAdapter(this@CircleDemoActivity,
                    android.R.layout.simple_spinner_item,
                    getResourceStrings())
        }

        clickabilityCheckbox = findViewById(R.id.toggleClickability)
        clickabilityCheckbox.setOnClickListener {
            toggleClickability(it)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    /** Get all the strings of patterns and return them as Array. */
    private fun getResourceStrings() = (patterns).map { getString(it.first) }.toTypedArray()

    /**
     * When the map is ready, move the camera to put the Circle in the middle of the screen,
     * create a circle in Sydney, and set the listeners for the map, circles, and SeekBars.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // we need to initialise map before creating a circle
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 4.0f))
            setContentDescription(getString(R.string.circle_demo_description))
            setOnMapLongClickListener { point ->
                // We know the center, let's place the outline at a point 3/4 along the view.
                val view: View = supportFragmentManager.findFragmentById(R.id.map)?.view
                        ?: return@setOnMapLongClickListener
                val radiusLatLng = map.projection.fromScreenLocation(
                        Point(view.height * 3 / 4, view.width * 3 / 4))
                // Create the circle.
                val newCircle = DraggableCircle(point, point.distanceFrom(radiusLatLng))
                circles.add(newCircle)
            }

            setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker) {
                    onMarkerMoved(marker)
                }

                override fun onMarkerDragEnd(marker: Marker) {
                    onMarkerMoved(marker)
                }

                override fun onMarkerDrag(marker: Marker) {
                    onMarkerMoved(marker)
                }
            })

            // Flip the red, green and blue components of the circle's stroke color.
            setOnCircleClickListener { c -> c.strokeColor = c.strokeColor xor 0x00ffffff }
        }

        fillColorArgb = Color.HSVToColor(fillAlphaBar.progress,
                floatArrayOf(fillHueBar.progress.toFloat(), 1f, 1f))
        strokeColorArgb = Color.HSVToColor(strokeAlphaBar.progress,
                floatArrayOf(strokeHueBar.progress.toFloat(), 1f, 1f))

        val circle = DraggableCircle(sydney, DEFAULT_RADIUS_METERS)
        circles.add(circle)

        // Set listeners for all the SeekBar
        fillHueBar.setOnSeekBarChangeListener(this)
        fillAlphaBar.setOnSeekBarChangeListener(this)

        strokeWidthBar.setOnSeekBarChangeListener(this)
        strokeHueBar.setOnSeekBarChangeListener(this)
        strokeAlphaBar.setOnSeekBarChangeListener(this)

        strokePatternSpinner.onItemSelectedListener = this
    }

    private fun getSelectedPattern(pos: Int): List<PatternItem>? = patterns[pos].second

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        if (parent.id == R.id.strokePatternSpinner) {
            circles.map { it.setStrokePattern(getSelectedPattern(pos)) }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Don't do anything here.
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // Don't do anything here.
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // Don't do anything here.
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        // Update the fillColorArgb if the SeekBars for it is changed, otherwise keep the old value
        fillColorArgb = when (seekBar) {
            fillHueBar -> Color.HSVToColor(Color.alpha(fillColorArgb),
                    floatArrayOf(progress.toFloat(), 1f, 1f))
            fillAlphaBar -> Color.argb(progress, Color.red(fillColorArgb),
                    Color.green(fillColorArgb), Color.blue(fillColorArgb))
            else -> fillColorArgb
        }

        // Set the strokeColorArgb if the SeekBars for it is changed, otherwise keep the old value
        strokeColorArgb = when (seekBar) {
            strokeHueBar -> Color.HSVToColor(Color.alpha(strokeColorArgb),
                    floatArrayOf(progress.toFloat(), 1f, 1f))
            strokeAlphaBar -> Color.argb(progress, Color.red(strokeColorArgb),
                    Color.green(strokeColorArgb), Color.blue(strokeColorArgb))
            else -> strokeColorArgb
        }

        // Apply the style change to all the circles.
        circles.map { it.onStyleChange() }
    }

    private fun onMarkerMoved(marker: Marker) {
        circles.forEach { if (it.onMarkerMoved(marker)) return }
    }

    /** Listener for the Clickable CheckBox, to set if all the circles can be click */
    fun toggleClickability(view: View) {
        circles.map { it.setClickable((view as CheckBox).isChecked) }
    }
}

/**
 * Extension function to find the distance from this to another LatLng object
 */
private fun LatLng.distanceFrom(other: LatLng): Double {
    val result = FloatArray(1)
    Location.distanceBetween(latitude, longitude, other.latitude, other.longitude, result)
    return result[0].toDouble()
}

private fun LatLng.getPointAtDistance(distance: Double): LatLng {
    val radiusOfEarth = 6371009.0
    val radiusAngle = (Math.toDegrees(distance / radiusOfEarth)
            / Math.cos(Math.toRadians(latitude)))
    return LatLng(latitude, longitude + radiusAngle)
}