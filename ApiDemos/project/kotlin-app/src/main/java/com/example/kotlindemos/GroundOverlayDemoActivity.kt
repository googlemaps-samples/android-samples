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

package com.example.kotlindemos

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.example.common_ui.R
import com.example.kotlindemos.utils.MapProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnGroundOverlayClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng

/**
 * This demo shows how to add a ground overlay to a map.
 *
 * A ground overlay is an image that is fixed to a map. Unlike markers, ground overlays are
 * oriented against the Earth's surface rather than the screen. Rotating, tilting, or zooming the
 * map changes the orientation of the camera, but not the overlay.
 */
class GroundOverlayDemoActivity :  SamplesBaseActivity(),
    OnSeekBarChangeListener,
    OnMapReadyCallback,
    OnGroundOverlayClickListener,
    MapProvider
{
    private val images: MutableList<BitmapDescriptor> = ArrayList()
    internal lateinit var groundOverlay: GroundOverlay

    // These are internal for testing purposes only.
    internal lateinit var groundOverlayRotated: GroundOverlay

    internal var groundOverlayRotatedClickCount = 0

    override lateinit var map: GoogleMap
    override var mapReady = false

    private lateinit var binding: com.example.common_ui.databinding.GroundOverlayDemoBinding
    private var currentEntry = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.example.common_ui.databinding.GroundOverlayDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.transparencySeekBar.max = TRANSPARENCY_MAX
        binding.transparencySeekBar.progress = 0

        // Set up programmatic click listeners for the buttons.
        // This is a better practice than using the android:onClick XML attribute, as it keeps
        // all the view logic in the Activity code.
        binding.switchImage.setOnClickListener {
            switchImage()
        }
        binding.toggleClickability.setOnClickListener {
            toggleClickability()
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        applyInsets(binding.mapContainer)
    }

    /**
     * This is called when the map is ready to be used.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap

        // Register a listener to respond to clicks on GroundOverlays.
        map.setOnGroundOverlayClickListener(this)

        // Move the camera to the Newark area.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NEWARK, 11f))

        map.moveCamera(CameraUpdateFactory.scrollBy(100f, 100f))

        // Prepare the BitmapDescriptor objects. Using a BitmapDescriptorFactory is the most
        // memory-efficient way to create the images that will be used for the overlays.
        images.clear()
        images.add(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
        images.add(BitmapDescriptorFactory.fromResource(R.drawable.newark_prudential_sunny))

        // Add a small, rotated overlay that is clickable by default.
        // The initial state of the "Clickable" checkbox determines its clickability.
        groundOverlayRotated = map.addGroundOverlay(
            GroundOverlayOptions()
                // The image to use for the overlay.
                .image(images[1])
                // The anchor point for the overlay. By default, the anchor is the center of the
                // image. Here, we set it to the top-left corner.
                .anchor(0f, 1f)
                // The location of the anchor point on the map, the width of the overlay in meters,
                // and the height of the overlay in meters.
                .position(NEAR_NEWARK, 4300f, 3025f)
                // The bearing of the overlay in degrees, clockwise from north.
                .bearing(30f)
                // The initial clickability of the overlay.
                .clickable(binding.toggleClickability.isChecked)
        ) ?: error("Expected a non null addGroundOverlay")

        // Add a large overlay at Newark on top of the smaller overlay.
        groundOverlay = map.addGroundOverlay(
            GroundOverlayOptions()
                .image(images[currentEntry]).anchor(0f, 1f)
                .position(NEWARK, 8600f, 6500f)
        ) ?: error("Expected a non null addGroundOverlay")

        groundOverlay.tag = images[currentEntry]

        binding.transparencySeekBar.setOnSeekBarChangeListener(this)

        // Override the default content description on the view for accessibility mode.
        // Ideally this string would be localized.
        map.setContentDescription("Google Map with ground overlay.")

        this.mapReady = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    /**
     * This is called when the transparency SeekBar is changed.
     */
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        // Update the transparency of the main ground overlay. The transparency ranges from
        // 0 (completely opaque) to 1 (completely transparent).
        groundOverlay.transparency = progress.toFloat() / TRANSPARENCY_MAX.toFloat()
    }

    /**
     * Cycles through the available images for the main ground overlay.
     */
    private fun switchImage() {
        currentEntry = (currentEntry + 1) % images.size
        groundOverlay.setImage(images[currentEntry])
        groundOverlay.tag = images[currentEntry]
    }

    /**
     * This is called when a ground overlay is clicked.
     */
    override fun onGroundOverlayClick(groundOverlay: GroundOverlay) {
        // In this demo, we only toggle the transparency of the smaller, rotated overlay.
        // The transparency is toggled between 0.0f (opaque) and 0.5f (semi-transparent).
        groundOverlayRotatedClickCount += 1
        with(groundOverlayRotated) {
            transparency = if (transparency < 0.25f) 0.5f else 0.0f
        }
    }

    /**
     * Toggles the clickability of the smaller, rotated ground overlay.
     */
    private fun toggleClickability() {
        // The clickability of an overlay can be changed at any time.
        groundOverlayRotated.isClickable = binding.toggleClickability.isChecked
    }

    companion object {
        private val TAG = GroundOverlayDemoActivity::class.java.simpleName
        private const val TRANSPARENCY_MAX = 100
        internal val NEWARK = LatLng(40.714086, -74.228697)
        internal val NEAR_NEWARK = LatLng(
            NEWARK.latitude - 0.001,
            NEWARK.longitude - 0.025
        )
    }
}