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


package com.example.mapdemo;

import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This shows how to create a simple activity with streetview
 */
public class StreetViewPanoramaViewDemoActivity extends SamplesBaseActivity {

    // George St, Sydney
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private StreetViewPanoramaView streetViewPanoramaView;

    private static final String STREETVIEW_BUNDLE_KEY = "StreetViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
        if (savedInstanceState == null) {
            options.position(SYDNEY);
        }

        streetViewPanoramaView = new StreetViewPanoramaView(this, options);
        addContentView(streetViewPanoramaView,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // *** IMPORTANT ***
        // StreetViewPanoramaView requires that the Bundle you pass contain _ONLY_
        // StreetViewPanoramaView SDK objects or sub-Bundles.
        Bundle streetViewBundle = null;
        if (savedInstanceState != null) {
            streetViewBundle = savedInstanceState.getBundle(STREETVIEW_BUNDLE_KEY);
        }
        streetViewPanoramaView.onCreate(streetViewBundle);

        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    @Override
    protected void onResume() {
        streetViewPanoramaView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        streetViewPanoramaView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        streetViewPanoramaView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle streetViewBundle = outState.getBundle(STREETVIEW_BUNDLE_KEY);
        if (streetViewBundle == null) {
            streetViewBundle = new Bundle();
            outState.putBundle(STREETVIEW_BUNDLE_KEY, streetViewBundle);
        }

        streetViewPanoramaView.onSaveInstanceState(streetViewBundle);
    }
}
