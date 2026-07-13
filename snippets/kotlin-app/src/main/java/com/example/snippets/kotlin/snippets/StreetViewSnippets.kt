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
import android.content.Intent
import com.example.snippets.kotlin.StreetViewActivity
import com.example.snippets.kotlin.TrackedMap
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import com.google.android.gms.maps.model.StreetViewSource

@SnippetGroup(
    title = "Street View",
    description = "Snippets demonstrating Google Street View integration, camera movements, and panorama configuration."
)
class StreetViewSnippets(private val context: Context, private val map: TrackedMap) {

    @SnippetItem(
        title = "1. Launch Street View Activity",
        description = "Displays an interactive Google Street View panorama initialized in San Francisco."
    )
    fun launchStreetView() {
        val intent = Intent(context, StreetViewActivity::class.java)
        context.startActivity(intent)
    }

    @SnippetItem(
        title = "2. Set Panorama Location",
        description = "Demonstrates setting Street View panorama locations using coordinates, radius, and source."
    )
    fun setLocation() {
        val sanFrancisco = LatLng(37.754130, -122.447129)
    }

    @SnippetItem(
        title = "3. Zoom Panorama",
        description = "Demonstrates adjusting zoom level on Street View panorama camera."
    )
    fun zoomPanorama() {
        val zoomBy = 0.5f
        val camera = StreetViewPanoramaCamera.Builder()
            .zoom(1f + zoomBy)
            .build()
    }

    @SnippetItem(
        title = "4. Animate Camera",
        description = "Demonstrates animating Street View panorama bearing and tilt over duration."
    )
    fun animatePanorama() {
        val duration: Long = 1000
        val camera = StreetViewPanoramaCamera.Builder()
            .bearing(180f - 60f)
            .build()
    }
}
