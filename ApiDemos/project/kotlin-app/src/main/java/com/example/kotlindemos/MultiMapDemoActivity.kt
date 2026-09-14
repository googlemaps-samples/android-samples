// Copyright 2026 Google LLC
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
import com.example.common_ui.R
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.Sample
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Demonstrates rendering and animating multiple independent GoogleMap instances concurrently.
 * Each quadrant showcases a UNESCO World Heritage Site with simultaneous smooth zoom animations.
 */
// [START maps_android_sample_multimap]
@Sample(
    id = "com.example.kotlindemos.MultiMapDemoActivity",
    title = "Multi-Map View",
    description = "Rendering multiple independent GoogleMap instances in a single activity layout.",
    category = "Map Initialization",
    complexity = Complexity.ADVANCED,
    tags = ["#multimap", "#multiple", "#layout", "#rendering"],
    apiCalls = [
        "SupportMapFragment.getMapAsync(OnMapReadyCallback)",
        "GoogleMap.animateCamera(CameraUpdate, int, CancelableCallback)",
        "GoogleMap.addMarker(MarkerOptions)"
    ],
    purpose = "Shows how to render and control multiple independent GoogleMap instances concurrently in one screen.",
    successCriteria = "All 4 map fragments render distinct geographic locations simultaneously with smooth scrolling.",
    failureIndicators = "GL context collision, thread locking, or tile stuttering when dragging multiple maps.",
    framework = Framework.KOTLIN_VIEWS
)
class MultiMapDemoActivity : SamplesBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multimap_demo)
        applyInsets(findViewById(R.id.map_container))

        setupMap(R.id.map1, GIZA, "Pyramids of Giza")
        setupMap(R.id.map2, MACHU_PICCHU, "Machu Picchu")
        setupMap(R.id.map3, TAJ_MAHAL, "Taj Mahal")
        setupMap(R.id.map4, COLOSSEUM, "Colosseum")
    }

    private fun setupMap(fragmentId: Int, location: LatLng, title: String) {
        val fragment = supportFragmentManager.findFragmentById(fragmentId) as? SupportMapFragment
        fragment?.getMapAsync { map ->
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(COMMON_START, INITIAL_ZOOM))
            map.addMarker(MarkerOptions().position(location).title(title))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, TARGET_ZOOM), ANIM_DURATION_MS, null)
        }
    }

    companion object {
        private val COMMON_START = LatLng(20.0, 0.0)
        private val GIZA = LatLng(29.9792, 31.1342)
        private val MACHU_PICCHU = LatLng(-13.1631, -72.5450)
        private val TAJ_MAHAL = LatLng(27.1751, 78.0421)
        private val COLOSSEUM = LatLng(41.8902, 12.4922)
        private const val INITIAL_ZOOM = 1.5f
        private const val TARGET_ZOOM = 15.5f
        private const val ANIM_DURATION_MS = 3000
    }
}
// [END maps_android_sample_multimap]