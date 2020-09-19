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

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

class InfoWindows {
    private GoogleMap map;

    private void addInfoWindow() {
        // [START maps_android_info_windows_add]
        final LatLng melbourneLatLng = new LatLng(-37.81319, 144.96298);
        Marker melbourne = map.addMarker(
            new MarkerOptions()
                .position(melbourneLatLng)
                .title("Melbourne")
                .snippet("Population: 4,137,400"));
        // [END maps_android_info_windows_add]
    }

    private void showHideInfoWindow() {
        // [START maps_android_info_windows_show_hide]
        final LatLng melbourneLatLng = new LatLng(-37.81319, 144.96298);
        Marker melbourne = map.addMarker(
            new MarkerOptions()
                .position(melbourneLatLng)
                .title("Melbourne"));
        melbourne.showInfoWindow();
        // [END maps_android_info_windows_show_hide]
    }

    // [START maps_android_info_windows_click_listener]
    class InfoWindowActivity extends AppCompatActivity implements
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            // Add markers to the map and do other map setup.
            // ...
            // Set a listener for info window events.
            googleMap.setOnInfoWindowClickListener(this);
        }

        @Override
        public void onInfoWindowClick(Marker marker) {
            Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
        }
    }
    // [END maps_android_info_windows_click_listener]
}
