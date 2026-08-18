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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Demonstrates bidirectional synchronization between a {@link SupportStreetViewPanoramaFragment}
 * (top pane) and a {@link SupportMapFragment} (bottom pane).
 *
 * Key concepts illustrated:
 * 1. **Map-to-Street View Sync**: Long-pressing and dragging the yellow "Pegman" marker on the map
 *    updates the Street View panorama to match the new drop coordinates.
 * 2. **Street View-to-Map Sync**: Navigating within Street View (tapping forward arrows/chevrons)
 *    updates Pegman's position on the map and smoothly pans the map camera to follow.
 * 3. **High-Accuracy Location**: Uses {@link FusedLocationProviderClient} with {@link Priority#PRIORITY_HIGH_ACCURACY}
 *    to teleport Pegman and Street View to the user's real-time physical location on demand.
 */
public class SplitStreetViewPanoramaAndMapDemoActivity extends SamplesBaseActivity
        implements OnMarkerDragListener, OnStreetViewPanoramaChangeListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String MARKER_POSITION_KEY = "MarkerPosition";

    // Default start location: George St, Sydney, Australia
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private StreetViewPanorama streetViewPanorama;
    private GoogleMap map;
    private Marker marker;
    private FusedLocationProviderClient fusedLocationClient;
    private CancellationTokenSource cancellationTokenSource;
    private boolean permissionRequested = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.split_street_view_panorama_and_map_demo);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final LatLng markerPosition;
        if (savedInstanceState == null) {
            markerPosition = SYDNEY;
        } else {
            markerPosition = savedInstanceState.getParcelable(MARKER_POSITION_KEY);
        }

        // Initialize the Street View fragment (top pane)
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        streetViewPanorama = panorama;
                        streetViewPanorama.setOnStreetViewPanoramaChangeListener(
                                SplitStreetViewPanoramaAndMapDemoActivity.this);
                        // Street View maintains its own state across orientation changes; only set position initially.
                        if (savedInstanceState == null) {
                            streetViewPanorama.setPosition(SYDNEY);
                        }
                    }
                });

        // Initialize the Google Map fragment (bottom pane)
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                // Center map camera on Pegman's location with street-level zoom
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 16f));
                googleMap.setOnMarkerDragListener(SplitStreetViewPanoramaAndMapDemoActivity.this);

                // Hide the default top-right map button in favor of the unified Material FAB
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                if (hasLocationPermission()) {
                    googleMap.setMyLocationEnabled(true);
                }

                // Create a draggable Pegman marker on the map
                marker = googleMap.addMarker(new MarkerOptions()
                        .position(markerPosition)
                        .icon(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.pegman))
                        .draggable(true));
            }
        });

        // Material FAB to locate the user and jump Pegman to their neighborhood
        FloatingActionButton btnMyLocation = findViewById(com.example.common_ui.R.id.btn_my_location);
        if (btnMyLocation != null) {
            btnMyLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToMyLocation();
                }
            });
        }
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    /**
     * Checks if fine or coarse location permission is granted.
     */
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests high-accuracy live location via {@link FusedLocationProviderClient} and teleports
     * Pegman, the map camera, and Street View to the user's location.
     */
    @SuppressLint("MissingPermission")
    private void moveToMyLocation() {
        if (!hasLocationPermission()) {
            if (permissionRequested && !ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
            ) && !ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
            )) {
                // Permanently denied ("Don't ask again") -> show informative toast guidance
                Toast.makeText(
                        this,
                        com.example.common_ui.R.string.location_permission_required_toast,
                        Toast.LENGTH_LONG
                ).show();
            } else {
                permissionRequested = true;
                PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, false);
            }
            return;
        }

        if (map != null) {
            map.setMyLocationEnabled(true);
        }
        Toast.makeText(this, getString(com.example.common_ui.R.string.getting_location), Toast.LENGTH_SHORT).show();

        // Cancel any pending active location request
        if (cancellationTokenSource != null) {
            cancellationTokenSource.cancel();
        }
        cancellationTokenSource = new CancellationTokenSource();

        // Actively query NLP / GNSS for the current high-accuracy position
        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.getToken()
        ).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    updatePositionToLocation(location);
                } else {
                    // Fallback to last known cached location if live fix is temporarily unavailable
                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location fallbackLocation) {
                            if (fallbackLocation != null) {
                                updatePositionToLocation(fallbackLocation);
                            } else {
                                Toast.makeText(SplitStreetViewPanoramaAndMapDemoActivity.this,
                                        getString(com.example.common_ui.R.string.waiting_for_location),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SplitStreetViewPanoramaAndMapDemoActivity.this,
                                    getString(com.example.common_ui.R.string.waiting_for_location),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SplitStreetViewPanoramaAndMapDemoActivity.this,
                        getString(com.example.common_ui.R.string.waiting_for_location),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates Pegman's position, centers the map camera, and looks up the closest
     * Street View panorama within a 200m radius of the user coordinates.
     */
    private void updatePositionToLocation(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (marker != null) {
            marker.setPosition(userLatLng);
        }
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f));
        }
        if (streetViewPanorama != null) {
            streetViewPanorama.setPosition(userLatLng, 200);
        }
        Toast.makeText(this, getString(com.example.common_ui.R.string.moved_pegman_to_location), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)
                || PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            moveToMyLocation();
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
            )) {
                Toast.makeText(
                        this,
                        com.example.common_ui.R.string.location_permission_required_toast,
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cancellationTokenSource != null) {
            cancellationTokenSource.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (marker != null) {
            outState.putParcelable(MARKER_POSITION_KEY, marker.getPosition());
        }
    }

    // --- Street View -> Map Synchronization ---

    /**
     * Called when the user navigates within the Street View panorama (e.g. stepping down a road).
     * Synchronizes Pegman's position on the map and smoothly pans the camera.
     */
    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
        if (location != null) {
            if (marker != null) {
                marker.setPosition(location.position);
            }
            if (map != null) {
                map.animateCamera(CameraUpdateFactory.newLatLng(location.position));
            }
        }
    }

    // --- Map -> Street View Synchronization ---

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    /**
     * Called when the user finishes dragging Pegman on the map.
     * Snaps the Street View panorama to the new drop location within a 150m search radius.
     */
    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (streetViewPanorama != null) {
            streetViewPanorama.setPosition(marker.getPosition(), 150);
        }
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }
}
