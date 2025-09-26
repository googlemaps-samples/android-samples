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
import android.os.Parcelable

import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import kotlinx.android.parcel.Parcelize
import java.util.Random

/**
 * This activity shows how to save the state of a MapFragment when the activity is recreated, like
 * after rotation of the device.
 */
class SaveStateDemoActivity : SamplesBaseActivity() {

  @Parcelize
  internal data class MarkerInfo(var hue: Float) : Parcelable

  /**
   * Example of a custom `MapFragment` showing how the position of a marker and other
   * custom
   * [Parcelable]s objects can be saved after rotation of the device.
   *
   *
   * Storing custom [Parcelable] objects directly in the [Bundle] provided by the
   * [.onActivityCreated] method will throw a `ClassNotFoundException`. This
   * is due to the fact that this Bundle is parceled (thus losing its ClassLoader attribute at
   * this moment) and unparceled later in a different ClassLoader.
   * <br></br>
   * A workaround to store these objects is to wrap the custom [Parcelable] objects in a
   * new
   * [Bundle] object.
   *
   *
   * However, note that it is safe to store [Parcelable] objects from the Maps API (eg.
   * MarkerOptions, LatLng, etc.) directly in the Bundle provided by the
   * [.onActivityCreated] method.
   */
  class SaveStateMapFragment : SupportMapFragment(), OnMarkerClickListener, OnMarkerDragListener {

    private lateinit var mMarkerPosition: LatLng
    private lateinit var mMarkerInfo: MarkerInfo
    private var mMoveCameraToMarker = false


    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      // Extract the state of the MapFragment:
      // - Objects from the API (eg. LatLng, MarkerOptions, etc.) were stored directly in
      //   the savedInsanceState Bundle.
      // - Custom Parcelable objects were wrapped in another Bundle.
      mMarkerPosition =
        savedInstanceState?.getParcelable(MARKER_POSITION) ?: DEFAULT_MARKER_POSITION
      mMarkerInfo =
        savedInstanceState?.getBundle(OTHER_OPTIONS)?.getParcelable(MARKER_INFO) ?: MarkerInfo(
          BitmapDescriptorFactory.HUE_RED)
      mMoveCameraToMarker = savedInstanceState == null

      lifecycleScope.launchWhenCreated {
        val map = awaitMap()
        map.addMarker {
          icon(BitmapDescriptorFactory.defaultMarker(mMarkerInfo.hue))
          position(mMarkerPosition)
          draggable(true)
        }

        map.setOnMarkerDragListener(this@SaveStateMapFragment)
        map.setOnMarkerClickListener(this@SaveStateMapFragment)

        if (mMoveCameraToMarker) {
          map.animateCamera(CameraUpdateFactory.newLatLng(mMarkerPosition))
        }
      }
    }

    override fun onSaveInstanceState(outState: Bundle) {
      super.onSaveInstanceState(outState)

      // All Parcelable objects of the API  (eg. LatLng, MarkerOptions, etc.) can be set
      // directly in the given Bundle.
      outState.putParcelable(MARKER_POSITION, mMarkerPosition)

      // All other custom Parcelable objects must be wrapped in another Bundle. Indeed,
      // failing to do so would throw a ClassNotFoundException. This is due to the fact that
      // this Bundle is being parceled (losing its ClassLoader at this time) and unparceled
      // later in a different ClassLoader.
      val bundle = Bundle()
      bundle.putParcelable(MARKER_INFO, mMarkerInfo)
      outState.putBundle(OTHER_OPTIONS, bundle)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
      val newHue = MARKER_HUES[Random()
        .nextInt(MARKER_HUES.size)]
      mMarkerInfo.hue = newHue
      marker.setIcon(BitmapDescriptorFactory.defaultMarker(newHue))
      return true
    }

    override fun onMarkerDragStart(marker: Marker) {}
    override fun onMarkerDrag(marker: Marker) {}
    override fun onMarkerDragEnd(marker: Marker) {
      mMarkerPosition = marker.position
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(com.example.common_ui.R.layout.save_state_demo)
  }

  companion object {
    /** Default marker position when the activity is first created.  */
    private val DEFAULT_MARKER_POSITION = LatLng(48.858179, 2.294576)

    /** List of hues to use for the marker  */
    private val MARKER_HUES = floatArrayOf(
      BitmapDescriptorFactory.HUE_RED,
      BitmapDescriptorFactory.HUE_ORANGE,
      BitmapDescriptorFactory.HUE_YELLOW,
      BitmapDescriptorFactory.HUE_GREEN,
      BitmapDescriptorFactory.HUE_CYAN,
      BitmapDescriptorFactory.HUE_AZURE,
      BitmapDescriptorFactory.HUE_BLUE,
      BitmapDescriptorFactory.HUE_VIOLET,
      BitmapDescriptorFactory.HUE_MAGENTA,
      BitmapDescriptorFactory.HUE_ROSE)

    // Bundle keys.
    private const val OTHER_OPTIONS = "options"
    private const val MARKER_POSITION = "markerPosition"
    private const val MARKER_INFO = "markerInfo"
  }
}