/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snippets.kotlin

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.snippets.common.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import android.widget.FrameLayout
import com.google.android.gms.maps.GoogleMapOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MapActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SNIPPET_TITLE = "snippet_title"
    }

    var mapView: MapView? = null
    var googleMap: GoogleMap? = null
    var currentIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_map)

        supportActionBar?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#7F52FF")))
        window.statusBarColor = android.graphics.Color.parseColor("#5835C4")

        val mainView = findViewById<android.view.View>(R.id.map_container)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // MapView will be dynamically initialized and added to map_view_holder in runSnippet()
        try {
            val appInfo = packageManager.getApplicationInfo(
                packageName,
                android.content.pm.PackageManager.GET_META_DATA,
            )
            val apiKey = appInfo.metaData?.getString("com.google.android.geo.API_KEY")
            if (apiKey.isNullOrEmpty() || apiKey == "DEFAULT_API_KEY" || apiKey == "YOUR_API_KEY" || !apiKey.startsWith("AIza")) {
                Toast.makeText(
                    this,
                    "ERROR: Invalid Google Maps API Key configured in secrets.properties",
                    Toast.LENGTH_LONG,
                ).show()
                Log.e("MapActivity", "Invalid MAPS_API_KEY: '$apiKey'")
                finish()
                return
            }
        } catch (e: Exception) {
            Log.e("MapActivity", "Failed to verify API key metadata", e)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val groupTitle = intent.getStringExtra("group_title")
        val snippetTitle = intent.getStringExtra(EXTRA_SNIPPET_TITLE)
        val snippetList = SnippetRegistry.getSnippetGroups().flatMap { it.items }
        currentIndex = snippetList.indexOfFirst {
            it.title == snippetTitle && (groupTitle == null || it.groupTitle == groupTitle)
        }
        Log.d("MapActivity", "onCreate: groupTitle='$groupTitle', snippetTitle='$snippetTitle', resolved index=$currentIndex, resolved item='${snippetList.getOrNull(currentIndex)?.let { "${it.groupTitle} - ${it.title}" }}'")
        if (currentIndex == -1 && snippetList.isNotEmpty()) {
            currentIndex = 0
            Log.d("MapActivity", "Fallback to index 0: '${snippetList[0].groupTitle} - ${snippetList[0].title}'")
        }

        findViewById<Button>(R.id.snapshot_button).apply {
            setOnClickListener {
                val map = googleMap
                if (map != null) {
                    val cam = map.cameraPosition
                    val center = cam.target
                    val rawBearing = cam.bearing
                    val bearing = (rawBearing % 360.0 + 360.0) % 360.0

                    val codeSnippet = """
                        CameraPosition.builder()
                            .target(LatLng(${center.latitude}, ${center.longitude}))
                            .zoom(${cam.zoom}f)
                            .tilt(${cam.tilt}f)
                            .bearing(${bearing}f)
                            .build()
                    """.trimIndent()

                    val cm = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                    cm?.setPrimaryClip(android.content.ClipData.newPlainText("CameraPosition", codeSnippet))
                    Toast.makeText(this@MapActivity, "📷 Camera Pose copied to clipboard!\n$codeSnippet", Toast.LENGTH_LONG).show()
                    Log.d("MapActivity", "Camera Pose copied:\n$codeSnippet")
                } else {
                    Toast.makeText(this@MapActivity, "Map NOT initialized yet", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.purpose_button).apply {
            setOnClickListener {
                val item = snippetList.getOrNull(currentIndex)
                val desc = if (!item?.description.isNullOrEmpty()) item?.description else "Demonstrates ${item?.title ?: snippetTitle}"
                androidx.appcompat.app.AlertDialog.Builder(this@MapActivity)
                    .setTitle("ℹ️ ${item?.title ?: snippetTitle ?: "Snippet Purpose"}")
                    .setMessage("🎯 Purpose & Point:\n$desc\n\n👀 What you should see / do:\nInteract with the live map on your device screen to test this capability.")
                    .setPositiveButton("Got It", null)
                    .show()
            }
        }

        findViewById<Button>(R.id.reset_view_button).apply {
            setOnClickListener {
                if (currentIndex >= 0) {
                    val item = snippetList[currentIndex]
                    runSnippet(item.groupTitle, item.title)
                }
            }
        }

        findViewById<Button>(R.id.previous_button).apply {
            setOnClickListener {
                if (snippetList.isEmpty()) return@setOnClickListener
                currentIndex = if (currentIndex > 0) currentIndex - 1 else snippetList.size - 1
                val item = snippetList[currentIndex]
                runSnippet(item.groupTitle, item.title)
            }
        }

        findViewById<Button>(R.id.next_button).apply {
            setOnClickListener {
                if (snippetList.isEmpty()) return@setOnClickListener
                currentIndex = if (currentIndex < snippetList.size - 1) currentIndex + 1 else 0
                val item = snippetList[currentIndex]
                runSnippet(item.groupTitle, item.title)
            }
        }

        if (currentIndex >= 0 && snippetList.isNotEmpty()) {
            val item = snippetList[currentIndex]
            runSnippet(item.groupTitle, item.title)
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = android.content.Intent(this, KotlinSnippetsActivity::class.java).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val groupTitle = intent.getStringExtra("group_title")
        val snippetTitle = intent.getStringExtra(EXTRA_SNIPPET_TITLE)
        runSnippet(groupTitle, snippetTitle)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun recreateMapView(mapId: String?) {
        val holder = findViewById<FrameLayout>(R.id.map_view_holder) ?: return

        mapView?.let {
            it.onPause()
            it.onStop()
            it.onDestroy()
            holder.removeView(it)
        }
        mapView = null
        googleMap = null

        val options = GoogleMapOptions()
        if (!mapId.isNullOrEmpty() && mapId != "YOUR_MAP_ID") {
            options.mapId(mapId)
            Log.d("MapActivity", "Recreating MapView with Map ID: $mapId")
        } else {
            Log.d("MapActivity", "Recreating MapView with default options (No Map ID)")
        }

        val newMapView = MapView(this, options)
        holder.addView(newMapView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        ))
        newMapView.onCreate(null)
        newMapView.onStart()
        newMapView.onResume()
        mapView = newMapView
    }

    private fun runSnippet(groupTitle: String?, snippetTitle: String?) {
        if (snippetTitle == null) return
        val key = if (groupTitle != null) "$groupTitle - $snippetTitle" else snippetTitle
        val snippet = SnippetRegistry.snippets[key]
            ?: SnippetRegistry.getSnippetGroups().flatMap { it.items }.find { it.title == snippetTitle }
            ?: return

        val allSnippets = SnippetRegistry.getSnippetGroups().flatMap { it.items }
        val idx = allSnippets.indexOfFirst { it.title == snippet.title && (groupTitle == null || it.groupTitle == groupTitle) }
        if (idx != -1) {
            currentIndex = idx
        }

        supportActionBar?.title = "🟣 ${snippet.title}"
        findViewById<android.view.View?>(androidx.appcompat.R.id.action_bar)?.setOnClickListener {
            findViewById<android.view.View?>(R.id.purpose_button)?.performClick()
        }

        recreateMapView(BuildConfig.MAP_ID)
        SnippetRegistry.clearTrackedItems()
        findViewById<android.widget.LinearLayout>(R.id.custom_controls_container)?.apply {
            removeAllViews()
            visibility = android.view.View.GONE
        }

        mapView?.getMapAsync { map ->
            this.googleMap = map
            TrackedMap.resetMapToDefaults(map)
            try {
                snippet.action(this@MapActivity, map, lifecycleScope)
                map.setOnMapLoadedCallback {
                    mapView?.contentDescription = "MapLoaded"
                }
                lifecycleScope.launch {
                    delay(2.seconds)
                    mapView?.contentDescription = "MapLoaded"
                }
            } catch (e: Exception) {
                Log.e("MapActivity", "Error running snippet: ${e.message}")
            }
        }
    }
}
