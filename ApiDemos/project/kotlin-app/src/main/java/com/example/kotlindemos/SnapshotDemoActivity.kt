// Copyright 2025 Google LLC
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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.ktx.awaitMap

/**
 * This shows how to take a snapshot of the map.
 */
class SnapshotDemoActivity : SamplesBaseActivity() {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private lateinit var map: GoogleMap
    private lateinit var waitForMapLoadCheckBox: CheckBox
    private lateinit var snapshotHolder: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snapshot_demo)
        waitForMapLoadCheckBox = findViewById(R.id.wait_for_map_load)
        snapshotHolder = findViewById(R.id.snapshot_holder)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        lifecycleScope.launchWhenCreated {
            map = mapFragment.awaitMap()
        }
        applyInsets(findViewById<View?>(R.id.map_container))
    }

    fun onScreenshot(view: View) {
        takeSnapshot()
    }

    fun onClearScreenshot(view: View) {
        val snapshotHolder =
            this.findViewById<ImageView>(R.id.snapshot_holder)
        snapshotHolder.setImageDrawable(null)
    }

    private fun takeSnapshot() {
        val callback =
            SnapshotReadyCallback { snapshot -> // Callback is called from the main thread, so we can modify the ImageView safely.
                snapshotHolder.setImageBitmap(snapshot)
            }
        if (waitForMapLoadCheckBox.isChecked) {
            map.setOnMapLoadedCallback { map.snapshot(callback) }
        } else {
            map.snapshot(callback)
        }
    }

    /**
     * Called when the clear button is clicked.
     */
    private fun clearSnapshot() {
        snapshotHolder.setImageDrawable(null)
    }
}