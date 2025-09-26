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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * This shows how to style a map with JSON.
 */
public class StyledMapDemoActivity extends SamplesBaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap = null;

    private static final String TAG = StyledMapDemoActivity.class.getSimpleName();

    private static final String SELECTED_STYLE = "selected_style";

    // Stores the ID of the currently selected style, so that we can re-apply it when
    // the activity restores state, for example when the device changes orientation.
    private int mSelectedStyleId = com.example.common_ui.R.string.style_label_default;

    // These are simply the string resource IDs for each of the style names. We use them
    // as identifiers when choosing which style to apply.
    private final int[] mStyleIds = {
            com.example.common_ui.R.string.style_label_retro,
            com.example.common_ui.R.string.style_label_night,
            com.example.common_ui.R.string.style_label_grayscale,
            com.example.common_ui.R.string.style_label_no_pois_no_transit,
            com.example.common_ui.R.string.style_label_default,
    };

    private static final LatLng SYDNEY = new LatLng(-33.8688, 151.2093);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectedStyleId = savedInstanceState.getInt(SELECTED_STYLE);
        }
        setContentView(com.example.common_ui.R.layout.styled_map_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);

        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store the selected map style, so we can assign it when the activity resumes.
        outState.putInt(SELECTED_STYLE, mSelectedStyleId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        mMap = map;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 14));
        setSelectedStyle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.example.common_ui.R.menu.styled_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == com.example.common_ui.R.id.menu_style_choose) {
            showStylesDialog();
        }
        return true;
    }

    /**
     * Shows a dialog listing the styles to choose from, and applies the selected
     * style when chosen.
     */
    private void showStylesDialog() {
        // mStyleIds stores each style's resource ID, and we extract the names here, rather
        // than using an XML array resource which AlertDialog.Builder.setItems() can also
        // accept. We do this since using an array resource would mean we would not have
        // constant values we can switch/case on, when choosing which style to apply.
        List<String> styleNames = new ArrayList<>();
        for (int style : mStyleIds) {
            styleNames.add(getString(style));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(com.example.common_ui.R.string.style_choose));
        builder.setItems(styleNames.toArray(new CharSequence[styleNames.size()]),
                (dialog, which) -> {
                    mSelectedStyleId = mStyleIds[which];
                    String msg = getString(com.example.common_ui.R.string.style_set_to, getString(mSelectedStyleId));
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, msg);
                    setSelectedStyle();
                });
        builder.show();
    }

    /**
     * Creates a {@link MapStyleOptions} object via loadRawResourceStyle() (or via the
     * constructor with a JSON String), then sets it on the {@link GoogleMap} instance,
     * via the setMapStyle() method.
     */
    private void setSelectedStyle() {
        MapStyleOptions style;
        int id = mSelectedStyleId;
        if (id == com.example.common_ui.R.string.style_label_retro) {
            // Sets the retro style via raw resource JSON.
            style = MapStyleOptions.loadRawResourceStyle(this, com.example.common_ui.R.raw.mapstyle_retro);
        } else if (id == com.example.common_ui.R.string.style_label_night) {
            // Sets the night style via raw resource JSON.
            style = MapStyleOptions.loadRawResourceStyle(this, com.example.common_ui.R.raw.mapstyle_night);
        } else if (id == com.example.common_ui.R.string.style_label_grayscale) {
            // Sets the grayscale style via raw resource JSON.
            style = MapStyleOptions.loadRawResourceStyle(this, com.example.common_ui.R.raw.mapstyle_grayscale);
        } else if (id == com.example.common_ui.R.string.style_label_no_pois_no_transit) {
            // Sets the no POIs or transit style via JSON string.
            style = new MapStyleOptions("[" +
                                                "  {" +
                                                "    \"featureType\":\"poi.business\"," +
                                                "    \"elementType\":\"all\"," +
                                                "    \"stylers\":[" +
                                                "      {" +
                                                "        \"visibility\":\"off\"" +
                                                "      }" +
                                                "    ]" +
                                                "  }," +
                                                "  {" +
                                                "    \"featureType\":\"transit\"," +
                                                "    \"elementType\":\"all\"," +
                                                "    \"stylers\":[" +
                                                "      {" +
                                                "        \"visibility\":\"off\"" +
                                                "      }" +
                                                "    ]" +
                                                "  }" +
                                                "]");
        } else if (id == com.example.common_ui.R.string.style_label_default) {
            // Removes previously set style, by setting it to null.
            style = null;
        } else {
            return;
        }
        mMap.setMapStyle(style);
    }

}