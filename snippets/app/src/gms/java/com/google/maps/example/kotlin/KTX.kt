package com.google.maps.example.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.cameraMoveEvents
import com.google.maps.example.R
import kotlinx.coroutines.flow.collect

internal class KTX : AppCompatActivity() {
  private lateinit var googleMap: GoogleMap

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // [START maps_android_ktx_obtain_map]
    lifecycleScope.launchWhenCreated {
      val mapFragment: SupportMapFragment? =
        supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
      val googleMap: GoogleMap? = mapFragment?.awaitMap()
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
    lifecycleScope.launchWhenCreated {
      googleMap.cameraMoveEvents().collect {
        print("Received camera move event")
      }
    }
    // [END maps_android_ktx_camera_events]
  }
}