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

package com.example.app_ktx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.cameraMoveEvents
import com.google.maps.example.ktx.R
import kotlinx.coroutines.launch

internal class KTX : AppCompatActivity() {
  private lateinit var googleMap: GoogleMap

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // [START maps_android_ktx_obtain_map]
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
        val mapFragment: SupportMapFragment? =
          supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        val googleMap: GoogleMap? = mapFragment?.awaitMap()
      }
    }
    // [END maps_android_ktx_obtain_map]


    // [START maps_android_ktx_add_marker]
    val sydney = LatLng(-33.852, 151.211)
    val marker = googleMap.addMarker {
      position(sydney)
      title("Marker in Sydney")
    }
    // [END maps_android_ktx_add_marker]

    // [START maps_android_ktx_camera_events]
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
        googleMap.cameraMoveEvents().collect {
          print("Received camera move event")
        }
      }
    }
    // [END maps_android_ktx_camera_events]
  }
}