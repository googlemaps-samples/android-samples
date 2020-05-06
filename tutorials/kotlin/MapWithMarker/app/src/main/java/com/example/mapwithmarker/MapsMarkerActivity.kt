package com.example.mapwithmarker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.MapsExperimentalFeature
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
class MapsMarkerActivity : AppCompatActivity() {

    @MapsExperimentalFeature
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        // This is using Android's Lifecycle-aware architecture components to launch and run the
        // provided code block so that it executes as soon as the Activity is at least in the
        // CREATED state.
        lifecycle.coroutineScope.launchWhenCreated {
            val googleMap = mapFragment?.awaitMap()
            // Execution pauses here until we get the callback from the Maps SDK with a googleMap.
            // This is where we can add markers or lines, add listeners or move the camera.
            // In this case, we just move the camera to Sydney and add a marker in Sydney.
            googleMap?.apply {
                val sydney = LatLng(-33.852, 151.211)
                addMarker {
                    position(sydney)
                    title("Marker in Sydney")
                }
                moveCamera(CameraUpdateFactory.newLatLng(sydney))
            }
        }
    }
}