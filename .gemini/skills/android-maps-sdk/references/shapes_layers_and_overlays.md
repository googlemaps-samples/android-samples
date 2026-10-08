# Shapes, Layers, and Overlays

This guide provides recipes for rendering geometric shapes, ground images, map tiles, and geographic vector formats (GeoJSON, KML, Heatmaps).

---

## 1. Polylines & Paths

Polylines draw continuous linear paths on the map.

```kotlin
val polyline = map.addPolyline(
    PolylineOptions()
        .add(
            LatLng(-34.0, 151.0),
            LatLng(-34.1, 151.1),
            LatLng(-34.2, 151.2)
        )
        .color(Color.BLUE)
        .width(10f)
        .geodesic(true) // Follows the curvature of the earth
        .startCap(RoundCap())
        .endCap(CustomCap(bitmapDescriptorFromVector(context, R.drawable.ic_arrow), 16f))
        .jointType(JointType.ROUND)
        .pattern(listOf(Dash(30f), Gap(20f), Dot(), Gap(20f)))
        .clickable(true)
)

map.setOnPolylineClickListener { clickedPolyline ->
    // Handle tap on route
}
```

---

## 2. Polygons & Enclosed Areas

Polygons enclose geographic regions and can include cutout holes:

```kotlin
val outerBoundary = listOf(
    LatLng(10.0, 10.0),
    LatLng(10.0, 20.0),
    LatLng(20.0, 20.0),
    LatLng(20.0, 10.0)
)
val innerHole = listOf(
    LatLng(13.0, 13.0),
    LatLng(13.0, 17.0),
    LatLng(17.0, 17.0),
    LatLng(17.0, 13.0)
)

val polygon = map.addPolygon(
    PolygonOptions()
        .addAll(outerBoundary)
        .addHole(innerHole)
        .strokeColor(Color.RED)
        .strokeWidth(5f)
        .fillColor(Color.argb(100, 255, 0, 0)) // Semi-transparent red
        .clickable(true)
)
```

---

## 3. Circles & Geographic Radii

```kotlin
val circle = map.addCircle(
    CircleOptions()
        .center(LatLng(37.7749, -122.4194))
        .radius(1000.0) // Radius in meters
        .strokeColor(Color.BLUE)
        .strokeWidth(4f)
        .fillColor(Color.argb(70, 0, 0, 255))
)
```

---

## 4. Ground Overlays & Floor Plans

Ground overlays fix an image directly to the map surface:

```kotlin
val image = BitmapDescriptorFactory.fromResource(R.drawable.campus_floorplan)
val newark = LatLng(40.712216, -74.22655)

val groundOverlay = map.addGroundOverlay(
    GroundOverlayOptions()
        .image(image)
        .position(newark, 8600f, 6500f) // Center coordinate and width/height in meters
        .bearing(180f) // Rotation in degrees clockwise from North
        .transparency(0.2f)
)
```

---

## 5. GeoJSON & KML Vector Layers (`android-maps-utils`)

Import and render structured GIS files easily using the Utility Library:

### Rendering GeoJSON
```kotlin
val layer = GeoJsonLayer(map, R.raw.geographic_data, context)

// Customize polygon styles within GeoJSON
val style = layer.defaultPolygonStyle
style.fillColor = Color.argb(128, 0, 150, 136)
style.strokeColor = Color.DKGRAY
style.strokeWidth = 2f

layer.addLayerToMap()

// Handling click events on features
layer.setOnFeatureClickListener { feature ->
    val name = feature.getProperty("NAME")
    Toast.makeText(context, "Clicked: $name", Toast.LENGTH_SHORT).show()
}
```

### Rendering KML / KMZ
```kotlin
val kmlLayer = KmlLayer(map, R.raw.trail_network, context)
kmlLayer.addLayerToMap()

// Removing KML layer when done:
kmlLayer.removeLayerFromMap()
```

---

## 6. Heatmaps (`HeatmapTileProvider`)

Visualize point density (e.g., crime statistics, earthquake catalogs, delivery zones):

```kotlin
// 1. Prepare coordinates
val latLngList: List<LatLng> = loadEarthquakeCoordinates()

// 2. Build Tile Provider
val provider = HeatmapTileProvider.Builder()
    .data(latLngList)
    .radius(50) // Blur radius in pixels (default: 20, max: 50)
    .opacity(0.8)
    .build()

// 3. Add Tile Overlay to Map
val overlay = map.addTileOverlay(TileOverlayOptions().tileProvider(provider))

// Update data dynamically:
provider.setData(newCoordinates)
overlay.clearTileCache()
```
