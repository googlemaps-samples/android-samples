// Copyright 2024 Google LLC
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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.AdvancedMarkerOptions.CollisionBehavior
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class AdvancedMarkersCollisionActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START maps_android_marker_collision]
        // Collision behavior can only be changed in the AdvancedMarkerOptions object.
        // Changes to collision behavior after a marker has been created are not possible
        val collisionBehavior: Int = CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL
        val advancedMarkerOptions: AdvancedMarkerOptions = AdvancedMarkerOptions()
            .position(LatLng(10.0, 10.0))
            .collisionBehavior(collisionBehavior)

        val marker: Marker = map.addMarker(advancedMarkerOptions) ?: error("Failed to add marker")
        // [END maps_android_marker_collision]
    }
}