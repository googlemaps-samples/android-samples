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
package com.example.kotlindemos

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView

import androidx.lifecycle.lifecycleScope
import com.example.common_ui.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch

/**
 * Demonstrates capturing a bitmap screenshot of a [GoogleMap] view using [GoogleMap.snapshot].
 *
 * Key Concepts:
 * 1. **Live Map Capture**: [GoogleMap.snapshot] takes an asynchronous render of the current
 *    map viewport and delivers it as an Android [android.graphics.Bitmap] via [SnapshotReadyCallback].
 * 2. **Tile Readiness Synchronization**: If the "Wait for Map Load" option is selected,
 *    [GoogleMap.setOnMapLoadedCallback] is invoked first to ensure all vector tiles, labels,
 *    and overlays are fully rendered before capturing the bitmap.
 * 3. **Material 3 Split View**: Displays the interactive map in a top card and the captured
 *    preview in a bottom card with empty-state placeholder handling.
 */
class SnapshotDemoActivity : SamplesBaseActivity() {
    private lateinit var map: GoogleMap
    private lateinit var binding: com.example.common_ui.databinding.SnapshotDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.example.common_ui.databinding.SnapshotDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.screenshotButton.setOnClickListener { takeSnapshot() }
        binding.clearButton.setOnClickListener { clearSnapshot() }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        lifecycleScope.launch {
            map = mapFragment.awaitMap()
            // Center on Venice, Italy — a visually rich standard vector map showing the Grand Canal and Rialto
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(VENICE, 14.5f))
        }
        applyInsets(binding.mapContainer)
    }

    /**
     * Captures a snapshot of the current map viewport.
     */
    private fun takeSnapshot() {
        if (!::map.isInitialized) return

        val callback = SnapshotReadyCallback { snapshot ->
            // Callback runs on the main UI thread, so we can update the ImageView and card state directly.
            binding.snapshotHolder.setImageBitmap(snapshot)
            binding.snapshotPlaceholder.visibility = View.GONE
            binding.snapshotLabel.visibility = View.VISIBLE
        }

        if (binding.waitForMapLoad.isChecked) {
            // Wait until all map tiles are rendered before taking the snapshot
            map.setOnMapLoadedCallback { map.snapshot(callback) }
        } else {
            // Take snapshot immediately with currently loaded tiles
            map.snapshot(callback)
        }
    }

    /**
     * Clears the captured snapshot image and restores the empty-state placeholder.
     */
    private fun clearSnapshot() {
        binding.snapshotHolder.setImageDrawable(null)
        binding.snapshotPlaceholder.visibility = View.VISIBLE
        binding.snapshotLabel.visibility = View.GONE
    }

    companion object {
        // Venice, Italy (Grand Canal & Rialto)
        internal val VENICE = LatLng(45.4380, 12.3350)
    }
}