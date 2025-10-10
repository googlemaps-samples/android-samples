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
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.common_ui.R
import com.example.common_ui.databinding.UiSettingsDemoBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings

class UiSettingsDemoActivity :
    SamplesBaseActivity(),
    OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var uiSettings: UiSettings
    private lateinit var binding: UiSettingsDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UiSettingsDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        applyInsets(binding.mapContainer)

        binding.zoomButtonsToggle.setOnClickListener { setZoomButtonsEnabled() }
        binding.compassToggle.setOnClickListener { setCompassEnabled() }
        binding.mylocationbuttonToggle.setOnClickListener { setMyLocationButtonEnabled() }
        binding.mylocationlayerToggle.setOnClickListener { setMyLocationLayerEnabled() }
        binding.scrollToggle.setOnClickListener { setScrollGesturesEnabled() }
        binding.zoomGesturesToggle.setOnClickListener { setZoomGesturesEnabled() }
        binding.tiltToggle.setOnClickListener { setTiltGesturesEnabled() }
        binding.rotateToggle.setOnClickListener { setRotateGesturesEnabled() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        uiSettings = map.uiSettings

        // Keep the UI Settings state in sync with the checkboxes.
        uiSettings.isZoomControlsEnabled = binding.zoomButtonsToggle.isChecked
        uiSettings.isCompassEnabled = binding.compassToggle.isChecked
        uiSettings.isMyLocationButtonEnabled = binding.mylocationbuttonToggle.isChecked
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = binding.mylocationlayerToggle.isChecked
        uiSettings.isScrollGesturesEnabled = binding.scrollToggle.isChecked
        uiSettings.isZoomGesturesEnabled = binding.zoomGesturesToggle.isChecked
        uiSettings.isTiltGesturesEnabled = binding.tiltToggle.isChecked
        uiSettings.isRotateGesturesEnabled = binding.rotateToggle.isChecked
    }

    private fun checkReady(): Boolean {
        if (!::map.isInitialized) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setZoomButtonsEnabled() {
        if (!checkReady()) {
            return
        }
        uiSettings.isZoomControlsEnabled = binding.zoomButtonsToggle.isChecked
    }

    private fun setCompassEnabled() {
        if (!checkReady()) {
            return
        }
        uiSettings.isCompassEnabled = binding.compassToggle.isChecked
    }

    private fun setMyLocationButtonEnabled() {
        if (!checkReady()) {
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            uiSettings.isMyLocationButtonEnabled = binding.mylocationbuttonToggle.isChecked
        } else {
            // Uncheck the box and request missing location permission.
            binding.mylocationbuttonToggle.isChecked = false
            PermissionUtils
                .requestLocationPermissions(this, 1, false)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationLayerEnabled() {
        if (!checkReady()) {
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = binding.mylocationlayerToggle.isChecked
        } else {
            // Uncheck the box and request missing location permission.
            binding.mylocationlayerToggle.isChecked = false
            PermissionUtils
                .requestLocationPermissions(this, 2, false)
        }
    }

    private fun setScrollGesturesEnabled() {
        if (!checkReady()) {
            return
        }
        uiSettings.isScrollGesturesEnabled = binding.scrollToggle.isChecked
    }

    private fun setZoomGesturesEnabled() {
        if (!checkReady()) {
            return
        }
        uiSettings.isZoomGesturesEnabled = binding.zoomGesturesToggle.isChecked
    }

    private fun setTiltGesturesEnabled() {
        if (!checkReady()) {
            return
        }
        uiSettings.isTiltGesturesEnabled = binding.tiltToggle.isChecked
    }

    private fun setRotateGesturesEnabled() {
        if (!checkReady()) {
            return
        }
        uiSettings.isRotateGesturesEnabled = binding.rotateToggle.isChecked
    }
}