---
name: maps-sdk-android
description: Guide for integrating the Google Maps SDK for Android (Views/Fragments) and Maps Compose into an Android application. Use when users ask to add Google Maps to their Android app or implement advanced map features.
---

# Google Maps SDK for Android Integration

You are an expert Android developer specializing in the Google Maps SDK for Android. Your task is to integrate the Maps SDK into the user's application. You support both **Jetpack Compose** (`maps-compose`) and **Classic Android Views** (`SupportMapFragment`).

## 1. Setup Dependencies

Add the necessary dependencies to the app-level `build.gradle.kts` file based on the UI framework:

### For Jetpack Compose (Recommended for new apps):
```kotlin
dependencies {
    // Google Maps Compose library
    implementation("com.google.maps.android:maps-compose:6.1.0") // Check for the latest version
    // Optional: Maps Compose Utilities (for clustering, etc.)
    // implementation("com.google.maps.android:maps-compose-utils:6.1.0")
}
```

### For Classic Android Views (Fragments/XML):
```kotlin
dependencies {
    // Google Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:19.0.0") // Check for the latest version
}
```

## 2. Setup the Secrets Gradle Plugin (Mandatory for both)

Never hardcode the API key. Use the Secrets Gradle Plugin for Android to inject the API key securely.

**Project-level `build.gradle.kts`:**
```kotlin
buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}
```

**App-level `build.gradle.kts`:**
```kotlin
plugins {
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}
```

**`local.properties`:**
```properties
MAPS_API_KEY=YOUR_API_KEY
```

**`AndroidManifest.xml`:**
```xml
<manifest ...>
    <application ...>
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}" />
    </application>
</manifest>
```

## 3. Implement the Map

### Option A: Jetpack Compose (Maps Compose)
Create a Composable and use `GoogleMap` along with `Marker`.
```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen() {
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = singapore),
            title = "Singapore",
            snippet = "Marker in Singapore"
        )
    }
}
```

### Option B: Classic Android Views
Use `SupportMapFragment` to manage the map lifecycle automatically.
```xml
<!-- activity_maps.xml -->
<fragment xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/map"
    android:name="com.google.android.gms.maps.SupportMapFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
```kotlin
// MapsActivity.kt
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val singapore = LatLng(1.35, 103.87)
        googleMap.addMarker(MarkerOptions().position(singapore).title("Marker in Singapore"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 10f))
    }
}
```

## 4. Advanced Features (Referencing `android-samples`)

When a user asks for advanced features, implement them using these established patterns:

*   **Marker Clustering:** Use the `maps-compose-utils` library and the `Clustering` composable. (Classic Views: Use `android-maps-utils` and `ClusterManager`).
*   **Drawing on the Map:** Use `Polyline`, `Polygon`, or `Circle` composables.
*   **Map Styling:** Apply custom JSON styling via `MapProperties(mapStyleOptions = MapStyleOptions(json))` in Compose, or `googleMap.setMapStyle()` in Classic Views.
*   **Live Synchronization:** For multi-device real-time updates (like taxi tracking), use Firebase Realtime Database to push/pull `LatLng` coordinates and animate marker states locally.
*   **Location Tracking:** Request `ACCESS_FINE_LOCATION`, then set `isMyLocationEnabled = true` on `MapProperties` (Compose) or `googleMap.isMyLocationEnabled = true` (Classic Views).

## 5. Execution Guidelines
1. Always prefer Jetpack Compose unless the user explicitly asks for XML/Fragments.
2. Never log or commit the raw API key.
3. Hoist state (like camera positions or marker lists) to the ViewModel if the map data is dynamic.
