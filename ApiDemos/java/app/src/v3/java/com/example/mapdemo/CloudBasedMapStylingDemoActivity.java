package com.example.mapdemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;

/**
 * This shows how to use Cloud-based Map Styling in a simple Activity. For more information on how
 * to style a map using this method, see:
 * https://developers.google.com/maps/documentation/android-sdk/cloud-based-map-styling
 **/
public class CloudBasedMapStylingDemoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAP_TYPE_KEY = "map_type";
    private GoogleMap map;
    private int currentMapType = GoogleMap.MAP_TYPE_NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentMapType = savedInstanceState.getInt(MAP_TYPE_KEY);
        }

        // The underlying style the map will use has been set in the layout
        // `cloud_styling_basic_demo` under the SupportMapFragment's `map:mapId` attribute.
        setContentView(R.layout.cloud_styling_basic_demo);
        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUpButtonListeners();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMapType(currentMapType);
    }

    private void setUpButtonListeners() {
        findViewById(R.id.styling_normal_mode).setOnClickListener(
            v -> setMapType(GoogleMap.MAP_TYPE_NORMAL));
        findViewById(R.id.styling_satellite_mode).setOnClickListener(
            v -> setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        findViewById(R.id.styling_hybrid_mode).setOnClickListener(
            v -> setMapType(GoogleMap.MAP_TYPE_HYBRID));
        findViewById(R.id.styling_terrain_mode).setOnClickListener(
            v -> setMapType(GoogleMap.MAP_TYPE_TERRAIN));
    }

    private void setMapType(int mapType) {
        currentMapType = mapType;
        map.setMapType(mapType);
    }
}