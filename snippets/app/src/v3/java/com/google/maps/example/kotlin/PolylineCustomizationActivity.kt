package com.google.maps.example.kotlin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.*
import com.google.maps.example.R

class PolylineCustomizationActivity : AppCompatActivity() {
    private lateinit var map: GoogleMap

    private fun multicoloredPolyline() {
        // [START maps_android_polyline_multicolored]
        val line = map.addPolyline(
            PolylineOptions()
                .add(LatLng(47.6677146, -122.3470447), LatLng(47.6442757, -122.2814693))
                .addSpan(StyleSpan(Color.RED))
                .addSpan(StyleSpan(Color.GREEN))
        )
        // [END maps_android_polyline_multicolored]
    }

    private fun multicoloredGradientPolyline() {
        // [START maps_android_polyline_gradient]
        val line = map.addPolyline(
            PolylineOptions()
                .add(LatLng(47.6677146, -122.3470447), LatLng(47.6442757, -122.2814693))
                .addSpan(
                    StyleSpan(
                        StrokeStyle.gradientBuilder(
                            Color.RED,
                            Color.YELLOW
                        ).build()
                    )
                )
        )
        // [END maps_android_polyline_gradient]
    }

    private fun stampedPolyline() {
        // [START maps_android_polyline_stamped]
        val stampStyle =
            TextureStyle.newBuilder(BitmapDescriptorFactory.fromResource(R.drawable.walking_dot)).build()
        val span = StyleSpan(StrokeStyle.colorBuilder(Color.RED).stamp(stampStyle).build())
        map.addPolyline(
            PolylineOptions()
                .add(LatLng(47.6677146, -122.3470447), LatLng(47.6442757, -122.2814693))
                .addSpan(span)
        )
        // [END maps_android_polyline_stamped]
    }
}