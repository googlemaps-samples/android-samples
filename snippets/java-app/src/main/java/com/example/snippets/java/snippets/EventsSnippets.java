/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snippets.java.snippets;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.example.snippets.java.R;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.PointOfInterest;

@SnippetGroup(
        title = "Events",
        description = "Snippets demonstrating clicks, camera events, POI clicks and indoor building levels."
)
public class EventsSnippets {

    private final Context context;
    private final TrackedMap map;

    public EventsSnippets(Context context, TrackedMap map) {
        this.context = context;
        this.map = map;
    }

    @SnippetItem(
            title = "1. MapView Disable Click Event",
            description = "What it does: Sets setClickable(false) on the underlying MapView container.\nHow to see the effect: Direct tap interaction on the map view is disabled."
    )
    public void mapViewDisableClickEvent() {
        // [START maps_android_events_disable_clicks_mapview]
        if (context instanceof android.app.Activity) {
            MapView mapView = null;
            if (context instanceof com.example.snippets.java.MapActivity) {
                mapView = ((com.example.snippets.java.MapActivity) context).mapView;
            }
            if (mapView == null) {
                mapView = ((android.app.Activity) context).findViewById(R.id.mapView);
            }
            if (mapView == null) {
                android.widget.FrameLayout holder = ((android.app.Activity) context).findViewById(com.example.snippets.common.R.id.map_view_holder);
                if (holder != null && holder.getChildCount() > 0 && holder.getChildAt(0) instanceof MapView) {
                    mapView = (MapView) holder.getChildAt(0);
                }
            }
            if (mapView != null) {
                mapView.setClickable(false);
            }
        }
        // [END maps_android_events_disable_clicks_mapview]
    }

    @SnippetItem(
            title = "2. Map Fragment Disable Click Event",
            description = "What it does: Sets setClickable(false) on the SupportMapFragment root view container.\nHow to see the effect: Touch interactions on the map fragment view are ignored."
    )
    public void mapFragmentDisableClickEvent() {
        // [START maps_android_events_disable_clicks_mapfragment]
        if (context instanceof FragmentActivity) {
            SupportMapFragment mapFragment = (SupportMapFragment) ((FragmentActivity) context)
                .getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                View view = mapFragment.getView();
                if (view != null) {
                    view.setClickable(false);
                }
            }
        }
        // [END maps_android_events_disable_clicks_mapfragment]
    }

    @SnippetItem(
            title = "3. Active Indoor Building Level",
            description = "What it does: Queries map.getFocusedBuilding() to retrieve active indoor level indices.\nHow to see the effect: Active floor level information is extracted when viewing an indoor building plan."
    )
    public void focusedBuilding() {
        // [START maps_android_events_active_level]
        IndoorBuilding building = map.getDelegate().getFocusedBuilding();
        if (building != null) {
            int activeLevelIndex = building.getActiveLevelIndex();
            IndoorLevel activeLevel = building.getLevels().get(activeLevelIndex);
        }
        // [END maps_android_events_active_level]
    }

    @SnippetItem(
            title = "4. POI Click Listener",
            description = "What it does: Registers an OnPoiClickListener to capture tap events on Points of Interest.\nHow to see the effect: Tap any POI icon (such as a park or business); a Toast popup displays its name, Place ID, and location."
    )
    public void poiClickListener() {
        // [START maps_android_on_poi_click_demo]
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Toast.makeText(context, "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                    Toast.LENGTH_SHORT).show();
            }
        });
        // [END maps_android_on_poi_click_demo]
    }
}
