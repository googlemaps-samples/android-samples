/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.app_rx

import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.rx.cameraIdleEvents
import com.google.maps.android.rx.cameraMoveCanceledEvents
import com.google.maps.android.rx.cameraMoveEvents
import com.google.maps.android.rx.cameraMoveStartedEvents
import com.google.maps.android.rx.markerClickEvents
import io.reactivex.rxjava3.core.Observable

internal class MapsRx {
  private fun markerClicks(googleMap: GoogleMap) {
    // [START maps_android_maps_rx_marker_click_events]
    googleMap.markerClickEvents()
      .subscribe { marker ->
        Log.d("MapsRx", "Marker ${marker.title} was clicked")
      }
    // [END maps_android_maps_rx_marker_click_events]
  }

  private fun cameraEvents(googleMap: GoogleMap) {
    // [START maps_android_maps_rx_camera_merge_events]
    Observable.merge(
      googleMap.cameraIdleEvents(),
      googleMap.cameraMoveEvents(),
      googleMap.cameraMoveCanceledEvents(),
      googleMap.cameraMoveStartedEvents()
    ).subscribe {
      // Notified when any camera event occurs
    }
    // [END maps_android_maps_rx_camera_merge_events]
  }
}