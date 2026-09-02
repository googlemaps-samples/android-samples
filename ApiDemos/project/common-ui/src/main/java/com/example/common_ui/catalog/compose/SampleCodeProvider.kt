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

package com.example.common_ui.catalog.compose

import com.example.common_ui.catalog.Framework

/**
 * Registry of curated, syntax-highlighted code snippets for all 31 catalog samples
 * in both Kotlin and Java.
 *
 * Extracts the core essence and API patterns of each demo without boilerplate.
 */
object SampleCodeProvider {

    data class SnippetPair(
        val kotlinCode: String,
        val javaCode: String
    )

    fun getCode(sampleId: String, framework: Framework): String {
        val snippet = SNIPPETS[sampleId] 
            ?: SNIPPETS.entries.firstOrNull { sampleId.endsWith(it.key.substringAfterLast('.')) }?.value
            ?: getFallbackSnippet(sampleId)
        return when (framework) {
            Framework.KOTLIN_VIEWS -> snippet.kotlinCode
            Framework.JAVA_VIEWS -> snippet.javaCode
        }
    }

    private val SNIPPETS = mapOf(
        // 1. Basic Map
        "com.example.kotlindemos.BasicMapDemoActivity" to SnippetPair(
            kotlinCode = """
// Initialize and attach SupportMapFragment
val mapFragment = supportFragmentManager
    .findFragmentById(R.id.map) as SupportMapFragment

mapFragment.getMapAsync { googleMap ->
    val sydney = LatLng(-33.852, 151.211)
    googleMap.addMarker(
        MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney")
    )
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))
}
            """.trimIndent(),
            javaCode = """
// Initialize and attach SupportMapFragment
SupportMapFragment mapFragment = (SupportMapFragment) 
    getSupportFragmentManager().findFragmentById(R.id.map);

mapFragment.getMapAsync(new OnMapReadyCallback() {
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f));
    }
});
            """.trimIndent()
        ),

        // 2. Programmatic Map
        "com.example.kotlindemos.ProgrammaticDemoActivity" to SnippetPair(
            kotlinCode = """
// Create SupportMapFragment programmatically with options
val options = GoogleMapOptions()
    .mapType(GoogleMap.MAP_TYPE_NORMAL)
    .compassEnabled(true)
    .rotateGesturesEnabled(true)
    .tiltGesturesEnabled(true)

val mapFragment = SupportMapFragment.newInstance(options)

supportFragmentManager.beginTransaction()
    .add(R.id.map_container, mapFragment)
    .commit()

mapFragment.getMapAsync { googleMap ->
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-33.852, 151.211), 12f))
}
            """.trimIndent(),
            javaCode = """
// Create SupportMapFragment programmatically with options
GoogleMapOptions options = new GoogleMapOptions()
    .mapType(GoogleMap.MAP_TYPE_NORMAL)
    .compassEnabled(true)
    .rotateGesturesEnabled(true)
    .tiltGesturesEnabled(true);

SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);

getSupportFragmentManager().beginTransaction()
    .add(R.id.map_container, mapFragment)
    .commit();

mapFragment.getMapAsync(googleMap -> {
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.852, 151.211), 12f));
});
            """.trimIndent()
        ),

        // 3. Raw MapView
        "com.example.kotlindemos.RawMapViewDemoActivity" to SnippetPair(
            kotlinCode = """
// Direct MapView embedding with explicit Activity lifecycle forwarding
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mapView = findViewById(R.id.map)
    mapView.onCreate(savedInstanceState)

    mapView.getMapAsync { googleMap ->
        googleMap.addMarker(MarkerOptions().position(SYDNEY).title("Direct MapView"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 10f))
    }
}

override fun onResume() { super.onResume(); mapView.onResume() }
override fun onPause() { mapView.onPause(); super.onPause() }
override fun onDestroy() { mapView.onDestroy(); super.onDestroy() }
override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
}
            """.trimIndent(),
            javaCode = """
// Direct MapView embedding with explicit Activity lifecycle forwarding
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mapView = findViewById(R.id.map);
    mapView.onCreate(savedInstanceState);

    mapView.getMapAsync(googleMap -> {
        googleMap.addMarker(new MarkerOptions().position(SYDNEY).title("Direct MapView"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 10f));
    });
}

@Override protected void onResume() { super.onResume(); mapView.onResume(); }
@Override protected void onPause() { mapView.onPause(); super.onPause(); }
@Override protected void onDestroy() { mapView.onDestroy(); super.onDestroy(); }
@Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
}
            """.trimIndent()
        ),

        // 4. Retained Map
        "com.example.kotlindemos.RetainMapDemoActivity" to SnippetPair(
            kotlinCode = """
// Retain SupportMapFragment across runtime configuration changes (rotation)
val mapFragment = supportFragmentManager
    .findFragmentById(R.id.map) as SupportMapFragment

// Preserves the fragment instance and map state during screen rotation
mapFragment.retainInstance = true

mapFragment.getMapAsync { googleMap ->
    googleMap.addMarker(MarkerOptions().position(SYDNEY).title("Retained Marker"))
}
            """.trimIndent(),
            javaCode = """
// Retain SupportMapFragment across runtime configuration changes (rotation)
SupportMapFragment mapFragment = (SupportMapFragment)
    getSupportFragmentManager().findFragmentById(R.id.map);

// Preserves the fragment instance and map state during screen rotation
mapFragment.setRetainInstance(true);

mapFragment.getMapAsync(googleMap -> {
    googleMap.addMarker(new MarkerOptions().position(SYDNEY).title("Retained Marker"));
});
            """.trimIndent()
        ),

        // 5. Multi-Map View
        "com.example.kotlindemos.MultiMapDemoActivity" to SnippetPair(
            kotlinCode = """
// Control multiple independent GoogleMap instances in one layout
val map1 = supportFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment
val map2 = supportFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment

map1.getMapAsync { googleMap ->
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 10f))
    googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
}

map2.getMapAsync { googleMap ->
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MELBOURNE, 10f))
    googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
}
            """.trimIndent(),
            javaCode = """
// Control multiple independent GoogleMap instances in one layout
SupportMapFragment map1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
SupportMapFragment map2 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);

map1.getMapAsync(googleMap -> {
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 10f));
    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
});

map2.getMapAsync(googleMap -> {
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MELBOURNE, 10f));
    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
});
            """.trimIndent()
        ),

        // 6. Map in ViewPager
        "com.example.kotlindemos.MapInPagerDemoActivity" to SnippetPair(
            kotlinCode = """
// Host MapView instances inside a swipeable ViewPager2
class MapPageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val mapView: MapView = view.findViewById(R.id.map)

    fun bind(city: CityLocation) {
        mapView.onCreate(null)
        mapView.getMapAsync { googleMap ->
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city.latLng, 11f))
            googleMap.addMarker(MarkerOptions().position(city.latLng).title(city.name))
        }
    }
}
            """.trimIndent(),
            javaCode = """
// Host MapView instances inside a swipeable ViewPager2
static class MapPageViewHolder extends RecyclerView.ViewHolder {
    private final MapView mapView;

    MapPageViewHolder(View view) {
        super(view);
        mapView = view.findViewById(R.id.map);
    }

    void bind(CityLocation city) {
        mapView.onCreate(null);
        mapView.getMapAsync(googleMap -> {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city.latLng, 11f));
            googleMap.addMarker(new MarkerOptions().position(city.latLng).title(city.name));
        });
    }
}
            """.trimIndent()
        ),

        // 7. Camera Demo
        "com.example.kotlindemos.CameraDemoActivity" to SnippetPair(
            kotlinCode = """
// Animate camera with tilt, bearing, and zoom
val cameraPosition = CameraPosition.builder()
    .target(LatLng(-33.852, 151.211))
    .zoom(17f)
    .bearing(90f) // East
    .tilt(45f)
    .build()

googleMap.animateCamera(
    CameraUpdateFactory.newCameraPosition(cameraPosition),
    2000, // Duration in ms
    object : GoogleMap.CancelableCallback {
        override fun onFinish() { Log.d("Camera", "Animation complete") }
        override fun onCancel() { Log.d("Camera", "Animation cancelled") }
    }
)
            """.trimIndent(),
            javaCode = """
// Animate camera with tilt, bearing, and zoom
CameraPosition cameraPosition = new CameraPosition.Builder()
    .target(new LatLng(-33.852, 151.211))
    .zoom(17f)
    .bearing(90f) // East
    .tilt(45f)
    .build();

googleMap.animateCamera(
    CameraUpdateFactory.newCameraPosition(cameraPosition),
    2000,
    new GoogleMap.CancelableCallback() {
        @Override public void onFinish() { Log.d("Camera", "Animation complete"); }
        @Override public void onCancel() { Log.d("Camera", "Animation cancelled"); }
    }
);
            """.trimIndent()
        ),

        // 8. Camera Clamping
        "com.example.kotlindemos.CameraClampingDemoActivity" to SnippetPair(
            kotlinCode = """
// Constrain camera bounds, zoom levels, and scroll region
val ADELAIDE_BOUNDS = LatLngBounds(LatLng(-35.0, 138.5), LatLng(-34.9, 138.7))

googleMap.setLatLngBoundsForCameraTarget(ADELAIDE_BOUNDS)
googleMap.setMinZoomPreference(10.0f)
googleMap.setMaxZoomPreference(16.0f)

googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ADELAIDE_BOUNDS.center, 12f))
            """.trimIndent(),
            javaCode = """
// Constrain camera bounds, zoom levels, and scroll region
LatLngBounds ADELAIDE_BOUNDS = new LatLngBounds(new LatLng(-35.0, 138.5), new LatLng(-34.9, 138.7));

googleMap.setLatLngBoundsForCameraTarget(ADELAIDE_BOUNDS);
googleMap.setMinZoomPreference(10.0f);
googleMap.setMaxZoomPreference(16.0f);

googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ADELAIDE_BOUNDS.getCenter(), 12f));
            """.trimIndent()
        ),

        // 9. Visible Region
        "com.example.kotlindemos.VisibleRegionDemoActivity" to SnippetPair(
            kotlinCode = """
// Query viewport visible bounds and coordinate projection
val projection = googleMap.projection
val visibleRegion = projection.visibleRegion
val bounds = visibleRegion.latLngBounds

// Convert LatLng to screen coordinates
val screenPoint = projection.toScreenLocation(bounds.center)
Log.d("Viewport", "Center screen point: \screenPoint, bounds: ounds")
            """.trimIndent(),
            javaCode = """
// Query viewport visible bounds and coordinate projection
Projection projection = googleMap.getProjection();
VisibleRegion visibleRegion = projection.getVisibleRegion();
LatLngBounds bounds = visibleRegion.latLngBounds;

// Convert LatLng to screen coordinates
Point screenPoint = projection.toScreenLocation(bounds.getCenter());
Log.d("Viewport", "Center screen point: " + screenPoint + ", bounds: " + bounds);
            """.trimIndent()
        ),

        // 10. Advanced Markers
        "com.example.kotlindemos.AdvancedMarkersDemoActivity" to SnippetPair(
            kotlinCode = """
// Create an Advanced Marker with PinConfig styling
val pinConfig = PinConfig.builder()
    .setBackgroundColor(Color.BLUE)
    .setBorderColor(Color.WHITE)
    .setGlyph(PinConfig.Glyph("★"))
    .build()

val markerOptions = AdvancedMarkerOptions()
    .position(LatLng(-33.852, 151.211))
    .title("Sydney Custom Pin")
    .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig))
    .collisionBehavior(AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL)

val marker = googleMap.addMarker(markerOptions)
            """.trimIndent(),
            javaCode = """
// Create an Advanced Marker with PinConfig styling
PinConfig pinConfig = PinConfig.builder()
    .setBackgroundColor(Color.BLUE)
    .setBorderColor(Color.WHITE)
    .setGlyph(new PinConfig.Glyph("★"))
    .build();

AdvancedMarkerOptions markerOptions = new AdvancedMarkerOptions()
    .position(new LatLng(-33.852, 151.211))
    .title("Sydney Custom Pin")
    .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig))
    .collisionBehavior(AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL);

Marker marker = googleMap.addMarker(markerOptions);
            """.trimIndent()
        ),

        // 11. Marker Demo
        "com.example.kotlindemos.MarkerDemoActivity" to SnippetPair(
            kotlinCode = """
// Add draggable markers with rotation and hue tinting
val marker = googleMap.addMarker(
    MarkerOptions()
        .position(SYDNEY)
        .title("Sydney Harbor")
        .snippet("Population: 5,300,000")
        .draggable(true)
        .rotation(45f)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
)

googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
    override fun onMarkerDragEnd(m: Marker) { Log.d("Marker", "New position: \m.position") }
    override fun onMarkerDragStart(m: Marker) {}
    override fun onMarkerDrag(m: Marker) {}
})
            """.trimIndent(),
            javaCode = """
// Add draggable markers with rotation and hue tinting
Marker marker = googleMap.addMarker(
    new MarkerOptions()
        .position(SYDNEY)
        .title("Sydney Harbor")
        .snippet("Population: 5,300,000")
        .draggable(true)
        .rotation(45f)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
);

googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
    @Override public void onMarkerDragEnd(Marker m) { Log.d("Marker", "New position: " + m.getPosition()); }
    @Override public void onMarkerDragStart(Marker m) {}
    @Override public void onMarkerDrag(Marker m) {}
});
            """.trimIndent()
        ),

        // 12. Marker Close Info Window on Retap
        "com.example.kotlindemos.MarkerCloseInfoWindowOnRetapDemoActivity" to SnippetPair(
            kotlinCode = """
// Dismiss info window when re-tapping an active marker
googleMap.setOnMarkerClickListener { marker ->
    if (marker.isInfoWindowShown) {
        marker.hideInfoWindow()
        true // Handled: dismiss without camera pan
    } else {
        false // Default: show info window and pan camera
    }
}
            """.trimIndent(),
            javaCode = """
// Dismiss info window when re-tapping an active marker
googleMap.setOnMarkerClickListener(marker -> {
    if (marker.isInfoWindowShown()) {
        marker.hideInfoWindow();
        return true; // Handled: dismiss without camera pan
    }
    return false; // Default: show info window and pan camera
});
            """.trimIndent()
        ),

        // 13. Polygon Demo
        "com.example.kotlindemos.PolygonDemoActivity" to SnippetPair(
            kotlinCode = """
// Draw a styled polygon with an interior hole
val polygon = googleMap.addPolygon(
    PolygonOptions()
        .addAll(outerBoundaryPoints)
        .addHole(interiorHolePoints)
        .fillColor(0x7F00FF00.toInt()) // Semi-transparent green
        .strokeColor(0xFF00AA00.toInt())
        .strokeWidth(8f)
        .clickable(true)
)

googleMap.setOnPolygonClickListener { clickedPolygon ->
    Toast.makeText(this, "Tapped polygon: \clickedPolygon.id", Toast.LENGTH_SHORT).show()
}
            """.trimIndent(),
            javaCode = """
// Draw a styled polygon with an interior hole
Polygon polygon = googleMap.addPolygon(
    new PolygonOptions()
        .addAll(outerBoundaryPoints)
        .addHole(interiorHolePoints)
        .fillColor(0x7F00FF00) // Semi-transparent green
        .strokeColor(0xFF00AA00)
        .strokeWidth(8f)
        .clickable(true)
);

googleMap.setOnPolygonClickListener(clickedPolygon -> {
    Toast.makeText(this, "Tapped polygon: " + clickedPolygon.getId(), Toast.LENGTH_SHORT).show();
});
            """.trimIndent()
        ),

        // 14. Polyline Demo
        "com.example.kotlindemos.PolylineDemoActivity" to SnippetPair(
            kotlinCode = """
// Draw a styled polyline with custom dash pattern and end caps
val pattern = listOf(Dash(30f), Gap(20f), Dot(), Gap(20f))

val polyline = googleMap.addPolyline(
    PolylineOptions()
        .addAll(routeCoordinates)
        .color(Color.RED)
        .width(12f)
        .pattern(pattern)
        .startCap(RoundCap())
        .endCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow_cap)))
        .jointType(JointType.ROUND)
)
            """.trimIndent(),
            javaCode = """
// Draw a styled polyline with custom dash pattern and end caps
List<PatternItem> pattern = Arrays.asList(new Dash(30f), new Gap(20f), new Dot(), new Gap(20f));

Polyline polyline = googleMap.addPolyline(
    new PolylineOptions()
        .addAll(routeCoordinates)
        .color(Color.RED)
        .width(12f)
        .pattern(pattern)
        .startCap(new RoundCap())
        .endCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow_cap)))
        .jointType(JointType.ROUND)
);
            """.trimIndent()
        ),

        // 15. Circle Demo
        "com.example.kotlindemos.CircleDemoActivity" to SnippetPair(
            kotlinCode = """
// Add and customize an interactive circular overlay
val circle = googleMap.addCircle(
    CircleOptions()
        .center(SYDNEY)
        .radius(1000.0) // Radius in meters
        .strokeColor(Color.RED)
        .strokeWidth(5f)
        .fillColor(Color.argb(70, 255, 0, 0))
        .clickable(true)
)

googleMap.setOnCircleClickListener { c ->
    c.strokeColor = Color.BLUE
}
            """.trimIndent(),
            javaCode = """
// Add and customize an interactive circular overlay
Circle circle = googleMap.addCircle(
    new CircleOptions()
        .center(SYDNEY)
        .radius(1000.0) // Radius in meters
        .strokeColor(Color.RED)
        .strokeWidth(5f)
        .fillColor(Color.argb(70, 255, 0, 0))
        .clickable(true)
);

googleMap.setOnCircleClickListener(c -> c.setStrokeColor(Color.BLUE));
            """.trimIndent()
        ),

        // 16. Data-Driven Boundaries
        "com.example.kotlindemos.DataDrivenBoundariesActivity" to SnippetPair(
            kotlinCode = """
// Style administrative boundaries dynamically with FeatureLayer
val localityLayer = googleMap.getFeatureLayer(FeatureType.LOCALITY)

localityLayer?.setFeatureStyle { feature ->
    val placeId = (feature as? PlaceFeature)?.placeId
    if (placeId == "ChIJP3Sa8ziYEmsRUKgyFmh9AQM") { // Sydney
        FeatureStyle.Builder()
            .fillColor(Color.argb(80, 66, 133, 244))
            .strokeColor(Color.BLUE)
            .strokeWeight(3f)
            .build()
    } else {
        null
    }
}
            """.trimIndent(),
            javaCode = """
// Style administrative boundaries dynamically with FeatureLayer
FeatureLayer localityLayer = googleMap.getFeatureLayer(FeatureType.LOCALITY);

if (localityLayer != null) {
    localityLayer.setFeatureStyle(feature -> {
        if (feature instanceof PlaceFeature) {
            String placeId = ((PlaceFeature) feature).getPlaceId();
            if ("ChIJP3Sa8ziYEmsRUKgyFmh9AQM".equals(placeId)) {
                return new FeatureStyle.Builder()
                    .fillColor(Color.argb(80, 66, 133, 244))
                    .strokeColor(Color.BLUE)
                    .strokeWeight(3f)
                    .build();
            }
        }
        return null;
    });
}
            """.trimIndent()
        ),

        // 17. Data-Driven Dataset Styling
        "com.example.kotlindemos.DataDrivenDatasetStylingActivity" to SnippetPair(
            kotlinCode = """
// Style custom cloud datasets dynamically
val datasetLayer = googleMap.getDatasetFeatureLayer("YOUR_DATASET_ID")

datasetLayer?.setFeatureStyle { feature ->
    val rating = feature.datasetAttributes["rating"]?.toDoubleOrNull() ?: 0.0
    val color = if (rating >= 4.5) Color.GREEN else Color.YELLOW
    FeatureStyle.Builder()
        .pointRadius(8f)
        .fillColor(color)
        .strokeColor(Color.WHITE)
        .strokeWeight(2f)
        .build()
}
            """.trimIndent(),
            javaCode = """
// Style custom cloud datasets dynamically
FeatureLayer datasetLayer = googleMap.getDatasetFeatureLayer("YOUR_DATASET_ID");

if (datasetLayer != null) {
    datasetLayer.setFeatureStyle(feature -> {
        String ratingStr = feature.getDatasetAttributes().get("rating");
        double rating = ratingStr != null ? Double.parseDouble(ratingStr) : 0.0;
        int color = rating >= 4.5 ? Color.GREEN : Color.YELLOW;
        return new FeatureStyle.Builder()
            .pointRadius(8f)
            .fillColor(color)
            .strokeColor(Color.WHITE)
            .strokeWeight(2f)
            .build();
    });
}
            """.trimIndent()
        ),

        // 18. Cloud-based Map Styling
        "com.example.kotlindemos.CloudBasedMapStylingDemoActivity" to SnippetPair(
            kotlinCode = """
// Initialize map with Cloud-based Map ID styling
val options = GoogleMapOptions()
    .mapId("94f71120025f190c") // Cloud-styled Map ID from Google Cloud Console

val mapFragment = SupportMapFragment.newInstance(options)
supportFragmentManager.beginTransaction()
    .replace(R.id.map_container, mapFragment)
    .commit()
            """.trimIndent(),
            javaCode = """
// Initialize map with Cloud-based Map ID styling
GoogleMapOptions options = new GoogleMapOptions()
    .mapId("94f71120025f190c"); // Cloud-styled Map ID from Google Cloud Console

SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
getSupportFragmentManager().beginTransaction()
    .replace(R.id.map_container, mapFragment)
    .commit();
            """.trimIndent()
        ),

        // 19. Styled Map Demo
        "com.example.kotlindemos.StyledMapDemoActivity" to SnippetPair(
            kotlinCode = """
// Apply client-side JSON style to base map
try {
    val success = googleMap.setMapStyle(
        MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json)
    )
    if (!success) Log.e("MapStyle", "Style parsing failed")
} catch (e: Resources.NotFoundException) {
    Log.e("MapStyle", "Can't find style resource", e)
}
            """.trimIndent(),
            javaCode = """
// Apply client-side JSON style to base map
try {
    boolean success = googleMap.setMapStyle(
        MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json)
    );
    if (!success) Log.e("MapStyle", "Style parsing failed");
} catch (Resources.NotFoundException e) {
    Log.e("MapStyle", "Can't find style resource", e);
}
            """.trimIndent()
        ),

        // 20. Map Color Scheme
        "com.example.kotlindemos.MapColorSchemeActivity" to SnippetPair(
            kotlinCode = """
// Configure automatic system night mode following
val options = GoogleMapOptions()
    .mapColorScheme(MapColorScheme.FOLLOW_SYSTEM)

val mapFragment = SupportMapFragment.newInstance(options)
supportFragmentManager.beginTransaction()
    .replace(R.id.map_container, mapFragment)
    .commit()
            """.trimIndent(),
            javaCode = """
// Configure automatic system night mode following
GoogleMapOptions options = new GoogleMapOptions()
    .mapColorScheme(MapColorScheme.FOLLOW_SYSTEM);

SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
getSupportFragmentManager().beginTransaction()
    .replace(R.id.map_container, mapFragment)
    .commit();
            """.trimIndent()
        ),

        // 21. Split Street View & Map Sync
        "com.example.kotlindemos.SplitStreetViewPanoramaAndMapDemoActivity" to SnippetPair(
            kotlinCode = """
// Synchronize 2D Map Pegman marker with 3D Street View panorama
val streetView = supportFragmentManager.findFragmentById(R.id.streetview) as SupportStreetViewPanoramaFragment
streetView.getStreetViewPanoramaAsync { panorama ->
    panorama.setOnStreetViewPanoramaChangeListener { location ->
        pegmanMarker.position = location.position
    }
}

googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
    override fun onMarkerDragEnd(marker: Marker) {
        streetViewPanorama?.setPosition(marker.position)
    }
    override fun onMarkerDrag(m: Marker) {}
    override fun onMarkerDragStart(m: Marker) {}
})
            """.trimIndent(),
            javaCode = """
// Synchronize 2D Map Pegman marker with 3D Street View panorama
SupportStreetViewPanoramaFragment streetView = (SupportStreetViewPanoramaFragment)
    getSupportFragmentManager().findFragmentById(R.id.streetview);

streetView.getStreetViewPanoramaAsync(panorama -> {
    panorama.setOnStreetViewPanoramaChangeListener(location -> {
        pegmanMarker.setPosition(location.position);
    });
});

googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
    @Override public void onMarkerDragEnd(Marker marker) {
        streetViewPanorama.setPosition(marker.getPosition());
    }
    @Override public void onMarkerDrag(Marker m) {}
    @Override public void onMarkerDragStart(Marker m) {}
});
            """.trimIndent()
        ),

        // 22. Street View Panorama Basic
        "com.example.kotlindemos.StreetViewPanoramaBasicDemoActivity" to SnippetPair(
            kotlinCode = """
// Initialize standalone Street View panorama
val panoramaFragment = supportFragmentManager
    .findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment

panoramaFragment.getStreetViewPanoramaAsync { panorama ->
    panorama.setPosition(LatLng(-33.87365, 151.20689))
    panorama.isStreetNamesEnabled = true
    panorama.isUserNavigationEnabled = true
}
            """.trimIndent(),
            javaCode = """
// Initialize standalone Street View panorama
SupportStreetViewPanoramaFragment panoramaFragment = (SupportStreetViewPanoramaFragment)
    getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);

panoramaFragment.getStreetViewPanoramaAsync(panorama -> {
    panorama.setPosition(new LatLng(-33.87365, 151.20689));
    panorama.setStreetNamesEnabled(true);
    panorama.setUserNavigationEnabled(true);
});
            """.trimIndent()
        ),

        // 23. Lite Mode in RecyclerView
        "com.example.kotlindemos.LiteListDemoActivity" to SnippetPair(
            kotlinCode = """
// Smooth Lite Mode map lifecycle inside RecyclerView rows
class MapViewHolder(view: View) : RecyclerView.ViewHolder(view), OnMapReadyCallback {
    val mapView: MapView = view.findViewById(R.id.lite_map)
    init {
        mapView.onCreate(null)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(item.position, 10f))
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
    }
}
            """.trimIndent(),
            javaCode = """
// Smooth Lite Mode map lifecycle inside RecyclerView rows
static class MapViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    MapView mapView;
    MapViewHolder(View view) {
        super(view);
        mapView = view.findViewById(R.id.lite_map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(item.position, 10f));
    }
}
            """.trimIndent()
        ),

        // 24. Lite Demo
        "com.example.kotlindemos.LiteDemoActivity" to SnippetPair(
            kotlinCode = """
// Configure Lite Mode static snapshot map
val options = GoogleMapOptions().liteMode(true)
val mapFragment = SupportMapFragment.newInstance(options)

supportFragmentManager.beginTransaction()
    .replace(R.id.map_container, mapFragment)
    .commit()

mapFragment.getMapAsync { googleMap ->
    googleMap.addMarker(MarkerOptions().position(DARWIN).title("Darwin"))
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DARWIN, 10f))
}
            """.trimIndent(),
            javaCode = """
// Configure Lite Mode static snapshot map
GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);

getSupportFragmentManager().beginTransaction()
    .replace(R.id.map_container, mapFragment)
    .commit();

mapFragment.getMapAsync(googleMap -> {
    googleMap.addMarker(new MarkerOptions().position(DARWIN).title("Darwin"));
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DARWIN, 10f));
});
            """.trimIndent()
        ),

        // 25. Snapshot Demo
        "com.example.kotlindemos.SnapshotDemoActivity" to SnippetPair(
            kotlinCode = """
// Capture an asynchronous bitmap snapshot of the map surface
googleMap.snapshot { bitmap ->
    if (bitmap != null) {
        imageViewSnapshot.setImageBitmap(bitmap)
        imageViewSnapshot.visibility = View.VISIBLE
    }
}
            """.trimIndent(),
            javaCode = """
// Capture an asynchronous bitmap snapshot of the map surface
googleMap.snapshot(bitmap -> {
    if (bitmap != null) {
        imageViewSnapshot.setImageBitmap(bitmap);
        imageViewSnapshot.setVisibility(View.VISIBLE);
    }
});
            """.trimIndent()
        ),

        // 26. My Location Layer
        "com.example.kotlindemos.MyLocationDemoActivity" to SnippetPair(
            kotlinCode = """
// Enable GPS blue dot indicator and My Location button
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    == PackageManager.PERMISSION_GRANTED) {
    googleMap.isMyLocationEnabled = true
    googleMap.uiSettings.isMyLocationButtonEnabled = true
} else {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
}

googleMap.setOnMyLocationClickListener { location ->
    Toast.makeText(this, "Current GPS: \location.latitude, \location.longitude", Toast.LENGTH_SHORT).show()
}
            """.trimIndent(),
            javaCode = """
// Enable GPS blue dot indicator and My Location button
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    == PackageManager.PERMISSION_GRANTED) {
    googleMap.setMyLocationEnabled(true);
    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
} else {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
}

googleMap.setOnMyLocationClickListener(location -> {
    Toast.makeText(this, "Current GPS: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
});
            """.trimIndent()
        ),

        // 27. Custom LocationSource
        "com.example.kotlindemos.LocationSourceDemoActivity" to SnippetPair(
            kotlinCode = """
// Custom mock LocationSource for simulated GPS playback
class MockLocationSource : LocationSource {
    private var listener: LocationSource.OnLocationChangedListener? = null
    override fun activate(l: LocationSource.OnLocationChangedListener) { listener = l }
    override fun deactivate() { listener = null }

    fun pushLocation(lat: Double, lng: Double) {
        val loc = Location("mock").apply { latitude = lat; longitude = lng; time = System.currentTimeMillis() }
        listener?.onLocationChanged(loc)
    }
}

googleMap.setLocationSource(mockLocationSource)
googleMap.isMyLocationEnabled = true
            """.trimIndent(),
            javaCode = """
// Custom mock LocationSource for simulated GPS playback
class MockLocationSource implements LocationSource {
    private OnLocationChangedListener listener;
    @Override public void activate(OnLocationChangedListener l) { this.listener = l; }
    @Override public void deactivate() { this.listener = null; }

    public void pushLocation(double lat, double lng) {
        Location loc = new Location("mock");
        loc.setLatitude(lat); loc.setLongitude(lng); loc.setTime(System.currentTimeMillis());
        if (listener != null) listener.onLocationChanged(loc);
    }
}

googleMap.setLocationSource(mockLocationSource);
googleMap.setMyLocationEnabled(true);
            """.trimIndent()
        ),

        // 28. Ground Overlays
        "com.example.kotlindemos.GroundOverlayDemoActivity" to SnippetPair(
            kotlinCode = """
// Anchor raster image overlay to geographic LatLngBounds
val NEWARK_BOUNDS = LatLngBounds(LatLng(40.712, -74.226), LatLng(40.774, -74.125))

val groundOverlay = googleMap.addGroundOverlay(
    GroundOverlayOptions()
        .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
        .positionFromBounds(NEWARK_BOUNDS)
        .transparency(0.3f)
        .clickable(true)
)

googleMap.setOnGroundOverlayClickListener { overlay ->
    overlay.transparency = 0.6f
}
            """.trimIndent(),
            javaCode = """
// Anchor raster image overlay to geographic LatLngBounds
LatLngBounds NEWARK_BOUNDS = new LatLngBounds(new LatLng(40.712, -74.226), new LatLng(40.774, -74.125));

GroundOverlay groundOverlay = googleMap.addGroundOverlay(
    new GroundOverlayOptions()
        .image(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922))
        .positionFromBounds(NEWARK_BOUNDS)
        .transparency(0.3f)
        .clickable(true)
);

googleMap.setOnGroundOverlayClickListener(overlay -> overlay.setTransparency(0.6f));
            """.trimIndent()
        ),

        // 29. Tile Overlays & TileProvider
        "com.example.kotlindemos.TileOverlayDemoActivity" to SnippetPair(
            kotlinCode = """
// Custom UrlTileProvider rendering coordinate grid tiles
val tileProvider = object : UrlTileProvider(256, 256) {
    override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
        return URL("https://example.com/tiles/{zoom}/{x}/{y}.png")
    }
}

val tileOverlay = googleMap.addTileOverlay(
    TileOverlayOptions()
        .tileProvider(tileProvider)
        .transparency(0.2f)
        .fadeIn(true)
)
            """.trimIndent(),
            javaCode = """
// Custom UrlTileProvider rendering coordinate grid tiles
TileProvider tileProvider = new UrlTileProvider(256, 256) {
    @Override public URL getTileUrl(int x, int y, int zoom) {
        try {
            return new URL("https://example.com/tiles/" + zoom + "/" + x + "/" + y + ".png");
        } catch (MalformedURLException e) { return null; }
    }
};

TileOverlay tileOverlay = googleMap.addTileOverlay(
    new TileOverlayOptions()
        .tileProvider(tileProvider)
        .transparency(0.2f)
        .fadeIn(true)
);
            """.trimIndent()
        ),

        // 30. Events & Gestures
        "com.example.kotlindemos.EventsDemoActivity" to SnippetPair(
            kotlinCode = """
// Handle map clicks, long presses, camera moves, and POI selection
googleMap.setOnMapClickListener { latLng ->
    tapTextView.text = "Tapped: "+latLng.latitude+", "+latLng.longitude"
}

googleMap.setOnPoiClickListener { poi ->
    tapTextView.text = "POI: "+poi.name+" ("+poi.placeId+")"
}

googleMap.setOnCameraIdleListener {
    val pos = googleMap.cameraPosition
    tapTextView.text = "Camera idle: "+pos.target+", zoom="+pos.zoom"
}
            """.trimIndent(),
            javaCode = """
// Handle map clicks, long presses, camera moves, and POI selection
googleMap.setOnMapClickListener(latLng -> {
    tapTextView.setText("Tapped: " + latLng.latitude + ", " + latLng.longitude);
});

googleMap.setOnPoiClickListener(poi -> {
    tapTextView.setText("POI: " + poi.name + " (Place ID: " + poi.placeId + ")");
});

googleMap.setOnCameraIdleListener(() -> {
    CameraPosition pos = googleMap.getCameraPosition();
    tapTextView.setText("Camera idle: " + pos.target + ", zoom=" + pos.zoom);
});
            """.trimIndent()
        ),

        // 31. UI Settings & Map Controls
        "com.example.kotlindemos.UiSettingsDemoActivity" to SnippetPair(
            kotlinCode = """
// Configure map gestures and UI controls
val uiSettings = googleMap.uiSettings
uiSettings.isZoomControlsEnabled = true
uiSettings.isCompassEnabled = true
uiSettings.isMyLocationButtonEnabled = true
uiSettings.isScrollGesturesEnabled = true
uiSettings.isZoomGesturesEnabled = true
uiSettings.isTiltGesturesEnabled = true
uiSettings.isRotateGesturesEnabled = true
            """.trimIndent(),
            javaCode = """
// Configure map gestures and UI controls
UiSettings uiSettings = googleMap.getUiSettings();
uiSettings.setZoomControlsEnabled(true);
uiSettings.setCompassEnabled(true);
uiSettings.setMyLocationButtonEnabled(true);
uiSettings.setScrollGesturesEnabled(true);
uiSettings.setZoomGesturesEnabled(true);
uiSettings.setTiltGesturesEnabled(true);
uiSettings.setRotateGesturesEnabled(true);
            """.trimIndent()
        )
    )

    private fun getFallbackSnippet(sampleId: String): SnippetPair {
        val simpleName = sampleId.substringAfterLast('.')
        return SnippetPair(
            kotlinCode = """
// Entry point for \"+simpleName+"
val mapFragment = supportFragmentManager
    .findFragmentById(R.id.map) as SupportMapFragment

mapFragment.getMapAsync { googleMap ->
    // Setup and configure \"+simpleName+" features
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-33.852, 151.211), 10f))
}
            """.trimIndent(),
            javaCode = """
// Entry point for \"+simpleName+"
SupportMapFragment mapFragment = (SupportMapFragment) 
    getSupportFragmentManager().findFragmentById(R.id.map);

mapFragment.getMapAsync(googleMap -> {
    // Setup and configure \"+simpleName+" features
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.852, 151.211), 10f));
});
            """.trimIndent()
        )
    }
}
