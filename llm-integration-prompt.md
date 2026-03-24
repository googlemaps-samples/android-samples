# Google Maps SDK for Android - AI Integration Prompt

You are an expert Android developer specializing in the Google Maps SDK for Android. Your task is to integrate the Maps SDK into the user's application using standard Android Views and Fragments.

Please follow these instructions carefully to ensure a secure and idiomatic implementation.

## 1. Setup Dependencies

Add the necessary dependency to the app-level `build.gradle.kts` file:

```kotlin
dependencies {
    // Google Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:19.0.0") // Check for the latest version
}
```

## 2. Setup the Secrets Gradle Plugin

Instead of hardcoding the Google Maps API key in `AndroidManifest.xml`, use the Secrets Gradle Plugin for Android to inject the API key securely.

First, add the plugin to the project-level `build.gradle.kts`:

```kotlin
buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}
```

Then, apply the plugin in the app-level `build.gradle.kts`:

```kotlin
plugins {
    // ...
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}
```

Add the API Key to `local.properties`:

```properties
MAPS_API_KEY=YOUR_API_KEY
```

In `AndroidManifest.xml`, reference the injected API key meta-data:

```xml
<manifest ...>
    <application ...>
        <!-- Google Maps API Key injected by Secrets Gradle Plugin -->
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}" />
        ...
    </application>
</manifest>
```

## 3. Implement the Map

The standard way to implement a map is by using a `SupportMapFragment`. 

**activity_maps.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<fragment xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/map"
    android:name="com.google.android.gms.maps.SupportMapFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MapsActivity" />
```

**MapsActivity.kt:**
```kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Singapore and move the camera
        val singapore = LatLng(1.35, 103.87)
        map.addMarker(MarkerOptions().position(singapore).title("Marker in Singapore"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 10f))
    }
}
```

## 4. Best Practices & Guidelines
*   **Lifecycle Management:** `SupportMapFragment` is the recommended approach because it manages the map lifecycle automatically. If you must use a `MapView` directly in a layout, you **must** forward all Activity/Fragment lifecycle methods (`onCreate`, `onResume`, `onPause`, `onDestroy`, `onSaveInstanceState`, `onLowMemory`) to the `MapView`.
*   **Main Thread:** All interactions with the `GoogleMap` object must occur on the main UI thread.
*   **Permissions:** You do not need to request `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION` permissions unless you are explicitly enabling the "My Location" layer via `map.isMyLocationEnabled = true`.
