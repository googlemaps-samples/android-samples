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

import com.example.snippets.kotlin.TrackedMap
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

@SnippetGroup(
    title = "Camera",
    description = "Snippets demonstrating camera controls, zoom constraints, bounds, and animations.",
)
class CameraControlSnippets(private val map: TrackedMap) {

    @SnippetItem(
        title = "1. Zoom Level Constraints",
        description = "What it does: Sets a minimum zoom level of 6.0 and maximum zoom level of 14.0 on the camera.\nHow to see the effect: Pinch to zoom in or out; zooming stops when reaching minimum zoom (level 6) or maximum zoom (level 14).",
    )
    fun zoomLevel() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-33.88, 151.21), 8.0f))
        // [START maps_android_camera_and_view_zoom_level]
        map.setMinZoomPreference(6.0f)
        map.setMaxZoomPreference(14.0f)
        // [END maps_android_camera_and_view_zoom_level]
    }

    @SnippetItem(
        title = "2. Fit Camera To Bounds (Australia)",
        description = "What it does: Moves the camera once to fit the entire geographic bounding box of Australia within the visible viewport.\nHow to see the effect: The map immediately shifts to frame all of Australia. (Does not restrict subsequent user panning.)",
    )
    fun settingBoundaries() {
        // [START maps_android_camera_and_view_setting_boundaries]
        val australiaBounds = LatLngBounds(
            LatLng((-44.0), 113.0),  // SW bounds
            LatLng((-10.0), 154.0) // NE bounds
        )
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0))
        // [END maps_android_camera_and_view_setting_boundaries]
    }

    @SnippetItem(
        title = "3. Centering Map Within An Area",
        description = "What it does: Centers the camera at the geographic midpoint of Australia at zoom level 10.\nHow to see the effect: The camera jumps directly to the center of Australia at a fixed zoom scale.",
    )
    fun centeringMapWithinAnArea() {
        // [START maps_android_camera_and_view_centering_within_area]
        val australiaBounds = LatLngBounds(
            LatLng((-44.0), 113.0),  // SW bounds
            LatLng((-10.0), 154.0) // NE bounds
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(australiaBounds.center, 10f))
        // [END maps_android_camera_and_view_centering_within_area]
    }

    @SnippetItem(
        title = "4. Panning Restrictions",
        description = "What it does: Constrains the camera target to the geographic bounding box of Adelaide.\nHow to see the effect: Drag or pan the map; scrolling stops when the camera center reaches the boundary of Adelaide.",
    )
    fun panningRestrictions() {
        // [START maps_android_camera_and_view_panning_restrictions]
        // Create a LatLngBounds that includes the city of Adelaide in Australia.
        val adelaideBounds = LatLngBounds(
            LatLng(-35.0, 138.58),  // SW bounds
            LatLng(-34.9, 138.61) // NE bounds
        )

        // Constrain the camera target to the Adelaide bounds.
        map.setLatLngBoundsForCameraTarget(adelaideBounds)
        // [END maps_android_camera_and_view_panning_restrictions]
    }

    @SnippetItem(
        title = "5. Common Map Movements",
        description = "What it does: Demonstrates instant camera placement, animated zoom transitions, and 3D orientation (bearing & tilt).\nHow to see the effect: Watch the map smoothly animate from Sydney to Mountain View with orientation and tilt changes.",
    )
    fun commonMapMovements() {
        // [START maps_android_camera_and_view_common_map_movements]
        val sydney = LatLng(-33.88, 151.21)
        val mountainView = LatLng(37.4, -122.1)

        // Move the camera instantly to Sydney with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn())

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        val cameraPosition = CameraPosition.Builder()
            .target(mountainView) // Sets the center of the map to Mountain View
            .zoom(17f)            // Sets the zoom
            .bearing(90f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()              // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        // [END maps_android_camera_and_view_common_map_movements]
    }
}
