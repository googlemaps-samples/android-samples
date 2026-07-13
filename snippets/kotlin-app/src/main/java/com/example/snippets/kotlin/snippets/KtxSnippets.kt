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

package com.example.snippets.kotlin.snippets

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.snippets.kotlin.R
import com.example.snippets.kotlin.TrackedMap
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline
import com.google.maps.android.ktx.addPolygon
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.cameraMoveEvents
import com.google.maps.android.ktx.cameraIdleEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.coroutines.launch

@SnippetGroup(
    title = "Kotlin Extensions (KTX)",
    description = "Snippets demonstrating Kotlin Coroutines and DSL extensions for Android Maps."
)
class KtxSnippets(
    private val context: Context,
    private val map: TrackedMap,
    private val scope: CoroutineScope
) {

    @SnippetItem(
        title = "1. Obtain Map via awaitMap()",
        description = "Obtains the GoogleMap instance asynchronously using a suspending function."
    )
    fun ktxObtainMap() {
        // [START maps_android_ktx_obtain_map]
        scope.launch {
            if (context is FragmentActivity) {
                val mapFragment =
                    context.supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                val googleMap = mapFragment?.awaitMap()
            }
        }
        // [END maps_android_ktx_obtain_map]
    }

    @SnippetItem(
        title = "2. Add Marker using DSL",
        description = "Adds a marker using the addMarker Kotlin DSL extension."
    )
    fun ktxAddMarker() {
        // [START maps_android_ktx_add_marker]
        val sydney = LatLng(-33.852, 151.211)
        val marker = map.delegate.addMarker {
            position(sydney)
            title("Marker in Sydney")
        }
        // [END maps_android_ktx_add_marker]
    }

    @SnippetItem(
        title = "3. Camera Move Events Flow",
        description = "Collects camera move events as a cold flow of coroutine events."
    )
    fun ktxCameraEvents() {
        // [START maps_android_ktx_camera_events]
        scope.launch {
            map.delegate.cameraMoveEvents().collect {
                Log.d("KTX", "Received camera move event")
            }
        }
        // [END maps_android_ktx_camera_events]
    }

    @SnippetItem(
        title = "4. Await Map Click",
        description = "Demonstrates suspending coroutine execution until the user clicks the map."
    )
    fun awaitMapClick() {
        scope.launch {
            val latLng = map.delegate.awaitMapClick()
            Log.d("KTX", "Map clicked at: $latLng")
        }
    }

    @SnippetItem(
        title = "5. Await Camera Idle",
        description = "Demonstrates suspending coroutine execution until the camera goes idle."
    )
    fun awaitCameraIdle() {
        scope.launch {
            map.delegate.awaitCameraIdle()
            Log.d("KTX", "Camera is now idle")
        }
    }

    @SnippetItem(
        title = "6. Polyline and Polygon DSLs",
        description = "Shows how to build polylines and polygons using Kotlin DSL builder functions."
    )
    fun polylinePolygonDsl() {
        // DSL for Polyline
        val polyline = map.delegate.addPolyline {
            add(LatLng(37.35, -122.0))
            add(LatLng(37.45, -122.0))
            color(android.graphics.Color.BLUE)
        }
        // DSL for Polygon
        val polygon = map.delegate.addPolygon {
            add(LatLng(37.35, -122.0))
            add(LatLng(37.45, -122.0))
            add(LatLng(37.45, -122.2))
            fillColor(android.graphics.Color.RED)
        }
    }

    @SnippetItem(
        title = "7. Camera Idle Events Flow",
        description = "Collects camera idle events as a cold flow of coroutine events."
    )
    fun cameraIdleEventsFlow() {
        scope.launch {
            map.delegate.cameraIdleEvents().collect {
                Log.d("KTX", "Camera idle flow event received")
            }
        }
    }
}

suspend fun GoogleMap.awaitMapClick(): LatLng = suspendCancellableCoroutine { continuation ->
    setOnMapClickListener { latLng ->
        setOnMapClickListener(null)
        continuation.resume(latLng)
    }
}

suspend fun GoogleMap.awaitCameraIdle(): Unit = suspendCancellableCoroutine { continuation ->
    setOnCameraIdleListener {
        setOnCameraIdleListener(null)
        continuation.resume(Unit)
    }
}
