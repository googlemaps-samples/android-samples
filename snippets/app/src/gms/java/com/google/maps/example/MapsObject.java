package com.google.maps.example;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

class MapsObject extends AppCompatActivity {

    // [START maps_android_on_create_set_content_view]
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    // [END maps_android_on_create_set_content_view]

    private void mapFragment() {
        // [START maps_android_map_fragment]
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.my_container, mapFragment)
            .commit();
        // [END maps_android_map_fragment]
    }

    private void mapType(GoogleMap map) {
        // [START maps_android_map_type]
        // Sets the map type to be "hybrid"
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // [END maps_android_map_type]
    }

    private void googleMapOptions() {
        // [START maps_android_google_map_options]
        GoogleMapOptions options = new GoogleMapOptions();
        // [END maps_android_google_map_options]

        // [START maps_android_google_map_options_configure]
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
            .compassEnabled(false)
            .rotateGesturesEnabled(false)
            .tiltGesturesEnabled(false);
        // [END maps_android_google_map_options_configure]
    }

    // [START maps_android_on_map_ready_callback]
    class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

        // [START_EXCLUDE]
        // [START maps_android_on_map_ready_add_marker]
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
        }
        // [END maps_android_on_map_ready_add_marker]

        private void getMapAsync() {
            // [START maps_android_get_map_async]
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // [END maps_android_get_map_async]
        }
        // [END_EXCLUDE]
    }
    // [END maps_android_on_map_ready_callback]
}
