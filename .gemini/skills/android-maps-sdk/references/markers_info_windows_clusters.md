# Markers, Info Windows, and Marker Clustering

This guide provides recipes for rendering points of interest, designing custom popup info windows, and clustering large datasets efficiently.

---

## 1. Marker Options & Custom Icons

### Adding Basic & Customized Markers
```kotlin
// [START maps_android_markers_add]
val sydney = LatLng(-34.0, 151.0)
val marker = map.addMarker(
    MarkerOptions()
        .position(sydney)
        .title("Sydney Opera House")
        .snippet("UNESCO World Heritage Site")
        .draggable(true)
        .flat(false) // True to lay flat against the map surface, rotating with map
        .rotation(45.0f)
        .alpha(0.9f)
        .zIndex(1.0f)
)
// [END maps_android_markers_add]
```

### Loading Custom Vector & Bitmap Icons
Always render vector drawables to bitmaps before passing to `BitmapDescriptorFactory`:

```kotlin
// [START maps_android_markers_icon_vector]
fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
    vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

// Applying custom icon:
map.addMarker(
    MarkerOptions()
        .position(location)
        .icon(bitmapDescriptorFromVector(context, R.drawable.ic_custom_pin))
        .anchor(0.5f, 1.0f) // Anchor bottom-center of icon to coordinate
)
// [END maps_android_markers_icon_vector]
```

---

## 2. Custom Info Windows (`InfoWindowAdapter`)

Google Maps provides two levels of customization via `GoogleMap.InfoWindowAdapter`:
- **`getInfoWindow(marker)`**: Replaces the entire info window frame (including the background bubble).
- **`getInfoContents(marker)`**: Replaces only the inside contents, preserving the default bubble frame.

```kotlin
// [START maps_android_markers_custom_infowindow]
class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val contentsView: View = LayoutInflater.from(context)
        .inflate(R.layout.custom_info_contents, null)

    override fun getInfoWindow(marker: Marker): View? {
        // Return null to use default info window frame
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val titleView = contentsView.findViewById<TextView>(R.id.title)
        val snippetView = contentsView.findViewById<TextView>(R.id.snippet)

        titleView.text = marker.title
        snippetView.text = marker.snippet
        return contentsView
    }
}

// In setup:
map.setInfoWindowAdapter(CustomInfoWindowAdapter(context))
map.setOnInfoWindowClickListener { marker ->
    // Handle tap on info window popup
    navigateToPlaceDetails(marker.tag as? String)
}
// [END maps_android_markers_custom_infowindow]
```

---

## 3. High-Performance Clustering (`ClusterManager`)

When displaying hundreds or thousands of markers, individual marker creation degrades performance. Use the `ClusterManager` from `android-maps-utils`.

### Step 1: Define Cluster Item with Value Equality
```kotlin
// [START maps_android_utils_clustering_item]
data class MyItem(
    private val position: LatLng,
    private val title: String,
    private val snippet: String
) : ClusterItem {

    override fun getPosition(): LatLng = position
    override fun getTitle(): String = title
    override fun getSnippet(): String = snippet
    override fun getZIndex(): Float? = null

    // Ensure equals & hashCode are implemented for accurate item removal
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MyItem) return false
        return position == other.position && title == other.title && snippet == other.snippet
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + snippet.hashCode()
        return result
    }
}
// [END maps_android_utils_clustering_item]
```

### Step 2: Initialize & Wire Cluster Manager
```kotlin
// [START maps_android_utils_clustering_add]
val clusterManager = ClusterManager<MyItem>(context, map)

// Route map camera & click listeners through ClusterManager
map.setOnCameraIdleListener(clusterManager)
map.setOnMarkerClickListener(clusterManager)
map.setOnInfoWindowClickListener(clusterManager)

// Add items to cluster manager
val items = listOf(
    MyItem(LatLng(51.503186, -0.126446), "Big Ben", "London landmark"),
    MyItem(LatLng(51.507351, -0.127758), "Trafalgar Square", "Public square")
)
clusterManager.addItems(items)

// Trigger clustering calculation
clusterManager.cluster()
// [END maps_android_utils_clustering_add]
```

### Step 3: Custom Cluster Rendering (`DefaultClusterRenderer`)
To customize individual marker icons or cluster bubble styles:

```kotlin
class CustomClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MyItem>
) : DefaultClusterRenderer<MyItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: MyItem, markerOptions: MarkerOptions) {
        markerOptions
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            .title(item.title)
    }

    override fun onBeforeClusterRendered(cluster: Cluster<MyItem>, markerOptions: MarkerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions)
    }
}

// In setup:
clusterManager.renderer = CustomClusterRenderer(context, map, clusterManager)
```
