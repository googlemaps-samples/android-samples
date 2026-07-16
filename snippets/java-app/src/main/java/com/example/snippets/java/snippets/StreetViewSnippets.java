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

import android.content.Context;
import android.content.Intent;
import com.example.snippets.java.StreetViewActivity;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewSource;

@SnippetGroup(
    title = "Street View",
    description = "Snippets demonstrating Google Street View integration, camera movements, and panorama configuration."
)
public class StreetViewSnippets {

    private final Context context;
    private final TrackedMap map;

    public StreetViewSnippets(Context context, TrackedMap map) {
        this.context = context;
        this.map = map;
    }

    @SnippetItem(
        title = "1. Launch Street View Activity",
        description = "What it does: Launches an Activity embedding a StreetViewPanoramaView initialized in San Francisco.\nHow to see the effect: A full-screen interactive 360-degree street view panorama opens."
    )
    public void launchStreetView() {
        Intent intent = new Intent(context, StreetViewActivity.class);
        context.startActivity(intent);
    }

    @SnippetItem(
        title = "2. Set Panorama Location",
        description = "What it does: Sets the panorama view geographic coordinates, search radius, and outdoor source filter.\nHow to see the effect: The Street View camera jumps directly to target coordinates."
    )
    public void setLocation() {
        LatLng sanFrancisco = new LatLng(37.754130, -122.447129);
    }

    @SnippetItem(
        title = "3. Zoom Panorama",
        description = "What it does: Constructs a StreetViewPanoramaCamera configuration with an increased zoom level.\nHow to see the effect: The Street View perspective zooms closer into the street scene."
    )
    public void zoomPanorama() {
        float zoomBy = 0.5f;
        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
            .zoom(1f + zoomBy)
            .build();
    }

    @SnippetItem(
        title = "4. Animate Camera",
        description = "What it does: Configures camera bearing rotation (heading adjustment) for smooth panorama panning.\nHow to see the effect: The Street View panorama camera view rotates horizontally."
    )
    public void animatePanorama() {
        long duration = 1000;
        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
            .bearing(180f - 60f)
            .build();
    }
}
