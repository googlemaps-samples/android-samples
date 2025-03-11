/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos

import com.example.kotlindemos.polyline.PolylineDemoActivity

/**
 * A list of all the demos we have available.
 */
class DemoDetailsList {
  companion object {
    val DEMOS = listOf(
      DemoDetails(
        com.example.common_ui.R.string.basic_demo_label,
        com.example.common_ui.R.string.basic_demo_details,
        BasicMapDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.camera_demo_label,
        com.example.common_ui.R.string.camera_demo_description,
        CameraDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.circle_demo_label,
        com.example.common_ui.R.string.circle_demo_details,
        CircleDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.close_info_window_demo_label,
        com.example.common_ui.R.string.close_info_window_demo_details,
        CloseInfoWindowDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.cloud_styling_label,
        com.example.common_ui.R.string.cloud_styling_description,
        CloudBasedMapStylingDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.events_demo_label,
        com.example.common_ui.R.string.events_demo_details,
        EventsDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.ground_overlay_demo_label,
        com.example.common_ui.R.string.ground_overlay_demo_details,
        GroundOverlayDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.indoor_demo_label,
        com.example.common_ui.R.string.indoor_demo_details,
        IndoorDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.layers_demo_label,
        com.example.common_ui.R.string.layers_demo_description,
        LayersDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.lite_demo_label,
        com.example.common_ui.R.string.lite_demo_details,
        LiteDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.lite_list_demo_label,
        com.example.common_ui.R.string.lite_list_demo_details,
        LiteListDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.marker_collision_label,
        com.example.common_ui.R.string.marker_collision_description,
        MarkerCollisionDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.markers_demo_label,
        com.example.common_ui.R.string.markers_demo_description,
        MarkerDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.my_location_demo_label,
        com.example.common_ui.R.string.my_location_demo_details,
        MyLocationDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.polygon_demo_label,
        com.example.common_ui.R.string.polygon_demo_details,
        PolygonDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.polyline_demo_label,
        com.example.common_ui.R.string.polyline_demo_description,
        PolylineDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.raw_map_view_demo_label,
        com.example.common_ui.R.string.raw_map_view_demo_description,
        RawMapViewDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.street_view_panorama_basic_demo_label,
        com.example.common_ui.R.string.street_view_panorama_basic_demo_details,
        StreetViewPanoramaBasicDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.street_view_panorama_navigation_demo_label,
        com.example.common_ui.R.string.street_view_panorama_navigation_demo_details,
        StreetViewPanoramaNavigationDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.split_street_view_panorama_and_map_demo_label,
        com.example.common_ui.R.string.split_street_view_panorama_and_map_demo_details,
        SplitStreetViewPanoramaAndMapDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.street_view_panorama_options_demo_label,
        com.example.common_ui.R.string.street_view_panorama_options_demo_details,
        StreetViewPanoramaOptionsDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.street_view_panorama_events_demo_label,
        com.example.common_ui.R.string.street_view_panorama_events_demo_details,
        StreetViewPanoramaEventsDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.street_view_panorama_view_demo_label,
        com.example.common_ui.R.string.street_view_panorama_view_demo_details,
        StreetViewPanoramaViewDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.tags_demo_label,
        com.example.common_ui.R.string.tags_demo_details,
        TagsDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.tile_coordinate_demo_label,
        com.example.common_ui.R.string.tile_coordinate_demo_description,
        TileCoordinateDemoActivity::class.java
      ),
      DemoDetails(
        com.example.common_ui.R.string.tile_overlay_demo_label,
        com.example.common_ui.R.string.tile_overlay_demo_description,
        TileOverlayDemoActivity::class.java
      ),
      DemoDetails(
        com.example.common_ui.R.string.ui_settings_demo_label,
        com.example.common_ui.R.string.ui_settings_demo_details,
        UiSettingsDemoActivity::class.java),
      DemoDetails(
        com.example.common_ui.R.string.region_demo_label,
        com.example.common_ui.R.string.region_demo_details,
        VisibleRegionDemoActivity::class.java)
    )
  }
}