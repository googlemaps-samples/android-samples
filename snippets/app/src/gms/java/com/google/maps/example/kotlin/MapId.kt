package com.google.maps.example.kotlin

import android.content.Context
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment

internal class MapId {
  private fun fragment() {
    // [START maps_android_support_map_fragment_map_id]
    val options = GoogleMapOptions()
      .mapId("YOUR_MAP_ID")
    val mapFragment = SupportMapFragment.newInstance(options)
    // [END maps_android_support_map_fragment_map_id]
  }

  private fun mapView(context: Context) {
    // [START maps_android_mapview_map_id]
    val options = GoogleMapOptions()
        .mapId("YOUR_MAP_ID")
    val mapView = MapView(context, options)
    // [END maps_android_mapview_map_id]
  }
}