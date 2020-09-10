package com.google.maps.example;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;

class POIBehaviorActivity extends AppCompatActivity {

    private GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START maps_android_marker_collision]
        Marker marker = map.addMarker(
            new MarkerOptions()
                .position(new LatLng(10, 10))
                .zIndex(10) // Optional.
                .collisionBehavior(Marker.CollisionBehavior.OPTIONAL_AND_HIDES_LOWER_PRIORITY));
        // [END maps_android_marker_collision]
    }
}
