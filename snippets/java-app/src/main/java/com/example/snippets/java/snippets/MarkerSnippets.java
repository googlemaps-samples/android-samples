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
            description = "What it does: Places a standard pin marker at LatLng(-33.852, 151.211) in Sydney, Australia.\nHow to see the effect: A red marker appears over Sydney; tap the pin to view its title callout."
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
            description = "What it does: Creates a marker at Perth with draggability enabled (draggable = true).\nHow to see the effect: Long-press and drag the marker across the map surface to reposition it."
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
            description = "What it does: Adds a standard Google Maps red pin marker at Melbourne.\nHow to see the effect: A standard red pin renders over central Melbourne."
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
            description = "What it does: Sets a custom hue color (HUE_AZURE) on a marker pin.\nHow to see the effect: The marker renders with a cyan/azure colored pin instead of the default red hue."
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
            description = "What it does: Sets semi-transparency alpha (0.7f) on the marker pin.\nHow to see the effect: Map visual tiles beneath the marker show partially through the semi-transparent icon pin."
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
            description = "What it does: Replaces the default marker pin with a custom drawable image asset (arrow.png).\nHow to see the effect: An arrow icon graphic displays at Melbourne instead of a standard pin."
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
            description = "What it does: Aligns the marker flat against the map surface plane rather than billboarded toward camera.\nHow to see the effect: When you rotate or tilt the camera, the marker stays glued to the ground plane orientation."
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
            description = "What it does: Applies a 90-degree rotation transform around the marker's center anchor point (0.5, 0.5).\nHow to see the effect: The marker icon appears rotated 90 degrees clockwise facing right."
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
            description = "What it does: Configures a high zIndex (1.0f) on a marker to force top stacking order precedence.\nHow to see the effect: The marker always renders on top of overlapping map polylines, shapes, or lower z-index pins."
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
            description = "What it does: Associates click count state with markers using tag and listens for tap events.\nHow to see the effect: Tap any marker pin; a Toast message pops up displaying the updated click count."
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
            description = "What it does: Configures a marker with title details and population snippet text.\nHow to see the effect: Tap the marker to display an info callout window showing the title and population snippet."
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
            description = "What it does: Programmatically calls showInfoWindow() and hideInfoWindow() on the marker instance.\nHow to see the effect: The info window popup programmatically displays and dismisses without user interaction."
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
            description = "What it does: Listens for user touch click events inside the displayed info window callout.\nHow to see the effect: Open the marker info window and tap directly on the popup box to trigger a Toast notification."
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
            description = "What it does: Configures collision behavior on an AdvancedMarker (REQUIRED_AND_HIDES_OPTIONAL).\nHow to see the effect: Zoom or pan in dense marker clusters; optional overlapping markers hide to prevent clutter."
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
