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

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Xml
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.common_ui.R
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.Sample
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.maps.android.ktx.awaitMap
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser

/**
 * Demonstrates feeding programmatic coordinates from a GPX track into the GoogleMap location layer
 * using a custom [LocationSource].
 */
// [START maps_android_sample_location_source]
@Sample(
    id = "com.example.kotlindemos.LocationSourceDemoActivity",
    title = "Custom LocationSource",
    description = "Providing a custom mock LocationSource for simulated GPS navigation playback along a trail.",
    category = "Location & Sensors",
    complexity = Complexity.ADVANCED,
    tags = ["#location", "#locationsource", "#mock", "#simulation", "#gpx", "#navigation"],
    apiCalls = [
        "GoogleMap.setLocationSource(LocationSource)",
        "LocationSource.activate(OnLocationChangedListener)",
        "LocationSource.deactivate()",
        "GoogleMap.setMyLocationEnabled(Boolean)",
        "GoogleMap.addPolyline(PolylineOptions)",
        "CameraUpdateFactory.newLatLngBounds(LatLngBounds, Int)"
    ],
    purpose = "Shows how to feed programmatic coordinates from a GPX track into the GoogleMap location layer using a custom LocationSource.",
    successCriteria = "The map bounds to the trail, draws a polyline, and the blue dot animates smoothly along the route.",
    failureIndicators = "Blue dot fails to move or location updates cause memory leaks.",
    framework = Framework.KOTLIN_VIEWS
)
class LocationSourceDemoActivity : SamplesBaseActivity() {

    private var googleMap: GoogleMap? = null
    private var locationSource: GpxLocationSource? = null
    private var trackBounds: LatLngBounds? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            enableMyLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_source_demo)
        findViewById<MaterialToolbar>(R.id.top_bar)?.setTitle(R.string.location_source_demo_label)

        val toggleButton = findViewById<MaterialButton>(R.id.btn_toggle_playback)
        val recenterButton = findViewById<MaterialButton>(R.id.btn_recenter_bounds)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        lifecycleScope.launch {
            val trackPoints = withContext(Dispatchers.IO) {
                parseGpxTrack(resources.openRawResource(R.raw.fowler_rattlesnake))
            }
            val source = GpxLocationSource(trackPoints = trackPoints, intervalMs = 60L)
            locationSource = source
            lifecycle.addObserver(source)

            val map = mapFragment.awaitMap()
            initMap(map, trackPoints, source)

            toggleButton?.setOnClickListener {
                val isPlaying = source.togglePlayback()
                toggleButton.setText(if (isPlaying) R.string.location_source_pause else R.string.location_source_play)
            }

            recenterButton?.setOnClickListener {
                trackBounds?.let { bounds ->
                    val padding = (resources.displayMetrics.density * 56).toInt()
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                }
            }
        }

        applyInsets(findViewById(R.id.map_container))
    }

    private fun initMap(map: GoogleMap, trackPoints: List<LatLng>, source: GpxLocationSource) {
        googleMap = map
        if (trackPoints.isEmpty()) return

        val boundsBuilder = LatLngBounds.builder()
        for (point in trackPoints) {
            boundsBuilder.include(point)
        }
        val bounds = boundsBuilder.build()
        trackBounds = bounds

        // 1. Draw route polyline with outer casing and core color
        map.addPolyline(
            PolylineOptions()
                .addAll(trackPoints)
                .color(0xFF0D47A1.toInt())
                .width(16f)
                .jointType(JointType.ROUND)
                .startCap(RoundCap())
                .endCap(RoundCap())
        )
        map.addPolyline(
            PolylineOptions()
                .addAll(trackPoints)
                .color(0xFF2196F3.toInt())
                .width(10f)
                .jointType(JointType.ROUND)
                .startCap(RoundCap())
                .endCap(RoundCap())
        )

        // 2. Center and bound camera
        val padding = (resources.displayMetrics.density * 56).toInt()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 14.5f))
        map.setOnMapLoadedCallback {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }

        // 3. Connect custom location source
        map.setLocationSource(source)
        enableMyLocation()
    }

    private fun enableMyLocation() {
        val map = googleMap ?: return
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun parseGpxTrack(inputStream: InputStream): List<LatLng> {
        val points = mutableListOf<LatLng>()
        inputStream.use { stream ->
            val parser = Xml.newPullParser()
            parser.setInput(stream, "UTF-8")
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "trkpt") {
                    val lat = parser.getAttributeValue(null, "lat")?.toDoubleOrNull()
                    val lon = parser.getAttributeValue(null, "lon")?.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        points.add(LatLng(lat, lon))
                    }
                }
                eventType = parser.next()
            }
        }
        return points
    }
}

/**
 * A [LocationSource] that sequentially replays GPS track points along a trail.
 */
class GpxLocationSource(
    private val trackPoints: List<LatLng>,
    private val intervalMs: Long = 60L
) : LocationSource, DefaultLifecycleObserver {

    private var listener: OnLocationChangedListener? = null
    private var isRunning = true
    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    private val stepRunnable = object : Runnable {
        override fun run() {
            if (!isRunning || listener == null || trackPoints.isEmpty()) return
            emitCurrentPoint()
            currentIndex = (currentIndex + 1) % trackPoints.size
            handler.postDelayed(this, intervalMs)
        }
    }

    override fun activate(listener: OnLocationChangedListener) {
        this.listener = listener
        if (isRunning) {
            emitCurrentPoint()
            handler.postDelayed(stepRunnable, intervalMs)
        }
    }

    override fun deactivate() {
        handler.removeCallbacks(stepRunnable)
        this.listener = null
    }

    fun togglePlayback(): Boolean {
        isRunning = !isRunning
        if (isRunning) {
            handler.post(stepRunnable)
        } else {
            handler.removeCallbacks(stepRunnable)
        }
        return isRunning
    }

    private fun emitCurrentPoint() {
        val currentListener = listener ?: return
        if (trackPoints.isEmpty()) return

        val p1 = trackPoints[currentIndex]
        val p2 = trackPoints[(currentIndex + 1) % trackPoints.size]

        val loc1 = Location("GpxTrackLocationSource").apply {
            latitude = p1.latitude
            longitude = p1.longitude
        }
        val loc2 = Location("GpxTrackLocationSource").apply {
            latitude = p2.latitude
            longitude = p2.longitude
        }
        val bearing = loc1.bearingTo(loc2)

        val location = Location("GpxTrackLocationSource").apply {
            latitude = p1.latitude
            longitude = p1.longitude
            accuracy = 6.0f
            this.bearing = bearing
            speed = 4.5f
            time = System.currentTimeMillis()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
        }
        currentListener.onLocationChanged(location)
    }

    override fun onPause(owner: LifecycleOwner) {
        handler.removeCallbacks(stepRunnable)
    }

    override fun onResume(owner: LifecycleOwner) {
        if (isRunning && listener != null) {
            handler.post(stepRunnable)
        }
    }
}
// [END maps_android_sample_location_source]
