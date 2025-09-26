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
import android.view.View
import android.widget.Button
import android.widget.TextView
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

  private lateinit var map: GoogleMap
  private lateinit var cameraTextView: TextView
  private val buttonIdToLatLngBoundsCameraMap = mapOf(
    Pair(R.id.clamp_latlng_adelaide, Pair(ADELAIDE, ADELAIDE_CAMERA)),
    Pair(R.id.clamp_latlng_pacific, Pair(PACIFIC, PACIFIC_CAMERA)),
  )

  /**
   * Internal min zoom level that can be toggled via the demo.
   */
  private var minZoom = DEFAULT_MIN_ZOOM

  /**
   * Internal max zoom level that can be toggled via the demo.
   */
  private var maxZoom = DEFAULT_MAX_ZOOM

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.camera_clamping_demo)
    cameraTextView = findViewById(R.id.camera_text)
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    lifecycleScope.launchWhenCreated {
      map = mapFragment.awaitMap()
      launch {
        map.cameraIdleEvents().collect {
            onCameraIdle()
        }
      }
      setButtonClickListeners()
    }
    applyInsets(findViewById<View?>(R.id.map_container))
  }

  private fun setButtonClickListeners() {
    // Min/max zooms
    findViewById<Button>(R.id.clamp_min_zoom).setOnClickListener {
      minZoom += ZOOM_DELTA
      // Constrains the minimum zoom level.
      map.setMinZoomPreference(minZoom)
      toast("Min zoom preference set to: $minZoom")
    }
    findViewById<Button>(R.id.clamp_max_zoom).setOnClickListener {
      maxZoom -= ZOOM_DELTA
      // Constrains the maximum zoom level.
      map.setMaxZoomPreference(maxZoom)
      toast("Max zoom preference set to: $maxZoom")
    }
    findViewById<Button>(R.id.clamp_zoom_reset).setOnClickListener {
      resetMinMaxZoom()
      map.resetMinMaxZoomPreference()
      toast("Min/Max zoom preferences reset.")
    }

    // Clamp
    val clampListener: (View) -> Unit = { view ->
      buttonIdToLatLngBoundsCameraMap[view.id]?.let { (latLngBounds, camera) ->
        map.setLatLngBoundsForCameraTarget(latLngBounds)
        map.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
      }
    }
    findViewById<Button>(R.id.clamp_latlng_adelaide).setOnClickListener(clampListener)
    findViewById<Button>(R.id.clamp_latlng_pacific).setOnClickListener(clampListener)
    findViewById<Button>(R.id.clamp_latlng_reset).setOnClickListener {
      map.setLatLngBoundsForCameraTarget(null)
      toast("LatLngBounds clamp reset.")
    }
  }

  private fun onCameraIdle() {
    cameraTextView.text = map.cameraPosition.toString()
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
    private const val DEFAULT_MAX_ZOOM = 22.0f
    private val ADELAIDE = LatLngBounds(
      LatLng(-35.0, 138.58), LatLng(-34.9, 138.61))
    private val ADELAIDE_CAMERA = CameraPosition.Builder()
      .target(LatLng(-34.92873, 138.59995)).zoom(20.0f).bearing(0f).tilt(0f).build()
    private val PACIFIC = LatLngBounds(
      LatLng(-15.0, 165.0), LatLng(15.0, -165.0))
    private val PACIFIC_CAMERA = CameraPosition.Builder()
      .target(LatLng(0.0, (-180).toDouble())).zoom(4.0f).bearing(0f).tilt(0f).build()
  }
}