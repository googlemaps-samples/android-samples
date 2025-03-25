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
import android.view.ViewGroup
import com.example.common_ui.R

import com.google.android.gms.maps.StreetViewPanoramaOptions
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.model.LatLng

/**
 * This shows how to create a simple activity with streetview
 */
class StreetViewPanoramaViewDemoActivity : SamplesBaseActivity() {
    private lateinit var streetViewPanoramaView: StreetViewPanoramaView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val options = StreetViewPanoramaOptions()
        savedInstanceState ?: options.position(SYDNEY)
        streetViewPanoramaView = StreetViewPanoramaView(this, options)
        addContentView(
            streetViewPanoramaView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // *** IMPORTANT ***
        // StreetViewPanoramaView requires that the Bundle you pass contain _ONLY_
        // StreetViewPanoramaView SDK objects or sub-Bundles.
        streetViewPanoramaView.onCreate(savedInstanceState?.getBundle(STREETVIEW_BUNDLE_KEY))
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    override fun onResume() {
        streetViewPanoramaView.onResume()
        super.onResume()
    }

    override fun onPause() {
        streetViewPanoramaView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        streetViewPanoramaView.onDestroy()
        super.onDestroy()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var streetViewBundle = outState.getBundle(STREETVIEW_BUNDLE_KEY)
        if (streetViewBundle == null) {
            streetViewBundle = Bundle()
            outState.putBundle(
                STREETVIEW_BUNDLE_KEY,
                streetViewBundle
            )
        }
        streetViewPanoramaView.onSaveInstanceState(streetViewBundle)
    }

    companion object {
        // George St, Sydney
        private val SYDNEY = LatLng(-33.87365, 151.20689)
        private const val STREETVIEW_BUNDLE_KEY = "StreetViewBundleKey"
    }
}