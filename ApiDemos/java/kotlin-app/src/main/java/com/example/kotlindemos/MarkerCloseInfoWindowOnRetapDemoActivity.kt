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

import android.os.Bundle
import android.view.View
import com.example.common_ui.R

import com.example.kotlindemos.OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


/**
 * This shows how to close the info window when the currently selected marker is re-tapped.
 */
class MarkerCloseInfoWindowOnRetapDemoActivity :
  SamplesBaseActivity(),
  OnGlobalLayoutAndMapReadyListener {

  private lateinit var map: GoogleMap

  /** Keeps track of the selected marker. It will be set to null if no marker is selected. */
  private var selectedMarker: Marker? = null

  /**
   * If user tapped on the the marker which was already showing info window,
   * the showing info window will be closed. Otherwise will show a different window.
   */
  private val markerClickListener = object : GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(marker: Marker): Boolean {
      if (marker == selectedMarker) {
        selectedMarker = null
        // Return true to indicate we have consumed the event and that we do not
        // want the the default behavior to occur (which is for the camera to move
        // such that the marker is centered and for the marker's info window to open,
        // if it has one).
        return true
      }

      selectedMarker = marker
      // Return false to indicate that we have not consumed the event and that
      // we wish for the default behavior to occur.
      return false
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.marker_close_info_window_on_retap_demo)

    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    OnMapAndViewReadyListener(mapFragment, this)
    applyInsets(findViewById<View?>(R.id.map_container))
  }

  override fun onMapReady(googleMap: GoogleMap?) {
    // Return if googleMap was null
    map = googleMap ?: return

    with(map) {
      uiSettings.isZoomControlsEnabled = false

      setOnMarkerClickListener(markerClickListener)

      // Set listener for map click event. Any showing info window closes
      // when the map is clicked. Clear the currently selected marker.
      setOnMapClickListener { selectedMarker = null }

      setContentDescription(getString(R.string.marker_close_info_window_on_retap_demo_description))

      // Add markers to different cities in Australia and include it in bounds
      val bounds = LatLngBounds.Builder()
      cities.map { city ->
        addMarker(MarkerOptions().apply {
          position(city.latLng)
          title(city.title)
          snippet(city.snippet)
        })
        bounds.include(city.latLng)
      }

      moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50))
    }
  }

  /**
   * Class to contain information about a marker.
   *
   * @property latLng latitude and longitude of the marker
   * @property title a string containing the city name
   * @property snippet a string containing the population of the city
   */
  class MarkerInfo(val latLng: LatLng, val title: String, val snippet: String)

  private val cities = listOf(
    MarkerInfo(LatLng(-27.47093, 153.0235),
               "Brisbane", "Population: 2,074,200"),
    MarkerInfo(LatLng(-37.81319, 144.96298),
               "Melbourne", "Population: 4,137,400"),
    MarkerInfo(LatLng(-33.87365, 151.20689),
               "Sydney", "Population: 4,627,300"),
    MarkerInfo(LatLng(-34.92873, 138.59995),
               "Adelaide", "Population: 1,213,000"),
    MarkerInfo(LatLng(-31.952854, 115.857342),
               "Perth", "Population: 1,738,800")
  )
}
