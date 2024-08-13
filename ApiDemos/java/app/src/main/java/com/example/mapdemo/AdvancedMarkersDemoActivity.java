// Copyright 2024 Google LLC
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
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.AdvancedMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapCapabilities;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PinConfig;

/**
 * The following sample showcases how to create Advanced Markers, and use all their customization
 * possibilities.
 */
// [START maps_android_sample_marker_advanced]
public class AdvancedMarkersDemoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final LatLng SINGAPORE = new LatLng(1.3521, 103.8198);
    private static final LatLng KUALA_LUMPUR = new LatLng(3.1390, 101.6869);
    private static final LatLng JAKARTA = new LatLng(-6.2088, 106.8456);
    private static final LatLng BANGKOK = new LatLng(13.7563, 100.5018);
    private static final LatLng MANILA = new LatLng(14.5995, 120.9842);
    private static final LatLng HO_CHI_MINH_CITY = new LatLng(10.7769, 106.7009);

    private static final float ZOOM_LEVEL = 3.5f;

    private static final String TAG = AdvancedMarkersDemoActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_markers_demo);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SINGAPORE, ZOOM_LEVEL));

        MapCapabilities capabilities = map.getMapCapabilities();
        Log.d(TAG, "Are advanced markers enabled? " + capabilities.isAdvancedMarkersAvailable());

        // This sample sets a view as the iconView for the Advanced Marker
        TextView textView = new TextView(this);
        textView.setText("Hello!");
        Marker advancedMarkerView = map.addMarker(new AdvancedMarkerOptions()
                .position(SINGAPORE)
                .iconView(textView)
                .zIndex(1f));

        // This uses PinConfig.Builder to create an instance of PinConfig.
        PinConfig.Builder pinConfigBuilder = PinConfig.builder();
        pinConfigBuilder.setBackgroundColor(Color.MAGENTA);
        PinConfig pinConfig = pinConfigBuilder.build();

        // Use the  PinConfig instance to set the icon for AdvancedMarkerOptions.
        AdvancedMarkerOptions advancedMarkerOptions = new AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig))
                .position(KUALA_LUMPUR);

        // Pass the AdvancedMarkerOptions instance to addMarker().
        Marker marker = map.addMarker(advancedMarkerOptions);

        // This sample changes the border color of the advanced marker
        PinConfig.Builder pinConfigBuilder2 = PinConfig.builder();
        pinConfigBuilder2.setBorderColor(Color.BLUE);
        PinConfig pinConfig2 = pinConfigBuilder2.build();

        AdvancedMarkerOptions advancedMarkerOptions2 = new AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig2))
                .position(JAKARTA);

        Marker marker2 = map.addMarker(advancedMarkerOptions2);

        // Set the glyph text.
        PinConfig.Builder pinConfigBuilder3 = PinConfig.builder();
        PinConfig.Glyph glyphText = new PinConfig.Glyph("A");

        // Alternatively, you can set the text color:
        // Glyph glyphText = new Glyph("A", Color.GREEN);
        pinConfigBuilder3.setGlyph(glyphText);
        PinConfig pinConfig3 = pinConfigBuilder3.build();

        AdvancedMarkerOptions advancedMarkerOptions3 = new AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig3))
                .position(BANGKOK);

        Marker marker3 = map.addMarker(advancedMarkerOptions3);

        // Create a transparent glyph.
        PinConfig.Builder pinConfigBuilder4 = PinConfig.builder();
        pinConfigBuilder4.setBackgroundColor(Color.MAGENTA);
        pinConfigBuilder4.setGlyph(new PinConfig.Glyph(Color.TRANSPARENT));
        PinConfig pinConfig4 = pinConfigBuilder4.build();

        AdvancedMarkerOptions advancedMarkerOptions4 = new AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig4))
                .position(MANILA);

        Marker marker4 = map.addMarker(advancedMarkerOptions4);

        // Collision behavior can only be changed in the AdvancedMarkerOptions object.
        // Changes to collision behavior after a marker has been created are not possible
        int collisionBehavior = AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL;
        AdvancedMarkerOptions advancedMarkerOptions5 = new AdvancedMarkerOptions()
                .position(HO_CHI_MINH_CITY)
                .collisionBehavior(collisionBehavior);

        Marker marker5 = map.addMarker(advancedMarkerOptions5);
    }
}
// [END maps_android_sample_marker_advanced]
