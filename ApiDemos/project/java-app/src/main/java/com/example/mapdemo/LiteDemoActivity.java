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


package com.example.mapdemo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This demo shows some features supported in lite mode.
 * In particular it demonstrates the use of {@link com.google.android.gms.maps.model.Marker}s to
 * launch the Google Maps Mobile application, {@link com.google.android.gms.maps.CameraUpdate}s
 * and {@link com.google.android.gms.maps.model.Polygon}s.
 */
public class LiteDemoActivity extends SamplesBaseActivity implements
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);

    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);

    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);

    private static final LatLng DARWIN = new LatLng(-12.459501, 130.839915);

    /**
     * A Polygon with five points in the Norther Territory, Australia.
     */
    private static final LatLng[] POLYGON = new LatLng[]{
            new LatLng(-18.000328, 130.473633), new LatLng(-16.173880, 135.087891),
            new LatLng(-19.663970, 137.724609), new LatLng(-23.202307, 135.395508),
            new LatLng(-19.705347, 129.550781)};

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout
        setContentView(com.example.common_ui.R.layout.lite_demo);

        // Get the map and register for the ready callback
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    /**
     * Move the camera to center on Darwin.
     */
    public void showDarwin(View v) {
        // Wait until map is ready
        if (map == null) {
            return;
        }

        // Center camera on Adelaide marker
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DARWIN, 10f));
    }

    /**
     * Move the camera to center on Adelaide.
     */
    public void showAdelaide(View v) {
        // Wait until map is ready
        if (map == null) {
            return;
        }

        // Center camera on Adelaide marker
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ADELAIDE, 10f));
    }

    /**
     * Move the camera to show all of Australia.
     * Construct a {@link com.google.android.gms.maps.model.LatLngBounds} from markers positions,
     * then move the camera.
     */
    public void showAustralia(View v) {
        // Wait until map is ready
        if (map == null) {
            return;
        }

        // Create bounds that include all locations of the map
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder()
                .include(PERTH)
                .include(ADELAIDE)
                .include(MELBOURNE)
                .include(SYDNEY)
                .include(DARWIN)
                .include(BRISBANE);

        // Move camera to show all markers and locations
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 20));
    }

    /**
     * Called when the map is ready to add all markers and objects to the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        addMarkers();
        addPolyObjects();
        showAustralia(null);
    }

    /**
     * Add a Polyline and a Polygon to the map.
     * The Polyline connects Melbourne, Adelaide and Perth. The Polygon is located in the Northern
     * Territory (Australia).
     */
    private void addPolyObjects() {
        map.addPolyline((new PolylineOptions())
                .add(MELBOURNE, ADELAIDE, PERTH)
                .color(Color.GREEN)
                .width(5f));

        map.addPolygon(new PolygonOptions()
                .add(POLYGON)
                .fillColor(Color.CYAN)
                .strokeColor(Color.BLUE)
                .strokeWidth(5));
    }

    /**
     * Add Markers with default info windows to the map.
     */
    private void addMarkers() {
        map.addMarker(new MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane"));

        map.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        map.addMarker(new MarkerOptions()
                .position(SYDNEY)
                .title("Sydney")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        map.addMarker(new MarkerOptions()
                .position(ADELAIDE)
                .title("Adelaide")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        map.addMarker(new MarkerOptions()
                .position(PERTH)
                .title("Perth")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
    }
}
