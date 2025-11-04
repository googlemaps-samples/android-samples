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
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import com.example.common_ui.R
import com.example.kotlindemos.utils.MapProvider


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
import com.google.maps.android.ktx.utils.sphericalDistance
import com.google.maps.android.ktx.utils.withSphericalOffset

import java.util.ArrayList

/**
 * This shows how to draw circles on a map.
 */
class CircleDemoActivity : SamplesBaseActivity(),
    SeekBar.OnSeekBarChangeListener,
    AdapterView.OnItemSelectedListener,
    OnMapReadyCallback,
    MapProvider {
    private val dot = Dot()
    private val dash = Dash(PATTERN_DASH_LENGTH.toFloat())
    private val gap = Gap(PATTERN_GAP_LENGTH.toFloat())
    private val patternDotted = listOf(dot, gap)
    private val patternDashed = listOf(dash, gap)
    private val patternMixed = listOf(dot, gap, dot, dash, gap)

    // These are the options for stroke patterns
    private val patterns: List<Pair<Int, List<PatternItem>?>> = listOf(
            Pair(R.string.pattern_solid, null),
            Pair(R.string.pattern_dashed, patternDashed),
            Pair(R.string.pattern_dotted, patternDotted),
            Pair(R.string.pattern_mixed, patternMixed)
    )

    override lateinit var map: GoogleMap

    override var mapReady = false

    internal val circles = ArrayList<DraggableCircle>(1)

    private var fillColorArgb : Int = 0
    private var strokeColorArgb: Int = 0

    internal lateinit var binding: com.example.common_ui.databinding.CircleDemoBinding

    /**
     * This class contains information about a circle, including its markers
     */
    internal inner class DraggableCircle(center: LatLng, private var radiusMeters: Double) {
        private val centerMarker: Marker? = map.addMarker(MarkerOptions().apply {
            position(center)
            draggable(true)
        })

        private val radiusMarker: Marker? = map.addMarker(
                MarkerOptions().apply {
                    position(center.withSphericalOffset(radiusMeters, 90.0))
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    draggable(true)
                })

        internal val circle: Circle = map.addCircle(
                CircleOptions().apply {
                    center(center)
                    radius(radiusMeters)
                    strokeWidth(binding.strokeWidthSeekBar.progress.toFloat())
                    strokeColor(strokeColorArgb)
                    fillColor(fillColorArgb)
                    clickable(binding.toggleClickability.isChecked)
                    strokePattern(getSelectedPattern(binding.strokePatternSpinner.selectedItemPosition))
                })

        fun onMarkerMoved(marker: Marker): Boolean {
            when (marker) {
                centerMarker -> {
                    circle.center = marker.position
                    radiusMarker?.position = marker.position.withSphericalOffset(radiusMeters, 90.0)
                }
                radiusMarker -> {
                    radiusMeters =
                        centerMarker?.position?.sphericalDistance(radiusMarker.position)!!
                    circle.radius = radiusMeters
                }
                else -> return false
            }
            return true
        }

        fun onStyleChange() {
            // [circle] is treated as implicit this inside the with block
            with(circle) {
                strokeWidth = binding.strokeWidthSeekBar.progress.toFloat()
                strokeColor = strokeColorArgb
                fillColor = fillColorArgb
            }
        }

        fun setStrokePattern(pattern: List<PatternItem>?) {
            circle.strokePattern = pattern
        }

        fun setClickable(boolean: Boolean) {
            circle.isClickable = boolean
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.example.common_ui.databinding.CircleDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set all the SeekBars
        binding.fillHueSeekBar.apply {
            max = MAX_HUE_DEGREE
            progress = MAX_HUE_DEGREE / 2
        }
        binding.fillAlphaSeekBar.apply {
            max = MAX_ALPHA
            progress = MAX_ALPHA / 2
        }
        binding.strokeWidthSeekBar.apply {
            max = MAX_WIDTH_PX
            progress = MAX_WIDTH_PX / 3
        }
        binding.strokeHueSeekBar.apply {
            max = MAX_HUE_DEGREE
            progress = 0
        }
        binding.strokeAlphaSeekBar.apply {
            max = MAX_ALPHA
            progress = MAX_ALPHA
        }

        binding.strokePatternSpinner.apply {
            adapter = ArrayAdapter(this@CircleDemoActivity,
                    android.R.layout.simple_spinner_item,
                    getResourceStrings())
        }

        binding.toggleClickability.setOnClickListener {
            toggleClickability()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        applyInsets(binding.mapContainer)
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
            moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 4.0f))
            setContentDescription(getString(R.string.circle_demo_description))
            setOnMapLongClickListener { point ->
                // We know the center, let's place the outline at a point 1/4 along the view.
                val viewRatio = 1.0 / 4.0
                val view: View = supportFragmentManager.findFragmentById(R.id.map)?.view
                        ?: return@setOnMapLongClickListener
                val radiusLatLng = map.projection.fromScreenLocation(
                        Point((view.height * viewRatio).toInt(),
                            (view.width * viewRatio).toInt())
                )
                // Create the circle.
                val newCircle = DraggableCircle(point, point.sphericalDistance(radiusLatLng))
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

        fillColorArgb = Color.HSVToColor(binding.fillAlphaSeekBar.progress,
                floatArrayOf(binding.fillHueSeekBar.progress.toFloat(), 1f, 1f))
        strokeColorArgb = Color.HSVToColor(binding.strokeAlphaSeekBar.progress,
                floatArrayOf(binding.strokeHueSeekBar.progress.toFloat(), 1f, 1f))

        val circle = DraggableCircle(SYDNEY, DEFAULT_RADIUS_METERS)
        circles.add(circle)

        // Set listeners for all the SeekBar
        binding.fillHueSeekBar.setOnSeekBarChangeListener(this)
        binding.fillAlphaSeekBar.setOnSeekBarChangeListener(this)

        binding.strokeWidthSeekBar.setOnSeekBarChangeListener(this)
        binding.strokeHueSeekBar.setOnSeekBarChangeListener(this)
        binding.strokeAlphaSeekBar.setOnSeekBarChangeListener(this)

        binding.strokePatternSpinner.onItemSelectedListener = this

        mapReady = true
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
            binding.fillHueSeekBar -> Color.HSVToColor(Color.alpha(fillColorArgb),
                    floatArrayOf(progress.toFloat(), 1f, 1f))
            binding.fillAlphaSeekBar -> Color.argb(progress, Color.red(fillColorArgb),
                    Color.green(fillColorArgb), Color.blue(fillColorArgb))
            else -> fillColorArgb
        }

        // Set the strokeColorArgb if the SeekBars for it is changed, otherwise keep the old value
        strokeColorArgb = when (seekBar) {
            binding.strokeHueSeekBar -> Color.HSVToColor(Color.alpha(strokeColorArgb),
                    floatArrayOf(progress.toFloat(), 1f, 1f))
            binding.strokeAlphaSeekBar -> Color.argb(progress, Color.red(strokeColorArgb),
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
    private fun toggleClickability() {
        circles.map { it.setClickable(binding.toggleClickability.isChecked) }
    }

    companion object {
        internal val SYDNEY = LatLng(-33.87365, 151.20689)
        private const val DEFAULT_RADIUS_METERS = 1_000_000.0

        private const val MAX_WIDTH_PX = 50
        private const val MAX_HUE_DEGREE = 360

        private const val MAX_ALPHA = 255
        private const val PATTERN_DASH_LENGTH = 100
        private const val PATTERN_GAP_LENGTH = 200
    }
}