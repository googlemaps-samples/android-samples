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
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;

class EventsActivity extends AppCompatActivity {

    private GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
    }

    private void mapViewDisableClickEvent() {
        // [START maps_android_events_disable_clicks_mapview]
        MapView mapView = findViewById(R.id.mapView);
        mapView.setClickable(false);
        // [END maps_android_events_disable_clicks_mapview]
    }

    private void mapFragmentDisableClickEvent() {
        // [START maps_android_events_disable_clicks_mapfragment]
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        View view = mapFragment.getView();
        view.setClickable(false);
        // [END maps_android_events_disable_clicks_mapfragment]
    }

    private void focusedBuilding() {
        // [START maps_android_events_active_level]
        IndoorBuilding building = map.getFocusedBuilding();
        if (building != null) {
            int activeLevelIndex = building.getActiveLevelIndex();
            IndoorLevel activeLevel = building.getLevels().get(activeLevelIndex);
        }
        // [END maps_android_events_active_level]
    }
}
