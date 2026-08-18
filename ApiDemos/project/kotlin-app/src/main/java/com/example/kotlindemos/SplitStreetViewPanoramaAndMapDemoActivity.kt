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

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import com.example.common_ui.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.StreetViewPanoramaLocation
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Demonstrates bidirectional synchronization between a [SupportStreetViewPanoramaFragment]
 * (top pane) and a [SupportMapFragment] (bottom pane).
 *
 * Key concepts illustrated:
 * 1. **Map-to-Street View Sync**: Long-pressing and dragging the yellow "Pegman" marker on the map
 *    updates the Street View panorama to match the new drop coordinates.
 * 2. **Street View-to-Map Sync**: Navigating within Street View (tapping forward arrows/chevrons)
 *    updates Pegman's position on the map and smoothly pans the map camera to follow.
 * 3. **High-Accuracy Location**: Uses [FusedLocationProviderClient] with [Priority.PRIORITY_HIGH_ACCURACY]
 *    to teleport Pegman and Street View to the user's real-time physical location on demand.
 */
class SplitStreetViewPanoramaAndMapDemoActivity : SamplesBaseActivity(),
    OnMarkerDragListener, OnStreetViewPanoramaChangeListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private var streetViewPanorama: StreetViewPanorama? = null
    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cancellationTokenSource: CancellationTokenSource? = null
    private var permissionRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.split_street_view_panorama_and_map_demo)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val markerPosition =
            savedInstanceState?.let { BundleCompat.getParcelable(it, MARKER_POSITION_KEY, LatLng::class.java) }
                ?: SYDNEY

        // Initialize the Street View fragment (top pane)
        val streetViewPanoramaFragment =
            supportFragmentManager.findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment?
        streetViewPanoramaFragment?.getStreetViewPanoramaAsync { panorama ->
            streetViewPanorama = panorama
            streetViewPanorama?.setOnStreetViewPanoramaChangeListener(
                this@SplitStreetViewPanoramaAndMapDemoActivity
            )
            // Street View maintains its own state across orientation changes; only set position initially.
            savedInstanceState ?: streetViewPanorama?.setPosition(SYDNEY)
        }

        // Initialize the Google Map fragment (bottom pane)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            map = googleMap
            // Center map camera on Pegman's location with street-level zoom
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 16f))
            googleMap.setOnMarkerDragListener(this@SplitStreetViewPanoramaAndMapDemoActivity)

            // Hide the default top-right map button in favor of the unified Material FAB
            googleMap.uiSettings.isMyLocationButtonEnabled = false
            if (hasLocationPermission()) {
                @SuppressLint("MissingPermission")
                googleMap.isMyLocationEnabled = true
            }

            // Create a draggable Pegman marker on the map
            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(markerPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pegman))
                    .draggable(true)
            )
        }

        // Material FAB to locate the user and jump Pegman to their neighborhood
        findViewById<FloatingActionButton>(R.id.btn_my_location)?.setOnClickListener {
            moveToMyLocation()
        }
        applyInsets(findViewById(R.id.map_container))
    }

    /**
     * Checks if fine or coarse location permission is granted.
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests high-accuracy live location via [FusedLocationProviderClient] and teleports
     * Pegman, the map camera, and Street View to the user's location.
     */
    @SuppressLint("MissingPermission")
    private fun moveToMyLocation() {
        if (!hasLocationPermission()) {
            if (permissionRequested && !ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) && !ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                // Permanently denied ("Don't ask again") -> show informative toast guidance
                Toast.makeText(
                    this,
                    R.string.location_permission_required_toast,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                permissionRequested = true
                PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, false)
            }
            return
        }

        map?.isMyLocationEnabled = true
        Toast.makeText(this, getString(R.string.getting_location), Toast.LENGTH_SHORT).show()

        // Cancel any pending active location request
        cancellationTokenSource?.cancel()
        val cts = CancellationTokenSource()
        cancellationTokenSource = cts

        // Actively query NLP / GNSS for the current high-accuracy position
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cts.token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                updatePositionToLocation(location)
            } else {
                // Fallback to last known cached location if live fix is temporarily unavailable
                fusedLocationClient.lastLocation.addOnSuccessListener { fallbackLocation: Location? ->
                    if (fallbackLocation != null) {
                        updatePositionToLocation(fallbackLocation)
                    } else {
                        Toast.makeText(this, getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Updates Pegman's position, centers the map camera, and looks up the closest
     * Street View panorama within a 200m radius of the user coordinates.
     */
    private fun updatePositionToLocation(location: Location) {
        val userLatLng = LatLng(location.latitude, location.longitude)
        marker?.position = userLatLng
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))
        streetViewPanorama?.setPosition(userLatLng, 200)
        Toast.makeText(this, getString(R.string.moved_pegman_to_location), Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (PermissionUtils.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || PermissionUtils.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            moveToMyLocation()
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(
                    this,
                    R.string.location_permission_required_toast,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            MARKER_POSITION_KEY,
            marker?.position
        )
    }

    // --- Street View -> Map Synchronization ---

    /**
     * Called when the user navigates within the Street View panorama (e.g. stepping down a road).
     * Synchronizes Pegman's position on the map and smoothly pans the camera.
     */
    override fun onStreetViewPanoramaChange(location: StreetViewPanoramaLocation) {
        marker?.position = location.position
        map?.animateCamera(CameraUpdateFactory.newLatLng(location.position))
    }

    // --- Map -> Street View Synchronization ---

    override fun onMarkerDragStart(marker: Marker) {}

    /**
     * Called when the user finishes dragging Pegman on the map.
     * Snaps the Street View panorama to the new drop location within a 150m search radius.
     */
    override fun onMarkerDragEnd(marker: Marker) {
        streetViewPanorama?.setPosition(marker.position, 150)
        map?.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
    }

    override fun onMarkerDrag(marker: Marker) {}

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val MARKER_POSITION_KEY = "MarkerPosition"

        // Default start location: George St, Sydney, Australia
        private val SYDNEY = LatLng(-33.87365, 151.20689)
    }
}