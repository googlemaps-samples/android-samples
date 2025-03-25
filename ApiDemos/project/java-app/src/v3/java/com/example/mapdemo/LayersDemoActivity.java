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

import android.annotation.SuppressLint;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import static com.google.android.libraries.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.libraries.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.libraries.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.libraries.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.libraries.maps.GoogleMap.MAP_TYPE_TERRAIN;

/**
 * Demonstrates the different base layers of a map.
 */
public class LayersDemoActivity extends AppCompatActivity
        implements OnItemSelectedListener, OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;

    private CheckBox mTrafficCheckbox;

    private CheckBox mMyLocationCheckbox;

    private CheckBox mBuildingsCheckbox;

    private CheckBox mIndoorCheckbox;

    private Spinner mSpinner;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mShowPermissionDeniedDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.layers_demo);

        mSpinner = (Spinner) findViewById(com.example.common_ui.R.id.layers_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.layers_array, android.com.example.common_ui.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.com.example.common_ui.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        mTrafficCheckbox = (CheckBox) findViewById(com.example.common_ui.R.id.traffic);
        mMyLocationCheckbox = (CheckBox) findViewById(com.example.common_ui.R.id.my_location);
        mBuildingsCheckbox = (CheckBox) findViewById(com.example.common_ui.R.id.buildings);
        mIndoorCheckbox = (CheckBox) findViewById(com.example.common_ui.R.id.indoor);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        updateMapType();
        updateTraffic();
        updateMyLocation();
        updateBuildings();
        updateIndoor();
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, com.example.common_ui.R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Called when the Traffic checkbox is clicked.
     */
    public void onTrafficToggled(View view) {
        updateTraffic();
    }

    private void updateTraffic() {
        if (!checkReady()) {
            return;
        }
        mMap.setTrafficEnabled(mTrafficCheckbox.isChecked());
    }

    /**
     * Called when the MyLocation checkbox is clicked.
     */
    public void onMyLocationToggled(View view) {
        updateMyLocation();
    }

    @SuppressLint("MissingPermission")
    private void updateMyLocation() {
        if (!checkReady()) {
            return;
        }

        if (!mMyLocationCheckbox.isChecked()) {
            mMap.setMyLocationEnabled(false);
            return;
        }

        // Enable the location layer. Request the location permission if needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Uncheck the box until the layer has been enabled and request missing permission.
            mMyLocationCheckbox.setChecked(false);
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, results,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            mMap.setMyLocationEnabled(true);
            mMyLocationCheckbox.setChecked(true);
        } else {
            mShowPermissionDeniedDialog = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mShowPermissionDeniedDialog) {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(false).show(getSupportFragmentManager(), "dialog");
            mShowPermissionDeniedDialog = false;
        }
    }

    /**
     * Called when the Buildings checkbox is clicked.
     */
    public void onBuildingsToggled(View view) {
        updateBuildings();
    }

    private void updateBuildings() {
        if (!checkReady()) {
            return;
        }
        mMap.setBuildingsEnabled(mBuildingsCheckbox.isChecked());
    }

    /**
     * Called when the Indoor checkbox is clicked.
     */
    public void onIndoorToggled(View view) {
        updateIndoor();
    }

    private void updateIndoor() {
        if (!checkReady()) {
            return;
        }
        mMap.setIndoorEnabled(mIndoorCheckbox.isChecked());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateMapType();
    }

    private void updateMapType() {
        // No toast because this can also be called by the Android framework in onResume() at which
        // point mMap may not be ready yet.
        if (mMap == null) {
            return;
        }

        String layerName = ((String) mSpinner.getSelectedItem());
        if (layerName.equals(getString(com.example.common_ui.R.string.normal))) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(com.example.common_ui.R.string.hybrid))) {
            mMap.setMapType(MAP_TYPE_HYBRID);


        } else if (layerName.equals(getString(com.example.common_ui.R.string.satellite))) {
            mMap.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(com.example.common_ui.R.string.terrain))) {
            mMap.setMapType(MAP_TYPE_TERRAIN);
        } else if (layerName.equals(getString(com.example.common_ui.R.string.none_map))) {
            mMap.setMapType(MAP_TYPE_NONE);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }
}
