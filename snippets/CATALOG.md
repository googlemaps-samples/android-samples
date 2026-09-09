# 🗺️ Maps SDK (2D) API Snippets Catalog

This document serves as a comprehensive developer reference mapping high-level concepts directly to source code examples.

## 📑 Snippet Concepts Index

This section maps high-level concepts (groups) to specific demonstration files and lines, split by language.

### 🟢 Kotlin Snippets Catalog

#### Camera
> Snippets demonstrating camera controls, zoom constraints, bounds, and animations.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Zoom Level Constraints**<br/>`maps_android_camera_and_view_zoom_level` | Sets minimum and maximum zoom preference bounds on the camera. | [CameraControlSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CameraControlSnippets.kt#L39-L42) |
| **2. Fit Camera To Bounds (Australia)**<br/>`maps_android_camera_and_view_setting_boundaries` | Moves the camera once to fit geographic boundaries (Australia) within the viewport. Note: This frames the map initially, but does not restrict subsequent user panning. | [CameraControlSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CameraControlSnippets.kt#L50-L56) |
| **3. Centering Map Within An Area**<br/>`maps_android_camera_and_view_centering_within_area` | Centers the camera on the center point of geographic bounds (Australia) at a zoom level of 10. | [CameraControlSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CameraControlSnippets.kt#L64-L70) |
| **4. Panning Restrictions**<br/>`maps_android_camera_and_view_panning_restrictions` | Restricts the camera target to specified geographic boundaries (Adelaide). | [CameraControlSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CameraControlSnippets.kt#L78-L87) |
| **5. Common Map Movements**<br/>`maps_android_camera_and_view_common_map_movements` | Demonstrates camera movement, animation, zoom, and CameraPosition builder. | [CameraControlSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CameraControlSnippets.kt#L95-L116) |

#### Cloud Customization
> Snippets demonstrating Google Cloud Console map customization capabilities loaded via Map ID.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Reusable Map Style**<br/>`maps_android_cloud_reusable_style` | Demonstrates loading a reusable, cross-platform map style created in Google Cloud Console. | [CloudCustomizationSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CloudCustomizationSnippets.kt#L38-L43) |
| **2. Style Roads and Polygons**<br/>`maps_android_cloud_style_roads` | Loads a Map ID configured with custom road network and geometry polygon styles. | [CloudCustomizationSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CloudCustomizationSnippets.kt#L51-L56) |
| **3. Feature Visibility Toggling**<br/>`maps_android_cloud_feature_visibility` | Loads a Map ID configured in Cloud Console to display or hide specific base map feature layers. | [CloudCustomizationSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CloudCustomizationSnippets.kt#L64-L69) |
| **4. Style Icons and Text Labels**<br/>`maps_android_cloud_style_labels` | Loads a Map ID configured with custom typography, label colors, and POI icon styles. | [CloudCustomizationSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CloudCustomizationSnippets.kt#L77-L82) |
| **5. Zoom-Level Styling**<br/>`maps_android_cloud_zoom_styling` | Loads a Map ID configured to apply distinct map styles dynamically across zoom levels. | [CloudCustomizationSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CloudCustomizationSnippets.kt#L90-L95) |
| **6. POI Density Filtering**<br/>`maps_android_cloud_poi_density` | Loads a Map ID configured with adjusted business and point-of-interest display density. | [CloudCustomizationSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CloudCustomizationSnippets.kt#L103-L108) |
| **7. Style Buildings**<br/>`maps_android_cloud_style_buildings` | Loads a Map ID configured with customized 2D and 3D building footprint styles. | [CloudCustomizationSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CloudCustomizationSnippets.kt#L116-L121) |
| **8. Style Landmarks**<br/>`maps_android_cloud_style_landmarks` | Loads a Map ID configured with specialized styling for prominent natural and urban landmarks. | [CloudCustomizationSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/CloudCustomizationSnippets.kt#L129-L134) |

#### Custom Geospatial Datasets
> Snippets demonstrating custom Cloud geospatial dataset feature layers, attribute styling, and click events.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Dataset - Boulder Trails**<br/>- | Loads Boulder Colorado Trails dataset. Styles lines green (Easy), blue (Moderate), or red (Difficult). Line width indicates dog permissions. | [DatasetLayerSnippets.kt:47](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/DatasetLayerSnippets.kt#L47) |
| **2. Dataset - NYC Squirrels**<br/>`maps_android_dds_nyc_squirrels` | Loads NYC Squirrel Sightings dataset. Renders sightings points colored by primary fur color (Black, Cinnamon, Gray). | [DatasetLayerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/DatasetLayerSnippets.kt#L165-L202) |
| **3. Dataset - Kyoto Temples (Clickable)**<br/>`maps_android_dds_kyoto_temples` | Loads Kyoto Temples dataset. Highlights temple boundary polygons in Blue, and updates clicked temple areas to Yellow. | [DatasetLayerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/DatasetLayerSnippets.kt#L217-L278) |

#### Data-Driven Boundary Styling
> Snippets demonstrating administrative boundary feature layers, polygon styling, and click events.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Boundaries - Localities (Hana, HI)**<br/>`maps_android_dds_locality_boundary` | Loads LOCALITY layer. Styles Hana, Hawaii (Place ID: ChIJ0zQtYiWsVHkRk8lRoB1RNPo) with purple fill and border. Centers camera. | [DataDrivenBoundarySnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/DataDrivenBoundarySnippets.kt#L50-L79) |
| **2. Boundaries - Admin Area 1 (States)**<br/>`maps_android_dds_state_boundaries` | Loads ADMINISTRATIVE_AREA_LEVEL_1 layer. Styles state/provincial boundaries with random colors based on Place ID hashes. Centers over US. | [DataDrivenBoundarySnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/DataDrivenBoundarySnippets.kt#L87-L115) |
| **3. Boundaries - Countries (Interactive)**<br/>`maps_android_dds_country_interactive` | Loads COUNTRY layer. Renders countries with 10% black fill. Taps toggle country coloring between light black and 33% opaque red. | [DataDrivenBoundarySnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/DataDrivenBoundarySnippets.kt#L123-L182) |

#### Events
> Snippets demonstrating clicks, camera events, POI clicks and indoor building levels.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. MapView Disable Click Event**<br/>`maps_android_events_disable_clicks_mapview` | Disables click events on a MapView directly. | [EventsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/EventsSnippets.kt#L41-L46) |
| **2. Map Fragment Disable Click Event**<br/>`maps_android_events_disable_clicks_mapfragment` | Disables click events on a SupportMapFragment view. | [EventsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/EventsSnippets.kt#L54-L61) |
| **3. Active Indoor Building Level**<br/>`maps_android_events_active_level` | Retrieves the active level of the currently focused indoor building. | [EventsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/EventsSnippets.kt#L69-L74) |
| **4. POI Click Listener**<br/>`maps_android_on_poi_click_demo` | Registers a listener for clicks on Point of Interests (POIs). | [EventsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/EventsSnippets.kt#L82-L91) |

#### Map Initialization
> Snippets showing how to initialize, configure map options, types, and renderers.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Basic Map Activity**<br/>`maps_android_mapsactivity` | Initializes a map and adds a marker in Sydney, Australia. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L47-L56) |
| **2. Map Fragment Transaction**<br/>`maps_android_map_fragment` | Shows how to add a SupportMapFragment dynamically. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L64-L72) |
| **3. Set Map Type**<br/>`maps_android_map_type` | Sets the map type to Hybrid. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L80-L85) |
| **4. Google Map Options**<br/>`maps_android_google_map_options` | Shows how to build and configure GoogleMapOptions. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L93-L95) |
| **5. Support Map Fragment Map ID**<br/>`maps_android_support_map_fragment_map_id` | Configures a SupportMapFragment with a Map ID. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L110-L114) |
| **6. MapView Map ID**<br/>`maps_android_mapview_map_id` | Configures a MapView with a Map ID. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L122-L126) |
| **7. Lite Mode Options**<br/>`maps_android_lite_mode_options` | Configures GoogleMapOptions for Lite Mode. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L134-L137) |
| **8. Cloud-based Map Styling**<br/>`maps_android_cloud_based_map_styling` | Loads a MapFragment configured with a Map ID from resources. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L145-L150) |
| **9. Renderer Opt-In**<br/>`maps_android_renderer_opt_in` | Requests the latest Map renderer version. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L158-L165) |
| **10. Set Map Color Scheme**<br/>`maps_android_map_color_scheme` | Configures the map color scheme (Dark Mode / Light Mode). | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L173-L176) |
| **11. Enable Traffic Layer**<br/>`maps_android_traffic_layer` | Toggles the real-time traffic overlay on the map. | [MapInitSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MapInitSnippets.kt#L184-L186) |

#### Markers
> Snippets demonstrating marker creation, styling, customization, and events.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Add a Marker**<br/>`maps_android_markers_add_a_marker` | Adds a simple marker in Sydney, Australia. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L45-L66) |
| **2. Draggable Marker**<br/>`maps_android_markers_draggable` | Creates a draggable marker at Perth. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L74-L82) |
| **3. Default Icon Marker**<br/>`maps_android_markers_default_icon` | Adds a default marker at Melbourne. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L90-L96) |
| **4. Custom Marker Color**<br/>`maps_android_markers_custom_marker_color` | Adds an azure-colored marker at Melbourne. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L104-L111) |
| **5. Marker Opacity**<br/>`maps_android_markers_opacity` | Adds a semi-transparent marker at Melbourne. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L119-L126) |
| **6. Custom Marker Image**<br/>`maps_android_markers_image` | Adds a marker with a custom arrow image resource. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L134-L143) |
| **7. Flat Marker**<br/>`maps_android_markers_flatten` | Creates a flat marker that rotates with the map. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L151-L158) |
| **8. Rotate Marker**<br/>`maps_android_markers_rotate` | Rotates a marker 90 degrees around its anchor. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L166-L174) |
| **9. Marker Z-Index**<br/>`maps_android_markers_z_index` | Sets a high z-index on a marker. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L182-L189) |
| **10. Marker Click Listener & Tag**<br/>`maps_android_markers_tag_sample` | Associates click counts with markers using tag objects. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L197-L242) |
| **11. Add Info Window**<br/>`maps_android_info_windows_add` | Creates a marker with title and snippet details. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L250-L258) |
| **12. Show/Hide Info Window**<br/>`maps_android_info_windows_show_hide` | Creates a marker and programmatically triggers its info window. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L266-L277) |
| **13. Info Window Click Listener**<br/>`maps_android_info_windows_click_listener` | Listens to clicks on info windows. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L285-L292) |
| **14. Marker Collision Behavior**<br/>`maps_android_marker_collision` | Configures collision behavior on an AdvancedMarker. | [MarkerSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MarkerSnippets.kt#L300-L309) |

#### My Location Layer
> Snippets demonstrating my location layer setup and button clicks.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Enable My Location Layer**<br/>`maps_android_my_location` | Enables the my location layer and registers click listeners. | [MyLocationSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/MyLocationSnippets.kt#L38-L52) |

#### Overlays
> Snippets demonstrating GroundOverlays and TileOverlays.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Ground Overlays**<br/>`maps_android_ground_overlays_add` | Creates, retains, changes and removes a ground overlay. | [OverlaySnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/OverlaySnippets.kt#L46-L52) |
| **2. Ground Overlay Position Image Location**<br/>`maps_android_ground_overlays_position_image_location` | Defines GroundOverlayOptions positioning via anchor and LatLng. | [OverlaySnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/OverlaySnippets.kt#L94-L99) |
| **3. Ground Overlay Position Image Bounds**<br/>`maps_android_ground_overlays_position_image_bounds` | Defines GroundOverlayOptions positioning via LatLngBounds. | [OverlaySnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/OverlaySnippets.kt#L107-L115) |
| **4. Tile Overlays Add**<br/>`maps_android_tile_overlays_add` | Adds a TileOverlay with a custom UrlTileProvider. | [OverlaySnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/OverlaySnippets.kt#L123-L161) |
| **5. Tile Overlays Transparency**<br/>`maps_android_tile_overlays_transparency` | Adds and toggles transparency of a TileOverlay. | [OverlaySnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/OverlaySnippets.kt#L177-L193) |

#### Shapes
> Snippets demonstrating shapes, custom styled polylines, polygons, and circles.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Simple Polyline**<br/>`maps_android_shapes_polylines_polylineoptions` | Creates a polyline and adds points to define a rectangle. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L53-L73) |
| **2. Simple Polygon**<br/>`maps_android_shapes_polygons_polygonoptions` | Creates a polygon defining a rectangle. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L81-L94) |
| **3. Polygon Autocompletion**<br/>`maps_android_shapes_polygons_autocompletion` | Demonstrates how uncompleted shapes are closed automatically. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L102-L124) |
| **4. Hollow Polygon**<br/>`maps_android_shapes_polygons_hollow` | Demonstrates adding holes to a polygon. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L132-L163) |
| **5. Circle**<br/>`maps_android_shapes_circles_circleoptions` | Creates a simple circle with center and radius. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L171-L188) |
| **6. Circle Click Event**<br/>`maps_android_shapes_circles_events` | Sets a click listener to toggle circle stroke color. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L196-L211) |
| **7. Custom Polyline Appearance**<br/>`maps_android_shapes_custom_appearances` | Shows custom caps, joints, patterns, and geodesic settings. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L219-L227) |
| **8. Associate Data Tag**<br/>`maps_android_shapes_associate_data` | Attaches custom tag metadata to a polyline. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L254-L268) |
| **9. Multicolored Polyline Spans**<br/>`maps_android_polyline_multicolored` | Creates a polyline with multiple StyleSpans. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L276-L283) |
| **10. Multicolored Gradient Polyline**<br/>`maps_android_polyline_gradient` | Creates a polyline with gradient StrokeStyle span. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L291-L304) |
| **11. Stamped Texture Polyline**<br/>`maps_android_polyline_stamped` | Creates a polyline styled with a custom texture stamp. | [ShapesSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/ShapesSnippets.kt#L312-L321) |

#### Street View
> Snippets demonstrating Google Street View integration, camera movements, and panorama configuration.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Launch Street View Activity**<br/>- | Displays an interactive Google Street View panorama initialized in San Francisco. | [StreetViewSnippets.kt:35](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/StreetViewSnippets.kt#L35) |
| **2. Set Panorama Location**<br/>- | Demonstrates setting Street View panorama locations using coordinates, radius, and source. | [StreetViewSnippets.kt:44](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/StreetViewSnippets.kt#L44) |
| **3. Zoom Panorama**<br/>- | Demonstrates adjusting zoom level on Street View panorama camera. | [StreetViewSnippets.kt:52](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/StreetViewSnippets.kt#L52) |
| **4. Animate Camera**<br/>- | Demonstrates animating Street View panorama bearing and tilt over duration. | [StreetViewSnippets.kt:63](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/StreetViewSnippets.kt#L63) |

#### Utility Library
> Snippets demonstrating marker clustering, heatmaps, GeoJSON, KML, and Multilayer managers.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Marker Clustering Setup**<br/>`maps_android_utils_clustering_cluster_manager` | Initializes a ClusterManager with a set of 10 items. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L104-L121) |
| **2. Disable Cluster Animation**<br/>`maps_android_utils_clustering_animation_off` | Disables animation on the ClusterManager. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L148-L150) |
| **3. Add Clustering Info Window Item**<br/>`maps_android_utils_clustering_info_window` | Adds an item with an explicit title and snippet to the ClusterManager. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L160-L174) |
| **3b. Clear Cluster Items**<br/>`maps_android_utils_clustering_clear` | Clears all items from the ClusterManager. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L183-L186) |
| **3c. Remove Single Cluster Item**<br/>`maps_android_utils_clustering_remove` | Removes a single item from the ClusterManager. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L194-L198) |
| **3d. Cluster Listeners**<br/>`maps_android_utils_clustering_listeners` | Sets click listeners on ClusterManager. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L206-L219) |
| **4. GeoJSON Layer from JSONObject**<br/>`maps_android_util_geojson_add_jsonobject` | Imports a GeoJSONLayer using a raw JSONObject. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L227-L233) |
| **5. Add GeoJSON Layer from File**<br/>`maps_android_util_geojson_add_file` | Imports a GeoJSONLayer using a raw resource file. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L241-L243) |
| **5b. Remove GeoJSON Layer**<br/>`maps_android_util_geojson_remove_layer` | Removes the imported GeoJSONLayer from the map. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L258-L260) |
| **6. GeoJSON Features and Styling**<br/>`maps_android_util_geojson_point_feature` | Adds a custom PointFeature to a GeoJsonLayer and configures its styles. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L271-L275) |
| **7. KML Layer from File Resource**<br/>`maps_android_utils_kml_add_file` | Displays a map focused on Google Campus in Mountain View with imported KML 3D building polygons. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L336-L338) |
| **8. KML Layer from Input Stream**<br/>`maps_android_utils_kml_add_input_stream` | Displays a map focused on Google Campus in Mountain View with imported KML polygons via InputStream. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L348-L351) |
| **9. Simple Heatmap**<br/>`maps_android_utils_heatmap_simple` | Creates a simple Heatmap from raw resource coordinates. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L400-L418) |
| **10. Add Custom Heatmap**<br/>`maps_android_utils_heatmap_customize` | Creates a heatmap with custom color gradients, opacity, and weighted coordinates. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L445-L466) |
| **10b. Remove Custom Heatmap**<br/>`maps_android_utils_heatmap_remove` | Removes the custom heatmap from the map. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L487-L489) |
| **11. Multilayer Collections Init**<br/>`maps_android_utils_multilayer_init` | Initializes Managers and layers for GeoJSON, KML and ClusterManager sharing the map's state. | [UtilsSnippets.kt](kotlin-app/src/main/java/com/example/snippets/kotlin/snippets/UtilsSnippets.kt#L498-L503) |

### ☕ Java Snippets Catalog

#### Camera
> Snippets demonstrating camera controls, zoom constraints, bounds, and animations.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Zoom Level Constraints**<br/>`maps_android_camera_and_view_zoom_level` | Sets minimum and maximum zoom preference bounds on the camera. | [CameraControlSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CameraControlSnippets.java#L45-L48) |
| **2. Fit Camera To Bounds (Australia)**<br/>`maps_android_camera_and_view_setting_boundaries` | Moves the camera once to fit geographic boundaries (Australia) within the viewport. Note: This frames the map initially, but does not restrict subsequent user panning. | [CameraControlSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CameraControlSnippets.java#L56-L62) |
| **3. Centering Map Within An Area**<br/>`maps_android_camera_and_view_centering_within_area` | Centers the camera on the center point of geographic bounds (Australia) at a zoom level of 10. | [CameraControlSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CameraControlSnippets.java#L70-L76) |
| **4. Panning Restrictions**<br/>`maps_android_camera_and_view_panning_restrictions` | Restricts the camera target to specified geographic boundaries (Adelaide). | [CameraControlSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CameraControlSnippets.java#L84-L93) |
| **5. Common Map Movements**<br/>`maps_android_camera_and_view_common_map_movements` | Demonstrates camera movement, animation, zoom, and CameraPosition builder. | [CameraControlSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CameraControlSnippets.java#L101-L122) |

#### Cloud Customization
> Snippets demonstrating Google Cloud Console map customization capabilities loaded via Map ID.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Reusable Map Style**<br/>`maps_android_cloud_reusable_style` | Demonstrates loading a reusable, cross-platform map style created in Google Cloud Console. | [CloudCustomizationSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CloudCustomizationSnippets.java#L46-L51) |
| **2. Style Roads and Polygons**<br/>`maps_android_cloud_style_roads` | Loads a Map ID configured with custom road network and geometry polygon styles. | [CloudCustomizationSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CloudCustomizationSnippets.java#L59-L64) |
| **3. Feature Visibility Toggling**<br/>`maps_android_cloud_feature_visibility` | Loads a Map ID configured in Cloud Console to display or hide specific base map feature layers. | [CloudCustomizationSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CloudCustomizationSnippets.java#L72-L77) |
| **4. Style Icons and Text Labels**<br/>`maps_android_cloud_style_labels` | Loads a Map ID configured with custom typography, label colors, and POI icon styles. | [CloudCustomizationSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CloudCustomizationSnippets.java#L85-L90) |
| **5. Zoom-Level Styling**<br/>`maps_android_cloud_zoom_styling` | Loads a Map ID configured to apply distinct map styles dynamically across zoom levels. | [CloudCustomizationSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CloudCustomizationSnippets.java#L98-L103) |
| **6. POI Density Filtering**<br/>`maps_android_cloud_poi_density` | Loads a Map ID configured with adjusted business and point-of-interest display density. | [CloudCustomizationSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CloudCustomizationSnippets.java#L111-L116) |
| **7. Style Buildings**<br/>`maps_android_cloud_style_buildings` | Loads a Map ID configured with customized 2D and 3D building footprint styles. | [CloudCustomizationSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CloudCustomizationSnippets.java#L124-L129) |
| **8. Style Landmarks**<br/>`maps_android_cloud_style_landmarks` | Loads a Map ID configured with specialized styling for prominent natural and urban landmarks. | [CloudCustomizationSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/CloudCustomizationSnippets.java#L137-L142) |

#### Custom Geospatial Datasets
> Snippets demonstrating custom Cloud geospatial dataset feature layers, attribute styling, and click events.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Dataset - Boulder Trails**<br/>`maps_android_dds_boulder_trails_java` | Loads Boulder Colorado Trails dataset. Styles lines green (Easy), blue (Moderate), or red (Difficult). Line width indicates dog permissions. | [DatasetLayerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/DatasetLayerSnippets.java#L71-L111) |
| **2. Dataset - NYC Squirrels**<br/>`maps_android_dds_nyc_squirrels_java` | Loads NYC Squirrel Sightings dataset. Renders sightings points colored by primary fur color (Black, Cinnamon, Gray). | [DatasetLayerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/DatasetLayerSnippets.java#L178-L220) |
| **3. Dataset - Kyoto Temples (Clickable)**<br/>`maps_android_dds_kyoto_temples_java` | Loads Kyoto Temples dataset. Highlights temple boundary polygons in Blue, and updates clicked temple areas to Yellow. | [DatasetLayerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/DatasetLayerSnippets.java#L237-L309) |

#### Data-Driven Boundary Styling
> Snippets demonstrating administrative boundary feature layers, polygon styling, and click events.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Boundaries - Localities (Hana, HI)**<br/>`maps_android_dds_locality_boundary_java` | Loads LOCALITY layer. Styles Hana, Hawaii (Place ID: ChIJ0zQtYiWsVHkRk8lRoB1RNPo) with purple fill and border. Centers camera. | [DataDrivenBoundarySnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/DataDrivenBoundarySnippets.java#L60-L90) |
| **2. Boundaries - Admin Area 1 (States)**<br/>`maps_android_dds_state_boundaries_java` | Loads ADMINISTRATIVE_AREA_LEVEL_1 layer. Styles state/provincial boundaries with random colors based on Place ID hashes. Centers over US. | [DataDrivenBoundarySnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/DataDrivenBoundarySnippets.java#L98-L128) |
| **3. Boundaries - Countries (Interactive)**<br/>`maps_android_dds_country_interactive_java` | Loads COUNTRY layer. Renders countries with 10% black fill. Taps toggle country coloring between light black and 33% opaque red. | [DataDrivenBoundarySnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/DataDrivenBoundarySnippets.java#L136-L202) |

#### Events
> Snippets demonstrating clicks, camera events, POI clicks and indoor building levels.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. MapView Disable Click Event**<br/>`maps_android_events_disable_clicks_mapview` | Disables click events on a MapView directly. | [EventsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/EventsSnippets.java#L53-L60) |
| **2. Map Fragment Disable Click Event**<br/>`maps_android_events_disable_clicks_mapfragment` | Disables click events on a SupportMapFragment view. | [EventsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/EventsSnippets.java#L68-L79) |
| **3. Active Indoor Building Level**<br/>`maps_android_events_active_level` | Retrieves the active level of the currently focused indoor building. | [EventsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/EventsSnippets.java#L87-L93) |
| **4. POI Click Listener**<br/>`maps_android_on_poi_click_demo` | Registers a listener for clicks on Point of Interests (POIs). | [EventsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/EventsSnippets.java#L101-L112) |

#### Map Initialization
> Snippets showing how to initialize, configure map options, types, and renderers.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Basic Map Activity**<br/>`maps_android_mapsactivity` | Initializes a map and adds a marker in Sydney, Australia. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L55-L62) |
| **2. Map Fragment Transaction**<br/>`maps_android_map_fragment` | Shows how to add a SupportMapFragment dynamically. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L70-L78) |
| **3. Set Map Type**<br/>`maps_android_map_type` | Sets the map type to Hybrid. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L86-L91) |
| **4. Google Map Options**<br/>`maps_android_google_map_options` | Shows how to build and configure GoogleMapOptions. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L99-L101) |
| **5. Support Map Fragment Map ID**<br/>`maps_android_support_map_fragment_map_id` | Configures a SupportMapFragment with a Map ID. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L116-L120) |
| **6. MapView Map ID**<br/>`maps_android_mapview_map_id` | Configures a MapView with a Map ID. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L128-L132) |
| **7. Lite Mode Options**<br/>`maps_android_lite_mode_options` | Configures GoogleMapOptions for Lite Mode. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L140-L143) |
| **8. Cloud-based Map Styling**<br/>`maps_android_cloud_based_map_styling` | Loads a MapFragment configured with a Map ID from resources. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L151-L155) |
| **9. Renderer Opt-In**<br/>`maps_android_renderer_opt_in` | Requests the latest Map renderer version. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L163-L174) |
| **10. Set Map Color Scheme**<br/>`maps_android_map_color_scheme` | Configures the map color scheme (Dark Mode / Light Mode). | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L182-L185) |
| **11. Enable Traffic Layer**<br/>`maps_android_traffic_layer` | Toggles the real-time traffic overlay on the map. | [MapInitSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MapInitSnippets.java#L193-L195) |

#### Markers
> Snippets demonstrating marker creation, styling, customization, and events.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Add a Marker**<br/>`maps_android_markers_add_a_marker` | Adds a simple marker in Sydney, Australia. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L52-L71) |
| **2. Draggable Marker**<br/>`maps_android_markers_draggable` | Creates a draggable marker at Perth. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L79-L86) |
| **3. Default Icon Marker**<br/>`maps_android_markers_default_icon` | Adds a default marker at Melbourne. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L94-L99) |
| **4. Custom Marker Color**<br/>`maps_android_markers_custom_marker_color` | Adds an azure-colored marker at Melbourne. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L107-L113) |
| **5. Marker Opacity**<br/>`maps_android_markers_opacity` | Adds a semi-transparent marker at Melbourne. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L121-L126) |
| **6. Custom Marker Image**<br/>`maps_android_markers_image` | Adds a marker with a custom arrow image resource. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L134-L142) |
| **7. Flat Marker**<br/>`maps_android_markers_flatten` | Creates a flat marker that rotates with the map. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L150-L156) |
| **8. Rotate Marker**<br/>`maps_android_markers_rotate` | Rotates a marker 90 degrees around its anchor. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L164-L171) |
| **9. Marker Z-Index**<br/>`maps_android_markers_z_index` | Sets a high z-index on a marker. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L179-L184) |
| **10. Marker Click Listener & Tag**<br/>`maps_android_markers_tag_sample` | Associates click counts with markers using tag objects. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L192-L232) |
| **11. Add Info Window**<br/>`maps_android_info_windows_add` | Creates a marker with title and snippet details. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L240-L247) |
| **12. Show/Hide Info Window**<br/>`maps_android_info_windows_show_hide` | Creates a marker and programmatically triggers its info window. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L255-L265) |
| **13. Info Window Click Listener**<br/>`maps_android_info_windows_click_listener` | Listens to clicks on info windows. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L273-L281) |
| **14. Marker Collision Behavior**<br/>`maps_android_marker_collision` | Configures collision behavior on an AdvancedMarker. | [MarkerSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MarkerSnippets.java#L289-L298) |

#### My Location Layer
> Snippets demonstrating my location layer setup and button clicks.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Enable My Location Layer**<br/>`maps_android_my_location` | Enables the my location layer and registers click listeners. | [MyLocationSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/MyLocationSnippets.java#L49-L69) |

#### Overlays
> Snippets demonstrating GroundOverlays and TileOverlays.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Ground Overlays**<br/>`maps_android_ground_overlays_add` | Creates, retains, changes and removes a ground overlay. | [OverlaySnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/OverlaySnippets.java#L52-L59) |
| **2. Ground Overlay Position Image Location**<br/>`maps_android_ground_overlays_position_image_location` | Defines GroundOverlayOptions positioning via anchor and LatLng. | [OverlaySnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/OverlaySnippets.java#L100-L105) |
| **3. Ground Overlay Position Image Bounds**<br/>`maps_android_ground_overlays_position_image_bounds` | Defines GroundOverlayOptions positioning via LatLngBounds. | [OverlaySnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/OverlaySnippets.java#L113-L120) |
| **4. Tile Overlays Add**<br/>`maps_android_tile_overlays_add` | Adds a TileOverlay with a custom UrlTileProvider. | [OverlaySnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/OverlaySnippets.java#L128-L170) |
| **5. Tile Overlays Transparency**<br/>`maps_android_tile_overlays_transparency` | Adds and toggles transparency of a TileOverlay. | [OverlaySnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/OverlaySnippets.java#L186-L202) |

#### Shapes
> Snippets demonstrating shapes, custom styled polylines, polygons, and circles.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Simple Polyline**<br/>`maps_android_shapes_polylines_polylineoptions` | Creates a polyline and adds points to define a rectangle. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L64-L84) |
| **2. Simple Polygon**<br/>`maps_android_shapes_polygons_polygonoptions` | Creates a polygon defining a rectangle. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L92-L103) |
| **3. Polygon Autocompletion**<br/>`maps_android_shapes_polygons_autocompletion` | Demonstrates how uncompleted shapes are closed automatically. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L111-L126) |
| **4. Hollow Polygon**<br/>`maps_android_shapes_polygons_hollow` | Demonstrates adding holes to a polygon. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L134-L159) |
| **5. Circle**<br/>`maps_android_shapes_circles_circleoptions` | Creates a simple circle with center and radius. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L167-L184) |
| **6. Circle Click Event**<br/>`maps_android_shapes_circles_events` | Sets a click listener to toggle circle stroke color. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L192-L209) |
| **7. Custom Polyline Appearance**<br/>`maps_android_shapes_custom_appearances` | Shows custom caps, joints, patterns, and geodesic settings. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L217-L223) |
| **8. Associate Data Tag**<br/>`maps_android_shapes_associate_data` | Attaches custom tag metadata to a polyline. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L250-L261) |
| **9. Multicolored Polyline Spans**<br/>`maps_android_polyline_multicolored` | Creates a polyline with multiple StyleSpans. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L269-L274) |
| **10. Multicolored Gradient Polyline**<br/>`maps_android_polyline_gradient` | Creates a polyline with gradient StrokeStyle span. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L282-L286) |
| **11. Stamped Texture Polyline**<br/>`maps_android_polyline_stamped` | Creates a polyline styled with a custom texture stamp. | [ShapesSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/ShapesSnippets.java#L294-L301) |

#### Street View
> Snippets demonstrating Google Street View integration, camera movements, and panorama configuration.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Launch Street View Activity**<br/>- | Displays an interactive Google Street View panorama initialized in San Francisco. | [StreetViewSnippets.java:43](java-app/src/main/java/com/example/snippets/java/snippets/StreetViewSnippets.java#L43) |
| **2. Set Panorama Location**<br/>- | Demonstrates setting Street View panorama locations using coordinates, radius, and source. | [StreetViewSnippets.java:52](java-app/src/main/java/com/example/snippets/java/snippets/StreetViewSnippets.java#L52) |
| **3. Zoom Panorama**<br/>- | Demonstrates adjusting zoom level on Street View panorama camera. | [StreetViewSnippets.java:60](java-app/src/main/java/com/example/snippets/java/snippets/StreetViewSnippets.java#L60) |
| **4. Animate Camera**<br/>- | Demonstrates animating Street View panorama bearing and tilt over duration. | [StreetViewSnippets.java:71](java-app/src/main/java/com/example/snippets/java/snippets/StreetViewSnippets.java#L71) |

#### Utility Library
> Snippets demonstrating marker clustering, heatmaps, GeoJSON, KML, and Multilayer managers.

| Feature & Region Tag | Description | Source |
| :--- | :--- | :--- |
| **1. Marker Clustering Setup**<br/>`maps_android_utils_clustering_cluster_manager` | Initializes a ClusterManager with a set of 10 items. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L125-L141) |
| **2. Disable Cluster Animation**<br/>`maps_android_utils_clustering_animation_off` | Disables animation on the ClusterManager. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L167-L169) |
| **3. Add Clustering Info Window Item**<br/>`maps_android_utils_clustering_info_window` | Adds an item with an explicit title and snippet to the ClusterManager. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L179-L193) |
| **3b. Clear Cluster Items**<br/>`maps_android_utils_clustering_clear` | Clears all items from the ClusterManager. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L202-L207) |
| **3c. Remove Single Cluster Item**<br/>`maps_android_utils_clustering_remove` | Removes a single item from the ClusterManager. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L215-L221) |
| **3d. Cluster Listeners**<br/>`maps_android_utils_clustering_listeners` | Sets click listeners on ClusterManager. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L229-L253) |
| **4. GeoJSON Layer from JSONObject**<br/>`maps_android_util_geojson_add_jsonobject` | Imports a GeoJSONLayer using a raw JSONObject. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L261-L267) |
| **5. Add GeoJSON Layer from File**<br/>`maps_android_util_geojson_add_file` | Imports a GeoJSONLayer using a raw resource file. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L275-L277) |
| **5b. Remove GeoJSON Layer**<br/>`maps_android_util_geojson_remove_layer` | Removes the imported GeoJSONLayer from the map. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L292-L294) |
| **6. GeoJSON Features and Styling**<br/>`maps_android_util_geojson_point_feature` | Adds a custom PointFeature to a GeoJsonLayer and configures its styles. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L305-L310) |
| **7. KML Layer from File Resource**<br/>`maps_android_utils_kml_add_file` | Displays a map focused on Google Campus in Mountain View with imported KML 3D building polygons. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L374-L376) |
| **8. KML Layer from Input Stream**<br/>`maps_android_utils_kml_add_input_stream` | Displays a map focused on Google Campus in Mountain View with imported KML polygons via InputStream. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L386-L389) |
| **9. Simple Heatmap**<br/>`maps_android_utils_heatmap_simple` | Creates a simple Heatmap from raw resource coordinates. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L442-L460) |
| **10. Add Custom Heatmap**<br/>`maps_android_utils_heatmap_customize` | Creates a heatmap with custom color gradients, opacity, and weighted coordinates. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L488-L510) |
| **10b. Remove Custom Heatmap**<br/>`maps_android_utils_heatmap_remove` | Removes the custom heatmap from the map. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L536-L538) |
| **11. Multilayer Collections Init**<br/>`maps_android_utils_multilayer_init` | Initializes Managers and layers for GeoJSON, KML and ClusterManager sharing the map's state. | [UtilsSnippets.java](java-app/src/main/java/com/example/snippets/java/snippets/UtilsSnippets.java#L547-L552) |

