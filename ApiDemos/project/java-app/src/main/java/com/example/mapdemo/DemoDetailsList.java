// Copyright 2025 Google LLC
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

/**
 * A list of all the demos we have available.
 */
public final class DemoDetailsList {

    /**
     * This class should not be instantiated.
     */
    private DemoDetailsList() {
    }

    public static final DemoDetails[] DEMOS = {
            new DemoDetails(com.example.common_ui.R.string.advanced_markers_demo_label,
                    com.example.common_ui.R.string.advanced_markers_demo_details,
                    AdvancedMarkersDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.basic_map_demo_label,
                    com.example.common_ui.R.string.basic_map_demo_description,
                    BasicMapDemoActivity.class),
            new DemoDetails(
                    com.example.common_ui.R.string.background_color_customization_demo_label,
                    com.example.common_ui.R.string.background_color_customization_demo_description,
                    BackgroundColorCustomizationDemoActivity.class),
            new DemoDetails(
                    com.example.common_ui.R.string.background_color_customization_programmatic_demo_label,
                    com.example.common_ui.R.string.background_color_customization_programmatic_demo_description,
                    BackgroundColorCustomizationProgrammaticDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.camera_demo_label,
                    com.example.common_ui.R.string.camera_demo_description,
                    CameraDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.camera_clamping_demo_label,
                    com.example.common_ui.R.string.camera_clamping_demo_description,
                    CameraClampingDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.cloud_styling_label,
                    com.example.common_ui.R.string.cloud_styling_description,
                    CloudBasedMapStylingDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.circle_demo_label,
                    com.example.common_ui.R.string.circle_demo_description,
                    CircleDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.data_driven_styling_label,
                    com.example.common_ui.R.string.data_driven_styling_description,
                    DataDrivenDatasetStylingActivity.class),
            new DemoDetails(com.example.common_ui.R.string.data_driven_boundaries_label,
                    com.example.common_ui.R.string.data_driven_boundaries_description,
                    DataDrivenBoundariesActivity.class),
            new DemoDetails(com.example.common_ui.R.string.events_demo_label,
                    com.example.common_ui.R.string.events_demo_description,
                    EventsDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.ground_overlay_demo_label,
                    com.example.common_ui.R.string.ground_overlay_demo_description,
                    GroundOverlayDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.indoor_demo_label,
                    com.example.common_ui.R.string.indoor_demo_description,
                    IndoorDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.layers_demo_label,
                    com.example.common_ui.R.string.layers_demo_description,
                    LayersDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.lite_demo_label,
                    com.example.common_ui.R.string.lite_demo_description,
                    LiteDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.lite_list_demo_label,
                    com.example.common_ui.R.string.lite_list_demo_description,
                    LiteListDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.location_source_demo_label,
                    com.example.common_ui.R.string.location_source_demo_description,
                    LocationSourceDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.map_in_pager_demo_label,
                    com.example.common_ui.R.string.map_in_pager_demo_description,
                    MapInPagerDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.map_color_scheme_demo_label,
                    com.example.common_ui.R.string.map_color_scheme_demo_description,
                    MapColorSchemeActivity.class),
            new DemoDetails(com.example.common_ui.R.string.marker_demo_label,
                    com.example.common_ui.R.string.marker_demo_description,
                    MarkerDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.marker_close_info_window_on_retap_demo_label,
                    com.example.common_ui.R.string.marker_close_info_window_on_retap_demo_description,
                    MarkerCloseInfoWindowOnRetapDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.multi_map_demo_label,
                    com.example.common_ui.R.string.multi_map_demo_description,
                    MultiMapDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.my_location_demo_label,
                    com.example.common_ui.R.string.my_location_demo_description,
                    MyLocationDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.options_demo_label,
                    com.example.common_ui.R.string.options_demo_description,
                    OptionsDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.polygon_demo_label,
                    com.example.common_ui.R.string.polygon_demo_description,
                    PolygonDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.polyline_demo_label,
                    com.example.common_ui.R.string.polyline_demo_description,
                    PolylineDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.programmatic_demo_label,
                    com.example.common_ui.R.string.programmatic_demo_description,
                    ProgrammaticDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.raw_map_view_demo_label,
                    com.example.common_ui.R.string.raw_map_view_demo_description,
                    RawMapViewDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.retain_map_demo_label,
                    com.example.common_ui.R.string.retain_map_demo_description,
                    RetainMapDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.save_state_demo_label,
                    com.example.common_ui.R.string.save_state_demo_description,
                    SaveStateDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.snapshot_demo_label,
                    com.example.common_ui.R.string.snapshot_demo_description,
                    SnapshotDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.split_street_view_panorama_and_map_demo_label,
                    com.example.common_ui.R.string.split_street_view_panorama_and_map_demo_description,
                    SplitStreetViewPanoramaAndMapDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.street_view_panorama_basic_demo_label,
                    com.example.common_ui.R.string.street_view_panorama_basic_demo_description,
                    StreetViewPanoramaBasicDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.street_view_panorama_events_demo_label,
                    com.example.common_ui.R.string.street_view_panorama_events_demo_description,
                    StreetViewPanoramaEventsDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.street_view_panorama_navigation_demo_label,
                    com.example.common_ui.R.string.street_view_panorama_navigation_demo_description,
                    StreetViewPanoramaNavigationDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.street_view_panorama_options_demo_label,
                    com.example.common_ui.R.string.street_view_panorama_options_demo_description,
                    StreetViewPanoramaOptionsDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.street_view_panorama_view_demo_label,
                    com.example.common_ui.R.string.street_view_panorama_view_demo_description,
                    StreetViewPanoramaViewDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.styled_map_demo_label,
                    com.example.common_ui.R.string.styled_map_demo_description,
                    StyledMapDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.tags_demo_label,
                    com.example.common_ui.R.string.tags_demo_description,
                    TagsDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.tile_coordinate_demo_label,
                    com.example.common_ui.R.string.tile_coordinate_demo_description,
                    TileCoordinateDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.tile_overlay_demo_label,
                    com.example.common_ui.R.string.tile_overlay_demo_description,
                    TileOverlayDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.ui_settings_demo_label,
                    com.example.common_ui.R.string.ui_settings_demo_description,
                    UiSettingsDemoActivity.class),
            new DemoDetails(com.example.common_ui.R.string.visible_region_demo_label,
                    com.example.common_ui.R.string.visible_region_demo_description,
                    VisibleRegionDemoActivity.class),
    };
}
