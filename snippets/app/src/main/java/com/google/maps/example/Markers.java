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

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

class Markers implements OnMapReadyCallback {

    // [START maps_android_markers_add_a_marker]
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    // [END maps_android_markers_add_a_marker]

    private void markerDraggable(GoogleMap map) {
        // [START maps_android_markers_draggable]
        final LatLng perthLocation = new LatLng(-31.90, 115.86);
        Marker perth = map.addMarker(
            new MarkerOptions()
                .position(perthLocation)
                .draggable(true));
        // [END maps_android_markers_draggable]
    }

    private void defaultIcon(GoogleMap map) {
        // [START maps_android_markers_default_icon]
        final LatLng melbourneLocation = new LatLng(-37.813, 144.962);
        Marker melbourne = map.addMarker(
            new MarkerOptions()
                .position(melbourneLocation));
        // [END maps_android_markers_default_icon]
    }

    private void customMarkerColor(GoogleMap map) {
        // [START maps_android_markers_custom_marker_color]
        final LatLng melbourneLocation = new LatLng(-37.813, 144.962);
        Marker melbourne = map.addMarker(
            new MarkerOptions()
                .position(melbourneLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        // [END maps_android_markers_custom_marker_color]
    }

    private void markerOpacity(GoogleMap map) {
        // [START maps_android_markers_opacity]
        final LatLng melbourneLocation = new LatLng(-37.813, 144.962);
        Marker melbourne = map.addMarker(new MarkerOptions()
            .position(melbourneLocation)
            .alpha(0.7f));
        // [END maps_android_markers_opacity]
    }

    private void markerImage(GoogleMap map) {
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

    private void markerFlatten(GoogleMap map) {
        // [START maps_android_markers_flatten]
        final LatLng perthLocation = new LatLng(-31.90, 115.86);
        Marker perth = map.addMarker(
            new MarkerOptions()
                .position(perthLocation)
                .flat(true));
        // [END maps_android_markers_flatten]
    }

    private void markerRotate(GoogleMap map) {
        // [START maps_android_markers_rotate]
        final LatLng perthLocation = new LatLng(-31.90, 115.86);
        Marker perth = map.addMarker(
            new MarkerOptions()
                .position(perthLocation)
                .anchor(0.5f,0.5f)
                .rotation(90.0f));
        // [END maps_android_markers_rotate]
    }

    private void markerZIndex(GoogleMap map) {
        // [START maps_android_markers_z_index]
        map.addMarker(new MarkerOptions()
            .position(new LatLng(10, 10))
            .title("Marker z1")
            .zIndex(1.0f));
        // [END maps_android_markers_z_index]
    }

    // [START maps_android_markers_tag_sample]
    /**
     * A demo class that stores and retrieves data objects with each marker.
     */
    public class MarkerDemoActivity extends AppCompatActivity implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

        private final LatLng PERTH = new LatLng(-31.952854, 115.857342);
        private final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
        private final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);

        private Marker markerPerth;
        private Marker markerSydney;
        private Marker markerBrisbane;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_markers);
            SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        /** Called when the map is ready. */
        @Override
        public void onMapReady(GoogleMap map) {
            // Add some markers to the map, and add a data object to each marker.
            markerPerth = map.addMarker(new MarkerOptions()
                .position(PERTH)
                .title("Perth"));
            markerPerth.setTag(0);

            markerSydney = map.addMarker(new MarkerOptions()
                .position(SYDNEY)
                .title("Sydney"));
            markerSydney.setTag(0);

            markerBrisbane = map.addMarker(new MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane"));
            markerBrisbane.setTag(0);

            // Set a listener for marker click.
            map.setOnMarkerClickListener(this);
        }

        /** Called when the user clicks a marker. */
        @Override
        public boolean onMarkerClick(final Marker marker) {

            // Retrieve the data from the marker.
            Integer clickCount = (Integer) marker.getTag();

            // Check if a click count was set, then display the click count.
            if (clickCount != null) {
                clickCount = clickCount + 1;
                marker.setTag(clickCount);
                Toast.makeText(this,
                    marker.getTitle() +
                        " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
            }

            // Return false to indicate that we have not consumed the event and that we wish
            // for the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            return false;
        }
    }
    // [END maps_android_markers_tag_sample]
}
