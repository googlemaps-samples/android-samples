package com.google.maps.example.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.Marker
import com.google.android.libraries.maps.model.MarkerOptions

class POIBehaviorActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START maps_android_marker_collision]
        val marker = map.addMarker(
            MarkerOptions()
                .position(LatLng(10.0, 10.0))
                .zIndex(10f) // Optional.
                .collisionBehavior(Marker.CollisionBehavior.OPTIONAL_AND_HIDES_LOWER_PRIORITY)
        )
        // [END maps_android_marker_collision]
    }
}