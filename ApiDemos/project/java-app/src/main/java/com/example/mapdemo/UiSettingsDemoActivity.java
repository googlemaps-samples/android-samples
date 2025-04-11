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

import android.Manifest.permission;
import android.annotation.SuppressLint;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * This shows how UI settings can be toggled.
 */
public class UiSettingsDemoActivity extends SamplesBaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private UiSettings mUiSettings;

    private CheckBox mMyLocationButtonCheckbox;

    private CheckBox mMyLocationLayerCheckbox;

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;

    /**
     * Flag indicating whether a requested permission has been denied after returning in {@link
     * #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mLocationPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.ui_settings_demo);

        mMyLocationButtonCheckbox = findViewById(com.example.common_ui.R.id.mylocationbutton_toggle);
        mMyLocationLayerCheckbox = findViewById(com.example.common_ui.R.id.mylocationlayer_toggle);

        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);

        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    /**
     * Returns whether the checkbox with the given id is checked.
     */
    private boolean isChecked(int id) {
        return ((CheckBox) findViewById(id)).isChecked();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mUiSettings = mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(isChecked(com.example.common_ui.R.id.zoom_buttons_toggle));
        mUiSettings.setCompassEnabled(isChecked(com.example.common_ui.R.id.compass_toggle));
        mUiSettings.setMyLocationButtonEnabled(isChecked(com.example.common_ui.R.id.mylocationbutton_toggle));
        mUiSettings.setScrollGesturesEnabled(isChecked(com.example.common_ui.R.id.scroll_toggle));
        mUiSettings.setZoomGesturesEnabled(isChecked(com.example.common_ui.R.id.zoom_gestures_toggle));
        mUiSettings.setTiltGesturesEnabled(isChecked(com.example.common_ui.R.id.tilt_toggle));
        mUiSettings.setRotateGesturesEnabled(isChecked(com.example.common_ui.R.id.rotate_toggle));

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(isChecked(com.example.common_ui.R.id.mylocationlayer_toggle));
    }

    /**
     * Checks if the map is ready (which depends on whether the Google Play services APK is
     * available. This should be called prior to calling any methods on GoogleMap.
     */
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, com.example.common_ui.R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void setZoomButtonsEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the zoom controls (+/- buttons in the bottom-right of the map for LTR
        // locale or bottom-left for RTL locale).
        mUiSettings.setZoomControlsEnabled(((CheckBox) v).isChecked());
    }

    public void setCompassEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the compass (icon in the top-left for LTR locale or top-right for RTL
        // locale that indicates the orientation of the map).
        mUiSettings.setCompassEnabled(((CheckBox) v).isChecked());
    }

    public void setMyLocationButtonEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the my location button (this DOES NOT enable/disable the my location
        // dot/chevron on the map). The my location button will never appear if the my location
        // layer is not enabled.
        // First verify that the location permission has been granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mUiSettings.setMyLocationButtonEnabled(mMyLocationButtonCheckbox.isChecked());
        } else {
            // Uncheck the box and request missing location permission.
            mMyLocationButtonCheckbox.setChecked(false);
            PermissionUtils
                .requestLocationPermissions(this, MY_LOCATION_PERMISSION_REQUEST_CODE, false);
        }
    }

    @SuppressLint("MissingPermission")
    public void setMyLocationLayerEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the my location layer (i.e., the dot/chevron on the map). If enabled, it
        // will also cause the my location button to show (if it is enabled); if disabled, the my
        // location button will never show.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(mMyLocationLayerCheckbox.isChecked());
        } else {
            // Uncheck the box and request missing location permission.
            mMyLocationLayerCheckbox.setChecked(false);
            PermissionUtils
                .requestLocationPermissions(this, LOCATION_LAYER_PERMISSION_REQUEST_CODE, false);
        }
    }

    public void setScrollGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables scroll gestures (i.e. panning the map).
        mUiSettings.setScrollGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setZoomGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables zoom gestures (i.e., double tap, pinch & stretch).
        mUiSettings.setZoomGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setTiltGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables tilt gestures.
        mUiSettings.setTiltGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setRotateGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables rotate gestures.
        mUiSettings.setRotateGesturesEnabled(((CheckBox) v).isChecked());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_PERMISSION_REQUEST_CODE) {
            // Enable the My Location button if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) ||
                PermissionUtils.isPermissionGranted(permissions, grantResults,
                    permission.ACCESS_COARSE_LOCATION)
            ) {
                mUiSettings.setMyLocationButtonEnabled(true);
                mMyLocationButtonCheckbox.setChecked(true);
            } else {
                mLocationPermissionDenied = true;
            }
            return;
        } else if (requestCode == LOCATION_LAYER_PERMISSION_REQUEST_CODE) {
            // Enable the My Location layer if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) ||
                PermissionUtils.isPermissionGranted(permissions, grantResults,
                    permission.ACCESS_COARSE_LOCATION)
            ) {
                mMap.setMyLocationEnabled(true);
                mMyLocationLayerCheckbox.setChecked(true);
            } else {
                mLocationPermissionDenied = true;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mLocationPermissionDenied) {
            PermissionUtils.PermissionDeniedDialog
                .newInstance(false).show(getSupportFragmentManager(), "dialog");
            mLocationPermissionDenied = false;
        }
    }
}
