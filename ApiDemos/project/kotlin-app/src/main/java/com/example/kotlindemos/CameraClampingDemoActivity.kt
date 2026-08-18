// Copyright 2026 Google LLC
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
import android.widget.Toast

import androidx.lifecycle.lifecycleScope
import com.example.common_ui.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.cameraIdleEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * This shows how to constrain the camera to specific boundaries and zoom levels.
 */
class CameraClampingDemoActivity : SamplesBaseActivity() {

  internal lateinit var map: GoogleMap
  private lateinit var binding: com.example.common_ui.databinding.CameraClampingDemoBinding


  /**
   * Internal min zoom level that can be toggled via the demo.
   */
  private var minZoom = DEFAULT_MIN_ZOOM
  private var maxZoom = DEFAULT_MAX_ZOOM

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = com.example.common_ui.databinding.CameraClampingDemoBinding.inflate(layoutInflater)
    setContentView(binding.root)
    updateZoomLabel(minZoom, maxZoom)
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    lifecycleScope.launch {
      map = mapFragment.awaitMap()
      updateCameraPosition()
      map.setOnCameraMoveListener {
        updateCameraPosition()
      }
      map.setOnCameraIdleListener {
        updateCameraPosition()
      }
      setControls()
    }
  }

  private fun setControls() {
    binding.zoomRangeSlider.addOnChangeListener { slider, _, fromUser ->
      val values = slider.values
      minZoom = values[0]
      maxZoom = values[1]
      updateZoomLabel(minZoom, maxZoom)
      if (fromUser && ::map.isInitialized) {
        map.setMinZoomPreference(minZoom)
        map.setMaxZoomPreference(maxZoom)
      }
    }

    binding.clampZoomReset.setOnClickListener {
      resetMinMaxZoom()
      binding.zoomRangeSlider.setValues(DEFAULT_MIN_ZOOM, DEFAULT_MAX_ZOOM)
      updateZoomLabel(DEFAULT_MIN_ZOOM, DEFAULT_MAX_ZOOM)
      if (::map.isInitialized) {
        map.resetMinMaxZoomPreference()
      }
      toast("Min/Max zoom preferences reset.")
    }

    binding.clampLatlngAdelaide.setOnClickListener {
      binding.latlngClampToggleGroup.check(R.id.clamp_latlng_adelaide)
      map.setLatLngBoundsForCameraTarget(ADELAIDE_BOUNDS)
      map.animateCamera(CameraUpdateFactory.newCameraPosition(ADELAIDE_CAMERA))
      binding.clampStatusText.text = getString(R.string.latlng_clamp_status_adelaide)
    }

    binding.clampLatlngPacific.setOnClickListener {
      binding.latlngClampToggleGroup.check(R.id.clamp_latlng_pacific)
      map.setLatLngBoundsForCameraTarget(PACIFIC)
      map.animateCamera(CameraUpdateFactory.newCameraPosition(PACIFIC_CAMERA))
      binding.clampStatusText.text = getString(R.string.latlng_clamp_status_pacific)
    }

    binding.clampLatlngReset.setOnClickListener {
      binding.latlngClampToggleGroup.clearChecked()
      map.setLatLngBoundsForCameraTarget(null)
      binding.clampStatusText.text = getString(R.string.latlng_clamp_status_none)
      toast("LatLngBounds clamp reset.")
    }
  }

  private fun updateZoomLabel(min: Float, max: Float) {
    binding.zoomLabel.text = getString(R.string.zoom_bounds_label, min, max)
  }

  private fun updateCameraPosition() {
    if (!::map.isInitialized) return
    val pos = map.cameraPosition
    binding.cameraText.text = getString(
      R.string.camera_position_format,
      pos.target.latitude,
      pos.target.longitude,
      pos.zoom,
      pos.tilt,
      pos.bearing
    )
  }

  private fun toast(msg: String) {
    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
  }

  private fun resetMinMaxZoom() {
    minZoom = DEFAULT_MIN_ZOOM
    maxZoom = DEFAULT_MAX_ZOOM
  }

  companion object {
    private val TAG = CameraClampingDemoActivity::class.java.name
    private const val ZOOM_DELTA = 2.0f
    private const val DEFAULT_MIN_ZOOM = 2.0f
    private const val DEFAULT_MAX_ZOOM = 21.0f
    val ADELAIDE_BOUNDS = LatLngBounds(
      LatLng(-35.0, 138.58), LatLng(-34.9, 138.61))
    private val ADELAIDE_CAMERA = CameraPosition.Builder()
      .target(LatLng(-34.92873, 138.59995)).zoom(20.0f).bearing(0f).tilt(0f).build()
    private val PACIFIC = LatLngBounds(
      LatLng(-15.0, 165.0), LatLng(15.0, -165.0))
    private val PACIFIC_CAMERA = CameraPosition.Builder()
      .target(LatLng(0.0, (-180).toDouble())).zoom(4.0f).bearing(0f).tilt(0f).build()
  }
}