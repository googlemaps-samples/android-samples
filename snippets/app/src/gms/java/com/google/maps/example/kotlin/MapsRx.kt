package com.google.maps.example.kotlin

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