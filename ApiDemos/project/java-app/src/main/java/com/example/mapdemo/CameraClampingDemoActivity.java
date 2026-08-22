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


package com.example.mapdemo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.os.Bundle;
import android.widget.Toast;

/**
 * This shows how to constrain the camera to specific boundaries and zoom levels.
 */
public class CameraClampingDemoActivity extends SamplesBaseActivity
        implements OnMapReadyCallback, OnCameraIdleListener, GoogleMap.OnCameraMoveListener {

    private static final String TAG = CameraClampingDemoActivity.class.getSimpleName();

    private static final float ZOOM_DELTA = 2.0f;
    private static final float DEFAULT_MIN_ZOOM = 2.0f;
    private static final float DEFAULT_MAX_ZOOM = 21.0f;

    static final LatLngBounds ADELAIDE_BOUNDS = new LatLngBounds(
            new LatLng(-35.0, 138.58), new LatLng(-34.9, 138.61));
    private static final CameraPosition ADELAIDE_CAMERA = new CameraPosition.Builder()
            .target(new LatLng(-34.92873, 138.59995)).zoom(20.0f).bearing(0).tilt(0).build();

    private static final LatLngBounds PACIFIC = new LatLngBounds(
            new LatLng(-15.0, 165.0), new LatLng(15.0, -165.0));
    private static final CameraPosition PACIFIC_CAMERA = new CameraPosition.Builder()
            .target(new LatLng(0, -180)).zoom(4.0f).bearing(0).tilt(0).build();

    protected GoogleMap mMap;

    /**
     * Internal min zoom level that can be toggled via the demo.
     */
    private float mMinZoom;

    /**
     * Internal max zoom level that can be toggled via the demo.
     */
    private float mMaxZoom;

    private com.example.common_ui.databinding.CameraClampingDemoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.common_ui.databinding.CameraClampingDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mMap = null;
        resetMinMaxZoom();
        updateZoomLabel(mMinZoom, mMaxZoom);

        binding.zoomRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            java.util.List<Float> values = slider.getValues();
            mMinZoom = values.get(0);
            mMaxZoom = values.get(1);
            updateZoomLabel(mMinZoom, mMaxZoom);
            if (fromUser && mMap != null) {
                mMap.setMinZoomPreference(mMinZoom);
                mMap.setMaxZoomPreference(mMaxZoom);
            }
        });

        binding.clampLatlngAdelaide.setOnClickListener(v -> onClampToAdelaide());
        binding.clampLatlngPacific.setOnClickListener(v -> onClampToPacific());
        binding.clampLatlngReset.setOnClickListener(v -> onLatLngClampReset());
        binding.clampZoomReset.setOnClickListener(v -> onMinMaxZoomClampReset());

        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setOnCameraMoveListener(this);
        map.setOnCameraIdleListener(this);
        updateCameraPosition();
    }

    @Override
    public void onCameraMove() {
        updateCameraPosition();
    }

    @Override
    public void onCameraIdle() {
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        if (mMap == null) return;
        CameraPosition pos = mMap.getCameraPosition();
        binding.cameraText.setText(getString(
            com.example.common_ui.R.string.camera_position_format,
            pos.target.latitude,
            pos.target.longitude,
            pos.zoom,
            pos.tilt,
            pos.bearing
        ));
    }

    private void updateZoomLabel(float min, float max) {
        binding.zoomLabel.setText(getString(com.example.common_ui.R.string.zoom_bounds_label, min, max));
    }

    /**
     * Before the map is ready many calls will fail.
     * This should be called on all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, com.example.common_ui.R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void toast(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void resetMinMaxZoom() {
        mMinZoom = DEFAULT_MIN_ZOOM;
        mMaxZoom = DEFAULT_MAX_ZOOM;
    }

    private void onClampToAdelaide() {
        if (!checkReady()) {
            return;
        }
        binding.latlngClampToggleGroup.check(com.example.common_ui.R.id.clamp_latlng_adelaide);
        mMap.setLatLngBoundsForCameraTarget(ADELAIDE_BOUNDS);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(ADELAIDE_CAMERA));
        binding.clampStatusText.setText(getString(com.example.common_ui.R.string.latlng_clamp_status_adelaide));
    }

    private void onClampToPacific() {
        if (!checkReady()) {
            return;
        }
        binding.latlngClampToggleGroup.check(com.example.common_ui.R.id.clamp_latlng_pacific);
        mMap.setLatLngBoundsForCameraTarget(PACIFIC);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(PACIFIC_CAMERA));
        binding.clampStatusText.setText(getString(com.example.common_ui.R.string.latlng_clamp_status_pacific));
    }

    private void onLatLngClampReset() {
        if (!checkReady()) {
            return;
        }
        binding.latlngClampToggleGroup.clearChecked();
        // Setting bounds to null removes any previously set bounds.
        mMap.setLatLngBoundsForCameraTarget(null);
        binding.clampStatusText.setText(getString(com.example.common_ui.R.string.latlng_clamp_status_none));
        toast("LatLngBounds clamp reset.");
    }

    private void onMinMaxZoomClampReset() {
        resetMinMaxZoom();
        binding.zoomRangeSlider.setValues(DEFAULT_MIN_ZOOM, DEFAULT_MAX_ZOOM);
        updateZoomLabel(DEFAULT_MIN_ZOOM, DEFAULT_MAX_ZOOM);
        if (mMap != null) {
            mMap.resetMinMaxZoomPreference();
        }
        toast("Min/Max zoom preferences reset.");
    }
}