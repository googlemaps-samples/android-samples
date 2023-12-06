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