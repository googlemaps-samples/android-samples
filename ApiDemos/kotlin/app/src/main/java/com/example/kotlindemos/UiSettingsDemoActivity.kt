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
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

const val REQUEST_CODE_LOCATION = 123

class UiSettingsDemoActivity :
        AppCompatActivity(),
        OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ui_settings_demo)
        val mapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // Return early if map is not initialised properly
        map = googleMap ?: return
        enableMyLocation()
        // Set all the settings of the map to match the current state of the checkboxes
        with(map.uiSettings) {
            isZoomControlsEnabled = isChecked(R.id.zoom_button)
            isCompassEnabled = isChecked(R.id.compass_button)
            isMyLocationButtonEnabled = isChecked(R.id.myloc_button)
            isIndoorLevelPickerEnabled = isChecked(R.id.level_button)
            isMapToolbarEnabled = isChecked(R.id.maptoolbar_button)
            isZoomGesturesEnabled = isChecked(R.id.zoomgest_button)
            isScrollGesturesEnabled = isChecked(R.id.scrollgest_button)
            isTiltGesturesEnabled = isChecked(R.id.tiltgest_button)
            isRotateGesturesEnabled = isChecked(R.id.rotategest_button)
        }
    }

    /** On click listener for checkboxes */
    fun onClick(view: View) {
        if (view !is CheckBox) {
            return
        }
        val isChecked: Boolean = view.isChecked
        with(map.uiSettings) {
            when (view.id) {
                R.id.zoom_button -> isZoomControlsEnabled = isChecked
                R.id.compass_button -> isCompassEnabled = isChecked
                R.id.myloc_button -> isMyLocationButtonEnabled = isChecked
                R.id.level_button -> isIndoorLevelPickerEnabled = isChecked
                R.id.maptoolbar_button -> isMapToolbarEnabled = isChecked
                R.id.zoomgest_button -> isZoomGesturesEnabled = isChecked
                R.id.scrollgest_button -> isScrollGesturesEnabled = isChecked
                R.id.tiltgest_button -> isTiltGesturesEnabled = isChecked
                R.id.rotategest_button -> isRotateGesturesEnabled = isChecked
            }
        }
    }

    /** Returns whether the checkbox with the given id is checked */
    private fun isChecked(id: Int) = findViewById<CheckBox>(id)?.isChecked ?: false

    /** Override the onRequestPermissionResult to use EasyPermissions */
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * enableMyLocation() will enable the location of the map if the user has given permission
     * for the app to access their device location.
     * Android Studio requires an explicit check before setting map.isMyLocationEnabled to true
     * but we are using EasyPermissions to handle it so we can suppress the "MissingPermission"
     * check.
     */
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun enableMyLocation() {
        if (hasLocationPermission()) {
            map.isMyLocationEnabled = true
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.location),
                    REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
