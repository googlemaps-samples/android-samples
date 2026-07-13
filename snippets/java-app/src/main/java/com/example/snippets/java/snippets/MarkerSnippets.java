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
import android.widget.Toast;
import com.example.snippets.java.R;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.AdvancedMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SnippetGroup(
        title = "Markers",
        description = "Snippets demonstrating marker creation, styling, customization, and events."
)
public class MarkerSnippets {

    private final Context context;
    private final TrackedMap map;

    public MarkerSnippets(Context context, TrackedMap map) {
        this.context = context;
        this.map = map;
    }

    @SnippetItem(
            title = "1. Add a Marker",
            description = "Adds a simple marker in Sydney, Australia."
    )
    public void addMarker() {
        // [START maps_android_markers_add_a_marker]
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        Marker marker = map.addMarker(new MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"));
        if (marker != null) {
            LatLng position = marker.getPosition();
            String title = marker.getTitle();
            String snippet = marker.getSnippet();
            boolean isDraggable = marker.isDraggable();
            boolean isFlat = marker.isFlat();
            boolean isVisible = marker.isVisible();
            float alpha = marker.getAlpha();
            float rotation = marker.getRotation();
            float zIndex = marker.getZIndex();
        }
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // [END maps_android_markers_add_a_marker]
    }

    @SnippetItem(
            title = "2. Draggable Marker",
            description = "Creates a draggable marker at Perth."
    )
    public void markerDraggable() {
        // [START maps_android_markers_draggable]
        final LatLng perthLocation = new LatLng(-31.90, 115.86);
        Marker perth = map.addMarker(
            new MarkerOptions()
                .position(perthLocation)
                .draggable(true));
        map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(perthLocation, 10f));
        // [END maps_android_markers_draggable]
    }

    @SnippetItem(
            title = "3. Default Icon Marker",
            description = "Adds a default marker at Melbourne."
    )
    public void defaultIcon() {
        // [START maps_android_markers_default_icon]
        final LatLng melbourneLocation = new LatLng(-37.813, 144.962);
        Marker melbourne = map.addMarker(
            new MarkerOptions()
                .position(melbourneLocation));
        // [END maps_android_markers_default_icon]
    }

    @SnippetItem(
            title = "4. Custom Marker Color",
            description = "Adds an azure-colored marker at Melbourne."
    )
    public void customMarkerColor() {
        // [START maps_android_markers_custom_marker_color]
        final LatLng melbourneLocation = new LatLng(-37.813, 144.962);
        Marker melbourne = map.addMarker(
            new MarkerOptions()
                .position(melbourneLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        // [END maps_android_markers_custom_marker_color]
    }

    @SnippetItem(
            title = "5. Marker Opacity",
            description = "Adds a semi-transparent marker at Melbourne."
    )
    public void markerOpacity() {
        // [START maps_android_markers_opacity]
        final LatLng melbourneLocation = new LatLng(-37.813, 144.962);
        Marker melbourne = map.addMarker(new MarkerOptions()
            .position(melbourneLocation)
            .alpha(0.7f));
        // [END maps_android_markers_opacity]
    }

    @SnippetItem(
            title = "6. Custom Marker Image",
            description = "Adds a marker with a custom arrow image resource."
    )
    public void markerImage() {
        // [START maps_android_markers_image]
        final LatLng melbourneLocation = new LatLng(-37.813, 144.962);
        Marker melbourne = map.addMarker(
            new MarkerOptions()
                .position(melbourneLocation)
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)));
        // [END maps_android_markers_image]
    }

    @SnippetItem(
            title = "7. Flat Marker",
            description = "Creates a flat marker that rotates with the map."
    )
    public void markerFlatten() {
        // [START maps_android_markers_flatten]
        final LatLng perthLocation = new LatLng(-31.90, 115.86);
        Marker perth = map.addMarker(
            new MarkerOptions()
                .position(perthLocation)
                .flat(true));
        // [END maps_android_markers_flatten]
    }

    @SnippetItem(
            title = "8. Rotate Marker",
            description = "Rotates a marker 90 degrees around its anchor."
    )
    public void markerRotate() {
        // [START maps_android_markers_rotate]
        final LatLng perthLocation = new LatLng(-31.90, 115.86);
        Marker perth = map.addMarker(
            new MarkerOptions()
                .position(perthLocation)
                .anchor(0.5f,0.5f)
                .rotation(90.0f));
        // [END maps_android_markers_rotate]
    }

    @SnippetItem(
            title = "9. Marker Z-Index",
            description = "Sets a high z-index on a marker."
    )
    public void markerZIndex() {
        // [START maps_android_markers_z_index]
        map.addMarker(new MarkerOptions()
            .position(new LatLng(10, 10))
            .title("Marker z1")
            .zIndex(1.0f));
        // [END maps_android_markers_z_index]
    }

    @SnippetItem(
            title = "10. Marker Click Listener & Tag",
            description = "Associates click counts with markers using tag objects."
    )
    public void markerClickAndTag() {
        // [START maps_android_markers_tag_sample]
        final LatLng PERTH = new LatLng(-31.952854, 115.857342);
        final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
        final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);

        Marker markerPerth = map.addMarker(new MarkerOptions()
            .position(PERTH)
            .title("Perth"));
        markerPerth.setTag(0);

        Marker markerSydney = map.addMarker(new MarkerOptions()
            .position(SYDNEY)
            .title("Sydney"));
        markerSydney.setTag(0);

        Marker markerBrisbane = map.addMarker(new MarkerOptions()
            .position(BRISBANE)
            .title("Brisbane"));
        markerBrisbane.setTag(0);

        // Set a listener for marker click.
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                // Retrieve the data from the marker.
                Integer clickCount = (Integer) marker.getTag();

                // Check if a click count was set, then display the click count.
                if (clickCount != null) {
                    clickCount = clickCount + 1;
                    marker.setTag(clickCount);
                    Toast.makeText(context,
                        marker.getTitle() + " has been clicked " + clickCount + " times.",
                        Toast.LENGTH_SHORT).show();
                }

                // Return false to indicate default behavior should occur.
                return false;
            }
        });
        // [END maps_android_markers_tag_sample]
    }

    @SnippetItem(
            title = "11. Add Info Window",
            description = "Creates a marker with title and snippet details."
    )
    public void addInfoWindow() {
        // [START maps_android_info_windows_add]
        final LatLng melbourneLatLng = new LatLng(-37.81319, 144.96298);
        Marker melbourne = map.addMarker(
            new MarkerOptions()
                .position(melbourneLatLng)
                .title("Melbourne")
                .snippet("Population: 4,137,400"));
        // [END maps_android_info_windows_add]
    }

    @SnippetItem(
            title = "12. Show/Hide Info Window",
            description = "Creates a marker and programmatically triggers its info window."
    )
    public void showHideInfoWindow() {
        // [START maps_android_info_windows_show_hide]
        final LatLng melbourneLatLng = new LatLng(-37.81319, 144.96298);
        Marker melbourne = map.addMarker(
            new MarkerOptions()
                .position(melbourneLatLng)
                .title("Melbourne"));
        melbourne.showInfoWindow();
        if (melbourne.isInfoWindowShown()) {
            melbourne.hideInfoWindow();
        }
        // [END maps_android_info_windows_show_hide]
    }

    @SnippetItem(
            title = "13. Info Window Click Listener",
            description = "Listens to clicks on info windows."
    )
    public void infoWindowClickListener() {
        // [START maps_android_info_windows_click_listener]
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(context, "Info window clicked",
                    Toast.LENGTH_SHORT).show();
            }
        });
        // [END maps_android_info_windows_click_listener]
    }

    @SnippetItem(
            title = "14. Marker Collision Behavior",
            description = "Configures collision behavior on an AdvancedMarker."
    )
    public void markerCollision() {
        // [START maps_android_marker_collision]
        // Collision behavior can only be changed in the AdvancedMarkerOptions object.
        // Changes to collision behavior after a marker has been created are not possible
        int collisionBehavior = AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL;
        AdvancedMarkerOptions options = new AdvancedMarkerOptions()
                .position(new LatLng(10.0, 10.0))
                .collisionBehavior(collisionBehavior);

        Marker marker = map.addMarker(options);
        // [END maps_android_marker_collision]
    }
}
