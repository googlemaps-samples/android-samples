/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snippets.kotlin

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions

/**
 * Decorator wrapper around GoogleMap to track elements added during a snippet session
 * and clean them up automatically.
 */
class TrackedMap(
    val delegate: GoogleMap,
    val items: MutableList<Any>,
) {
    init {
        lastInstance = this
    }

    var registeredPoiClickListener: GoogleMap.OnPoiClickListener? = null
    var registeredMapClickListener: GoogleMap.OnMapClickListener? = null

    fun setOnPoiClickListener(listener: GoogleMap.OnPoiClickListener?) {
        registeredPoiClickListener = listener
        delegate.setOnPoiClickListener(listener)
    }

    fun addMarker(options: MarkerOptions): Marker? {
        val marker = delegate.addMarker(options)
        if (marker != null) items.add(marker)
        return marker
    }

    fun addMarker(options: com.google.android.gms.maps.model.AdvancedMarkerOptions): Marker? {
        val marker = delegate.addMarker(options)
        if (marker != null) items.add(marker)
        return marker
    }

    fun addPolyline(options: PolylineOptions): Polyline? {
        val polyline = delegate.addPolyline(options)
        if (polyline != null) items.add(polyline)
        return polyline
    }

    fun addPolygon(options: PolygonOptions): Polygon? {
        val polygon = delegate.addPolygon(options)
        if (polygon != null) items.add(polygon)
        return polygon
    }

    fun addCircle(options: CircleOptions): Circle? {
        val circle = delegate.addCircle(options)
        if (circle != null) items.add(circle)
        return circle
    }

    fun addGroundOverlay(options: GroundOverlayOptions): GroundOverlay? {
        val groundOverlay = delegate.addGroundOverlay(options)
        if (groundOverlay != null) items.add(groundOverlay)
        return groundOverlay
    }

    fun addTileOverlay(options: TileOverlayOptions): TileOverlay? {
        val tileOverlay = delegate.addTileOverlay(options)
        if (tileOverlay != null) items.add(tileOverlay)
        return tileOverlay
    }

    // Pass-through delegation for camera and settings
    fun moveCamera(update: CameraUpdate) = delegate.moveCamera(update)
    fun animateCamera(update: CameraUpdate) = delegate.animateCamera(update)
    fun animateCamera(update: CameraUpdate, callback: GoogleMap.CancelableCallback?) =
        delegate.animateCamera(update, callback)
    fun animateCamera(update: CameraUpdate, durationMs: Int, callback: GoogleMap.CancelableCallback?) =
        delegate.animateCamera(update, durationMs, callback)

    fun setMinZoomPreference(minZoomPreference: Float) = delegate.setMinZoomPreference(minZoomPreference)
    fun setMaxZoomPreference(maxZoomPreference: Float) = delegate.setMaxZoomPreference(maxZoomPreference)
    fun setLatLngBoundsForCameraTarget(bounds: LatLngBounds?) = delegate.setLatLngBoundsForCameraTarget(bounds)

    fun setOnMapClickListener(listener: GoogleMap.OnMapClickListener?) {
        registeredMapClickListener = listener
        delegate.setOnMapClickListener(listener)
    }
    fun setOnMapLongClickListener(listener: GoogleMap.OnMapLongClickListener?) = delegate.setOnMapLongClickListener(listener)
    fun setOnMarkerClickListener(listener: GoogleMap.OnMarkerClickListener?) = delegate.setOnMarkerClickListener(listener)
    fun setOnInfoWindowClickListener(listener: GoogleMap.OnInfoWindowClickListener?) = delegate.setOnInfoWindowClickListener(listener)
    fun setOnCameraIdleListener(listener: GoogleMap.OnCameraIdleListener?) = delegate.setOnCameraIdleListener(listener)
    fun setOnCameraMoveListener(listener: GoogleMap.OnCameraMoveListener?) = delegate.setOnCameraMoveListener(listener)
    fun setOnCameraMoveStartedListener(listener: GoogleMap.OnCameraMoveStartedListener?) = delegate.setOnCameraMoveStartedListener(listener)

    fun setMapType(type: Int) { delegate.mapType = type }
    fun getMapType(): Int = delegate.mapType

    fun setTrafficEnabled(enabled: Boolean) { delegate.isTrafficEnabled = enabled }
    fun isTrafficEnabled(): Boolean = delegate.isTrafficEnabled

    fun setIndoorEnabled(enabled: Boolean): Boolean = delegate.setIndoorEnabled(enabled)
    fun isIndoorEnabled(): Boolean = delegate.isIndoorEnabled

    fun setMyLocationEnabled(enabled: Boolean) {
        @SuppressWarnings("MissingPermission")
        delegate.isMyLocationEnabled = enabled
    }
    fun isMyLocationEnabled(): Boolean = delegate.isMyLocationEnabled

    fun getUiSettings(): UiSettings = delegate.uiSettings
    fun getProjection(): Projection = delegate.projection
    fun getCameraPosition(): CameraPosition = delegate.cameraPosition
    fun getMaxZoomLevel(): Float = delegate.maxZoomLevel
    fun getMinZoomLevel(): Float = delegate.minZoomLevel

    companion object {
        var lastInstance: TrackedMap? = null

        /** Aggressively resets all GoogleMap camera locks, styles, padding, UI settings, and event listeners. */
        fun resetMapToDefaults(map: GoogleMap?) {
            if (map == null) return
            map.clear()
            map.resetMinMaxZoomPreference()
            map.setLatLngBoundsForCameraTarget(null)
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            map.setMapStyle(null)
            map.setPadding(0, 0, 0, 0)
            map.isTrafficEnabled = false
            map.setIndoorEnabled(false)
            map.isBuildingsEnabled = true

            map.uiSettings.apply {
                setAllGesturesEnabled(true)
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = true
                isMapToolbarEnabled = true
                isIndoorLevelPickerEnabled = true
                isScrollGesturesEnabledDuringRotateOrZoom = true
            }

            map.setOnMapClickListener(null)
            map.setOnMapLongClickListener(null)
            map.setOnCameraMoveListener(null)
            map.setOnCameraMoveStartedListener(null)
            map.setOnCameraMoveCanceledListener(null)
            map.setOnCameraIdleListener(null)
            map.setOnMarkerClickListener(null)
            map.setOnMarkerDragListener(null)
            map.setOnInfoWindowClickListener(null)
            map.setOnInfoWindowLongClickListener(null)
            map.setOnInfoWindowCloseListener(null)
            map.setInfoWindowAdapter(null)
            map.setOnPoiClickListener(null)
            map.setOnPolygonClickListener(null)
            map.setOnPolylineClickListener(null)
            map.setOnGroundOverlayClickListener(null)
            map.setOnCircleClickListener(null)
        }
    }
}
