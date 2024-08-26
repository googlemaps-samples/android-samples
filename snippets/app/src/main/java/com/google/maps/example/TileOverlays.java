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

package com.google.maps.example;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

class TileOverlays implements OnMapReadyCallback {
    // [START maps_android_tile_overlays_add]
    private GoogleMap map;

    TileProvider tileProvider = new UrlTileProvider(256, 256) {

        @Override
        public URL getTileUrl(int x, int y, int zoom) {

            /* Define the URL pattern for the tile images */
            String s = String.format("http://my.image.server/images/%d/%d/%d.png", zoom, x, y);

            if (!checkTileExists(x, y, zoom)) {
                return null;
            }

            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }

        /*
         * Check that the tile server supports the requested x, y and zoom.
         * Complete this stub according to the tile range you support.
         * If you support a limited range of tiles at different zoom levels, then you
         * need to define the supported x, y range at each zoom level.
         */
        private boolean checkTileExists(int x, int y, int zoom) {
            int minZoom = 12;
            int maxZoom = 16;

            return (zoom >= minZoom && zoom <= maxZoom);
        }
    };

    TileOverlay tileOverlay = map.addTileOverlay(new TileOverlayOptions()
        .tileProvider(tileProvider));
    // [END maps_android_tile_overlays_add]

    // [START maps_android_tile_overlays_transparency]
    private TileOverlay tileOverlayTransparent;

    @Override
    public void onMapReady(GoogleMap map) {
        tileOverlayTransparent = map.addTileOverlay(new TileOverlayOptions()
            .tileProvider(new UrlTileProvider(256, 256) {
                // [START_EXCLUDE]
                @Override
                public URL getTileUrl(int i, int i1, int i2) {
                    return null;
                }
                // [END_EXCLUDE]
            })
            .transparency(0.5f));
    }

    // Switch between 0.0f and 0.5f transparency.
    public void toggleTileOverlayTransparency() {
        if (tileOverlayTransparent != null) {
            tileOverlayTransparent.setTransparency(0.5f - tileOverlayTransparent.getTransparency());
        }
    }
    // [END maps_android_tile_overlays_transparency]

    private void removeAndClearCache() {
        // [START maps_android_tile_overlays_remove]
        tileOverlay.remove();
        // [END maps_android_tile_overlays_remove]

        // [START maps_android_tile_overlays_clear_tile_cache]
        tileOverlay.clearTileCache();
        // [END maps_android_tile_overlays_clear_tile_cache]
    }
}
