// Copyright 2026 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.kotlindemos

import android.graphics.Color
import androidx.core.graphics.toColorInt

import com.google.android.gms.maps.OnMapReadyCallback
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import android.widget.TextView
import com.example.common_ui.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapCapabilities
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PinConfig


private val SINGAPORE = LatLng(1.3521, 103.8198)
private val KUALA_LUMPUR = LatLng(3.1390, 101.6869)
private val JAKARTA = LatLng(-6.2088, 106.8456)
private val BANGKOK = LatLng(13.7563, 100.5018)
private val MANILA = LatLng(14.5995, 120.9842)
private val HO_CHI_MINH_CITY = LatLng(10.7769, 106.7009)

private const val ZOOM_LEVEL = 3.5f

private val TAG = AdvancedMarkersDemoActivity::class.java.name


/**
 * The following sample showcases how to create Advanced Markers, and use all their customization
 * possibilities.
 */
// [START maps_android_sample_marker_advanced]
class AdvancedMarkersDemoActivity : SamplesBaseActivity(), OnMapReadyCallback {

    /**
     * This method is called when the activity is first created.
     *
     * It sets up the activity's layout and then initializes the map.
     *
     * The key logic here is to check if the developer has provided a Map ID in the
     * `strings.xml` file.
     *
     * If the `R.string.map_id` value is not the default "DEMO_MAP_ID", it means a
     * custom Map ID has been provided. In this case, we can rely on the simpler setup
     * where the `SupportMapFragment` is inflated directly from the XML layout, and it
     * will automatically use the Map ID from the string resource.
     *
     * However, if the `R.string.map_id` is still the default value, we fall back to a
     * programmatic setup. This involves:
     * 1. Retrieving the Map ID from the `secrets.properties` file, which is managed by the
     *    `ApiDemoApplication` class.
     * 2. Creating a `GoogleMapOptions` object.
     * 3. Explicitly setting the retrieved `mapId` on the `GoogleMapOptions`. This step is
     *    **critical** because Advanced Markers will not work without a valid Map ID.
     * 4. Creating a new `SupportMapFragment` instance with these options and replacing the
     *    placeholder fragment in the layout.
     *
     * This dual approach ensures that the demo can run seamlessly while also providing a
     * clear path for developers to use their own Map IDs, which is a requirement for using
     * Advanced Markers.
     */
    /**
     * This method is called when the activity is first created.
     *
     * It sets up the activity's layout and then initializes the map.
     *
     * The key logic here is to check if the developer has provided a Map ID in the
     * `strings.xml` file.
     *
     * If the `R.string.map_id` value is not the default "DEMO_MAP_ID", it means a
     * custom Map ID has been provided. In this case, we can rely on the simpler setup
     * where the `SupportMapFragment` is inflated directly from the XML layout, and it
     * will automatically use the Map ID from the string resource.
     *
     * However, if the `R.string.map_id` is still the default value, we fall back to a
     * programmatic setup. This involves:
     * 1. Retrieving the Map ID from the `secrets.properties` file, via the
     *    `ApiDemoApplication.mapId` property.
     * 2. Creating a `GoogleMapOptions` object.
     * 3. Explicitly setting the retrieved `mapId` on the `GoogleMapOptions`. This step is
     *    **critical** because Advanced Markers will not work without a valid Map ID.
     * 4. Creating a new `SupportMapFragment` instance with these options and replacing the
     *    placeholder fragment in the layout.
     *
     * This dual approach ensures that the demo can run seamlessly while also providing a
     * clear path for developers to use their own Map IDs, which is a requirement for using
     * Advanced Markers.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.common_ui.R.layout.advanced_markers_demo)

        if (getString(com.example.common_ui.R.string.map_id) != "DEMO_MAP_ID") {
            val mapFragment = supportFragmentManager.findFragmentById(com.example.common_ui.R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        } else {
            val mapId = (application as ApiDemoApplication).mapId

            // --- Map ID Check ---
            if (mapId == null) {
                finish()
                return // Exit early if no valid Map ID
            }

            // --- Programmatically create and add the map fragment ---
            val mapOptions = GoogleMapOptions().apply {
                mapId(mapId)
            }
            val mapFragment = SupportMapFragment.newInstance(mapOptions)
            supportFragmentManager.beginTransaction()
                .replace(R.id.map, mapFragment) // Use the container ID
                .commit()
            mapFragment.getMapAsync(this)
        }

        applyInsets(findViewById(com.example.common_ui.R.id.map_container))
    }

    override fun onMapReady(map: GoogleMap) {

        val bounds = LatLngBounds.builder()
            .include(SINGAPORE)
            .include(KUALA_LUMPUR)
            .include(JAKARTA)
            .include(BANGKOK)
            .include(MANILA)
            .include(HO_CHI_MINH_CITY)
            .build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))

        val capabilities: MapCapabilities = map.mapCapabilities
        Log.d(TAG, "are advanced marker enabled?" + capabilities.isAdvancedMarkersAvailable)

        // 1. Custom View as iconView (Framed circular badge with Android logo)
        val iconImageView = android.widget.ImageView(this).apply {
            setImageResource(R.drawable.ic_android)
            setColorFilter("#3DDC84".toColorInt()) // Android Green
            setBackgroundResource(R.drawable.bg_marker_badge)
            val padding = (8 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
            layoutParams = android.view.ViewGroup.LayoutParams(
                (44 * resources.displayMetrics.density).toInt(),
                (44 * resources.displayMetrics.density).toInt()
            )
        }
        map.addMarker(
            AdvancedMarkerOptions()
                .position(SINGAPORE)
                .iconView(iconImageView)
                .title("Singapore (Custom Framed Badge)")
                .zIndex(1f)
        )

        // 2. PinConfig with custom background color
        val pinConfigMagenta = PinConfig.builder()
            .setBackgroundColor(Color.MAGENTA)
            .build()
        map.addMarker(
            AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfigMagenta))
                .position(KUALA_LUMPUR)
                .title("Kuala Lumpur (Magenta Pin)")
        )

        // 3. PinConfig with custom border color
        val pinConfigBorder = PinConfig.builder()
            .setBorderColor(Color.BLUE)
            .build()
        map.addMarker(
            AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfigBorder))
                .position(JAKARTA)
                .title("Jakarta (Blue Border)")
        )

        // 4. PinConfig with text glyph ("A")
        val pinConfigTextGlyph = PinConfig.builder()
            .setGlyph(PinConfig.Glyph("A"))
            .build()
        map.addMarker(
            AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfigTextGlyph))
                .position(BANGKOK)
                .title("Bangkok (Text Glyph 'A')")
        )

        // 5. PinConfig with transparent glyph (cutout / donut pin)
        val pinConfigHole = PinConfig.builder()
            .setBackgroundColor(Color.MAGENTA)
            .setGlyph(PinConfig.Glyph(Color.TRANSPARENT))
            .build()
        map.addMarker(
            AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfigHole))
                .position(MANILA)
                .title("Manila (Transparent Cutout Glyph)")
        )

        // 6. Collision behavior
        val collisionBehavior =
            AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL
        map.addMarker(
            AdvancedMarkerOptions()
                .position(HO_CHI_MINH_CITY)
                .collisionBehavior(collisionBehavior)
                .title("Ho Chi Minh City (Collision Behavior)")
        )
    }
}
// [END maps_android_sample_marker_advanced]