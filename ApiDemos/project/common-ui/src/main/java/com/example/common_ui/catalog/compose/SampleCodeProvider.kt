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
 * Registry of curated, syntax-highlighted code snippets for each sample in Kotlin and Java.
 *
 * Extracts the core essence of the demo without boilerplate.
 */
object SampleCodeProvider {

    data class SnippetPair(
        val kotlinCode: String,
        val javaCode: String
    )

    fun getCode(sampleId: String, framework: Framework): String {
        val snippet = SNIPPETS[sampleId] ?: getFallbackSnippet(sampleId)
        return when (framework) {
            Framework.KOTLIN_VIEWS -> snippet.kotlinCode
            Framework.JAVA_VIEWS -> snippet.javaCode
        }
    }

    private val SNIPPETS = mapOf(
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

        "com.example.kotlindemos.ProgrammaticDemoActivity" to SnippetPair(
            kotlinCode = """
// Create SupportMapFragment programmatically
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
// Create SupportMapFragment programmatically
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
    Toast.makeText(this, "Tapped polygon: \polygon.id", Toast.LENGTH_SHORT).show()
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
        @Override
        public void onFinish() { Log.d("Camera", "Animation complete"); }
        @Override
        public void onCancel() { Log.d("Camera", "Animation cancelled"); }
    }
);
            """.trimIndent()
        ),

        "com.example.kotlindemos.DataDrivenBoundariesActivity" to SnippetPair(
            kotlinCode = """
// Style administrative boundaries dynamically
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
// Style administrative boundaries dynamically
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

        "com.example.kotlindemos.SnapshotDemoActivity" to SnippetPair(
            kotlinCode = """
// Capture an asynchronous bitmap snapshot of the map
googleMap.snapshot { bitmap ->
    if (bitmap != null) {
        imageViewSnapshot.setImageBitmap(bitmap)
        imageViewSnapshot.visibility = View.VISIBLE
    }
}
            """.trimIndent(),
            javaCode = """
// Capture an asynchronous bitmap snapshot of the map
googleMap.snapshot(bitmap -> {
    if (bitmap != null) {
        imageViewSnapshot.setImageBitmap(bitmap);
        imageViewSnapshot.setVisibility(View.VISIBLE);
    }
});
            """.trimIndent()
        ),

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
        )
    )

    private fun getFallbackSnippet(sampleId: String): SnippetPair {
        val simpleName = sampleId.substringAfterLast('.')
        return SnippetPair(
            kotlinCode = """
// Entry point for SampleActivity
val mapFragment = supportFragmentManager
    .findFragmentById(R.id.map) as SupportMapFragment

mapFragment.getMapAsync { googleMap ->
    // Setup and configure SampleActivity features
    googleMap.uiSettings.isZoomControlsEnabled = true
}
            """.trimIndent(),
            javaCode = """
// Entry point for SampleActivity
SupportMapFragment mapFragment = (SupportMapFragment) 
    getSupportFragmentManager().findFragmentById(R.id.map);

mapFragment.getMapAsync(googleMap -> {
    // Setup and configure SampleActivity features
    googleMap.getUiSettings().setZoomControlsEnabled(true);
});
            """.trimIndent()
        )
    }
}
