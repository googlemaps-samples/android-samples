// Copyright 2025 Google LLC
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

import android.R
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap

/**
 * Demonstrates how to instantiate a SupportMapFragment programmatically and add a marker to it.
 */
class ProgrammaticDemoActivity : SamplesBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // It isn't possible to set a fragment's id programmatically so we set a tag instead and
        // search for it using that.
        val mapFragment =
            supportFragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG) as SupportMapFragment?
                ?: SupportMapFragment.newInstance().also {
                    // Then we add it using a FragmentTransaction.
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.add(R.id.content, it, MAP_FRAGMENT_TAG)
                    fragmentTransaction.commit()
                }

        lifecycleScope.launchWhenCreated {
            val map = mapFragment.awaitMap()
            map.addMarker {
                position(LatLng(0.0, 0.0))
                title("Marker")
            }
        }
      applyInsets(findViewById<View?>(com.example.common_ui.R.id.map_container))
    }

    companion object {
        private const val MAP_FRAGMENT_TAG = "map"
    }
}