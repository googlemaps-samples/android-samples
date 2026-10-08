# Google Maps Android Sample Catalog Index

This document provides a comprehensive mapping of all production samples and documentation snippets available in the repository. Ground your recommendations and implementations in these verified sample architectures.

---

## 1. ApiDemos Samples Index

Located in:
- Common Catalog UI: `:ApiDemos:common-ui` (`ApiDemos/project/common-ui/src/main/java/com/example/common_ui/catalog/`)
- Java View Implementations: `:ApiDemos:java-app` (`ApiDemos/project/java-app/src/main/java/com/example/mapdemo/`)
- Kotlin View Implementations: `:ApiDemos:kotlin-app` (`ApiDemos/project/kotlin-app/src/main/java/com/example/kotlindemos/`)
- Reviewer Module: `:ApiDemos:reviewer` (`ApiDemos/project/reviewer/src/main/java/com/example/reviewer/`)

| Sample ID | Title | Key API Calls | Java Activity | Kotlin Activity |
| :--- | :--- | :--- | :--- | :--- |
| `basic_map` | Basic Map | `SupportMapFragment`, `getMapAsync()`, `awaitMap()`, `addMarker()` | `BasicMapDemoActivity.java` | `BasicMapDemoActivity.kt` |
| `camera_clamping` | Camera Clamping | `setLatLngBoundsForCameraTarget()`, `setMinZoomPreference()`, `setMaxZoomPreference()` | `CameraClampingDemoActivity.java` | `CameraClampingDemoActivity.kt` |
| `camera_demo` | Camera Controls | `animateCamera()`, `moveCamera()`, `CameraUpdateFactory`, `stopAnimation()` | `CameraDemoActivity.java` | `CameraDemoActivity.kt` |
| `circle_demo` | Circle Overlays | `addCircle()`, `CircleOptions`, `fillColor()`, `strokeColor()`, `radius()` | `CircleDemoActivity.java` | `CircleDemoActivity.kt` |
| `data_driven_boundaries` | Data-Driven Boundaries | `getFeatureLayer()`, `FeatureType.ADMINISTRATIVE_AREA_LEVEL_1`, `FeatureLayer.setFeatureStyle()` | `DataDrivenBoundariesActivity.java` | `DataDrivenBoundariesActivity.kt` |
| `events_demo` | Map & Gesture Events | `setOnMapClickListener()`, `setOnMapLongClickListener()`, `setOnCameraMoveListener()` | `EventsDemoActivity.java` | `EventsDemoActivity.kt` |
| `ground_overlay` | Ground Overlays | `addGroundOverlay()`, `GroundOverlayOptions`, `image()`, `position()` | `GroundOverlayDemoActivity.java` | `GroundOverlayDemoActivity.kt` |
| `indoor_demo` | Indoor Maps | `setIndoorEnabled()`, `getFocusedBuilding()`, `OnIndoorStateChangeListener` | `IndoorDemoActivity.java` | `IndoorDemoActivity.kt` |
| `layers_demo` | Map Layers & Types | `setMapType()`, `setTrafficEnabled()`, `setBuildingsEnabled()`, `setMyLocationEnabled()` | `LayersDemoActivity.java` | `LayersDemoActivity.kt` |
| `lite_demo` | Lite Mode Map | `GoogleMapOptions.liteMode(true)`, `MapView`, snapshot interaction | `LiteDemoActivity.java` | `LiteDemoActivity.kt` |
| `lite_list_demo` | Lite Maps in List | `RecyclerView`, `MapView` recycled in ViewHolders, `setMapLocation()` | `LiteListDemoActivity.java` | `LiteListDemoActivity.kt` |
| `location_source_demo` | Custom Location Source | `setLocationSource()`, `LocationSource.OnLocationChangedListener` | `LocationSourceDemoActivity.java` | `LocationSourceDemoActivity.kt` |
| `marker_close_on_click` | Marker Click Behavior | `setOnMarkerClickListener()`, consuming click events, popup handling | `MarkerCloseOnClickDemoActivity.java` | `MarkerCloseOnClickDemoActivity.kt` |
| `marker_demo` | Advanced Markers | `MarkerOptions`, `icon(BitmapDescriptor)`, `anchor()`, `draggable()`, `InfoWindowAdapter` | `MarkerDemoActivity.java` | `MarkerDemoActivity.kt` |
| `multi_map` | Multi-Map Viewport | Multiple `SupportMapFragment` instances in a single Activity | `MultiMapDemoActivity.java` | `MultiMapDemoActivity.kt` |
| `my_location` | Device Location Layer | `setMyLocationEnabled()`, location runtime permissions, `MyLocationButton` | `MyLocationDemoActivity.java` | `MyLocationDemoActivity.kt` |
| `polygon_demo` | Polygon Shapes | `addPolygon()`, `PolygonOptions`, `strokeJointType()`, `strokePattern()` | `PolygonDemoActivity.java` | `PolygonDemoActivity.kt` |
| `polyline_demo` | Polyline Paths | `addPolyline()`, `PolylineOptions`, `startCap()`, `endCap()`, `jointType()` | `PolylineDemoActivity.java` | `PolylineDemoActivity.kt` |
| `programmatic` | Programmatic Map Creation | Creating `SupportMapFragment.newInstance(GoogleMapOptions)` via FragmentTransaction | `ProgrammaticDemoActivity.java` | `ProgrammaticDemoActivity.kt` |
| `raw_mapview` | Raw MapView Lifecycle | `MapView`, forwarding `onCreate()`, `onStart()`, `onResume()`, `onPause()`, `onDestroy()` | `RawMapViewDemoActivity.java` | `RawMapViewDemoActivity.kt` |
| `retain_map` | Retained Map Fragment | `setRetainInstance(true)` across Activity configuration changes (rotations) | `RetainMapDemoActivity.java` | `RetainMapDemoActivity.kt` |
| `save_state` | State Persistence | `onSaveInstanceState()`, restoring camera target and marker selections | `SaveStateDemoActivity.java` | `SaveStateDemoActivity.kt` |
| `snapshot_demo` | Map Snapshot & Bitmap | `snapshot(SnapshotReadyCallback)`, `awaitSnapshot()`, Bitmap post-processing | `SnapshotDemoActivity.java` | `SnapshotDemoActivity.kt` |
| `split_street_view_map` | Split Map & Street View | Coordinating `GoogleMap` marker position with `StreetViewPanorama` camera orientation | `SplitStreetViewPanoramaAndMapDemoActivity.java` | `SplitStreetViewPanoramaAndMapDemoActivity.kt` |
| `street_view_basic` | Street View Panorama | `SupportStreetViewPanoramaFragment`, `getStreetViewPanoramaAsync()`, `setPosition()` | `StreetViewPanoramaBasicDemoActivity.java` | `StreetViewPanoramaBasicDemoActivity.kt` |
| `street_view_events` | Street View Events | `OnStreetViewPanoramaChangeListener`, `OnStreetViewPanoramaCameraChangeListener` | `StreetViewPanoramaEventsDemoActivity.java` | `StreetViewPanoramaEventsDemoActivity.kt` |
| `street_view_navigation` | Street View Navigation | `StreetViewPanoramaLink`, panning, navigating between connected panoramas | `StreetViewPanoramaNavigationDemoActivity.java` | `StreetViewPanoramaNavigationDemoActivity.kt` |
| `street_view_options` | Street View Options | `StreetViewPanoramaOptions`, street names enabled, user navigation enabled | `StreetViewPanoramaOptionsDemoActivity.java` | `StreetViewPanoramaOptionsDemoActivity.kt` |
| `street_view_view` | Raw StreetViewPanoramaView | `StreetViewPanoramaView` lifecycle forwarding and view embedding | `StreetViewPanoramaViewDemoActivity.java` | `StreetViewPanoramaViewDemoActivity.kt` |
| `styled_map` | Cloud-Based Styling | `MapStyleOptions.loadRawResourceStyle()`, `MapId`, custom JSON styling | `StyledMapDemoActivity.java` | `StyledMapDemoActivity.kt` |
| `tags_demo` | Object Tag Metadata | `Marker.setTag()`, `Polyline.setTag()`, `Polygon.setTag()`, click telemetry | `TagsDemoActivity.java` | `TagsDemoActivity.kt` |
| `tile_coordinate` | Custom Coordinate Tiles | Custom `TileProvider`, drawing grid and tile coordinate bitmaps dynamically | `TileCoordinateDemoActivity.java` | `TileCoordinateDemoActivity.kt` |
| `tile_overlay` | Tile Overlays | `addTileOverlay()`, `TileOverlayOptions`, `UrlTileProvider`, tile caching | `TileOverlayDemoActivity.java` | `TileOverlayDemoActivity.kt` |
| `visible_region` | Visible Region Telemetry | `Projection.getVisibleRegion()`, `LatLngBounds`, `farLeft`, `nearRight`, Camera actions | `VisibleRegionDemoActivity.java` | `VisibleRegionDemoActivity.kt` |

---

## 2. Documentation Snippets Index

Located in:
- Java Snippets: `:snippets:java-app` (`snippets/java-app/src/main/java/com/example/snippets/java/snippets/`)
- Kotlin Snippets: `:snippets:kotlin-app` (`snippets/kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/`)

| Snippet Group | Target File | Key Documented Region Tags |
| :--- | :--- | :--- |
| **Map Initialization** | `MapInitSnippets` | `maps_android_mapsactivity`, `maps_android_mapfragment`, `maps_android_mapview_lifecycle`, `maps_android_map_options`, `maps_android_map_type`, `maps_android_maps_initializer` |
| **Camera Controls** | `CameraSnippets` | `maps_android_camera_move`, `maps_android_camera_animate`, `maps_android_camera_zoom`, `maps_android_camera_bounds`, `maps_android_camera_position`, `maps_android_camera_clamping` |
| **Markers** | `MarkerSnippets` | `maps_android_markers_add`, `maps_android_markers_icon_vector`, `maps_android_markers_icon_bitmap`, `maps_android_markers_draggable`, `maps_android_markers_infowindow`, `maps_android_markers_custom_infowindow` |
| **Shapes & Lines** | `ShapesSnippets` | `maps_android_polylines_add`, `maps_android_polylines_style`, `maps_android_polygons_add`, `maps_android_polygons_holes`, `maps_android_circles_add` |
| **Layers & GeoData** | `UtilsSnippets` | `maps_android_utils_geojson_add`, `maps_android_utils_geojson_style`, `maps_android_utils_kml_add`, `maps_android_utils_kml_remove_layer`, `maps_android_utils_heatmaps_add`, `maps_android_utils_heatmaps_gradient`, `maps_android_utils_clustering_add` |
| **Dataset Styling** | `DatasetLayerSnippets` | `maps_android_datasets_get_layer`, `maps_android_datasets_style_feature`, `maps_android_datasets_click_listener` |
| **Street View** | `StreetViewSnippets` | `maps_android_streetview_launch`, `maps_android_streetview_location`, `maps_android_streetview_camera`, `maps_android_streetview_events` |
| **Events & Gestures** | `EventsSnippets` | `maps_android_events_click`, `maps_android_events_longclick`, `maps_android_events_camera_idle`, `maps_android_events_poi_click` |
