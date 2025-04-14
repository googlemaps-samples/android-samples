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

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.maps.CameraUpdate;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.GoogleMap.CancelableCallback;
import com.google.android.libraries.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.libraries.maps.GoogleMap.OnCameraMoveCanceledListener;
import com.google.android.libraries.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.libraries.maps.GoogleMap.OnCameraMoveStartedListener;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.PolylineOptions;

/**
 * This shows how to change the camera position for the map.
 */
// [START maps_camera_events]
public class CameraDemoActivity extends AppCompatActivity implements
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback {
    // [START_EXCLUDE silent]
    private static final String TAG = CameraDemoActivity.class.getName();

    /**
     * The amount by which to scroll the camera. Note that this amount is in raw pixels, not dp
     * (density-independent pixels).
     */
    private static final int SCROLL_BY_PX = 100;

    public static final CameraPosition BONDI =
            new CameraPosition.Builder().target(new LatLng(-33.891614, 151.276417))
                    .zoom(15.5f)
                    .bearing(300)
                    .tilt(50)
                    .build();

    public static final CameraPosition SYDNEY =
            new CameraPosition.Builder().target(new LatLng(-33.87365, 151.20689))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();
    // [END_EXCLUDE]

    private GoogleMap map;
    // [START_EXCLUDE silent]
    private CompoundButton animateToggle;
    private CompoundButton customDurationToggle;
    private SeekBar customDurationBar;
    private PolylineOptions currPolylineOptions;
    private boolean isCanceled = false;
    // [END_EXCLUDE]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.camera_demo);
        // [START_EXCLUDE silent]
        animateToggle = findViewById(com.example.common_ui.R.id.animate);
        customDurationToggle = findViewById(com.example.common_ui.R.id.duration_toggle);
        customDurationBar = findViewById(com.example.common_ui.R.id.duration_bar);

        updateEnabledState();
        // [END_EXCLUDE]

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
    }

    // [START_EXCLUDE silent]
    @Override
    protected void onResume() {
        super.onResume();
        updateEnabledState();
    }
    // [END_EXCLUDE]

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnCameraMoveCanceledListener(this);
        // [START_EXCLUDE silent]
        // We will provide our own zoom controls.
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        // [END_EXCLUDE]

        // Show Sydney
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.87365, 151.20689), 10));
    }

    // [START_EXCLUDE silent]
    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (map == null) {
            Toast.makeText(this, com.example.common_ui.R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Called when the Go To Bondi button is clicked.
     */
    public void onGoToBondi(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.newCameraPosition(BONDI));
    }

    /**
     * Called when the Animate To Sydney button is clicked.
     */
    public void onGoToSydney(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.newCameraPosition(SYDNEY), new CancelableCallback() {
            @Override
            public void onFinish() {
                Toast.makeText(getBaseContext(), "Animation to Sydney complete", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getBaseContext(), "Animation to Sydney canceled", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    /**
     * Called when the stop button is clicked.
     */
    public void onStopAnimation(View view) {
        if (!checkReady()) {
            return;
        }

        map.stopAnimation();
    }

    /**
     * Called when the zoom in button (the one with the +) is clicked.
     */
    public void onZoomIn(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.zoomIn());
    }

    /**
     * Called when the zoom out button (the one with the -) is clicked.
     */
    public void onZoomOut(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.zoomOut());
    }

    /**
     * Called when the tilt more button (the one with the /) is clicked.
     */
    public void onTiltMore(View view) {
        if (!checkReady()) {
            return;
        }

        CameraPosition currentCameraPosition = map.getCameraPosition();
        float currentTilt = currentCameraPosition.tilt;
        float newTilt = currentTilt + 10;

        newTilt = (newTilt > 90) ? 90 : newTilt;

        CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
                .tilt(newTilt).build();

        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Called when the tilt less button (the one with the \) is clicked.
     */
    public void onTiltLess(View view) {
        if (!checkReady()) {
            return;
        }

        CameraPosition currentCameraPosition = map.getCameraPosition();

        float currentTilt = currentCameraPosition.tilt;

        float newTilt = currentTilt - 10;
        newTilt = (newTilt > 0) ? newTilt : 0;

        CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
                .tilt(newTilt).build();

        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Called when the left arrow button is clicked. This causes the camera to move to the left
     */
    public void onScrollLeft(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0));
    }

    /**
     * Called when the right arrow button is clicked. This causes the camera to move to the right.
     */
    public void onScrollRight(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0));
    }

    /**
     * Called when the up arrow button is clicked. The causes the camera to move up.
     */
    public void onScrollUp(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX));
    }

    /**
     * Called when the down arrow button is clicked. This causes the camera to move down.
     */
    public void onScrollDown(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(0, SCROLL_BY_PX));
    }

    /**
     * Called when the animate button is toggled
     */
    public void onToggleAnimate(View view) {
        updateEnabledState();
    }

    /**
     * Called when the custom duration checkbox is toggled
     */
    public void onToggleCustomDuration(View view) {
        updateEnabledState();
    }

    /**
     * Update the enabled state of the custom duration controls.
     */
    private void updateEnabledState() {
        customDurationToggle.setEnabled(animateToggle.isChecked());
        customDurationBar
                .setEnabled(animateToggle.isChecked() && customDurationToggle.isChecked());
    }

    private void changeCamera(CameraUpdate update) {
        changeCamera(update, null);
    }

    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
    private void changeCamera(CameraUpdate update, CancelableCallback callback) {
        if (animateToggle.isChecked()) {
            if (customDurationToggle.isChecked()) {
                int duration = customDurationBar.getProgress();
                // The duration must be strictly positive so we make it at least 1.
                map.animateCamera(update, Math.max(duration, 1), callback);
            } else {
                map.animateCamera(update, callback);
            }
        } else {
            map.moveCamera(update);
        }
    }
    // [END_EXCLUDE]

    @Override
    public void onCameraMoveStarted(int reason) {
        // [START_EXCLUDE silent]
        if (!isCanceled) {
            map.clear();
        }
        // [END_EXCLUDE]

        String reasonText = "UNKNOWN_REASON";
        // [START_EXCLUDE silent]
        currPolylineOptions = new PolylineOptions().width(5);
        // [END_EXCLUDE]
        switch (reason) {
            case OnCameraMoveStartedListener.REASON_GESTURE:
                // [START_EXCLUDE silent]
                currPolylineOptions.color(Color.BLUE);
                // [END_EXCLUDE]
                reasonText = "GESTURE";
                break;
            case OnCameraMoveStartedListener.REASON_API_ANIMATION:
                // [START_EXCLUDE silent]
                currPolylineOptions.color(Color.RED);
                // [END_EXCLUDE]
                reasonText = "API_ANIMATION";
                break;
            case OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                // [START_EXCLUDE silent]
                currPolylineOptions.color(Color.GREEN);
                // [END_EXCLUDE]
                reasonText = "DEVELOPER_ANIMATION";
                break;
        }
        Log.d(TAG, "onCameraMoveStarted(" + reasonText + ")");
        // [START_EXCLUDE silent]
        addCameraTargetToPath();
        // [END_EXCLUDE]
    }

    @Override
    public void onCameraMove() {
        // [START_EXCLUDE silent]
        // When the camera is moving, add its target to the current path we'll draw on the map.
        if (currPolylineOptions != null) {
            addCameraTargetToPath();
        }
        // [END_EXCLUDE]
        Log.d(TAG, "onCameraMove");
    }

    @Override
    public void onCameraMoveCanceled() {
        // [START_EXCLUDE silent]
        // When the camera stops moving, add its target to the current path, and draw it on the map.
        if (currPolylineOptions != null) {
            addCameraTargetToPath();
            map.addPolyline(currPolylineOptions);
        }
        isCanceled = true;  // Set to clear the map when dragging starts again.
        currPolylineOptions = null;
        // [END_EXCLUDE]
        Log.d(TAG, "onCameraMoveCancelled");
    }

    @Override
    public void onCameraIdle() {
        // [START_EXCLUDE silent]
        if (currPolylineOptions != null) {
            addCameraTargetToPath();
            map.addPolyline(currPolylineOptions);
        }
        currPolylineOptions = null;
        isCanceled = false;  // Set to *not* clear the map when dragging starts again.
        // [END_EXCLUDE]
        Log.d(TAG, "onCameraIdle");
    }

    // [START_EXCLUDE silent]
    private void addCameraTargetToPath() {
        LatLng target = map.getCameraPosition().target;
        currPolylineOptions.add(target);
    }
    // [END_EXCLUDE]
}
// [END maps_camera_events]