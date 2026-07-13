/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/org/example/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snippets.kotlin.snippets

import com.example.snippets.kotlin.R
import com.example.snippets.kotlin.TrackedMap
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.UrlTileProvider
import java.net.MalformedURLException
import java.net.URL

@SnippetGroup(
    title = "Overlays",
    description = "Snippets demonstrating GroundOverlays and TileOverlays."
)
class OverlaySnippets(private val map: TrackedMap) {

    @SnippetItem(
        title = "1. Ground Overlays",
        description = "Creates, retains, changes and removes a ground overlay."
    )
    fun groundOverlays() {
        // [START maps_android_ground_overlays_add]
        val newarkLatLng = LatLng(40.714086, -74.228697)
        val newarkMap = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
            .position(newarkLatLng, 8600f, 6500f)
        map.addGroundOverlay(newarkMap)
        // [END maps_android_ground_overlays_add]

        // [START maps_android_ground_overlays_retain]
        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
        val imageOverlay = map.addGroundOverlay(newarkMap)
        imageOverlay?.let {
            val isClickable = it.isClickable
            val isVisible = it.isVisible
            val bearing = it.bearing
            val transparency = it.transparency
            val zIndex = it.zIndex
            val position = it.position
            val width = it.width
            val height = it.height
        }
        // [END maps_android_ground_overlays_retain]

        // [START maps_android_ground_overlays_remove]
        imageOverlay?.remove()
        // [END maps_android_ground_overlays_remove]

        // [START maps_android_ground_overlays_change_image]
        // Update the GroundOverlay with a new image of the same dimension
        imageOverlay?.setImage(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
        // [END maps_android_ground_overlays_change_image]

        // [START maps_android_ground_overlays_associate_data]
        val sydneyGroundOverlay = map.addGroundOverlay(
            GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.harbour_bridge))
                .position(LatLng(-33.873, 151.206), 100f)
                .clickable(true)
        )
        sydneyGroundOverlay?.tag = "Sydney"
        // [END maps_android_ground_overlays_associate_data]
    }

    @SnippetItem(
        title = "2. Ground Overlay Position Image Location",
        description = "Defines GroundOverlayOptions positioning via anchor and LatLng."
    )
    fun positionImageLocation() {
        // [START maps_android_ground_overlays_position_image_location]
        val newarkMap = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
            .anchor(0f, 1f)
            .position(LatLng(40.714086, -74.228697), 8600f, 6500f)
        // [END maps_android_ground_overlays_position_image_location]
    }

    @SnippetItem(
        title = "3. Ground Overlay Position Image Bounds",
        description = "Defines GroundOverlayOptions positioning via LatLngBounds."
    )
    fun positionImageBounds() {
        // [START maps_android_ground_overlays_position_image_bounds]
        val newarkBounds = LatLngBounds(
            LatLng(40.712216, -74.22655),  // South west corner
            LatLng(40.773941, -74.12544)   // North east corner
        )
        val newarkMap = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
            .positionFromBounds(newarkBounds)
        // [END maps_android_ground_overlays_position_image_bounds]
    }

    @SnippetItem(
        title = "4. Tile Overlays Add",
        description = "Adds a TileOverlay with a custom UrlTileProvider."
    )
    fun tileOverlaysAdd() {
        // [START maps_android_tile_overlays_add]
        val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {

                /* Define the URL pattern for the tile images */
                val url = "http://my.image.server/images/$zoom/$x/$y.png"
                return if (!checkTileExists(x, y, zoom)) {
                    null
                } else try {
                    URL(url)
                } catch (e: MalformedURLException) {
                    throw AssertionError(e)
                }
            }

            /*
             * Check that the tile server supports the requested x, y and zoom.
             * Complete this stub according to the tile range you support.
             * If you support a limited range of tiles at different zoom levels, then you
             * need to define the supported x, y range at each zoom level.
             */
            private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
                val minZoom = 12
                val maxZoom = 16
                return zoom in minZoom..maxZoom
            }
        }

        val tileOverlay = map.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(tileProvider)
        )
        tileOverlay?.let {
            val isVisible = it.isVisible
            val fadeIn = it.fadeIn
            val transparency = it.transparency
            val zIndex = it.zIndex
        }
        // [END maps_android_tile_overlays_add]

        // [START maps_android_tile_overlays_remove]
        tileOverlay?.remove()
        // [END maps_android_tile_overlays_remove]

        // [START maps_android_tile_overlays_clear_tile_cache]
        tileOverlay?.clearTileCache()
        // [END maps_android_tile_overlays_clear_tile_cache]
    }

    @SnippetItem(
        title = "5. Tile Overlays Transparency",
        description = "Adds and toggles transparency of a TileOverlay."
    )
    fun tileOverlaysTransparency() {
        // [START maps_android_tile_overlays_transparency]
        val tileOverlayTransparent = map.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(object : UrlTileProvider(256, 256) {
                    // [START_EXCLUDE]
                    override fun getTileUrl(i: Int, i1: Int, i2: Int): URL? {
                        return null
                    } // [END_EXCLUDE]
                })
                .transparency(0.5f)
        )

        // Switch between 0.0f and 0.5f transparency.
        tileOverlayTransparent?.let {
            it.transparency = 0.5f - it.transparency
        }
        // [END maps_android_tile_overlays_transparency]
    }
}
