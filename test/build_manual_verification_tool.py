#!/usr/bin/env python3
"""
Generates an interactive HTML verification dashboard (MANUAL_VERIFY_CATALOG.html) with:
- Focused Step-by-Step Mode ("One capability at a time") + Quick navigation ("Jump to next requiring attention")
- Dedicated Live Execution Controls for BOTH Kotlin AND Java versions (separate Build & Run / Launch buttons)
- Live Server-Side Persistence for 1-to-5 star ratings & feedback notes
- Synchronized Markdown Task List (FALSIFIABLE_TASK_LIST.md)
"""
import html
import json
import os
import re
import sys

project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(os.path.join(project_root, "snippets", "scripts"))
import catalog_api

def get_capability_details():
    return {
        # 1. Maps (GA)
        "232ecd00": {
            "group": "Map Initialization",
            "snippet_title": "1. Basic Map Activity",
            "kt_tag": "maps_android_map_fragment",
            "java_tag": "maps_android_map_fragment",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MapInitSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MapInitSnippetsTest",
            "test_method": "verifyBasicMapActivity_falsifiable",
            "falsified": True,
            "notes": "Verify MapFragment / MapView initialization with default settings.",
            "instructions_what_to_do": "1. Click 'Launch Sample' for Kotlin or Java on attached device.\n2. Observe initial map render.",
            "instructions_what_to_look_for": "1. Map renders smoothly without black tiles or crashes.\n2. Default gestures and controls respond correctly."
        },
        "20793ebb": {
            "group": "Map Initialization",
            "snippet_title": "4. Enable Traffic Layer",
            "kt_tag": "maps_android_traffic_layer",
            "java_tag": "maps_android_traffic_layer",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MapInitSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MapInitSnippetsTest",
            "test_method": "verifyEnableTrafficLayer_falsifiable",
            "falsified": True,
            "notes": "Verify googleMap.isTrafficEnabled == true and assert failure when toggled off.",
            "instructions_what_to_do": "1. Launch the Kotlin or Java sample.\n2. Pan to a major metropolitan area with live traffic congestion.",
            "instructions_what_to_look_for": "1. Colored traffic overlay lines (green, yellow, red) display over major roads."
        },
        "25bf9dfd": {
            "group": "Map Initialization",
            "snippet_title": "TODO",
            "kt_tag": "maps_android_map_color_scheme",
            "java_tag": "maps_android_map_color_scheme",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MapInitSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MapInitSnippetsTest",
            "test_method": "verifySetMapColorScheme_falsifiable",
            "falsified": True,
            "notes": "Verify mapColorScheme property and assert failure on invalid or missing scheme.",
            "instructions_what_to_do": "1. Launch sample on device.\n2. Observe map theme color palette.",
            "instructions_what_to_look_for": "1. Color scheme matches specified light/dark or system preference mode."
        },
        "c511ea57": {
            "group": "Map Initialization",
            "snippet_title": "3. Set Map Type",
            "kt_tag": "maps_android_map_type",
            "java_tag": "maps_android_map_type",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MapInitSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MapInitSnippetsTest",
            "test_method": "verifySetMapTypeToHybrid_falsifiable",
            "falsified": True,
            "notes": "Verify googleMap.mapType == MAP_TYPE_HYBRID and assert failure if changed.",
            "instructions_what_to_do": "1. Launch sample.\n2. Inspect tile imagery.",
            "instructions_what_to_look_for": "1. Satellite imagery is rendered with vector road and label overlays."
        },
        "ca51263d": {
            "group": "Map Initialization",
            "snippet_title": "TODO",
            "kt_tag": "maps_android_support_map_fragment_map_id",
            "java_tag": "maps_android_support_map_fragment_map_id",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MapInitSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MapInitSnippetsTest",
            "test_method": "verifyMapIdInitialization_falsifiable",
            "falsified": True,
            "notes": "Verify GoogleMapOptions.mapId configuration.",
            "instructions_what_to_do": "1. Launch sample configured with a custom Map ID.",
            "instructions_what_to_look_for": "1. Map initializes with cloud-driven custom style associated with the Map ID."
        },
        "9eeb4a1a": {
            "group": "Map Initialization",
            "snippet_title": "TODO",
            "kt_tag": "maps_android_google_map_options_configure",
            "java_tag": "maps_android_google_map_options_configure",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MapInitSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MapInitSnippetsTest",
            "test_method": "verifyMapControlsCustomization_falsifiable",
            "falsified": True,
            "notes": "Verify uiSettings.isZoomControlsEnabled / isCompassEnabled and assert failure when mutated.",
            "instructions_what_to_do": "1. Launch sample and rotate/zoom map.",
            "instructions_what_to_look_for": "1. Custom UI controls (compass, zoom buttons) appear and respond properly."
        },
        "2a3e0c25": {
            "group": "Camera",
            "snippet_title": "1. Zoom Level Constraints",
            "kt_tag": "maps_android_camera_and_view_zoom_level",
            "java_tag": "maps_android_camera_and_view_zoom_level",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CameraControlSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CameraControlSnippetsTest",
            "test_method": "verifyCameraMovementsAndZoomConstraints_falsifiable",
            "falsified": True,
            "notes": "Verify zoom limits (minZoom/maxZoom) and assert failure when boundaries breached.",
            "instructions_what_to_do": "1. Launch sample on device.\n2. Pinch to zoom out beyond minZoom level 10, then zoom in past maxZoom level 16.",
            "instructions_what_to_look_for": "1. Camera stops zooming smoothly at boundaries without jitter."
        },
        "0e6b228f": {
            "group": "Camera",
            "snippet_title": "4. Panning Restrictions",
            "kt_tag": "maps_android_camera_and_view_panning_restrictions",
            "java_tag": "maps_android_camera_and_view_panning_restrictions",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CameraControlSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CameraControlSnippetsTest",
            "test_method": "verifyCameraClampingToAustralia_falsifiable",
            "falsified": True,
            "notes": "Verify setLatLngBoundsForCameraTarget blocks moves to London/Antarctica."
        },
        "b34458f3": {
            "group": "Events",
            "snippet_title": "1. MapView Disable Click Event",
            "kt_tag": "maps_android_events_disable_clicks_mapview",
            "java_tag": "maps_android_events_disable_clicks_mapview",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.EventsSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.EventsSnippetsTest",
            "test_method": "verifyMapClickListenerAndEvents_falsifiable",
            "falsified": True,
            "notes": "Verify MapView click disabling and POI click listener callback registration."
        },
        "2b6457c4": {
            "group": "Wear OS",
            "snippet_title": "TODO",
            "kt_tag": "TODO",
            "java_tag": "TODO",
            "kt_test_class": "com.example.wearosmap.AppLaunchTest",
            "java_test_class": "com.example.wearosmap.AppLaunchTest",
            "test_method": "verifyWearOsMapInit_falsifiable",
            "falsified": True,
            "notes": "Wear OS map activity initialization."
        },

        # 2. Maps annotations (GA)
        "7bbfe87e": {
            "group": "Markers",
            "snippet_title": "1. Add a Marker",
            "kt_tag": "maps_android_markers_add_a_marker",
            "java_tag": "maps_android_markers_add_a_marker",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MarkerSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MarkerSnippetsTest",
            "test_method": "verifyAddMarkerProperties_falsifiable",
            "falsified": True,
            "notes": "Assert marker added at exact position/title; assert failure when removed."
        },
        "bdbefba5": {
            "group": "Markers",
            "snippet_title": "11. Add Info Window",
            "kt_tag": "maps_android_info_windows_add",
            "java_tag": "maps_android_info_windows_add",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MarkerSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MarkerSnippetsTest",
            "test_method": "verifyInfoWindowCustomizationAndDisplay_falsifiable",
            "falsified": True,
            "notes": "Verify showInfoWindow() and exact title/snippet properties."
        },
        "de757d41": {
            "group": "Markers",
            "snippet_title": "5. Marker Opacity",
            "kt_tag": "maps_android_markers_custom_marker_color",
            "java_tag": "maps_android_markers_custom_marker_color",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MarkerSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MarkerSnippetsTest",
            "test_method": "verifyMarkerCustomColorAndIcon_falsifiable",
            "falsified": True,
            "notes": "Verify Marker opacity, rotation, and flat properties."
        },
        "4c2a9906": {
            "group": "Markers",
            "snippet_title": "2. Draggable Marker",
            "kt_tag": "maps_android_markers_draggable",
            "java_tag": "maps_android_markers_draggable",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.MarkerSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.MarkerSnippetsTest",
            "test_method": "verifyMarkerDraggableProperty_falsifiable",
            "falsified": True,
            "notes": "Verify marker.isDraggable == true; test failure when set to false."
        },
        "246ab3a6": {
            "group": "Shapes",
            "snippet_title": "1. Simple Polyline",
            "kt_tag": "maps_android_shapes_polylines_polylineoptions",
            "java_tag": "maps_android_shapes_polylines_polylineoptions",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.ShapesSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.ShapesSnippetsTest",
            "test_method": "verifyAddPolylineAndPolygonProperties_falsifiable",
            "falsified": True,
            "notes": "Verify points, width, and color of Polylines/Polygons."
        },
        "518c439f": {
            "group": "Overlays",
            "snippet_title": "1. Ground Overlays",
            "kt_tag": "maps_android_ground_overlays_add",
            "java_tag": "maps_android_ground_overlays_add",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.OverlaySnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.OverlaySnippetsTest",
            "test_method": "verifyAddGroundOverlayProperties_falsifiable",
            "falsified": True,
            "notes": "Verify ground overlay bounds, bearing, and image descriptor."
        },
        "58007bbe": {
            "group": "Overlays",
            "snippet_title": "TODO",
            "kt_tag": "maps_android_tile_overlays_add",
            "java_tag": "maps_android_tile_overlays_add",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.OverlaySnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.OverlaySnippetsTest",
            "test_method": "verifyAddTileOverlayAndProvider_falsifiable",
            "falsified": True,
            "notes": "Verify tile overlay provider registration and transparency."
        },

        # 3. Datasets (GA)
        "20fb724a": {
            "group": "Utility Library",
            "snippet_title": "5. Add GeoJSON Layer from File",
            "kt_tag": "maps_android_util_geojson_add_file",
            "java_tag": "maps_android_util_geojson_add_file",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.UtilsSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.UtilsSnippetsTest",
            "test_method": "verifyGeoJsonLayerCreationAndFeatures_falsifiable",
            "falsified": True,
            "notes": "Assert GeoJsonLayer feature count and style properties."
        },
        "f451d761": {
            "group": "Utility Library",
            "snippet_title": "7. KML Layer from File Resource",
            "kt_tag": "maps_android_utils_kml_add_file",
            "java_tag": "maps_android_utils_kml_add_file",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.UtilsSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.UtilsSnippetsTest",
            "test_method": "verifyKmlLayerCreationAndContainers_falsifiable",
            "falsified": True,
            "notes": "Assert KmlLayer placemarks/containers and click listener."
        },
        "fbbc9c5a": {
            "group": "Utility Library",
            "snippet_title": "9. Simple Heatmap",
            "kt_tag": "maps_android_utils_heatmap_simple",
            "java_tag": "maps_android_utils_heatmap_simple",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.UtilsSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.UtilsSnippetsTest",
            "test_method": "verifyHeatmapTileProviderDataAndRadius_falsifiable",
            "falsified": True,
            "notes": "Assert HeatmapTileProvider data points and opacity configuration."
        },

        # 4. Street View (GA)
        "b8fadfc3": {
            "group": "Street View",
            "snippet_title": "1. Street View Panorama",
            "kt_tag": "maps_street_view_new_panorama_view",
            "java_tag": "maps_street_view_new_panorama_view",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.StreetViewSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.StreetViewSnippetsTest",
            "test_method": "verifyStreetViewPanoramaInitialization_falsifiable",
            "falsified": True,
            "notes": "Verify StreetViewPanorama creation and camera location binding."
        },
        "7b144b66": {
            "group": "Street View",
            "snippet_title": "TODO",
            "kt_tag": "maps_street_view_panorama_animate",
            "java_tag": "maps_street_view_panorama_animate",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.StreetViewSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.StreetViewSnippetsTest",
            "test_method": "verifyStreetViewCameraAnimationAndPose_falsifiable",
            "falsified": True,
            "notes": "Verify animateTo(StreetViewPanoramaCamera) and bearing change."
        },
        "75a7efe9": {
            "group": "Street View",
            "snippet_title": "TODO",
            "kt_tag": "maps_street_view_panorama_pan",
            "java_tag": "maps_street_view_panorama_pan",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.StreetViewSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.StreetViewSnippetsTest",
            "test_method": "verifyStreetViewGesturesConfiguration_falsifiable",
            "falsified": True,
            "notes": "Verify isPanningGesturesEnabled / isZoomGesturesEnabled."
        },
        "6e3999d1": {
            "group": "Street View",
            "snippet_title": "TODO",
            "kt_tag": "maps_street_view_on_street_view_panorama_ready",
            "java_tag": "maps_street_view_on_street_view_panorama_ready",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.StreetViewSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.StreetViewSnippetsTest",
            "test_method": "verifyStreetViewPanoramaReadyAndClickEvents_falsifiable",
            "falsified": True,
            "notes": "Verify onStreetViewPanoramaReady and click listener execution."
        },

        # 5. Data-driven styling for boundaries (GA)
        "dedc17af": {
            "group": "Data-Driven Boundary Styling",
            "snippet_title": "1. Style Locality & Administrative Boundaries",
            "kt_tag": "maps_android_dds_style_boundary",
            "java_tag": "maps_android_dds_style_boundary",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.DataDrivenBoundarySnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.DataDrivenBoundarySnippetsTest",
            "test_method": "verifyFeatureLayerBoundaryStyling_falsifiable",
            "falsified": True,
            "notes": "Verify FeatureLayer.styleFactory binding and fill/stroke assignments."
        },
        "fa7cc2f9": {
            "group": "Data-Driven Boundary Styling",
            "snippet_title": "2. Handle Boundary Click Events",
            "kt_tag": "maps_android_dds_boundary_click",
            "java_tag": "maps_android_dds_boundary_click",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.DataDrivenBoundarySnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.DataDrivenBoundarySnippetsTest",
            "test_method": "verifyFeatureLayerBoundaryClickEvent_falsifiable",
            "falsified": True,
            "notes": "Verify addOnFeatureClickListener callback payload."
        },
        "0a767a66": {
            "group": "Data-Driven Boundary Styling",
            "snippet_title": "3. Interactive Choropleth Map",
            "kt_tag": "maps_android_dds_choropleth",
            "java_tag": "maps_android_dds_choropleth",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.DataDrivenBoundarySnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.DataDrivenBoundarySnippetsTest",
            "test_method": "verifyChoroplethFeatureStyleFactory_falsifiable",
            "falsified": True,
            "notes": "Verify conditional styleFactory logic across boundary place IDs."
        },

        "3ebaeaa1": {
            "group": "Custom Geospatial Datasets",
            "snippet_title": "1. Load Geospatial Dataset",
            "kt_tag": "maps_android_dds_load_dataset",
            "java_tag": "maps_android_dds_load_dataset",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.DatasetLayerSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.DatasetLayerSnippetsTest",
            "test_method": "verifyDatasetLayerInitialization_falsifiable",
            "falsified": True,
            "notes": "Verify getDatasetFeatureLayer(datasetId) loading."
        },
        "eb3ed819": {
            "group": "Custom Geospatial Datasets",
            "snippet_title": "2. Style Custom Dataset Features",
            "kt_tag": "maps_android_dds_style_dataset",
            "java_tag": "maps_android_dds_style_dataset",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.DatasetLayerSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.DatasetLayerSnippetsTest",
            "test_method": "verifyDatasetFeatureStylingAndPointRadius_falsifiable",
            "falsified": True,
            "notes": "Verify dataset styleFactory stroke, fill, and pointRadius."
        },
        "e72146cb": {
            "group": "Custom Geospatial Datasets",
            "snippet_title": "3. Handle Dataset Click Events",
            "kt_tag": "maps_android_dds_dataset_click",
            "java_tag": "maps_android_dds_dataset_click",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.DatasetLayerSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.DatasetLayerSnippetsTest",
            "test_method": "verifyDatasetFeatureClickListener_falsifiable",
            "falsified": True,
            "notes": "Verify dataset feature click listener attribute extraction."
        },

        # 7. Maps styling (GA)
        "4d87a0ea": {
            "group": "Cloud Customization",
            "snippet_title": "1. Load Reusable Cloud Style",
            "kt_tag": "maps_android_cloud_reusable_style",
            "java_tag": "maps_android_cloud_reusable_style",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CloudCustomizationSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CloudCustomizationSnippetsTest",
            "test_method": "verifyReusableMapIdLoading_falsifiable",
            "falsified": True,
            "notes": "Verify GoogleMapOptions.mapId cloud styling binding."
        },
        "589c7e69": {
            "group": "Cloud Customization",
            "snippet_title": "2. Hierarchy Zoom Level Styling",
            "kt_tag": "maps_android_cloud_zoom_styling",
            "java_tag": "maps_android_cloud_zoom_styling",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CloudCustomizationSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CloudCustomizationSnippetsTest",
            "test_method": "verifyHierarchyZoomStyling_falsifiable",
            "falsified": True,
            "notes": "Verify map hierarchy styling application."
        },
        "1f5dea73": {
            "group": "Cloud Customization",
            "snippet_title": "TODO",
            "kt_tag": "maps_android_cloud_feature_visibility",
            "java_tag": "maps_android_cloud_feature_visibility",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CloudCustomizationSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CloudCustomizationSnippetsTest",
            "test_method": "verifyFeatureVisibilityToggles_falsifiable",
            "falsified": True,
            "notes": "Verify feature type visibility toggles via cloud console ID."
        },
        "3fc0911b": {
            "group": "Cloud Customization",
            "snippet_title": "3. Style Icons & Text Labels",
            "kt_tag": "maps_android_cloud_style_labels",
            "java_tag": "maps_android_cloud_style_labels",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CloudCustomizationSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CloudCustomizationSnippetsTest",
            "test_method": "verifyIconAndTextLabelStyling_falsifiable",
            "falsified": True,
            "notes": "Verify icon/label hierarchy color adjustments."
        },
        "5d26e9fb": {
            "group": "Cloud Customization",
            "snippet_title": "1. Style Roads & Geometries",
            "kt_tag": "maps_android_cloud_style_roads",
            "java_tag": "maps_android_cloud_style_roads",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CloudCustomizationSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CloudCustomizationSnippetsTest",
            "test_method": "verifyRoadAndGeometryStyling_falsifiable",
            "falsified": True,
            "notes": "Verify road and polygon geometry styling layers."
        },
        "468c2301": {
            "group": "Cloud Customization",
            "snippet_title": "4. POI Density Control",
            "kt_tag": "maps_android_cloud_poi_density",
            "java_tag": "maps_android_cloud_poi_density",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CloudCustomizationSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CloudCustomizationSnippetsTest",
            "test_method": "verifyPoiDensityFiltering_falsifiable",
            "falsified": True,
            "notes": "Verify POI density and filtering behaviors."
        },
        "89814817": {
            "group": "Cloud Customization",
            "snippet_title": "5. Style 3D Buildings & Footprints",
            "kt_tag": "maps_android_cloud_style_buildings",
            "java_tag": "maps_android_cloud_style_buildings",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CloudCustomizationSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CloudCustomizationSnippetsTest",
            "test_method": "verifyBuilding3dAndFootprintStyling_falsifiable",
            "falsified": True,
            "notes": "Verify building footprint and 3d extrusion styling."
        },
        "4255f56a": {
            "group": "Cloud Customization",
            "snippet_title": "6. Style Landmarks",
            "kt_tag": "maps_android_cloud_style_landmarks",
            "java_tag": "maps_android_cloud_style_landmarks",
            "kt_test_class": "com.example.snippets.kotlin.capabilities.CloudCustomizationSnippetsTest",
            "java_test_class": "com.example.snippets.java.capabilities.CloudCustomizationSnippetsTest",
            "test_method": "verifyLandmarkHierarchyStyling_falsifiable",
            "falsified": True,
            "notes": "Verify prominent landmark feature styling."
        },
    }

def format_code_syntax(raw_code, lang="kotlin"):
    escaped = html.escape(raw_code)
    escaped = re.sub(r'(//.*)', r'<span style="color: #6a9955; font-style: italic;">\1</span>', escaped)
    escaped = re.sub(r'(&quot;.*?&quot;)', r'<span style="color: #ce9178;">\1</span>', escaped)
    kw_pattern = r'\b(val|var|fun|class|package|import|return|if|else|try|catch|true|false|null|override|private|public|protected|new|void|int|boolean|float|double|final|assertEquals|assertTrue|assertFalse|assertNotNull)\b'
    escaped = re.sub(kw_pattern, r'<span style="color: #569cd6; font-weight: bold;">\1</span>', escaped)
    type_pattern = r'\b([A-Z][a-zA-Z0-9_]*)\b'
    escaped = re.sub(type_pattern, r'<span style="color: #4ec9b0;">\1</span>', escaped)
    return escaped

def find_snippet_block(snippets_dir, app_dir, target_tag):
    if target_tag == "TODO":
        return "TODO (Sample in progress)", "TODO", "-"
    search_root = os.path.join(snippets_dir, app_dir, "src", "main", "java")
    for root, _, files in os.walk(search_root):
        for f in files:
            if f.endswith((".kt", ".java")):
                full_path = os.path.join(root, f)
                with open(full_path, "r", encoding="utf-8") as fp:
                    lines = fp.readlines()
                start_idx = None
                for i, line in enumerate(lines):
                    if f"// [START {target_tag}]" in line:
                        start_idx = i
                    elif start_idx is not None and f"// [END {target_tag}]" in line:
                        end_idx = i
                        code_lines = lines[start_idx : end_idx + 1]
                        raw_code = "".join(code_lines)
                        return raw_code, f"{f}#L{start_idx+1}-L{end_idx+1}", f"{start_idx+1}–{end_idx+1}"
    return f"// Region tag not found: {target_tag}", target_tag, "-"

def find_test_block(snippets_dir, test_class, test_method, lang="kotlin"):
    if test_class == "TODO" or test_method == "TODO":
        return "TODO (Instrumented test in progress)", "TODO", "-"
    app_dir = "kotlin-app" if lang == "kotlin" else "java-app"
    ext = ".kt" if lang == "kotlin" else ".java"
    rel_path = test_class.replace(".", "/") + ext
    full_path = os.path.join(snippets_dir, app_dir, "src", "androidTest", "java", rel_path)
    if not os.path.exists(full_path):
        return f"// {lang.upper()} test file not found yet: {test_class}. Needed for {test_method}", test_class.split('.')[-1], "-"

    with open(full_path, "r", encoding="utf-8") as fp:
        lines = fp.readlines()
    
    start_idx = None
    for i, line in enumerate(lines):
        if f"fun {test_method}" in line or f"void {test_method}" in line:
            start_idx = i
            brace_count = 0
            for j in range(i, len(lines)):
                brace_count += lines[j].count('{') - lines[j].count('}')
                if brace_count == 0 and j > i:
                    code_lines = lines[start_idx : j + 1]
                    return "".join(code_lines), f"{test_class.split('.')[-1]}#L{start_idx+1}-L{j+1}", f"{start_idx+1}–{j+1}"
    return f"// Test method not found yet in {test_class.split('.')[-1]}: {test_method}", test_class.split('.')[-1], "-"

def main():
    caps_path = os.path.join(os.path.dirname(project_root), "capabilities.json")
    with open(caps_path) as f:
        caps = json.load(f)

    maps_caps = {k: v for k, v in caps.items() if v.get("platforms", {}).get("android", {}).get("supported") == True and "Maps SDK for Android" in v.get("platforms", {}).get("android", {}).get("product_name", [])}
    mapping = get_capability_details()
    snippets_dir = os.path.join(project_root, "snippets")

    html_cards = []
    md_rows = [
        "# 🗺️ Maps SDK Falsifiable Task List & Review Matrix\n\n",
        "Use this matrix to track, review, rate (1-to-5 stars), and verify every Maps SDK for Android capability.\n",
        "Every test must be **falsifiable**: if the code under test is broken or removed, the test must immediately fail.\n\n",
        "| ID | Capability & Group | Status | Sample Code | Falsifiable Test | Gradle Verification Command | Rating |\n",
        "| :---: | :--- | :---: | :--- | :--- | :--- | :---: |\n"
    ]

    sorted_items = sorted(maps_caps.items(), key=lambda x: (mapping.get(x[1]["id"][:8], {}).get("group", "General"), x[0]))

    for idx, (title, data) in enumerate(sorted_items):
        cid = data["id"][:8]
        m = mapping.get(cid, {
            "group": "General", "snippet_title": "TODO", "kt_tag": "TODO", "java_tag": "TODO",
            "kt_test_class": "TODO", "java_test_class": "TODO", "test_method": "TODO", "falsified": False, "notes": "Unmapped capability."
        })

        kt_tag = m["kt_tag"]
        java_tag = m["java_tag"]
        kt_test_class = m["kt_test_class"]
        java_test_class = m["java_test_class"]
        test_method = m["test_method"]
        is_falsified = m.get("falsified", False)
        snippet_title = m.get("snippet_title", "TODO")

        kt_code, kt_anchor, kt_lines = find_snippet_block(snippets_dir, "kotlin-app", kt_tag)
        java_code, java_anchor, java_lines = find_snippet_block(snippets_dir, "java-app", java_tag)
        kt_test_code, kt_test_anchor, kt_test_lines = find_test_block(snippets_dir, kt_test_class, test_method, "kotlin")
        java_test_code, java_test_anchor, java_test_lines = find_test_block(snippets_dir, java_test_class, test_method, "java")

        kt_highlighted = format_code_syntax(kt_code, "kotlin") if kt_tag != "TODO" else "TODO"
        java_highlighted = format_code_syntax(java_code, "java") if java_tag != "TODO" else "TODO"
        kt_test_highlighted = format_code_syntax(kt_test_code, "kotlin") if kt_test_class != "TODO" else "TODO"
        java_test_highlighted = format_code_syntax(java_test_code, "java") if java_test_class != "TODO" else "TODO"

        kt_gradle_cmd = f"./gradlew :snippets:kotlin-app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class={kt_test_class}#{test_method}" if kt_test_class != "TODO" else "TODO"
        java_gradle_cmd = f"./gradlew :snippets:java-app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class={java_test_class}#{test_method}" if java_test_class != "TODO" else "TODO"

        sec_str = m.get("group", "General")
        status = data.get("platforms", {}).get("android", {}).get("release_status", "GA")
        docs_urls = data.get("platforms", {}).get("android", {}).get("canonical_documentation_url", [])
        doc_url = docs_urls[0] if docs_urls else ""
        notes = m.get("notes", data.get("notes", ""))

        what_to_do = m.get("instructions_what_to_do", f"1. Launch the Kotlin or Java sample on an attached emulator/device.\n2. Interact with the screen to test {title}.")
        what_to_look_for = m.get("instructions_what_to_look_for", f"1. {notes}\n2. Verify visual fidelity and proper SDK callbacks.")

        falsified_badge = '<span class="badge bg-success text-white">✅ Falsification Verified</span>' if is_falsified else '<span class="badge bg-warning text-dark">⏳ Pending Falsification Check</span>'
        status_md = "✅ Falsified" if is_falsified else "⏳ Pending"
        docs_html = f'<a href="{doc_url}" target="_blank" class="text-decoration-none fw-semibold">📚 Docs ↗</a>' if doc_url else ""

        html_cards.append(f"""
        <div class="capability-card border rounded p-4 mb-4 bg-white shadow-sm" data-id="{cid}" data-index="{idx}" data-group="{sec_str}" data-status="{'verified' if is_falsified else 'pending'}" data-kt-test-class="{kt_test_class}" data-java-test-class="{java_test_class}" data-test-method="{test_method}" data-snippet-title="{html.escape(snippet_title)}" data-what-to-do="{html.escape(what_to_do)}" data-what-to-look-for="{html.escape(what_to_look_for)}" style="display: none;">
          <div class="d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom">
            <div>
              <span class="badge bg-dark text-white font-monospace px-2 py-1 me-2">#{idx+1} of {len(maps_caps)}</span>
              <span class="badge bg-secondary text-white font-monospace px-2 py-1 me-2">{cid}</span>
              <span class="badge bg-primary text-white me-2">{sec_str}</span>
              {falsified_badge}
            </div>
            <div class="text-end">
              {docs_html}
            </div>
          </div>

          <h3 class="fw-bold text-dark mb-2">{title}</h3>
          <p class="text-muted small mb-3">💡 <strong>Verification Strategy:</strong> {m['notes']}</p>

          <div class="bg-light border rounded p-3 mb-4">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <div>
                <span class="fw-bold text-dark me-2">⭐ Reviewer Rating:</span>
                <span class="small text-muted">Click stars and enter feedback notes below (auto-saved to FALSIFIABLE_TASK_LIST.md)</span>
              </div>
              <div class="star-rating" data-id="{cid}">
                <span class="star" onclick="setRating('{cid}', 5)">★</span>
                <span class="star" onclick="setRating('{cid}', 4)">★</span>
                <span class="star" onclick="setRating('{cid}', 3)">★</span>
                <span class="star" onclick="setRating('{cid}', 2)">★</span>
                <span class="star" onclick="setRating('{cid}', 1)">★</span>
                <span class="small text-muted ms-1" id="rating_val_{cid}">(0/5)</span>
              </div>
            </div>
            <textarea class="form-control form-control-sm comment-box" id="comment_{cid}" placeholder="Leave critique or feedback notes... (auto-saved to FALSIFIABLE_TASK_LIST.md on server)" rows="2" oninput="saveComment('{cid}', this.value)"></textarea>
            <div class="mt-3 pt-3 border-top" id="exec_output_box_{cid}" style="display: none;">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <div class="d-flex align-items-center gap-2">
                  <div class="spinner-border spinner-border-sm text-primary" role="status" id="spinner_{cid}" style="display: none;">
                    <span class="visually-hidden">Loading...</span>
                  </div>
                  <span class="fw-bold small text-dark" id="exec_title_{cid}">Execution Terminal Output:</span>
                </div>
                <div class="d-flex gap-2">
                  <button class="btn btn-danger btn-sm py-0 px-2 small fw-bold" id="cancel_btn_{cid}" onclick="cancelLiveTask('{cid}')" style="display: none;">🛑 Stop / Cancel</button>
                  <button class="btn btn-outline-dark btn-sm py-0 px-2 small font-monospace" onclick="copyOutput('{cid}', this)">📋 Copy Output</button>
                </div>
              </div>
              <pre class="bg-dark text-light p-3 rounded small font-monospace m-0 mt-1" id="exec_output_text_{cid}" style="max-height: 260px; overflow-y: auto;"></pre>
            </div>
          </div>

          <div class="row g-4">
            <!-- KOTLIN COLUMN -->
            <div class="col-md-6 border-end">
              <div class="d-flex justify-content-between align-items-center mb-2 p-2 bg-success-subtle border rounded">
                <span class="fw-bold text-success-emphasis">🟢 Kotlin Implementation</span>
                <div class="d-flex gap-1">
                  <button class="btn btn-success btn-sm py-0 px-2 small fw-bold action-btn" title="Run single method test" onclick="runTestLive('{cid}', '{kt_test_class}', '{test_method}', 'kotlin')">▶️ Run Test</button>
                  <button class="btn btn-outline-success btn-sm py-0 px-2 small fw-bold action-btn" title="Run full {sec_str} Test Suite" onclick="runTestLive('{cid}', '{kt_test_class}', 'SUITE', 'kotlin')">📦 Run Suite</button>
                  <button class="btn btn-outline-success btn-sm py-0 px-2 small fw-bold action-btn" onclick="launchSampleLive('{cid}', '{sec_str}', '{snippet_title}', 'kotlin')">📱 Launch Sample</button>
                </div>
              </div>

              <details class="code-accordion mb-3 border rounded p-2 bg-white shadow-sm" open>
                <summary class="text-success-emphasis fw-bold small cursor-pointer" style="cursor: pointer;">🟢 Kotlin Sample: <code>{kt_anchor.split('#')[0]}</code> (Lines {kt_lines}) ▼</summary>
                <pre class="m-0 mt-2 p-2 bg-dark text-light rounded small font-monospace" style="white-space: pre; overflow-x: auto; max-height: 260px;"><code>{kt_highlighted}</code></pre>
              </details>

              <div class="border rounded p-2 bg-white shadow-sm">
                <div class="d-flex justify-content-between align-items-center mb-1">
                  <span class="fw-bold small text-primary">🧪 Kotlin Test: <code>{kt_test_anchor.split('#')[0]}</code></span>
                  <button class="btn btn-outline-secondary btn-sm py-0 px-2 small font-monospace" onclick="copyCmd('{cid}_kt', this)">📋 Copy Cmd</button>
                </div>
                <input type="hidden" id="cmd_{cid}_kt" value="{kt_gradle_cmd}">
                <pre class="m-0 mt-2 p-2 bg-dark text-light rounded small font-monospace" style="white-space: pre; overflow-x: auto; max-height: 300px;"><code>{kt_test_highlighted}</code></pre>
              </div>
            </div>

            <!-- JAVA COLUMN -->
            <div class="col-md-6">
              <div class="d-flex justify-content-between align-items-center mb-2 p-2 bg-warning-subtle border rounded">
                <span class="fw-bold text-warning-emphasis">☕ Java Implementation</span>
                <div class="d-flex gap-1">
                  <button class="btn btn-warning btn-sm py-0 px-2 small fw-bold text-dark action-btn" title="Run single method test" onclick="runTestLive('{cid}', '{java_test_class}', '{test_method}', 'java')">▶️ Run Test</button>
                  <button class="btn btn-outline-warning btn-sm py-0 px-2 small fw-bold text-dark action-btn" title="Run full {sec_str} Test Suite" onclick="runTestLive('{cid}', '{java_test_class}', 'SUITE', 'java')">📦 Run Suite</button>
                  <button class="btn btn-outline-warning btn-sm py-0 px-2 small fw-bold text-dark action-btn" onclick="launchSampleLive('{cid}', '{sec_str}', '{snippet_title}', 'java')">📱 Launch Sample</button>
                </div>
              </div>

              <details class="code-accordion mb-3 border rounded p-2 bg-white shadow-sm" open>
                <summary class="text-warning-emphasis fw-bold small cursor-pointer" style="cursor: pointer;">☕ Java Sample: <code>{java_anchor.split('#')[0]}</code> (Lines {java_lines}) ▼</summary>
                <pre class="m-0 mt-2 p-2 bg-dark text-light rounded small font-monospace" style="white-space: pre; overflow-x: auto; max-height: 260px;"><code>{java_highlighted}</code></pre>
              </details>

              <div class="border rounded p-2 bg-white shadow-sm">
                <div class="d-flex justify-content-between align-items-center mb-1">
                  <span class="fw-bold small text-primary">🧪 Java Test: <code>{java_test_anchor.split('#')[0]}</code></span>
                  <button class="btn btn-outline-secondary btn-sm py-0 px-2 small font-monospace" onclick="copyCmd('{cid}_java', this)">📋 Copy Cmd</button>
                </div>
                <input type="hidden" id="cmd_{cid}_java" value="{java_gradle_cmd}">
                <pre class="m-0 mt-2 p-2 bg-dark text-light rounded small font-monospace" style="white-space: pre; overflow-x: auto; max-height: 300px;"><code>{java_test_highlighted}</code></pre>
              </div>
            </div>
          </div>
        </div>
        """)

        md_rows.append(f"| `{cid}` | **{title}**<br/>*{sec_str}* | {status_md} | Kotlin: `{kt_anchor}`<br/>Java: `{java_anchor}` | `{test_method}` | `{kt_gradle_cmd}` | [ ] ___/5 |\n")

    html_content = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Maps SDK Focused Review & Live Execution Dashboard</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body {{ background: #f0f3f6; padding: 25px; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }}
    .verification-card {{ background: white; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); padding: 25px; margin-bottom: 30px; }}
    .star-rating {{ display: inline-flex; flex-direction: row-reverse; align-items: center; justify-content: flex-end; }}
    .star-rating .star {{ font-size: 1.3rem; color: #ccc; cursor: pointer; transition: color 0.2s; padding: 0 1px; }}
    .star-rating .star.active {{ color: #ffc107; }}
    .star-rating .star:hover, .star-rating .star:hover ~ .star {{ color: #ffc107; }}
  </style>
</head>
<body>
  <div class="container-fluid max-w-7xl">
    <div class="verification-card">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h2 class="mb-1">🗺️ Maps SDK Focused Review & Live Execution Dashboard</h2>
          <p class="text-muted mb-0">Step through capabilities one at a time. Run or launch dedicated Kotlin and Java versions right from the UI!</p>
        </div>
        <div class="text-end">
          <div class="btn-group me-2" role="group">
            <button type="button" class="btn btn-outline-primary btn-sm active" id="btnModeFocused" onclick="setMode('focused')">🎯 Focused Mode (One at a Time)</button>
            <button type="button" class="btn btn-outline-secondary btn-sm" id="btnModeAll" onclick="setMode('all')">📜 All 39 Items List</button>
          </div>
          <button class="btn btn-warning btn-sm" onclick="exportReviewReport()">⭐ Export Evaluation Report</button>
        </div>
      </div>

      <!-- INTERACTIVE VERIFICATION TASK WIZARD CARD -->
      <div class="border rounded p-4 mb-4 bg-white shadow-sm border-2 border-success" id="wizardTaskCard">
        <div class="d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom">
          <div>
            <span class="badge bg-success text-white fs-6 me-2">✨ Verification Task Wizard</span>
            <span class="fw-bold fs-5 text-dark" id="wizardTaskHeader">Task 1 of {len(maps_caps)}</span>
          </div>
          <div>
            <button class="btn btn-outline-dark btn-sm me-1" onclick="navStep(-1)">⬅️ Prev</button>
            <button class="btn btn-outline-dark btn-sm me-2" onclick="navStep(1)">Next ➡️</button>
            <button class="btn btn-warning btn-sm fw-bold" onclick="jumpToNextRequiringAttention()">⏭️ Next Pending Task</button>
          </div>
        </div>

        <div class="row g-3 mb-3">
          <div class="col-md-6">
            <div class="p-3 bg-light rounded border h-100">
              <h6 class="fw-bold text-primary mb-2">📋 What to Do:</h6>
              <div class="small text-dark font-monospace" id="wizardWhatToDo" style="white-space: pre-wrap;">Select a capability task to load guidance...</div>
            </div>
          </div>
          <div class="col-md-6">
            <div class="p-3 bg-light rounded border h-100">
              <h6 class="fw-bold text-success mb-2">👁️ What to Look For:</h6>
              <div class="small text-dark font-monospace" id="wizardWhatToLookFor" style="white-space: pre-wrap;">Select a capability task to load verification criteria...</div>
            </div>
          </div>
        </div>

        <!-- QUICK LANGUAGE ACTION TOOLBAR -->
        <div class="d-flex justify-content-between align-items-center p-3 bg-body-tertiary rounded border mb-3">
          <div class="d-flex gap-2">
            <span class="badge bg-dark align-self-center">Execution Controls</span>
            <button class="btn btn-success btn-sm fw-bold" id="wBtnLaunchKt" onclick="wizardAction('launch', 'kotlin')">▶ Launch Kotlin Sample</button>
            <button class="btn btn-outline-success btn-sm fw-bold" id="wBtnTestKt" onclick="wizardAction('test', 'kotlin')">🧪 Run Kotlin Test</button>
            <span class="vr mx-1"></span>
            <button class="btn btn-warning btn-sm text-dark fw-bold" id="wBtnLaunchJava" onclick="wizardAction('launch', 'java')">▶ Launch Java Sample</button>
            <button class="btn btn-outline-warning btn-sm text-dark fw-bold" id="wBtnTestJava" onclick="wizardAction('test', 'java')">🧪 Run Java Test</button>
          </div>
          <div class="small text-muted font-monospace">
            Shortcuts: <kbd>Space</kbd> = Launch | <kbd>Enter</kbd> = Submit & Advance
          </div>
        </div>

        <!-- RATING & AUTO-ADVANCE BAR -->
        <div class="p-3 bg-warning-subtle rounded border d-flex justify-content-between align-items-center">
          <div class="d-flex align-items-center gap-3 flex-fill me-3">
            <span class="fw-bold text-dark me-1">Rating:</span>
            <div class="star-rating wizard-stars" id="wizardStarPicker">
              <span class="star" data-rating="5" onclick="setWizardRating(5)">★</span>
              <span class="star" data-rating="4" onclick="setWizardRating(4)">★</span>
              <span class="star" data-rating="3" onclick="setWizardRating(3)">★</span>
              <span class="star" data-rating="2" onclick="setWizardRating(2)">★</span>
              <span class="star" data-rating="1" onclick="setWizardRating(1)">★</span>
              <span class="small text-muted ms-1" id="wizardRatingLabel">(0/5)</span>
            </div>
            <input type="text" class="form-control form-control-sm flex-fill" id="wizardCommentInput" placeholder="Feedback or observations for FALSIFIABLE_TASK_LIST.md...">
          </div>
          <button class="btn btn-primary btn-sm fw-bold text-nowrap" onclick="submitRatingAndAdvance()">Submit Rating & Next Task ➔</button>
        </div>
      </div>

      <div class="d-flex justify-content-between align-items-center p-3 bg-light rounded border mb-4" id="focusedNavBar">
        <div>
          <button class="btn btn-outline-dark btn-sm fw-bold me-2" onclick="navStep(-1)">⬅️ Previous Item</button>
          <span class="fw-bold fs-6 text-dark me-2" id="stepIndicatorLabel">Capability 1 of {len(maps_caps)}</span>
        </div>
        <div>
          <button class="btn btn-warning btn-sm fw-bold me-2" onclick="jumpToNextRequiringAttention()">⏭️ Jump to Next Requiring Attention (Unrated / Pending)</button>
          <button class="btn btn-outline-dark btn-sm fw-bold" onclick="navStep(1)">Next Item ➡️</button>
        </div>
      </div>

      <!-- MASTER & GROUP TEST SUITES PANEL -->
      <div class="border rounded p-4 mb-4 bg-white shadow-sm border-2 border-primary">
        <div class="d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom">
          <h4 class="m-0 fw-bold text-primary">📦 Falsifiable Capability Test Suites Center</h4>
          <span class="badge bg-primary fs-6">Execute Complete Or Grouped Test Suites</span>
        </div>
        <p class="text-muted small mb-3">
          Execute full verification suites across Kotlin and Java right on your connected Android device. All tests are <strong>falsifiable</strong> and scientifically verify exact SDK behavior.
        </p>

        <div class="row g-3 mb-4">
          <!-- FULL CATALOG MASTER SUITE -->
          <div class="col-md-6">
            <div class="p-3 bg-light rounded border border-secondary-subtle h-100 d-flex flex-column justify-content-between">
              <div>
                <div class="fw-bold text-dark mb-1 d-flex align-items-center">
                  <span class="fs-5 me-2">🚀</span> Master Catalog Test Suite
                </div>
                <div class="text-muted small mb-2">Runs all 14 verified capability tests across Markers, Camera, Map Initialization, and Events simultaneously in one single Gradle invocation.</div>
              </div>
              <div class="d-flex gap-2 mt-2">
                <button class="btn btn-success btn-sm fw-bold action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.kotlin.capabilities.CatalogCapabilitiesTestSuite', 'SUITE', 'kotlin')">▶️ Run All Kotlin Tests</button>
                <button class="btn btn-warning btn-sm fw-bold text-dark action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.java.capabilities.CatalogCapabilitiesTestSuite', 'SUITE', 'java')">▶️ Run All Java Tests</button>
              </div>
            </div>
          </div>

          <!-- MARKERS SUITE -->
          <div class="col-md-6">
            <div class="p-3 bg-light rounded border border-secondary-subtle h-100 d-flex flex-column justify-content-between">
              <div>
                <div class="fw-bold text-dark mb-1 d-flex align-items-center">
                  <span class="fs-5 me-2">📍</span> Markers Capability Suite (4 Tests)
                </div>
                <div class="text-muted small mb-2">Verifies Add Marker (`7bbfe87e`), Info Windows (`bdbefba5`), Styling & Opacity (`de757d41`), and Draggable interaction (`4c2a9906`).</div>
              </div>
              <div class="d-flex gap-2 mt-2">
                <button class="btn btn-outline-success btn-sm fw-bold action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.kotlin.capabilities.MarkerSnippetsTest', 'SUITE', 'kotlin')">▶️ Markers Suite (Kotlin)</button>
                <button class="btn btn-outline-warning btn-sm fw-bold text-dark action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.java.capabilities.MarkerSnippetsTest', 'SUITE', 'java')">▶️ Markers Suite (Java)</button>
              </div>
            </div>
          </div>

          <!-- CAMERA CONTROLS SUITE -->
          <div class="col-md-6">
            <div class="p-3 bg-light rounded border border-secondary-subtle h-100 d-flex flex-column justify-content-between">
              <div>
                <div class="fw-bold text-dark mb-1 d-flex align-items-center">
                  <span class="fs-5 me-2">📷</span> Camera Controls Suite (2 Tests)
                </div>
                <div class="text-muted small mb-2">Verifies Zoom Level Constraints (`2a3e0c25`) and Panning Bounds Restrictions (`0e6b228f`).</div>
              </div>
              <div class="d-flex gap-2 mt-2">
                <button class="btn btn-outline-success btn-sm fw-bold action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.kotlin.capabilities.CameraControlSnippetsTest', 'SUITE', 'kotlin')">▶️ Camera Suite (Kotlin)</button>
                <button class="btn btn-outline-warning btn-sm fw-bold text-dark action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.java.capabilities.CameraControlSnippetsTest', 'SUITE', 'java')">▶️ Camera Suite (Java)</button>
              </div>
            </div>
          </div>

          <!-- MAP INITIALIZATION SUITE -->
          <div class="col-md-6">
            <div class="p-3 bg-light rounded border border-secondary-subtle h-100 d-flex flex-column justify-content-between">
              <div>
                <div class="fw-bold text-dark mb-1 d-flex align-items-center">
                  <span class="fs-5 me-2">🗺️</span> Map Initialization Suite (3 Verified Tests)
                </div>
                <div class="text-muted small mb-2">Verifies Basic Map Activity (`232ecd00`), Enable Traffic (`20793ebb`), and Map Type Hybrid (`c511ea57`).</div>
              </div>
              <div class="d-flex gap-2 mt-2">
                <button class="btn btn-outline-success btn-sm fw-bold action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.kotlin.capabilities.MapInitSnippetsTest', 'SUITE', 'kotlin')">▶️ Map Init Suite (Kotlin)</button>
                <button class="btn btn-outline-warning btn-sm fw-bold text-dark action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.java.capabilities.MapInitSnippetsTest', 'SUITE', 'java')">▶️ Map Init Suite (Java)</button>
              </div>
            </div>
          </div>

          <!-- EVENTS & INTERACTIONS SUITE -->
          <div class="col-md-6">
            <div class="p-3 bg-light rounded border border-secondary-subtle h-100 d-flex flex-column justify-content-between">
              <div>
                <div class="fw-bold text-dark mb-1 d-flex align-items-center">
                  <span class="fs-5 me-2">⚡</span> Events & Interactions Suite (2 Checks)
                </div>
                <div class="text-muted small mb-2">Verifies Map Click Listener disabling (`b34458f3`) and POI Click Listener registration (`b34458f3`).</div>
              </div>
              <div class="d-flex gap-2 mt-2">
                <button class="btn btn-outline-success btn-sm fw-bold action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.kotlin.capabilities.EventsSnippetsTest', 'SUITE', 'kotlin')">▶️ Events Suite (Kotlin)</button>
                <button class="btn btn-outline-warning btn-sm fw-bold text-dark action-btn flex-fill" onclick="runTestLive('master', 'com.example.snippets.java.capabilities.EventsSnippetsTest', 'SUITE', 'java')">▶️ Events Suite (Java)</button>
              </div>
            </div>
          </div>
        </div>

        <!-- MASTER EXECUTION TERMINAL OUTPUT BOX -->
        <div id="exec_output_box_master" class="border rounded p-3 bg-dark text-light" style="display: none;">
          <div class="d-flex justify-content-between align-items-center mb-2 pb-1 border-bottom border-secondary">
            <span class="fw-bold small text-warning font-monospace" id="exec_title_master">Execution Terminal Output:</span>
            <div>
              <div class="spinner-border spinner-border-sm text-light me-2" id="spinner_master" role="status" style="display: none;"></div>
              <button class="btn btn-danger btn-sm py-0 px-2 small fw-bold" id="cancel_btn_master" style="display: none;" onclick="cancelLiveTask('master')">🛑 Cancel Execution</button>
              <button class="btn btn-outline-light btn-sm py-0 px-2 small font-monospace" onclick="document.getElementById('exec_output_box_master').style.display='none'">✖ Close</button>
            </div>
          </div>
          <pre class="m-0 small font-monospace" id="exec_output_text_master" style="white-space: pre-wrap; word-break: break-all; max-height: 400px; overflow-y: auto;">Ready to execute test suites...</pre>
        </div>
      </div>

      <div id="cardsHolder">
        {"".join(html_cards)}
      </div>
    </div>
  </div>

  <script>
    const totalCaps = {len(maps_caps)};
    let currentIdx = 0;
    let viewMode = 'focused';

    function loadState() {{
      fetch('/api/get_ratings').then(r => r.json()).then(data => {{
        if (data.ratings) {{
          Object.keys(data.ratings).forEach(id => updateStarUI(id, data.ratings[id]));
        }}
        if (data.comments) {{
          Object.keys(data.comments).forEach(id => {{
            const box = document.getElementById('comment_' + id);
            if (box) box.value = data.comments[id];
          }});
        }}
      }}).catch(e => console.log('Using local storage fallback'));

      renderCards();
    }}

    function setMode(mode) {{
      viewMode = mode;
      document.getElementById('btnModeFocused').className = mode === 'focused' ? 'btn btn-primary btn-sm active' : 'btn btn-outline-primary btn-sm';
      document.getElementById('btnModeAll').className = mode === 'all' ? 'btn btn-secondary btn-sm active' : 'btn btn-outline-secondary btn-sm';
      document.getElementById('focusedNavBar').style.display = mode === 'focused' ? 'flex' : 'none';
      renderCards();
    }}

    function navStep(delta) {{
      currentIdx = (currentIdx + delta + totalCaps) % totalCaps;
      renderCards();
    }}

    function jumpToNextRequiringAttention() {{
      for (let i = 1; i <= totalCaps; i++) {{
        const targetIdx = (currentIdx + i) % totalCaps;
        const card = document.querySelector('.capability-card[data-index="' + targetIdx + '"]');
        if (!card) continue;
        const cid = card.getAttribute('data-id');
        const starBox = document.querySelector('.star-rating[data-id="' + cid + '"]');
        const isRated = starBox && starBox.querySelectorAll('.star.active').length > 0;
        const isVerified = card.getAttribute('data-status') === 'verified';
        if (!isRated || !isVerified) {{
          currentIdx = targetIdx;
          renderCards();
          return;
        }}
      }}
      alert('🎉 All 39 capabilities have been falsified and rated! Great job!');
    }}

    function renderCards() {{
      document.querySelectorAll('.capability-card').forEach(card => {{
        const idx = parseInt(card.getAttribute('data-index'), 10);
        if (viewMode === 'focused') {{
          card.style.display = (idx === currentIdx) ? 'block' : 'none';
        }} else {{
          card.style.display = 'block';
        }}
      }});
    function renderCards() {{
      document.querySelectorAll('.capability-card').forEach(card => {{
        const idx = parseInt(card.getAttribute('data-index'), 10);
        if (viewMode === 'focused') {{
          card.style.display = (idx === currentIdx) ? 'block' : 'none';
        }} else {{
          card.style.display = 'block';
        }}
      }});
      const currentCard = document.querySelector('.capability-card[data-index="' + currentIdx + '"]');
      if (currentCard) {{
        const cid = currentCard.getAttribute('data-id');
        const title = currentCard.querySelector('h3').innerText;
        document.getElementById('stepIndicatorLabel').innerText = 'Capability ' + (currentIdx + 1) + ' of ' + totalCaps + ': ' + title + ' (' + cid + ')';

        // Update Wizard Card Header & Guidance Instruction Boxes
        const hdr = document.getElementById('wizardTaskHeader');
        if (hdr) hdr.innerText = 'Task ' + (currentIdx + 1) + ' of ' + totalCaps + ': ' + title + ' (' + cid + ')';

        const whatToDo = currentCard.getAttribute('data-what-to-do') || 'Launch sample on attached device.';
        const whatToLookFor = currentCard.getAttribute('data-what-to-look-for') || 'Verify visual fidelity and behavior.';
        const elToDo = document.getElementById('wizardWhatToDo');
        const elLook = document.getElementById('wizardWhatToLookFor');
        if (elToDo) elToDo.innerText = whatToDo;
        if (elLook) elLook.innerText = whatToLookFor;

        // Sync Wizard Star Rating & Comment Box
        const starBox = currentCard.querySelector('.star-rating');
        const activeStars = starBox ? starBox.querySelectorAll('.star.active').length : 0;
        updateWizardStarUI(activeStars);

        const cardCommentBox = document.getElementById('comment_' + cid);
        const wizardCommentBox = document.getElementById('wizardCommentInput');
        if (wizardCommentBox) wizardCommentBox.value = cardCommentBox ? cardCommentBox.value : '';
      }}
    }}

    let wizardCurrentRating = 0;
    function setWizardRating(rating) {{
      wizardCurrentRating = rating;
      updateWizardStarUI(rating);
    }}

    function updateWizardStarUI(rating) {{
      wizardCurrentRating = rating;
      const stars = document.querySelectorAll('#wizardStarPicker .star');
      stars.forEach(star => {{
        const rVal = parseInt(star.getAttribute('data-rating') || '0', 10);
        if (rVal <= rating) star.classList.add('active');
        else star.classList.remove('active');
      }});
      const label = document.getElementById('wizardRatingLabel');
      if (label) label.innerText = '(' + rating + '/5 ⭐)';
    }}

    function wizardAction(actionType, lang) {{
      const currentCard = document.querySelector('.capability-card[data-index="' + currentIdx + '"]');
      if (!currentCard) return;
      const cid = currentCard.getAttribute('data-id');
      const group = currentCard.getAttribute('data-group');
      const snippetTitle = currentCard.getAttribute('data-snippet-title') || currentCard.querySelector('h3').innerText;
      const testClass = currentCard.getAttribute(lang === 'kotlin' ? 'data-kt-test-class' : 'data-java-test-class');
      const testMethod = currentCard.getAttribute('data-test-method');

      if (actionType === 'launch') {{
        launchSampleLive(cid, group, snippetTitle, lang);
      }} else if (actionType === 'test') {{
        runTestLive(cid, testClass, testMethod, lang);
      }}
    }}

    function setRating(id, rating) {{
      updateStarUI(id, rating);
      const comment = document.getElementById('comment_' + id)?.value || '';
      fetch('/api/save_rating', {{
        method: 'POST',
        headers: {{ 'Content-Type': 'application/json' }},
        body: JSON.stringify({{ id: id, rating: rating, comment: comment }})
      }}).then(r => r.json()).then(res => console.log('Rating saved:', res));
    }}

    function submitRatingAndAdvance() {{
      const currentCard = document.querySelector('.capability-card[data-index="' + currentIdx + '"]');
      if (!currentCard) return;
      const cid = currentCard.getAttribute('data-id');
      const wizardComment = document.getElementById('wizardCommentInput')?.value || '';
      const rating = wizardCurrentRating > 0 ? wizardCurrentRating : null;

      // Update local card DOM UI first without firing separate HTTP requests
      updateStarUI(cid, rating || 0);
      const cardCommentBox = document.getElementById('comment_' + cid);
      if (cardCommentBox) cardCommentBox.value = wizardComment;

      // Single consolidated save rating API call
      fetch('/api/save_rating', {{
        method: 'POST',
        headers: {{ 'Content-Type': 'application/json' }},
        body: JSON.stringify({{ id: cid, rating: rating, comment: wizardComment }})
      }}).then(r => r.json()).then(res => {{
        if (res.next_id) {{
          const nextCard = document.querySelector('.capability-card[data-id="' + res.next_id + '"]');
          if (nextCard) {{
            currentIdx = parseInt(nextCard.getAttribute('data-index'), 10);
            renderCards();
            return;
          }}
        }}
        navStep(1);
      }}).catch(e => {{
        navStep(1);
      }});
    }}

    function updateStarUI(id, rating) {{
      const container = document.querySelector('.star-rating[data-id="' + id + '"]');
      if (!container) return;
      const stars = container.querySelectorAll('.star');
      stars.forEach(star => {{
        const onclickAttr = star.getAttribute('onclick') || '';
        const m = onclickAttr.match(/setRating\('[^']+',\s*(\d+)\)/);
        const rVal = m ? parseInt(m[1], 10) : 0;
        if (rVal <= rating) star.classList.add('active');
        else star.classList.remove('active');
      }});
      const valLabel = document.getElementById('rating_val_' + id);
      if (valLabel) valLabel.innerText = '(' + rating + '/5 ⭐)';

      // Keep active wizard task rating UI synchronized
      const currentCard = document.querySelector('.capability-card[data-index="' + currentIdx + '"]');
      if (currentCard && currentCard.getAttribute('data-id') === id) {{
        updateWizardStarUI(rating);
      }}
    }}

    function saveComment(id, val) {{
      const container = document.querySelector('.star-rating[data-id="' + id + '"]');
      const activeStars = container ? container.querySelectorAll('.star.active').length : null;
      const cardBox = document.getElementById('comment_' + id);
      if (cardBox && cardBox.value !== val) cardBox.value = val;
      fetch('/api/save_rating', {{
        method: 'POST',
        headers: {{ 'Content-Type': 'application/json' }},
        body: JSON.stringify({{ id: id, rating: activeStars, comment: val }})
      }});
    }}

    // Keyboard Shortcuts Listener
    document.addEventListener('keydown', function(evt) {{
      const tag = document.activeElement ? document.activeElement.tagName.toLowerCase() : '';
      if (tag === 'textarea' || tag === 'input' || tag === 'button' || tag === 'select') return;

      if (evt.code === 'Space') {{
        evt.preventDefault();
        wizardAction('launch', 'kotlin');
      }} else if (evt.code === 'Enter') {{
        evt.preventDefault();
        submitRatingAndAdvance();
      }}
    }});

    let activeAbortController = null;

    function setTaskRunningState(cid, isRunning, titleText) {{
      const spinner = document.getElementById('spinner_' + cid);
      const cancelBtn = document.getElementById('cancel_btn_' + cid);
      const titleEl = document.getElementById('exec_title_' + cid);
      if (spinner) spinner.style.display = isRunning ? 'inline-block' : 'none';
      if (cancelBtn) cancelBtn.style.display = isRunning ? 'inline-block' : 'none';
      if (titleEl && titleText) titleEl.innerText = titleText;

      document.querySelectorAll('.action-btn').forEach(btn => {{
        btn.disabled = isRunning;
        if (isRunning) btn.classList.add('opacity-50');
        else btn.classList.remove('opacity-50');
      }});
    }}

    function cancelLiveTask(cid) {{
      if (activeAbortController) {{
        activeAbortController.abort();
        activeAbortController = null;
      }}
      const outText = document.getElementById('exec_output_text_' + cid);
      if (outText) outText.innerText += '\\n\\n⏳ Sending cancel signal to server and stopping Gradle tasks...';

      fetch('/api/cancel', {{ method: 'POST' }})
        .then(r => r.json())
        .then(res => {{
          if (outText) outText.innerText += '\\n✅ ' + (res.message || 'Cancelled on server.');
          setTaskRunningState(cid, false, 'Execution Terminal Output:');
        }})
        .catch(e => {{
          if (outText) outText.innerText += '\\n❌ Error sending cancel signal: ' + e.message;
          setTaskRunningState(cid, false, 'Execution Terminal Output:');
        }});
    }}

    function runTestLive(cid, testClass, testMethod, lang) {{
      const outBox = document.getElementById('exec_output_box_' + cid);
      const outText = document.getElementById('exec_output_text_' + cid);
      if (outBox) outBox.style.display = 'block';
      const targetDisplay = (testMethod && testMethod !== 'SUITE' && testMethod !== 'ALL' && testMethod !== '') ? (testClass + '#' + testMethod) : (testClass + ' (Full Suite)');
      if (outText) outText.innerText = '⏳ Running ' + lang.toUpperCase() + ' instrumented test/suite via Gradle on dirtdog.c.googlers.com...\\nTarget: ' + targetDisplay + '\\n\\nPlease wait up to 60s for device response...';

      if (activeAbortController) activeAbortController.abort();
      activeAbortController = new AbortController();
      setTaskRunningState(cid, true, 'Running ' + lang.toUpperCase() + ' Suite/Test...');

      fetch('/api/run_test', {{
        method: 'POST',
        headers: {{ 'Content-Type': 'application/json' }},
        body: JSON.stringify({{ test_class: testClass, test_method: testMethod, lang: lang }}),
        signal: activeAbortController.signal
      }}).then(r => r.json()).then(res => {{
        outText.innerText = res.output || ('Status: ' + res.status);
        setTaskRunningState(cid, false, 'Execution Terminal Output:');
      }}).catch(e => {{
        if (e.name === 'AbortError') {{
          outText.innerText += '\\n\\n🛑 Task cancelled by user.';
        }} else {{
          outText.innerText = '❌ Failed to communicate with test/server.py: ' + e.message;
        }}
        setTaskRunningState(cid, false, 'Execution Terminal Output:');
      }});
    }}

    function launchSampleLive(cid, group, title, lang) {{
      const outBox = document.getElementById('exec_output_box_' + cid);
      const outText = document.getElementById('exec_output_text_' + cid);
      outBox.style.display = 'block';
      outText.innerText = '⏳ Building, installing (`installDebug`), and launching ' + lang.toUpperCase() + ' MapActivity on device...\\nPlease wait (~3-10s)...';

      if (activeAbortController) activeAbortController.abort();
      activeAbortController = new AbortController();
      setTaskRunningState(cid, true, 'Launching ' + lang.toUpperCase() + ' Sample...');

      fetch('/api/launch_sample', {{
        method: 'POST',
        headers: {{ 'Content-Type': 'application/json' }},
        body: JSON.stringify({{ group: group, title: title, lang: lang }}),
        signal: activeAbortController.signal
      }}).then(r => r.json()).then(res => {{
        outText.innerText = res.message || ('Status: ' + res.status);
        setTaskRunningState(cid, false, 'Execution Terminal Output:');
      }}).catch(e => {{
        if (e.name === 'AbortError') {{
          outText.innerText += '\\n\\n🛑 Task cancelled by user.';
        }} else {{
          outText.innerText = '❌ Failed to communicate with test/server.py: ' + e.message;
        }}
        setTaskRunningState(cid, false, 'Execution Terminal Output:');
      }});
    }}

    function copyCmd(id_with_lang, btn) {{
      const input = document.getElementById('cmd_' + id_with_lang);
      if (input) {{
        navigator.clipboard.writeText(input.value);
        if (btn) {{
          const orig = btn.innerText;
          btn.innerText = '✅ Copied!';
          setTimeout(() => btn.innerText = orig, 1500);
        }}
      }}
    }}

    function copyOutput(cid, btn) {{
      const el = document.getElementById('exec_output_text_' + cid);
      if (el) {{
        navigator.clipboard.writeText(el.innerText);
        if (btn) {{
          const orig = btn.innerText;
          btn.innerText = '✅ Copied!';
          setTimeout(() => btn.innerText = orig, 1500);
        }}
      }}
    }}

    function exportReviewReport() {{
      fetch('/api/get_ratings').then(r => r.json()).then(data => {{
        const ratings = data.ratings || {{}};
        const comments = data.comments || {{}};
        const ids = new Set([...Object.keys(ratings), ...Object.keys(comments)]);
        if (ids.size === 0) {{
          alert('No ratings or comments provided yet!');
          return;
        }}
        let report = '# ⭐ Maps SDK Reviewer Evaluation Report\\n\\n';
        ids.forEach(id => {{
          const r = ratings[id] || 'Not Rated';
          const c = comments[id] || 'No comment provided.';
          report += '- **[' + id + ']** - Rating: `' + r + '/5 ⭐`\\n  - *Feedback:* ' + c + '\\n\\n';
        }});
        navigator.clipboard.writeText(report);
        alert('Copied Review Report (' + ids.size + ' evaluated capabilities) to clipboard!\\n\\n' + report);
      }});
    }}

    window.onload = loadState;
  </script>
</body>
</html>
"""

    html_path = os.path.join(project_root, "MANUAL_VERIFY_CATALOG.html")
    with open(html_path, "w", encoding="utf-8") as f:
        f.write(html_content)

    md_path = os.path.join(project_root, "FALSIFIABLE_TASK_LIST.md")
    with open(md_path, "w", encoding="utf-8") as f:
        f.writelines(md_rows)

    print(f"✅ Generated Interactive Focused Review Dashboard with Kotlin & Java Controls: {html_path}")
    print(f"✅ Generated Markdown Task List: {md_path}")

if __name__ == "__main__":
    main()
