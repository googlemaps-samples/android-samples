# Data-Driven Styling & Administrative Boundaries

This guide explains how to use Cloud-based Map Styling and Data-Driven Styling for administrative boundaries (countries, states, counties, postal codes, localities) and custom datasets.

---

## 1. Prerequisites for Data-Driven Styling

Data-Driven Styling requires:
1. **Cloud Console Map ID**: A Map ID created in the Google Cloud Console configured with **Vector** map type.
2. **Cloud Styling Enabled**: Specific feature layers (such as *Administrative Area Level 1*, *Country*, or *Locality*) must be enabled on the associated map style.
3. **Map ID Configured on Map Initialization**:

In XML layout:
```xml
<fragment
    android:id="@+id/map"
    android:name="com.google.android.gms.maps.SupportMapFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    map:mapId="@string/map_id" />
```

Or programmatically:
```kotlin
val options = GoogleMapOptions().mapId("YOUR_VECTOR_MAP_ID")
val mapFragment = SupportMapFragment.newInstance(options)
```

---

## 2. Accessing Administrative Boundary Feature Layers

Retrieve the `FeatureLayer` for the desired geographic boundary type:

```kotlin
val areaLevel1Layer: FeatureLayer? = map.getFeatureLayer(
    FeatureType.ADMINISTRATIVE_AREA_LEVEL_1 // States / Provinces
)

// Check if layer is available (returns false if MapId is missing or raster)
if (areaLevel1Layer?.isAvailable == true) {
    applyBoundaryStyling(areaLevel1Layer)
} else {
    Log.w("MapStyling", "FeatureLayer is not available on this Map ID")
}
```

Available `FeatureType` constants:
- `FeatureType.ADMINISTRATIVE_AREA_LEVEL_1`: States, provinces, regions.
- `FeatureType.ADMINISTRATIVE_AREA_LEVEL_2`: Counties, prefectures, districts.
- `FeatureType.COUNTRY`: National borders.
- `FeatureType.LOCALITY`: Incorporated cities and municipalities.
- `FeatureType.POSTAL_CODE`: ZIP / Postal code areas.
- `FeatureType.DATASET`: Custom polygons uploaded to Google Cloud Console.

---

## 3. Dynamic Boundary Styling (`FeatureLayer.setFeatureStyle`)

Apply custom fill, stroke, and opacity based on feature place IDs or application state:

```kotlin
// Define target place ID (e.g. State of Washington)
val WASHINGTON_PLACE_ID = "ChIJ-bDD5__lhVQRuvNfbGhRlAw"
var selectedPlaceId: String? = null

areaLevel1Layer.setFeatureStyle { feature ->
    if (feature is PlaceFeature) {
        val isSelected = feature.placeId == selectedPlaceId
        val isWashington = feature.placeId == WASHINGTON_PLACE_ID

        when {
            isSelected -> {
                FeatureStyle.Builder()
                    .fillColor(Color.argb(180, 255, 152, 0)) // Vivid Orange
                    .strokeColor(Color.rgb(230, 81, 0))
                    .strokeWeight(4f)
                    .build()
            }
            isWashington -> {
                FeatureStyle.Builder()
                    .fillColor(Color.argb(100, 76, 175, 80)) // Translucent Green
                    .strokeColor(Color.rgb(46, 125, 50))
                    .strokeWeight(2f)
                    .build()
            }
            else -> null // Default base map styling
        }
    } else {
        null
    }
}
```

---

## 4. Handling Boundary Click Events

Detect when a user taps within an administrative boundary:

```kotlin
areaLevel1Layer.addOnFeatureClickListener { event ->
    val features = event.features
    val clickedFeature = features.firstOrNull() as? PlaceFeature
    if (clickedFeature != null) {
        selectedPlaceId = clickedFeature.placeId
        // Refresh styling to reflect new selection
        areaLevel1Layer.setFeatureStyle(areaLevel1Layer.featureStyle)

        Toast.makeText(context, "Selected: ${clickedFeature.placeId}", Toast.LENGTH_SHORT).show()
    }
}
```

To clear boundary styles:
```kotlin
areaLevel1Layer.setFeatureStyle(null)
```
