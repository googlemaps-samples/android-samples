/*
 * Copyright 2018 Google LLC
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
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import com.example.common_ui.R

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

/**
 * Demonstrates the different base layers of a map.
 */
class LayersDemoActivity :
        SamplesBaseActivity(),
        OnMapReadyCallback,
        AdapterView.OnItemSelectedListener,
        EasyPermissions.PermissionCallbacks {

    private val TAG = MarkerDemoActivity::class.java.name

    private lateinit var map: GoogleMap

    private lateinit var binding: com.example.common_ui.databinding.LayersDemoBinding

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * [.onRequestPermissionsResult].
     */
    private var showPermissionDeniedDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.example.common_ui.databinding.LayersDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layersSpinner.apply {
            adapter = ArrayAdapter.createFromResource(this@LayersDemoActivity,
                    R.array.layers_array, android.R.layout.simple_spinner_item).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }

            // set a listener for when the spinner to select map type is changed.
            onItemSelectedListener = this@LayersDemoActivity
        }

        binding.traffic.setOnClickListener { onTrafficToggled() }
        binding.myLocation.setOnClickListener { onMyLocationToggled() }
        binding.buildings.setOnClickListener { onBuildingsToggled() }
        binding.indoor.setOnClickListener { onIndoorToggled() }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        applyInsets(binding.mapContainer)
    }

    /**
     * Display a dialog box asking the user to grant permissions if they were denied
     */
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (showPermissionDeniedDialog) {
            AlertDialog.Builder(this).apply {
                setPositiveButton(R.string.ok, null)
                setMessage(R.string.location_permission_denied)
                create()
            }.show()
            showPermissionDeniedDialog = false
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        updateMapType()

        // check the state of all checkboxes and update the map accordingly
        with(map) {
            isTrafficEnabled = binding.traffic.isChecked
            isBuildingsEnabled = binding.buildings.isChecked
            isIndoorEnabled = binding.indoor.isChecked
        }

        // Must deal with the location checkbox separately as must check that
        // location permission have been granted before enabling the 'My Location' layer.
        if (binding.myLocation.isChecked) enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST_CODE)
    private fun enableMyLocation() {
        // Enable the location layer. Request the location permission if needed.
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (EasyPermissions.hasPermissions(this, *permissions)) {
            map.isMyLocationEnabled = true
        } else {
            // if permissions are not currently granted, request permissions
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_rationale_location),
                    LOCATION_PERMISSION_REQUEST_CODE, *permissions)
        }
    }

    /**
     * Change the type of the map depending on the currently selected item in the spinner
     */
    private fun updateMapType() {
        // This can also be called by the Android framework in onCreate() at which
        // point map may not be ready yet.
        if (!::map.isInitialized) return

        map.mapType = when (binding.layersSpinner.selectedItem) {
            getString(R.string.normal) -> MAP_TYPE_NORMAL
            getString(R.string.hybrid) -> MAP_TYPE_HYBRID
            getString(R.string.satellite) -> MAP_TYPE_SATELLITE
            getString(R.string.terrain) -> MAP_TYPE_TERRAIN
            getString(R.string.none_map) -> MAP_TYPE_NONE
            else -> {
                map.mapType // do not change map type
                Log.e(TAG, "Error setting layer with name ${binding.layersSpinner.selectedItem}")
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,
                permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        // Un-check the box until the layer has been enabled
        // and show dialog box with permission rationale.
        binding.myLocation.isChecked = false
        showPermissionDeniedDialog = true
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        // do nothing, handled in updateMyLocation
    }

    /**
     * Called as part of the AdapterView.OnItemSelectedListener
     */
    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        updateMapType()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Do nothing.
    }

    private fun onTrafficToggled() {
        map.isTrafficEnabled = binding.traffic.isChecked
    }

    @SuppressLint("MissingPermission")
    private fun onMyLocationToggled() {
        if (!binding.myLocation.isChecked) {
            map.isMyLocationEnabled = false
        } else {
            enableMyLocation()
        }
    }

    private fun onBuildingsToggled() {
        map.isBuildingsEnabled = binding.buildings.isChecked
    }

    private fun onIndoorToggled() {
        map.isIndoorEnabled = binding.indoor.isChecked
    }
}