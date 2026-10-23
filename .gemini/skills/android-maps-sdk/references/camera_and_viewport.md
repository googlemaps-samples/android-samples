# Camera Controls & Viewport Geometry

This guide provides recipes for camera manipulation, smooth animations, viewport boundary clamping, and coordinate projection.

---

## 1. Camera Anatomy

A `CameraPosition` is composed of four parameters:
- **`target`**: `LatLng` coordinates representing the point in the center of the viewport.
- **`zoom`**: Float defining the scale (typically 2.0 to 21.0).
- **`tilt`**: Angle in degrees away from nadir (0° looking straight down, up to 67.5°).
- **`bearing`**: Direction the camera points, in degrees clockwise from North (0° to 359.9°).

```kotlin
val position = CameraPosition.Builder()
    .target(LatLng(37.7749, -122.4194)) // San Francisco
    .zoom(15.5f)
    .bearing(300f)
    .tilt(50f)
    .build()

map.animateCamera(CameraUpdateFactory.newCameraPosition(position))
```

---

## 2. Camera Animations & Callbacks

Use `animateCamera` with a `GoogleMap.CancelableCallback` to coordinate multi-step tours, choreograph UI transitions, or detect when user pan gestures interrupt animations:

```kotlin
map.animateCamera(
    CameraUpdateFactory.newLatLngZoom(targetLocation, 16f),
    2000, // Duration in milliseconds
    object : GoogleMap.CancelableCallback {
        override fun onFinish() {
            // Camera arrived at target
            showTargetMarkerDetails()
        }

        override fun onCancel() {
            // Animation interrupted by user gesture or another animation
            restoreDefaultUIState()
        }
    }
)
```

---

## 3. Fitting Multiple Coordinates with `LatLngBounds`

To fit a collection of markers or shapes within the current viewport:

```kotlin
val boundsBuilder = LatLngBounds.Builder()
for (marker in markerList) {
    boundsBuilder.include(marker.position)
}
val bounds = boundsBuilder.build()
val paddingPx = 100 // Padding in pixels from map edges

// Ensure the map view has been measured before calling newLatLngBounds
map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, paddingPx))
```

> [!NOTE]
> If calling `newLatLngBounds` in `onCreate` or before the layout pass finishes, the map dimensions are 0x0 and an `IllegalStateException` is thrown. Either use `addOnGlobalLayoutListener` or pass explicit width and height: `CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)`.

---

## 4. Camera Clamping & Constraints

Restrict user panning to specific geographical areas (e.g., a specific city, country, or park) and restrict zoom levels:

```kotlin
// Define bounding box (e.g. Adelaide region)
val ADELAIDE_BOUNDS = LatLngBounds(
    LatLng(-35.0, 138.4), // Southwest corner
    LatLng(-34.8, 138.7)  // Northeast corner
)

// Constrain camera center target to bounds
map.setLatLngBoundsForCameraTarget(ADELAIDE_BOUNDS)

// Constrain allowable zoom levels
map.setMinZoomPreference(10.0f)
map.setMaxZoomPreference(18.0f)
```

To release clamping constraints:
```kotlin
map.setLatLngBoundsForCameraTarget(null)
map.resetMinMaxZoomPreference()
```

---

## 5. Viewport Coordinates & Projection

To convert between on-screen pixel coordinates (`android.graphics.Point`) and geographic coordinates (`LatLng`), use the map's `Projection`:

```kotlin
val projection = map.projection

// Convert screen touch point to LatLng
val touchLatLng = projection.fromScreenLocation(Point(x, y))

// Convert LatLng to screen coordinates
val screenPoint = projection.toScreenLocation(LatLng(-34.0, 151.0))

// Inspect current visible region bounding polygon
val visibleRegion = projection.visibleRegion
val bounds = visibleRegion.latLngBounds
val nearLeft = visibleRegion.nearLeft
val farRight = visibleRegion.farRight
```
