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

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.GoogleMap.OnCircleClickListener;
import com.google.android.libraries.maps.GoogleMap.OnGroundOverlayClickListener;
import com.google.android.libraries.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.libraries.maps.GoogleMap.OnPolygonClickListener;
import com.google.android.libraries.maps.GoogleMap.OnPolylineClickListener;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.UiSettings;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.Circle;
import com.google.android.libraries.maps.model.CircleOptions;
import com.google.android.libraries.maps.model.GroundOverlay;
import com.google.android.libraries.maps.model.GroundOverlayOptions;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.LatLngBounds;
import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.libraries.maps.model.Polygon;
import com.google.android.libraries.maps.model.PolygonOptions;
import com.google.android.libraries.maps.model.Polyline;
import com.google.android.libraries.maps.model.PolylineOptions;

/**
 * This shows how to use setTag/getTag on API objects.
 */
public class TagsDemoActivity extends AppCompatActivity implements
        OnCircleClickListener,
        OnGroundOverlayClickListener,
        OnMarkerClickListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        OnPolygonClickListener,
        OnPolylineClickListener  {

    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private static final LatLng DARWIN = new LatLng(-12.425892, 130.86327);
    private static final LatLng HOBART = new LatLng(-42.8823388, 147.311042);
    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private static class CustomTag {
        private final String description;
        private int clickCount;

        public CustomTag(String description) {
            this.description = description;
            clickCount = 0;
        }

        public void incrementClickCount() {
            clickCount++;
        }

        @Override
        public String toString() {
            return "The " + description + " has been clicked " + clickCount + " times.";
        }
    }

    private GoogleMap mMap = null;

    private Circle mAdelaideCircle;
    private GroundOverlay mSydneyGroundOverlay;
    private Marker mHobartMarker;
    private Polygon mDarwinPolygon;
    private Polyline mPolyline;

    private TextView mTagText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.tags_demo);

        mTagText = (TextView) findViewById(com.example.common_ui.R.id.tag_text);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        UiSettings mUiSettings = mMap.getUiSettings();

        // Turn off the map toolbar.
        mUiSettings.setMapToolbarEnabled(false);

        // Disable interaction with the map - other than clicking.
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setZoomGesturesEnabled(false);
        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);

        // Add a circle, a ground overlay, a marker, a polygon and a polyline to the map.
        addObjectsToMap();

        // Set listeners for click events.  See the bottom of this class for their behavior.
        mMap.setOnCircleClickListener(this);
        mMap.setOnGroundOverlayClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnPolygonClickListener(this);
        mMap.setOnPolylineClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription(getString(com.example.common_ui.R.string.tags_demo_map_description));

        // Create bounds that include all locations of the map.
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(ADELAIDE)
                .include(BRISBANE)
                .include(DARWIN)
                .include(HOBART)
                .include(PERTH)
                .include(SYDNEY)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void addObjectsToMap() {
        // A circle centered on Adelaide.
        mAdelaideCircle = mMap.addCircle(new CircleOptions()
                .center(ADELAIDE)
                .radius(500000)
                .fillColor(Color.argb(150, 66, 173, 244))
                .strokeColor(Color.rgb(66, 173, 244))
                .clickable(true));
        mAdelaideCircle.setTag(new CustomTag("Adelaide circle"));

        // A ground overlay at Sydney.
        mSydneyGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.harbour_bridge))
                .position(SYDNEY, 700000)
                .clickable(true));
        mSydneyGroundOverlay.setTag(new CustomTag("Sydney ground overlay"));

        // A marker at Hobart.
        mHobartMarker = mMap.addMarker(new MarkerOptions().position(HOBART));
        mHobartMarker.setTag(new CustomTag("Hobart marker"));

        // A polygon centered at Darwin.
        mDarwinPolygon = mMap.addPolygon(new PolygonOptions()
                .add(
                        new LatLng(DARWIN.latitude + 3, DARWIN.longitude - 3),
                        new LatLng(DARWIN.latitude + 3, DARWIN.longitude + 3),
                        new LatLng(DARWIN.latitude - 3, DARWIN.longitude + 3),
                        new LatLng(DARWIN.latitude - 3, DARWIN.longitude - 3))
                .fillColor(Color.argb(150, 34, 173, 24))
                .strokeColor(Color.rgb(34, 173, 24))
                .clickable(true));
        mDarwinPolygon.setTag(new CustomTag("Darwin polygon"));

        // A polyline from Perth to Brisbane.
        mPolyline = mMap.addPolyline(new PolylineOptions()
                .add(PERTH, BRISBANE)
                .color(Color.rgb(103, 24, 173))
                .width(30)
                .clickable(true));
        mPolyline.setTag(new CustomTag("Perth to Brisbane polyline"));
    }

    //
    // Click event listeners.
    //

    private void onClick(CustomTag tag) {
        tag.incrementClickCount();
        mTagText.setText(tag.toString());
    }

    @Override
    public void onCircleClick(Circle circle) {
        onClick((CustomTag) circle.getTag());
    }

    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        onClick((CustomTag) groundOverlay.getTag());
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        onClick((CustomTag) marker.getTag());
        // We return true to indicate that we have consumed the event and that we do not wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return true;
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        onClick((CustomTag) polygon.getTag());
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        onClick((CustomTag) polyline.getTag());
    }
}