# Street View Panorama Integration

This guide provides recipes for embedding Google Street View panoramas, manipulating panorama cameras, navigating links, and synchronizing panoramas with interactive map views.

---

## 1. Embedding Street View

### Option A: `SupportStreetViewPanoramaFragment`
```xml
<fragment
    android:id="@+id/streetviewpanorama"
    android:name="com.google.android.gms.maps.SupportStreetViewPanoramaFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

In Activity / Fragment:
```kotlin
// [START maps_android_streetview_launch]
val streetViewFragment = supportFragmentManager
    .findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment

streetViewFragment.getStreetViewPanoramaAsync { panorama ->
    // Center panorama on target coordinates
    val sanFrancisco = LatLng(37.7749, -122.4194)
    panorama.setPosition(sanFrancisco, StreetViewSource.OUTDOOR)
}
// [END maps_android_streetview_launch]
```

### Option B: Raw `StreetViewPanoramaView`
When using `StreetViewPanoramaView` directly in custom layouts, forward all lifecycle methods (`onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`, `onSaveInstanceState`).

---

## 2. Setting Panorama Location & Sources

Filter panoramas to outdoor-only photography or search within a specific radius:

```kotlin
// [START maps_android_streetview_location]
val target = LatLng(48.8584, 2.2945) // Eiffel Tower, Paris
val radiusMeters = 50

// Restrict to outdoor Street View imagery within 50 meters
panorama.setPosition(target, radiusMeters, StreetViewSource.OUTDOOR)
// [END maps_android_streetview_location]
```

---

## 3. Controlling Camera Orientation

Change zoom, tilt, and bearing programmatically:

```kotlin
// [START maps_android_streetview_camera]
val camera = StreetViewPanoramaCamera.Builder()
    .zoom(1.5f)
    .tilt(30f)   // Pitch up/down (-90 to +90 degrees)
    .bearing(180f) // Facing South
    .build()

// Animate camera orientation over 2 seconds
panorama.animateTo(camera, 2000)
// [END maps_android_streetview_camera]
```

---

## 4. Split Map & Street View Synchronization

In a split-screen layout containing both a `GoogleMap` and a `StreetViewPanorama`:

```kotlin
// [START maps_android_streetview_split_sync]
// 1. Move panorama when map marker is dragged
map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
    override fun onMarkerDragEnd(marker: Marker) {
        panorama.setPosition(marker.position, StreetViewSource.OUTDOOR)
    }
    override fun onMarkerDragStart(marker: Marker) {}
    override fun onMarkerDrag(marker: Marker) {}
})

// 2. Rotate map marker icon when panorama camera turns
panorama.setOnStreetViewPanoramaCameraChangeListener { camera ->
    // Update marker bearing or pegman orientation on the map
    marker.rotation = camera.bearing
}

// 3. Move map marker when user navigates to an adjacent panorama
panorama.setOnStreetViewPanoramaChangeListener { location ->
    location?.position?.let { newLatLng ->
        marker.position = newLatLng
        map.animateCamera(CameraUpdateFactory.newLatLng(newLatLng))
    }
}
// [END maps_android_streetview_split_sync]
```
