// Copyright 2023 Google LLC
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

        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(SINGAPORE, ZOOM_LEVEL))
        }

        val capabilities: MapCapabilities = map.mapCapabilities
        Log.d(TAG, "are advanced marker enabled?" + capabilities.isAdvancedMarkersAvailable)

        // This sample sets a view as the iconView for the Advanced Marker
        val textView = TextView(this)
        textView.text = "Hello!"
        val advancedMarkerView: Marker? = map.addMarker(
            AdvancedMarkerOptions().position(SINGAPORE).iconView(textView).zIndex(1f)
        )

        // This uses PinConfig.Builder to create an instance of PinConfig.
        val pinConfigBuilder: PinConfig.Builder = PinConfig.builder()
        pinConfigBuilder.setBackgroundColor(Color.MAGENTA)
        val pinConfig: PinConfig = pinConfigBuilder.build()


        // Use the  PinConfig instance to set the icon for AdvancedMarkerOptions.
        val advancedMarkerOptions: AdvancedMarkerOptions =
            AdvancedMarkerOptions().icon(BitmapDescriptorFactory.fromPinConfig(pinConfig))
                .position(KUALA_LUMPUR)


        // Pass the AdvancedMarkerOptions instance to addMarker().
        val marker: Marker? = map.addMarker(advancedMarkerOptions)

        // This sample changes the border color of the advanced marker
        val pinConfigBuilder2: PinConfig.Builder = PinConfig.builder()
        pinConfigBuilder2.setBorderColor(Color.BLUE)
        val pinConfig2: PinConfig = pinConfigBuilder2.build()

        val advancedMarkerOptions2: AdvancedMarkerOptions = AdvancedMarkerOptions()
            .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig2))
            .position(JAKARTA)


        val marker2: Marker? = map.addMarker(advancedMarkerOptions2)

        // Set the glyph text.
        val pinConfigBuilder3: PinConfig.Builder = PinConfig.builder()
        val glyphText = PinConfig.Glyph("A")

        // Alternatively, you can set the text color:
        // Glyph glyphText = new Glyph("A", Color.GREEN);
        pinConfigBuilder3.setGlyph(glyphText)
        val pinConfig3: PinConfig = pinConfigBuilder3.build()

        val advancedMarkerOptions3: AdvancedMarkerOptions = AdvancedMarkerOptions()
            .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig3))
            .position(BANGKOK)

        val marker3: Marker? = map.addMarker(advancedMarkerOptions3)

        // Create a transparent glyph.
        val pinConfigBuilder4: PinConfig.Builder = PinConfig.builder()
        pinConfigBuilder4.setBackgroundColor(Color.MAGENTA)
        pinConfigBuilder4.setGlyph(PinConfig.Glyph(Color.TRANSPARENT))
        val pinConfig4: PinConfig = pinConfigBuilder4.build()

        val advancedMarkerOptions4: AdvancedMarkerOptions = AdvancedMarkerOptions()
            .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig4))
            .position(MANILA)

        val marker4: Marker? = map.addMarker(advancedMarkerOptions4)

        // Collision behavior can only be changed in the AdvancedMarkerOptions object.
        // Changes to collision behavior after a marker has been created are not possible
        val collisionBehavior: Int =
            AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL
        val advancedMarkerOptions5: AdvancedMarkerOptions = AdvancedMarkerOptions()
            .position(HO_CHI_MINH_CITY)
            .collisionBehavior(collisionBehavior)

        val marker5: Marker? = map.addMarker(advancedMarkerOptions5)
    }
}
// [END maps_android_sample_marker_advanced]