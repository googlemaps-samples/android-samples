/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import com.example.common_ui.R // Ensure correct R import

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

const val REQUEST_CODE_LOCATION = 123

class UiSettingsDemoActivity :
    SamplesBaseActivity(),
    OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks { // Added EasyPermissions.PermissionCallbacks

    private lateinit var map: GoogleMap

    // Checkboxes - Find them once in onCreate for efficiency
    private lateinit var zoomButtonCheckbox: CheckBox
    private lateinit var compassCheckbox: CheckBox
    private lateinit var myLocationButtonCheckbox: CheckBox
    private lateinit var myLocationLayerCheckbox: CheckBox
    private lateinit var scrollCheckbox: CheckBox
    private lateinit var zoomGesturesCheckbox: CheckBox
    private lateinit var tiltCheckbox: CheckBox
    private lateinit var rotateCheckbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_settings_demo)

        // Find checkboxes
        zoomButtonCheckbox = findViewById(R.id.zoom_buttons_toggle)
        compassCheckbox = findViewById(R.id.compass_toggle)
        myLocationButtonCheckbox = findViewById(R.id.mylocationbutton_toggle)
        myLocationLayerCheckbox = findViewById(R.id.mylocationlayer_toggle)
        scrollCheckbox = findViewById(R.id.scroll_toggle)
        zoomGesturesCheckbox = findViewById(R.id.zoom_gestures_toggle)
        tiltCheckbox = findViewById(R.id.tilt_toggle)
        rotateCheckbox = findViewById(R.id.rotate_toggle)

        // Programmatic Map Fragment Creation (assuming ApiDemoApplication provides mapId)
        val mapId = (application as? ApiDemoApplication)?.mapId // Safely get mapId
        // Use default map options if mapId isn't available (or handle error)
        val mapOptions = GoogleMapOptions().apply {
            mapId?.let { mapId(it) } // Set mapId if available
        }

        val mapFragment = SupportMapFragment.newInstance(mapOptions)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.map_fragment_container, mapFragment)
        }.commit()
        mapFragment.getMapAsync(this)

        applyInsets(findViewById<View?>(R.id.map_container))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap // Assign the map instance

        // Set initial UI settings based on checkbox states BEFORE setting listeners
        updateUiSettingsFromCheckboxes()

        // Attempt to enable MyLocation layer immediately if checkbox is initially checked
        // AND permission is already granted. If permission needed, request happens here.
        if (myLocationLayerCheckbox.isChecked) {
            enableMyLocation()
        }

        // Set listeners AFTER initial setup
        setupCheckboxListeners()
    }

    /** Updates the GoogleMap UI settings based on the current state of checkboxes. */
    @SuppressLint("MissingPermission") // Permissions checked before enabling myLocationLayer
    private fun updateUiSettingsFromCheckboxes() {
        if (!::map.isInitialized) return // Ensure map is ready

        with(map.uiSettings) {
            isZoomControlsEnabled = zoomButtonCheckbox.isChecked
            isCompassEnabled = compassCheckbox.isChecked
            isMyLocationButtonEnabled = myLocationButtonCheckbox.isChecked
            // isIndoorLevelPickerEnabled = false // Or handle based on context if needed
            isScrollGesturesEnabled = scrollCheckbox.isChecked
            isZoomGesturesEnabled = zoomGesturesCheckbox.isChecked
            isTiltGesturesEnabled = tiltCheckbox.isChecked
            isRotateGesturesEnabled = rotateCheckbox.isChecked
        }
        // My Location Layer state is handled separately due to permissions
        // See enableMyLocation() and setMyLocationLayerEnabled()
    }


    /** Sets up onClickListeners for all the UI settings checkboxes. */
    private fun setupCheckboxListeners() {
        if (!::map.isInitialized) return // Ensure map is ready

        zoomButtonCheckbox.setOnClickListener { it: CheckBox -> map.uiSettings.isZoomControlsEnabled = it.isChecked }
        compassCheckbox.setOnClickListener { it: CheckBox ->  map.uiSettings.isCompassEnabled = it.isChecked }
        myLocationButtonCheckbox.setOnClickListener { it: CheckBox ->  map.uiSettings.isMyLocationButtonEnabled = it.isChecked }
        scrollCheckbox.setOnClickListener { it: CheckBox ->  map.uiSettings.isScrollGesturesEnabled = it.isChecked }
        zoomGesturesCheckbox.setOnClickListener { it: CheckBox ->  map.uiSettings.isZoomGesturesEnabled = it.isChecked }
        tiltCheckbox.setOnClickListener { it: CheckBox ->  map.uiSettings.isTiltGesturesEnabled = it.isChecked }
        rotateCheckbox.setOnClickListener { it: CheckBox ->  map.uiSettings.isRotateGesturesEnabled = it.isChecked }

        // Special handling for My Location Layer due to permissions
        myLocationLayerCheckbox.setOnClickListener { view ->
            setMyLocationLayerEnabled((view as CheckBox).isChecked)
        }
    }

    /** Toggles the My Location layer based on checkbox state and permissions. */
    @SuppressLint("MissingPermission") // Permissions are checked/requested by enableMyLocation()
    private fun setMyLocationLayerEnabled(enabled: Boolean) {
        if (!::map.isInitialized) return

        if (enabled) {
            // Attempt to enable the layer, requesting permission if necessary.
            enableMyLocation()
        } else {
            // Disable the layer. No permission needed for this.
            map.isMyLocationEnabled = false
        }
    }

    /**
     * enableMyLocation() checks for permission and enables the My Location layer
     * *only* if permission is granted and the checkbox is checked.
     * If permission is not granted, it requests it.
     */
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun enableMyLocation() {
        if (hasLocationPermission()) {
            // Permission is granted, enable layer only if the checkbox is checked.
            if (::map.isInitialized) { // Check map again just in case
                map.isMyLocationEnabled = myLocationLayerCheckbox.isChecked
            }
        } else {
            // Permission is not granted, request it.
            EasyPermissions.requestPermissions(
                this,
                "Location permission is required for this demo",
                REQUEST_CODE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    /** Returns whether the checkbox with the given id is checked (used only for initial setup now) */
    // private fun isChecked(id: Int) = findViewById<CheckBox>(id)?.isChecked ?: false
    // Can be removed or kept if needed elsewhere. Using direct member variables now.


    // --- EasyPermissions Methods ---

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Forward result to EasyPermissions
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            // Permission was granted, try enabling the layer again
            // The @AfterPermissionGranted method `enableMyLocation` will be called automatically by EasyPermissions.
            // Log.d("UiSettingsDemo", "Location permission granted.") // Optional logging
        }
    }

    @SuppressLint("MissingPermission")
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            // Permission was denied. Ensure the checkbox reflects the state (layer is off).
            myLocationLayerCheckbox.isChecked = false
            if (::map.isInitialized) {
                map.isMyLocationEnabled = false // Ensure layer is off
            }
            // Optional: Show a message to the user explaining why the feature is disabled.
            Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show()

            // If rationale should be shown (permission permanently denied), EasyPermissions can help
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                // Consider showing dialog guiding user to app settings
                // new AppSettingsDialog.Builder(this).build().show() // Example using EasyPermissions helper
            }
        }
    }

    // Helper extension function for concise listener setting (optional but nice)
    private inline fun CheckBox.setOnClickListener(crossinline listener: (view: CheckBox) -> Unit) {
        this.setOnClickListener { listener(it as CheckBox) }
    }
}