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
package com.example.kotlindemos

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.common_ui.R
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap

/**
 * This shows how to retain a map across activity restarts (e.g., from screen rotations), which can
 * be faster than relying on state serialization.
 */
class RetainMapDemoActivity : SamplesBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_demo)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.retainInstance = true
        }
        lifecycleScope.launchWhenCreated {
            val map = mapFragment.awaitMap()
            map.addMarker {
                position(LatLng(0.0, 0.0))
                title("Marker")
            }
        }
        applyInsets(findViewById<View?>(R.id.map_container))
    }
}