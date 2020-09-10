package com.google.maps.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.libraries.maps.GoogleMapOptions;
import com.google.android.libraries.maps.MapFragment;

public class CloudBasedMapStylingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START maps_android_cloud_based_map_styling]
        MapFragment mapFragment = MapFragment.newInstance(
            new GoogleMapOptions()
                .mapId(getResources().getString(R.string.map_id)));
        // [END maps_android_cloud_based_map_styling]
    }
}