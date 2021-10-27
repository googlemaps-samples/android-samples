// Copyright 2020 Google LLC
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

package com.google.maps.example.kotlin

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.UrlTileProvider
import java.net.MalformedURLException
import java.net.URL

internal class TileOverlays : OnMapReadyCallback {
    // [START maps_android_tile_overlays_add]
    private lateinit var map: GoogleMap

    var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
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
    // [END maps_android_tile_overlays_add]

    // [START maps_android_tile_overlays_transparency]
    private var tileOverlayTransparent: TileOverlay? = null

    override fun onMapReady(map: GoogleMap) {
        tileOverlayTransparent = map.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(object : UrlTileProvider(256, 256) {
                    // [START_EXCLUDE]
                    override fun getTileUrl(i: Int, i1: Int, i2: Int): URL? {
                        return null
                    } // [END_EXCLUDE]
                })
                .transparency(0.5f)
        )
    }

    // Switch between 0.0f and 0.5f transparency.
    fun toggleTileOverlayTransparency() {
        tileOverlayTransparent?.let {
            it.transparency = 0.5f - it.transparency
        }
    }
    // [END maps_android_tile_overlays_transparency]

    private fun removeAndClearCache() {
        // [START maps_android_tile_overlays_remove]
        tileOverlay?.remove()
        // [END maps_android_tile_overlays_remove]

        // [START maps_android_tile_overlays_clear_tile_cache]
        tileOverlay?.clearTileCache()
        // [END maps_android_tile_overlays_clear_tile_cache]
    }
}
