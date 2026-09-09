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

package com.example.snippets.java.snippets;

import com.example.snippets.java.R;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import java.net.MalformedURLException;
import java.net.URL;

@SnippetGroup(
        title = "Overlays",
        description = "Snippets demonstrating GroundOverlays and TileOverlays."
)
public class OverlaySnippets {

    private final TrackedMap map;

    public OverlaySnippets(TrackedMap map) {
        this.map = map;
    }

    @SnippetItem(
            title = "1. Ground Overlays",
            description = "What it does: Binds an image bitmap (historical map graphic) to fixed geographic coordinates on the map surface.\nHow to see the effect: A historical 1922 map image overlays the ground in Newark, NJ."
    )
    public void groundOverlays() {
        // [START maps_android_ground_overlays_add]
        LatLng newarkLatLng = new LatLng(40.714086, -74.228697);

        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
            .position(newarkLatLng, 8600f, 6500f);
        map.addGroundOverlay(newarkMap);
        // [END maps_android_ground_overlays_add]

        // [START maps_android_ground_overlays_retain]
        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
        GroundOverlay imageOverlay = map.addGroundOverlay(newarkMap);
        if (imageOverlay != null) {
            boolean isClickable = imageOverlay.isClickable();
            boolean isVisible = imageOverlay.isVisible();
            float bearing = imageOverlay.getBearing();
            float transparency = imageOverlay.getTransparency();
            float zIndex = imageOverlay.getZIndex();
            LatLng position = imageOverlay.getPosition();
            float width = imageOverlay.getWidth();
            float height = imageOverlay.getHeight();
        }
        // [END maps_android_ground_overlays_retain]

        // [START maps_android_ground_overlays_remove]
        imageOverlay.remove();
        // [END maps_android_ground_overlays_remove]

        // [START maps_android_ground_overlays_change_image]
        // Update the GroundOverlay with a new image of the same dimension
        imageOverlay.setImage(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922));
        // [END maps_android_ground_overlays_change_image]

        // [START maps_android_ground_overlays_associate_data]
        GroundOverlay sydneyGroundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.harbour_bridge))
            .position(new LatLng(-33.873, 151.206), 100)
            .clickable(true));

        sydneyGroundOverlay.setTag("Sydney");
        // [END maps_android_ground_overlays_associate_data]
    }

    @SnippetItem(
            title = "2. Ground Overlay Position Image Location",
            description = "What it does: Configures ground overlay placement using anchor offset and center LatLng coordinates.\nHow to see the effect: The image aligns precisely relative to its anchor point and width/height dimensions."
    )
    public void positionImageLocation() {
        // [START maps_android_ground_overlays_position_image_location]
        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
            .anchor(0, 1)
            .position(new LatLng(40.714086, -74.228697), 8600f, 6500f);
        // [END maps_android_ground_overlays_position_image_location]
    }

    @SnippetItem(
            title = "3. Ground Overlay Position Image Bounds",
            description = "What it does: Stretches and bounds a ground overlay image across a LatLngBounds rectangle.\nHow to see the effect: The image overlay scales to fit exactly within SW and NE geographic boundary corners."
    )
    public void positionImageBounds() {
        // [START maps_android_ground_overlays_position_image_bounds]
        LatLngBounds newarkBounds = new LatLngBounds(
            new LatLng(40.712216, -74.22655),       // South west corner
            new LatLng(40.773941, -74.12544));      // North east corner
        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
            .positionFromBounds(newarkBounds);
        // [END maps_android_ground_overlays_position_image_bounds]
    }

    @SnippetItem(
            title = "4. Tile Overlays Add",
            description = "What it does: Adds a custom TileOverlay layer backed by a custom web URL tile server (UrlTileProvider).\nHow to see the effect: Custom raster imagery tiles fetch and overlay the base map as you pan and zoom."
    )
    public void tileOverlaysAdd() {
        // [START maps_android_tile_overlays_add]
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
        if (tileOverlay != null) {
            boolean isVisible = tileOverlay.isVisible();
            boolean fadeIn = tileOverlay.getFadeIn();
            float transparency = tileOverlay.getTransparency();
            float zIndex = tileOverlay.getZIndex();
        }
        // [END maps_android_tile_overlays_add]

        // [START maps_android_tile_overlays_remove]
        tileOverlay.remove();
        // [END maps_android_tile_overlays_remove]

        // [START maps_android_tile_overlays_clear_tile_cache]
        tileOverlay.clearTileCache();
        // [END maps_android_tile_overlays_clear_tile_cache]
    }

    @SnippetItem(
            title = "5. Tile Overlays Transparency",
            description = "What it does: Configures and toggles semi-transparency (0.5f) on a custom tile overlay layer.\nHow to see the effect: Base map vector features show through the semi-transparent custom tile layer."
    )
    public void tileOverlaysTransparency() {
        // [START maps_android_tile_overlays_transparency]
        TileOverlay tileOverlayTransparent = map.addTileOverlay(new TileOverlayOptions()
            .tileProvider(new UrlTileProvider(256, 256) {
                // [START_EXCLUDE]
                @Override
                public URL getTileUrl(int i, int i1, int i2) {
                    return null;
                }
                // [END_EXCLUDE]
            })
            .transparency(0.5f));

        // Switch between 0.0f and 0.5f transparency.
        if (tileOverlayTransparent != null) {
            tileOverlayTransparent.setTransparency(0.5f - tileOverlayTransparent.getTransparency());
        }
        // [END maps_android_tile_overlays_transparency]
    }
}
