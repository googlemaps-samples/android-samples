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

package com.example.common_ui.catalog

/**
 * Master catalog registry of all Google Maps Platform samples, snippets, and demos.
 *
 * Organizes samples by Category, Complexity, Framework, and Hashtags with rich HTML expectations.
 */
object SampleCatalogRegistry {

    val SAMPLES: List<SampleItem> = listOf(
        // ==========================================
        // 🗺️ MAP INITIALIZATION & LIFECYCLE
        // ==========================================
        SampleItem(
            id = "basic_map",
            title = "Basic Map",
            description = "Fundamental map instantiation, lifecycle binding, and default camera centering.",
            category = "Map Initialization",
            complexity = Complexity.SNIPPET,
            tags = listOf("#map", "#init", "#lifecycle", "#quickstart"),
            purpose = "Demonstrates clean, minimal map instantiation using SupportMapFragment.",
            successCriteria = "The map loads default vector tiles cleanly centered at the initial coordinates with working gestures.",
            failureIndicators = "Grey tiles (missing API key or auth mismatch), crash on back navigation, or map failing to unpause.",
            kotlinActivity = "com.example.kotlindemos.BasicMapDemoActivity",
            javaActivity = "com.example.mapdemo.BasicMapDemoActivity"
        ),
        SampleItem(
            id = "programmatic_map",
            title = "Programmatic Map",
            description = "Instantiating and attaching a SupportMapFragment entirely in code without XML layout.",
            category = "Map Initialization",
            complexity = Complexity.SNIPPET,
            tags = listOf("#programmatic", "#fragment", "#dynamic", "#init"),
            purpose = "Shows how to dynamically instantiate and attach SupportMapFragment using FragmentManager transactions.",
            successCriteria = "Map attaches dynamically to the container layout and renders correctly on launch.",
            failureIndicators = "Blank screen, fragment transaction exception, or duplicate map fragments on orientation change.",
            kotlinActivity = "com.example.kotlindemos.ProgrammaticDemoActivity",
            javaActivity = "com.example.mapdemo.ProgrammaticDemoActivity"
        ),
        SampleItem(
            id = "raw_mapview",
            title = "Raw MapView",
            description = "Direct MapView embedding with explicit Activity lifecycle forwarding.",
            category = "Map Initialization",
            complexity = Complexity.SIMPLE,
            tags = listOf("#mapview", "#lifecycle", "#embedding"),
            purpose = "Shows how to embed MapView directly in a layout and forward all Activity lifecycle callbacks.",
            successCriteria = "MapView loads tiles and pauses/resumes correctly when backgrounded and foregrounded.",
            failureIndicators = "Black rendering surface, memory leaks on orientation change, or crash when onLowMemory is triggered.",
            kotlinActivity = "com.example.kotlindemos.RawMapViewDemoActivity",
            javaActivity = "com.example.mapdemo.RawMapViewDemoActivity"
        ),
        SampleItem(
            id = "retain_map",
            title = "Retained Map",
            description = "Retaining map state across runtime configuration changes (screen rotations).",
            category = "Map Initialization",
            complexity = Complexity.SIMPLE,
            tags = listOf("#retain", "#configuration", "#rotation", "#lifecycle"),
            purpose = "Demonstrates retaining map instance state across orientation changes without reloading tiles.",
            successCriteria = "Rotating device does not flash or re-initialize map state; markers and camera remain intact.",
            failureIndicators = "Map resets to initial position or flashes white/black on rotation.",
            kotlinActivity = "com.example.kotlindemos.RetainMapDemoActivity",
            javaActivity = "com.example.mapdemo.RetainMapDemoActivity"
        ),
        SampleItem(
            id = "multimap",
            title = "Multi-Map View",
            description = "Rendering multiple independent GoogleMap instances in a single activity layout.",
            category = "Map Initialization",
            complexity = Complexity.ADVANCED,
            tags = listOf("#multimap", "#multiple", "#layout", "#rendering"),
            purpose = "Shows how to render and control multiple independent GoogleMap instances concurrently in one screen.",
            successCriteria = "All 4 map fragments render distinct geographic locations simultaneously with smooth scrolling.",
            failureIndicators = "GL context collision, thread locking, or tile stuttering when dragging multiple maps.",
            kotlinActivity = "com.example.kotlindemos.MultiMapDemoActivity",
            javaActivity = "com.example.mapdemo.MultiMapDemoActivity"
        ),
        SampleItem(
            id = "map_in_pager",
            title = "Map in ViewPager",
            description = "Hosting MapView instances inside a swipeable ViewPager2 structure.",
            category = "Map Initialization",
            complexity = Complexity.ADVANCED,
            tags = listOf("#viewpager", "#pager", "#fragments", "#touch"),
            purpose = "Demonstrates embedding maps inside horizontal ViewPager pages with proper gesture delegation.",
            successCriteria = "Horizontal page swipes work smoothly without getting swallowed by map panning gestures.",
            failureIndicators = "Map gestures intercept pager swipes making it impossible to change pages.",
            kotlinActivity = "com.example.kotlindemos.MapInPagerDemoActivity",
            javaActivity = "com.example.mapdemo.MapInPagerDemoActivity"
        ),

        // ==========================================
        // 🎥 CAMERA CONTROLS & VIEWPORT
        // ==========================================
        SampleItem(
            id = "camera_demo",
            title = "Camera Controls & Animation",
            description = "Programmatic camera panning, zooming, tilt, bearing, and smooth animations.",
            category = "Camera Controls",
            complexity = Complexity.SIMPLE,
            tags = listOf("#camera", "#animation", "#bearing", "#tilt", "#zoom", "#pan"),
            purpose = "Demonstrates programmatic camera movements, animated transitions, tilt angles, and bearing rotations.",
            successCriteria = "Buttons animate camera smoothly with custom durations, stops, and rotation angles.",
            failureIndicators = "Jerky animations, unexpected camera jumps, or tilt angle exceeding platform constraints.",
            kotlinActivity = "com.example.kotlindemos.CameraDemoActivity",
            javaActivity = "com.example.mapdemo.CameraDemoActivity"
        ),
        SampleItem(
            id = "camera_clamping",
            title = "Camera Clamping & Bounds",
            description = "Constraining camera viewport to LatLngBounds and dynamic min/max zoom limits.",
            category = "Camera Controls",
            complexity = Complexity.SIMPLE,
            tags = listOf("#camera", "#clamping", "#bounds", "#zoomlimits", "#latlngbounds"),
            purpose = "Demonstrates restricting camera panning to a specific bounding box (Adelaide/Pacific) and zoom slider limits.",
            successCriteria = "User cannot pan the camera outside the clamped region; zoom sliders enforce min/max bounds immediately.",
            failureIndicators = "Camera pans outside bounding box or resetting bounds fails when selecting 'Reset Bounds'.",
            kotlinActivity = "com.example.kotlindemos.CameraClampingDemoActivity",
            javaActivity = "com.example.mapdemo.CameraClampingDemoActivity"
        ),
        SampleItem(
            id = "visible_region",
            title = "Visible Region & Projection",
            description = "Querying current viewport bounding coordinates via GoogleMap.projection.",
            category = "Camera Controls",
            complexity = Complexity.SIMPLE,
            tags = listOf("#camera", "#projection", "#visibleregion", "#latlngbounds"),
            purpose = "Demonstrates reading GoogleMap.projection.visibleRegion and calculating viewport bounds dynamically.",
            successCriteria = "Bounding coordinates update live in the UI as the camera pans and zooms.",
            failureIndicators = "Projection returns null or stale LatLng bounds after camera idle.",
            kotlinActivity = "com.example.kotlindemos.VisibleRegionDemoActivity",
            javaActivity = "com.example.mapdemo.VisibleRegionDemoActivity"
        ),

        // ==========================================
        // 📍 MARKERS & INFO WINDOWS
        // ==========================================
        SampleItem(
            id = "advanced_markers",
            title = "Advanced Markers & Pins",
            description = "Modern PinConfig pins, custom glyphs, badge icon views, and collision behavior.",
            category = "Markers & Overlays",
            complexity = Complexity.ADVANCED,
            tags = listOf("#markers", "#advancedmarkers", "#pinconfig", "#collision", "#badges", "#mapid"),
            purpose = "Demonstrates Cloud-backed Advanced Markers with custom colors, pin glyphs, collision behaviors, and custom View icons.",
            successCriteria = "Custom colored pins and badge icon views render sharply at correct anchor points with collision handling.",
            failureIndicators = "Pins render as default red markers (missing Map ID), collision behavior ignored, or badge text blurry.",
            kotlinActivity = "com.example.kotlindemos.AdvancedMarkersDemoActivity",
            javaActivity = "com.example.mapdemo.AdvancedMarkersDemoActivity"
        ),
        SampleItem(
            id = "marker_demo",
            title = "Standard Markers & Info Windows",
            description = "Placing markers, custom icons, draggable pins, and custom info window layouts.",
            category = "Markers & Overlays",
            complexity = Complexity.SIMPLE,
            tags = listOf("#markers", "#infowindow", "#draggable", "#icons", "#anchor"),
            purpose = "Demonstrates adding standard markers with alpha, rotation, draggable pins, and custom InfoWindowAdapter views.",
            successCriteria = "Tapping markers displays custom info windows with formatted content; dragging pins updates position.",
            failureIndicators = "Info window clicks not detected or custom snippet styling not applied.",
            kotlinActivity = "com.example.kotlindemos.MarkerDemoActivity",
            javaActivity = "com.example.mapdemo.MarkerDemoActivity"
        ),
        SampleItem(
            id = "marker_close_retap",
            title = "Close Info Window on Retap",
            description = "Custom marker click listener to toggle info window visibility on subsequent taps.",
            category = "Markers & Overlays",
            complexity = Complexity.SNIPPET,
            tags = listOf("#markers", "#infowindow", "#click", "#toggle"),
            purpose = "Shows how to toggle info window open/closed when re-tapping an already selected marker.",
            successCriteria = "Tapping marker opens info window; tapping same marker again closes it.",
            failureIndicators = "Info window stays stuck open on repeated taps.",
            kotlinActivity = "com.example.kotlindemos.MarkerCloseInfoWindowOnRetapDemoActivity",
            javaActivity = "com.example.mapdemo.MarkerCloseInfoWindowOnRetapDemoActivity"
        ),

        // ==========================================
        // 🔷 SHAPES & GEOMETRY
        // ==========================================
        SampleItem(
            id = "polygons",
            title = "Polygons & Holes",
            description = "Drawing geodesic polygons with fill colors, stroke patterns, click events, and interior holes.",
            category = "Shapes & Geometry",
            complexity = Complexity.SIMPLE,
            tags = listOf("#shapes", "#polygons", "#holes", "#geometry", "#stroke", "#fill"),
            purpose = "Demonstrates drawing styled polygons with interior holes (donut polygons), click listeners, and stroke caps.",
            successCriteria = "Polygons render with specified fill opacity and interior cutout holes properly subtracted.",
            failureIndicators = "Holes not rendering as transparent cutouts or stroke color incorrect.",
            kotlinActivity = "com.example.kotlindemos.PolygonDemoActivity",
            javaActivity = "com.example.mapdemo.PolygonDemoActivity"
        ),
        SampleItem(
            id = "polylines",
            title = "Polylines & Patterns",
            description = "Drawing polylines with joint types, dash/dot stroke patterns, joint styles, and spans.",
            category = "Shapes & Geometry",
            complexity = Complexity.SIMPLE,
            tags = listOf("#shapes", "#polylines", "#patterns", "#dashes", "#stroke", "#routes"),
            purpose = "Demonstrates drawing customizable polylines with dash/gap patterns, round end caps, and bevel joints.",
            successCriteria = "Polylines render crisp dashed and dotted stroke lines along coordinate vertices.",
            failureIndicators = "Line caps distorted or custom pattern ignored on high-DPI screens.",
            kotlinActivity = "com.example.kotlindemos.PolylineDemoActivity",
            javaActivity = "com.example.mapdemo.PolylineDemoActivity"
        ),
        SampleItem(
            id = "circles",
            title = "Circles & Geodesic Radii",
            description = "Drawing geographic circles with dynamic center drag, radius sliders, and stroke styling.",
            category = "Shapes & Geometry",
            complexity = Complexity.SIMPLE,
            tags = listOf("#shapes", "#circles", "#radius", "#geodesic"),
            purpose = "Demonstrates drawing circles with radius defined in meters and dynamic updates via seekbars.",
            successCriteria = "Adjusting radius slider dynamically updates circle boundary in real-time.",
            failureIndicators = "Circle distorted or radius math inaccurate across high latitudes.",
            kotlinActivity = "com.example.kotlindemos.CircleDemoActivity",
            javaActivity = "com.example.mapdemo.CircleDemoActivity"
        ),

        // ==========================================
        // 🔲 OVERLAYS & TILES
        // ==========================================
        SampleItem(
            id = "ground_overlay",
            title = "Ground Overlays",
            description = "Anchoring raster bitmap images to geographic LatLngBounds on the map surface.",
            category = "Overlays & Tiles",
            complexity = Complexity.SIMPLE,
            tags = listOf("#overlays", "#groundoverlay", "#images", "#bounds", "#transparency"),
            purpose = "Demonstrates overlaying historical or custom aerial images onto the map with transparency sliders.",
            successCriteria = "Historical Newark map image appears pinned to geographic coordinates with adjustable transparency.",
            failureIndicators = "Overlay image stretched/misaligned or opacity slider unresponsive.",
            kotlinActivity = "com.example.kotlindemos.GroundOverlayDemoActivity",
            javaActivity = "com.example.mapdemo.GroundOverlayDemoActivity"
        ),
        SampleItem(
            id = "tile_overlay",
            title = "Tile Overlays & TileProvider",
            description = "Custom TileProvider rendering coordinate grid tiles and custom imagery.",
            category = "Overlays & Tiles",
            complexity = Complexity.SIMPLE,
            tags = listOf("#overlays", "#tiles", "#tileprovider", "#customtiles"),
            purpose = "Demonstrates generating custom raster tiles on the fly using a custom TileProvider (coordinate overlays).",
            successCriteria = "Tile grid numbers (x, y, zoom) render cleanly over the base map.",
            failureIndicators = "Tile rendering blocks UI thread or tiles fail to fetch on pan.",
            kotlinActivity = "com.example.kotlindemos.TileOverlayDemoActivity",
            javaActivity = "com.example.mapdemo.TileOverlayDemoActivity"
        ),

        // ==========================================
        // 📍 LOCATION & SENSORS
        // ==========================================
        SampleItem(
            id = "my_location",
            title = "My Location Layer",
            description = "Enabling blue dot location indicator and My Location button with runtime permissions.",
            category = "Location & Sensors",
            complexity = Complexity.SIMPLE,
            tags = listOf("#location", "#mylocation", "#permissions", "#bluedot"),
            purpose = "Demonstrates requesting ACCESS_FINE_LOCATION permissions and enabling the blue dot location layer.",
            successCriteria = "Tapping My Location button centers camera on user's current GPS position.",
            failureIndicators = "Permission denial causes unhandled crash or location button missing.",
            kotlinActivity = "com.example.kotlindemos.MyLocationDemoActivity",
            javaActivity = "com.example.mapdemo.MyLocationDemoActivity"
        ),
        SampleItem(
            id = "location_source",
            title = "Custom LocationSource",
            description = "Providing a custom mock LocationSource for simulated GPS navigation playback.",
            category = "Location & Sensors",
            complexity = Complexity.ADVANCED,
            tags = listOf("#location", "#locationsource", "#mock", "#simulation", "#navigation"),
            purpose = "Shows how to feed programmatic coordinates into the GoogleMap location layer using a custom LocationSource.",
            successCriteria = "The blue dot animates smoothly along a simulated route when navigation starts.",
            failureIndicators = "Blue dot fails to move or location updates cause memory leaks.",
            kotlinActivity = "com.example.kotlindemos.LocationSourceDemoActivity",
            javaActivity = "com.example.mapdemo.LocationSourceDemoActivity"
        ),

        // ==========================================
        // 🚶 STREET VIEW PANORAMAS
        // ==========================================
        SampleItem(
            id = "split_street_view",
            title = "Split Street View & Map Sync",
            description = "Dual synchronized view: draggable 2D Pegman marker synchronized with 3D Street View panorama.",
            category = "Street View",
            complexity = Complexity.ADVANCED,
            tags = listOf("#streetview", "#panorama", "#pegman", "#sync", "#bidirectional"),
            purpose = "Demonstrates bidirectional synchronization: dragging map Pegman updates panorama; walking Street View moves map marker.",
            successCriteria = "Moving Pegman on map instantly loads new 360 panorama; street navigation rotates Pegman bearing.",
            failureIndicators = "Infinite update feedback loops, Pegman desyncing from panorama, or FAB jump failing.",
            kotlinActivity = "com.example.kotlindemos.SplitStreetViewPanoramaAndMapDemoActivity",
            javaActivity = "com.example.mapdemo.SplitStreetViewPanoramaAndMapDemoActivity"
        ),
        SampleItem(
            id = "street_view_events",
            title = "Street View Events & Gestures",
            description = "Capturing panorama change events, camera tilts, orientation changes, and user clicks.",
            category = "Street View",
            complexity = Complexity.SIMPLE,
            tags = listOf("#streetview", "#events", "#listeners", "#camera"),
            purpose = "Demonstrates registering event listeners for panorama changes, camera movements, and point taps in Street View.",
            successCriteria = "Status text displays real-time pano ID, bearing, tilt, and click coordinates.",
            failureIndicators = "Events not firing when navigating through panorama arrows.",
            kotlinActivity = "com.example.kotlindemos.StreetViewPanoramaEventsDemoActivity",
            javaActivity = "com.example.mapdemo.StreetViewPanoramaEventsDemoActivity"
        ),

        // ==========================================
        // 🎨 STYLING & CLOUD CUSTOMIZATION
        // ==========================================
        SampleItem(
            id = "cloud_styling",
            title = "Cloud-Based Map Styling",
            description = "Loading vector map styles managed dynamically in Google Cloud Console via Map IDs.",
            category = "Styling & Cloud",
            complexity = Complexity.SIMPLE,
            tags = listOf("#styling", "#cloudstyling", "#mapid", "#vector"),
            purpose = "Demonstrates loading cloud-managed map styles configured in Cloud Console using a Map ID.",
            successCriteria = "Map renders with custom cloud styling (e.g., custom landmark colors, muted transit lines).",
            failureIndicators = "Map ID fails to resolve, showing default fallback styling without cloud customizations.",
            kotlinActivity = "com.example.kotlindemos.CloudBasedMapStylingDemoActivity",
            javaActivity = "com.example.mapdemo.CloudBasedMapStylingDemoActivity"
        ),
        SampleItem(
            id = "styled_map",
            title = "JSON Map Styling (Retro / Dark)",
            description = "Applying raw JSON styling rules locally for Retro, Grayscale, and Night mode aesthetics.",
            category = "Styling & Cloud",
            complexity = Complexity.SIMPLE,
            tags = listOf("#styling", "#json", "#darkmode", "#night", "#retro"),
            purpose = "Demonstrates applying local JSON MapStyleOptions to change base map theme dynamically.",
            successCriteria = "Selecting style options in the toolbar instantly restyles the map (Night / Retro / Standard).",
            failureIndicators = "Invalid JSON causes silent fallback or parsing exception.",
            kotlinActivity = "com.example.kotlindemos.StyledMapDemoActivity",
            javaActivity = "com.example.mapdemo.StyledMapDemoActivity"
        ),
        SampleItem(
            id = "map_color_scheme",
            title = "System Color Scheme (Dark/Light)",
            description = "Automatically adapting map colors to system dark mode / light mode settings.",
            category = "Styling & Cloud",
            complexity = Complexity.SNIPPET,
            tags = listOf("#styling", "#colorscheme", "#darkmode", "#systemtheme"),
            purpose = "Demonstrates configuring MapColorScheme.DARK and MapColorScheme.LIGHT based on system dark theme.",
            successCriteria = "Map adjusts palette when switching between dark and light device theme.",
            failureIndicators = "Map remains light when system dark mode is enabled.",
            kotlinActivity = "com.example.kotlindemos.MapColorSchemeActivity",
            javaActivity = "com.example.mapdemo.MapColorSchemeActivity"
        ),

        // ==========================================
        // 🌐 DATA-DRIVEN STYLING & DATASETS
        // ==========================================
        SampleItem(
            id = "data_driven_boundaries",
            title = "Data-Driven Boundaries",
            description = "Dynamic styling and click handlers for administrative boundaries (Localities, States, Countries).",
            category = "Data-Driven Styling",
            complexity = Complexity.ADVANCED,
            tags = listOf("#boundaries", "#datadriven", "#featurelayer", "#locality", "#choropleth"),
            purpose = "Demonstrates styling administrative boundaries dynamically via FeatureLayer and capturing boundary clicks.",
            successCriteria = "Boundaries render with custom stroke and fill colors; tapping a region highlights its polygon.",
            failureIndicators = "Boundary layer is null (requires vector map / Map ID) or click listener not firing.",
            kotlinActivity = "com.example.kotlindemos.DataDrivenBoundariesActivity",
            javaActivity = "com.example.mapdemo.DataDrivenBoundariesActivity"
        ),
        SampleItem(
            id = "data_driven_datasets",
            title = "Data-Driven Dataset Styling",
            description = "Styling custom geospatial datasets uploaded to Google Cloud Platform based on attributes.",
            category = "Data-Driven Styling",
            complexity = Complexity.ADVANCED,
            tags = listOf("#datasets", "#datadriven", "#clouddata", "#attributes", "#filtering"),
            purpose = "Demonstrates loading a Cloud Dataset FeatureLayer and applying dynamic style rules based on feature properties.",
            successCriteria = "Dataset points and polygons display distinct styling according to attribute values.",
            failureIndicators = "Dataset ID invalid or attributes fail to filter correctly.",
            kotlinActivity = "com.example.kotlindemos.DataDrivenDatasetStylingActivity",
            javaActivity = "com.example.mapdemo.DataDrivenDatasetStylingActivity"
        ),

        // ==========================================
        // 📋 LISTS & RECYCLED VIEWS
        // ==========================================
        SampleItem(
            id = "lite_list",
            title = "Lite Mode in RecyclerView",
            description = "High-performance Lite Mode map instances inside smooth scrolling RecyclerView list rows.",
            category = "Lists & Performance",
            complexity = Complexity.ADVANCED,
            tags = listOf("#litemode", "#recyclerview", "#lists", "#viewholder", "#lifecycle"),
            purpose = "Demonstrates embedding MapView lite mode instances inside RecyclerView rows with proper lifecycle management.",
            successCriteria = "List scrolls at 60/120fps without stutter; map snapshots display accurate markers per row.",
            failureIndicators = "RecyclerView scrolling stutters or recycled MapViews display stale map markers.",
            kotlinActivity = "com.example.kotlindemos.LiteListDemoActivity",
            javaActivity = "com.example.mapdemo.LiteListDemoActivity"
        ),

        // ==========================================
        // 📸 SNAPSHOT & BITMAP CAPTURE
        // ==========================================
        SampleItem(
            id = "snapshot_demo",
            title = "Map Snapshot & Image Capture",
            description = "Asynchronous bitmap frame capture using GoogleMap.snapshot() rendered in Material 3 preview cards.",
            category = "Snapshots & Sharing",
            complexity = Complexity.SIMPLE,
            tags = listOf("#snapshot", "#bitmap", "#export", "#material3", "#capture"),
            purpose = "Demonstrates taking asynchronous high-resolution bitmap snapshots of the map with snapshot ready callbacks.",
            successCriteria = "Tapping 'Take Snapshot' captures the current map frame and displays it in the Material 3 preview card.",
            failureIndicators = "Snapshot returns blank bitmap or blocks UI thread during GL readback.",
            kotlinActivity = "com.example.kotlindemos.SnapshotDemoActivity",
            javaActivity = "com.example.mapdemo.SnapshotDemoActivity"
        ),

        // ==========================================
        // ⚛️ JETPACK COMPOSE
        // ==========================================
        SampleItem(
            id = "fire_markers_compose",
            title = "FireMarkers (Jetpack Compose)",
            description = "Declarative GoogleMap composable with animated incident markers, camera state, and recomposition.",
            category = "Jetpack Compose",
            complexity = Complexity.ADVANCED,
            tags = listOf("#compose", "#jetpackcompose", "#declarative", "#animations", "#state"),
            purpose = "Demonstrates modern declarative Google Maps architecture in Compose with smooth marker state updates.",
            successCriteria = "Incident markers animate smoothly without recomposition flicker; camera state survives configuration changes.",
            failureIndicators = "Recomposition jank, markers blinking on position updates, or memory leak in animation loop.",
            composeActivity = "com.example.firemarkers.MainActivity"
        ),

        // ==========================================
        // ⌚ WEAR OS
        // ==========================================
        SampleItem(
            id = "wearos_ambient",
            title = "Wear OS Ambient Map",
            description = "Optimized wearable map container with safe AmbientModeSupport handling for round smartwatches.",
            category = "Wear OS",
            complexity = Complexity.ADVANCED,
            tags = listOf("#wearos", "#wearable", "#ambient", "#smartwatch"),
            purpose = "Demonstrates embedding Google Maps on Wear OS smartwatches with safe ambient mode transitions.",
            successCriteria = "Map renders cleanly within circular wearable display bounds and enters low-power ambient mode gracefully.",
            failureIndicators = "Map UI clipped by round screen cutouts or crash when device enters ambient mode.",
            kotlinActivity = "com.example.wearosmap.kt.MainActivity",
            javaActivity = "com.example.wearosmap.MainActivity"
        )
    )

    fun getAllCategories(): List<String> =
        SAMPLES.map { it.category }.distinct()

    fun getAllTags(): List<String> =
        SAMPLES.flatMap { it.tags }.distinct().sorted()

    fun filter(
        framework: Framework = Framework.KOTLIN_VIEWS,
        complexity: Complexity? = null,
        selectedTags: Set<String> = emptySet(),
        searchQuery: String = "",
        category: String? = null
    ): List<SampleItem> {
        return SAMPLES.filter { sample ->
            // Framework match
            val hasActivity = when (framework) {
                Framework.KOTLIN_VIEWS -> sample.kotlinActivity != null
                Framework.JAVA_VIEWS -> sample.javaActivity != null
                Framework.COMPOSE -> sample.composeActivity != null
            }
            if (!hasActivity) return@filter false

            // Complexity filter
            if (complexity != null && sample.complexity != complexity) {
                return@filter false
            }

            // Category filter
            if (category != null && sample.category != category) {
                return@filter false
            }

            // Tags filter (must contain ALL selected tags)
            if (selectedTags.isNotEmpty() && !sample.tags.containsAll(selectedTags)) {
                return@filter false
            }

            // Search query filter (matches title, description, or tags)
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim().lowercase()
                val matchesTitle = sample.title.lowercase().contains(query)
                val matchesDesc = sample.description.lowercase().contains(query)
                val matchesTags = sample.tags.any { it.lowercase().contains(query) }
                val matchesCategory = sample.category.lowercase().contains(query)
                if (!matchesTitle && !matchesDesc && !matchesTags && !matchesCategory) {
                    return@filter false
                }
            }

            true
        }
    }
}
