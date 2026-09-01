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
 * Uses Fully Qualified Class Names (FQCN) as evaluation identifiers.
 * Organizes samples by Category, Complexity, Framework (Kotlin & Java), and Hashtags with rich HTML expectations.
 */
object SampleCatalogRegistry {

    val SAMPLES: List<SampleItem> = listOf(
        // ==========================================
        // 🗺️ MAP INITIALIZATION & LIFECYCLE
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.BasicMapDemoActivity",
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
            id = "com.example.kotlindemos.ProgrammaticDemoActivity",
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
            id = "com.example.kotlindemos.RawMapViewDemoActivity",
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
            id = "com.example.kotlindemos.RetainMapDemoActivity",
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
            id = "com.example.kotlindemos.MultiMapDemoActivity",
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
            id = "com.example.kotlindemos.MapInPagerDemoActivity",
            title = "Map in ViewPager",
            description = "Hosting MapView instances inside a swipeable ViewPager2 structure.",
            category = "Map Initialization",
            complexity = Complexity.ADVANCED,
            tags = listOf("#viewpager", "#swiping", "#touchinterception", "#fragments"),
            purpose = "Demonstrates embedding maps inside ViewPager tabs with proper touch disallow interception.",
            successCriteria = "Panning map does not accidentally trigger ViewPager page swipe.",
            failureIndicators = "Swiping horizontally pans the ViewPager instead of the map camera.",
            kotlinActivity = "com.example.kotlindemos.MapInPagerDemoActivity",
            javaActivity = "com.example.mapdemo.MapInPagerDemoActivity"
        ),

        // ==========================================
        // 📷 CAMERA & VIEWPORT CONTROLS
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.CameraDemoActivity",
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
            id = "com.example.kotlindemos.CameraClampingDemoActivity",
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
            id = "com.example.kotlindemos.VisibleRegionDemoActivity",
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
            id = "com.example.kotlindemos.AdvancedMarkersDemoActivity",
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
            id = "com.example.kotlindemos.MarkerDemoActivity",
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
            id = "com.example.kotlindemos.MarkerCloseInfoWindowOnRetapDemoActivity",
            title = "Marker InfoWindow Re-tap Toggle",
            description = "Toggling InfoWindow dismiss when re-tapping an active marker.",
            category = "Markers & Overlays",
            complexity = Complexity.SNIPPET,
            tags = listOf("#markers", "#infowindow", "#toggle", "#gestures"),
            purpose = "Shows how to implement re-tap to dismiss toggle behavior for active marker info windows.",
            successCriteria = "First tap opens info window; second tap on the same marker closes it cleanly.",
            failureIndicators = "Info window stays stuck open or re-tap triggers unnecessary camera repositioning.",
            kotlinActivity = "com.example.kotlindemos.MarkerCloseInfoWindowOnRetapDemoActivity",
            javaActivity = "com.example.mapdemo.MarkerCloseInfoWindowOnRetapDemoActivity"
        ),

        // ==========================================
        // 📐 SHAPES & GEOMETRY
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.PolygonDemoActivity",
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
            id = "com.example.kotlindemos.PolylineDemoActivity",
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
            id = "com.example.kotlindemos.CircleDemoActivity",
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
        // 🗺️ DATA-DRIVEN STYLING (CLOUD MAPS)
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.DataDrivenBoundariesActivity",
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
            id = "com.example.kotlindemos.DataDrivenDatasetStylingActivity",
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
        // 🎨 STYLING & CLOUD THEMES
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.CloudBasedMapStylingDemoActivity",
            title = "Cloud-Based Map Styling",
            description = "Using Cloud Map IDs for server-side JSON styling and feature management.",
            category = "Styling & Cloud",
            complexity = Complexity.SIMPLE,
            tags = listOf("#cloudstyling", "#mapid", "#vector", "#theming"),
            purpose = "Demonstrates linking a map to a Cloud-managed Map ID for instant over-the-air style updates.",
            successCriteria = "Map renders with the customized cloud style colors without local JSON parsing.",
            failureIndicators = "Default styling rendered (Map ID unlinked or network error during initial style fetch).",
            kotlinActivity = "com.example.kotlindemos.CloudBasedMapStylingDemoActivity",
            javaActivity = "com.example.mapdemo.CloudBasedMapStylingDemoActivity"
        ),
        SampleItem(
            id = "com.example.kotlindemos.StyledMapDemoActivity",
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
            id = "com.example.kotlindemos.MapColorSchemeActivity",
            title = "Map Color Scheme (System / Light / Dark)",
            description = "Configuring automatic system dark mode following via MapColorScheme.",
            category = "Styling & Cloud",
            complexity = Complexity.SNIPPET,
            tags = listOf("#colorscheme", "#darkmode", "#systemtheme", "#followsystem"),
            purpose = "Shows how to set GoogleMapOptions.mapColorScheme to follow system night mode automatically.",
            successCriteria = "Toggling device dark mode flips map styling between light and dark palettes seamlessly.",
            failureIndicators = "Map remains stuck in light theme when system dark mode is enabled.",
            kotlinActivity = "com.example.kotlindemos.MapColorSchemeActivity",
            javaActivity = "com.example.mapdemo.MapColorSchemeActivity"
        ),

        // ==========================================
        // 🏙️ STREET VIEW & PANORAMAS
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.SplitStreetViewPanoramaAndMapDemoActivity",
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
            id = "com.example.kotlindemos.StreetViewPanoramaBasicDemoActivity",
            title = "Basic Street View Panorama",
            description = "Instantiating a StreetViewPanoramaFragment and loading coordinates.",
            category = "Street View",
            complexity = Complexity.SNIPPET,
            tags = listOf("#streetview", "#panorama", "#init", "#sydney"),
            purpose = "Demonstrates embedding StreetViewPanoramaFragment and setting initial position by LatLng.",
            successCriteria = "360-degree panorama loads smoothly with working touch gestures.",
            failureIndicators = "Black panorama canvas, missing imagery at coordinates, or gesture freeze.",
            kotlinActivity = "com.example.kotlindemos.StreetViewPanoramaBasicDemoActivity",
            javaActivity = "com.example.mapdemo.StreetViewPanoramaBasicDemoActivity"
        ),

        // ==========================================
        // ⚡ LISTS & RECYCLERVIEW PERFORMANCE
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.LiteListDemoActivity",
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
        SampleItem(
            id = "com.example.kotlindemos.LiteDemoActivity",
            title = "Lite Mode Grid",
            description = "Lightweight non-interactive raster map cards arranged in a multi-column grid layout.",
            category = "Lists & Performance",
            complexity = Complexity.SIMPLE,
            tags = listOf("#litemode", "#grid", "#performance", "#snapshots"),
            purpose = "Demonstrates rendering multiple Lite Mode maps in a GridView with low memory consumption.",
            successCriteria = "Grid tiles render crisp static maps with markers without launching full GL renderer.",
            failureIndicators = "High memory spike or missing markers on grid cells.",
            kotlinActivity = "com.example.kotlindemos.LiteDemoActivity",
            javaActivity = "com.example.mapdemo.LiteDemoActivity"
        ),

        // ==========================================
        // 📸 SNAPSHOTS & SHARING
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.SnapshotDemoActivity",
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
        // 📍 LOCATION & SENSORS
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.MyLocationDemoActivity",
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
            id = "com.example.kotlindemos.LocationSourceDemoActivity",
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
        // 🔲 OVERLAYS & CUSTOM TILES
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.GroundOverlayDemoActivity",
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
            id = "com.example.kotlindemos.TileOverlayDemoActivity",
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
        // 👆 EVENTS & GESTURES
        // ==========================================
        SampleItem(
            id = "com.example.kotlindemos.EventsDemoActivity",
            title = "Events & Gestures",
            description = "Handling map taps, long clicks, camera change events, and POI selections.",
            category = "Events & Gestures",
            complexity = Complexity.SNIPPET,
            tags = listOf("#events", "#gestures", "#clicks", "#poi", "#listeners"),
            purpose = "Demonstrates registering listeners for map clicks, long presses, camera moves, and POI selections.",
            successCriteria = "Event log text updates with coordinates and POI names upon user interaction.",
            failureIndicators = "Click events swallowed or POI name unresolved.",
            kotlinActivity = "com.example.kotlindemos.EventsDemoActivity",
            javaActivity = "com.example.mapdemo.EventsDemoActivity"
        ),
        SampleItem(
            id = "com.example.kotlindemos.UiSettingsDemoActivity",
            title = "UI Settings & Map Controls",
            description = "Configuring zoom buttons, compass, my location button, and gesture toggles.",
            category = "Events & Gestures",
            complexity = Complexity.SIMPLE,
            tags = listOf("#uisettings", "#controls", "#gestures", "#compass", "#zoombuttons"),
            purpose = "Shows how to toggle GoogleMap.uiSettings controls (compass, zoom buttons, scroll/tilt gestures).",
            successCriteria = "Toggling checkboxes in the drawer instantly enables/disables corresponding map gestures and UI controls.",
            failureIndicators = "Gesture toggles ignored or UI control icons clipped by safe area.",
            kotlinActivity = "com.example.kotlindemos.UiSettingsDemoActivity",
            javaActivity = "com.example.mapdemo.UiSettingsDemoActivity"
        )
    )

    fun getAllTags(): List<String> {
        return SAMPLES.flatMap { it.tags }.distinct().sorted()
    }

    fun getCategories(): List<String> {
        return SAMPLES.map { it.category }.distinct().sorted()
    }

    fun filter(
        framework: Framework = Framework.KOTLIN_VIEWS,
        complexity: Complexity? = null,
        selectedTags: Set<String> = emptySet(),
        searchQuery: String = ""
    ): List<SampleItem> {
        return SAMPLES.filter { sample ->
            val matchesFramework = sample.getActivityForFramework(framework) != null
            val matchesComplexity = complexity == null || sample.complexity == complexity
            val matchesTags = selectedTags.isEmpty() || sample.tags.any { selectedTags.contains(it) }
            val matchesSearch = searchQuery.isBlank() ||
                sample.title.contains(searchQuery, ignoreCase = true) ||
                sample.description.contains(searchQuery, ignoreCase = true) ||
                sample.category.contains(searchQuery, ignoreCase = true) ||
                sample.tags.any { it.contains(searchQuery, ignoreCase = true) } ||
                sample.id.contains(searchQuery, ignoreCase = true)

            matchesFramework && matchesComplexity && matchesTags && matchesSearch
        }
    }

    fun findById(id: String?): SampleItem? {
        if (id == null) return null
        return SAMPLES.find { it.id == id || it.kotlinActivity == id || it.javaActivity == id }
    }
}
