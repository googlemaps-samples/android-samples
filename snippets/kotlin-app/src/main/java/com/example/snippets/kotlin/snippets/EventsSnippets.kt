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

package com.example.snippets.kotlin.snippets

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.snippets.kotlin.R
import com.example.snippets.kotlin.TrackedMap
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.IndoorBuilding

@SnippetGroup(
    title = "Events",
    description = "Snippets demonstrating clicks, camera events, POI clicks and indoor building levels."
)
class EventsSnippets(private val context: Context, private val map: TrackedMap) {

    @SnippetItem(
        title = "1. MapView Disable Click Event",
        description = "What it does: Sets isClickable = false on the underlying MapView container.\nHow to see the effect: Direct tap interaction on the map view is disabled.",
    )
    fun mapViewDisableClickEvent() {
        // [START maps_android_events_disable_clicks_mapview]
        if (context is android.app.Activity) {
            val mapView = (context as? com.example.snippets.kotlin.MapActivity)?.mapView
                ?: context.findViewById<MapView>(R.id.mapView)
                ?: (context.findViewById<android.widget.FrameLayout>(com.example.snippets.common.R.id.map_view_holder)?.getChildAt(0) as? MapView)
            mapView?.isClickable = false
        }
        // [END maps_android_events_disable_clicks_mapview]
    }

    @SnippetItem(
        title = "2. Map Fragment Disable Click Event",
        description = "What it does: Sets isClickable = false on the SupportMapFragment root view container.\nHow to see the effect: Touch interactions on the map fragment view are ignored.",
    )
    fun mapFragmentDisableClickEvent() {
        // [START maps_android_events_disable_clicks_mapfragment]
        if (context is FragmentActivity) {
            val mapFragment = context.supportFragmentManager
                .findFragmentById(R.id.map) as? SupportMapFragment
            val view = mapFragment?.view
            view?.isClickable = false
        }
        // [END maps_android_events_disable_clicks_mapfragment]
    }

    @SnippetItem(
        title = "3. Active Indoor Building Level",
        description = "What it does: Queries map.focusedBuilding to retrieve active indoor level indices.\nHow to see the effect: Active floor level information is extracted when viewing an indoor building plan.",
    )
    fun focusedBuilding() {
        // [START maps_android_events_active_level]
        map.delegate.focusedBuilding?.let { building: IndoorBuilding ->
            val activeLevelIndex = building.activeLevelIndex
            val activeLevel = building.levels[activeLevelIndex]
        }
        // [END maps_android_events_active_level]
    }

    @SnippetItem(
        title = "4. POI Click Listener",
        description = "What it does: Registers an OnPoiClickListener to capture tap events on Points of Interest.\nHow to see the effect: Tap any POI icon (such as a park or business); a Toast popup displays its name, Place ID, and location.",
    )
    fun poiClickListener() {
        // [START maps_android_on_poi_click_demo]
        map.setOnPoiClickListener { poi ->
            Toast.makeText(
                context, """Clicked: ${poi.name}
                Place ID:${poi.placeId}
                Latitude:${poi.latLng.latitude} Longitude:${poi.latLng.longitude}""",
                Toast.LENGTH_SHORT
            ).show()
        }
        // [END maps_android_on_poi_click_demo]
    }
}
