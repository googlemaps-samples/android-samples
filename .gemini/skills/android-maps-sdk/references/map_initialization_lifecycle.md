# Map Initialization & Lifecycle Management

This guide covers map instantiation, lifecycle synchronization, and memory safety across Jetpack Compose, Kotlin Views, and Java Views.

---

## 1. Instantiation Strategies Compared

| Strategy | Best For | Lifecycle Handling | Configuration Changes |
| :--- | :--- | :--- | :--- |
| **`SupportMapFragment`** | Standard XML-based activities/fragments | **Automatic** (delegated to Fragment lifecycle) | Handled by Android FragmentManager |
| **`MapView`** | Custom view hierarchies, dialogs, lists, or custom composite views | **Manual** (must forward all lifecycle methods) | Must be preserved via `onSaveInstanceState` |
| **Compose `GoogleMap`** | Jetpack Compose applications | **Automatic** (bound to Composable lifecycle) | Preserved via `rememberCameraPositionState` |
| **Lite Mode `MapView`** | Lists (`RecyclerView`), cards, low-power devices | **Minimal** (read-only snapshot, no GL thread) | Rebindable per ViewHolder |

---

## 2. Kotlin Coroutine Map Initialization (`awaitMap()`)

In modern Kotlin Android apps, avoid nesting asynchronous callbacks inside `onCreate`. Use the suspending `awaitMap()` extension function provided by `android-maps-utils` (`com.google.maps.android.awaitMap`):

```kotlin
// [START maps_android_mapfragment_await]
class BasicMapDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_map_demo)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val googleMap = mapFragment.awaitMap()
                onMapReady(googleMap)
            }
        }
    }

    private fun onMapReady(map: GoogleMap) {
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))
    }
}
// [END maps_android_mapfragment_await]
```

---

## 3. Raw `MapView` Lifecycle Contract

When using `MapView` directly in an XML layout or programmatically, you **must** forward all lifecycle events. Failure to do so causes black viewports, unrendered tiles, or memory leaks:

```kotlin
// [START maps_android_mapview_lifecycle]
class RawMapViewDemoActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.raw_mapview_demo)

        mapView = findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            googleMap = map
            map.addMarker(MarkerOptions().position(LatLng(-34.0, 151.0)).title("Marker in Sydney"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-34.0, 151.0), 10f))
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
// [END maps_android_mapview_lifecycle]
```

### Clean Architecture: `MapViewLifecycleObserver`

To avoid polluting your Activity with boilerplate, wrap `MapView` lifecycle forwarding into a reusable `DefaultLifecycleObserver`:

```kotlin
class MapViewLifecycleObserver(
    private val mapView: MapView
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) = mapView.onStart()
    override fun onResume(owner: LifecycleOwner) = mapView.onResume()
    override fun onPause(owner: LifecycleOwner) = mapView.onPause()
    override fun onStop(owner: LifecycleOwner) = mapView.onStop()
    override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
}

// In Activity/Fragment:
lifecycle.addObserver(MapViewLifecycleObserver(mapView))
```

---

## 4. Programmatic Map Fragment Transactions

When adding a `SupportMapFragment` dynamically with custom `GoogleMapOptions`:

```kotlin
// [START maps_android_map_options]
val options = GoogleMapOptions()
    .mapType(GoogleMap.MAP_TYPE_NORMAL)
    .compassEnabled(true)
    .rotateGesturesEnabled(true)
    .tiltGesturesEnabled(true)
    .zoomControlsEnabled(false)

val mapFragment = SupportMapFragment.newInstance(options)
supportFragmentManager.beginTransaction()
    .replace(R.id.map_container, mapFragment)
    .commit()
// [END maps_android_map_options]
```

> [!WARNING]
> **View Hierarchy Occlusion Pitfall**: If switching between a `MapView` and a dynamically added `SupportMapFragment` in the same container, remove any existing Fragment before rendering the `MapView` to prevent invisible overlapping fragments from consuming touch events and blocking GL surface rendering.

---

## 5. Lite Mode for Performance & RecyclerView

Lite Mode displays an image representation of the map at specified coordinates. It eliminates the overhead of OpenGL rendering and camera threads:

```kotlin
// [START maps_android_mapview_litemode]
val options = GoogleMapOptions().liteMode(true)
val liteMapView = MapView(context, options)
liteMapView.onCreate(null)
liteMapView.getMapAsync { map ->
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
    map.addMarker(MarkerOptions().position(location))
}
// [END maps_android_mapview_litemode]
```
