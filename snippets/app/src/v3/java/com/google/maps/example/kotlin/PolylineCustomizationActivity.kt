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