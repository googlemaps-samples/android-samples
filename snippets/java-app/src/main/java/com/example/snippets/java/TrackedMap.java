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

package com.example.snippets.java;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import java.util.List;

/** Decorator wrapper around GoogleMap to track elements added during a snippet session. */
public class TrackedMap {

    public static TrackedMap lastInstance = null;
    public GoogleMap.OnPoiClickListener registeredPoiClickListener = null;
    public GoogleMap.OnMapClickListener registeredMapClickListener = null;

    private final GoogleMap delegate;
    private final List<Object> items;

    public TrackedMap(GoogleMap delegate, List<Object> items) {
        this.delegate = delegate;
        this.items = items;
        lastInstance = this;
    }

    public void setOnPoiClickListener(GoogleMap.OnPoiClickListener listener) {
        this.registeredPoiClickListener = listener;
        delegate.setOnPoiClickListener(listener);
    }

    public GoogleMap getDelegate() {
        return delegate;
    }

    public List<Object> getItems() {
        return items;
    }

    public Marker addMarker(MarkerOptions options) {
        Marker marker = delegate.addMarker(options);
        if (marker != null) items.add(marker);
        return marker;
    }

    public Marker addMarker(com.google.android.gms.maps.model.AdvancedMarkerOptions options) {
        Marker marker = delegate.addMarker(options);
        if (marker != null) items.add(marker);
        return marker;
    }

    public Polyline addPolyline(PolylineOptions options) {
        Polyline polyline = delegate.addPolyline(options);
        if (polyline != null) items.add(polyline);
        return polyline;
    }

    public Polygon addPolygon(PolygonOptions options) {
        Polygon polygon = delegate.addPolygon(options);
        if (polygon != null) items.add(polygon);
        return polygon;
    }

    public Circle addCircle(CircleOptions options) {
        Circle circle = delegate.addCircle(options);
        if (circle != null) items.add(circle);
        return circle;
    }

    public GroundOverlay addGroundOverlay(GroundOverlayOptions options) {
        GroundOverlay groundOverlay = delegate.addGroundOverlay(options);
        if (groundOverlay != null) items.add(groundOverlay);
        return groundOverlay;
    }

    public TileOverlay addTileOverlay(TileOverlayOptions options) {
        TileOverlay tileOverlay = delegate.addTileOverlay(options);
        if (tileOverlay != null) items.add(tileOverlay);
        return tileOverlay;
    }

    public void moveCamera(CameraUpdate update) {
        delegate.moveCamera(update);
    }

    public void animateCamera(CameraUpdate update) {
        delegate.animateCamera(update);
    }

    public void animateCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        delegate.animateCamera(update, callback);
    }

    public void animateCamera(CameraUpdate update, int durationMs, GoogleMap.CancelableCallback callback) {
        delegate.animateCamera(update, durationMs, callback);
    }

    public void setMinZoomPreference(float minZoomPreference) {
        delegate.setMinZoomPreference(minZoomPreference);
    }

    public void setMaxZoomPreference(float maxZoomPreference) {
        delegate.setMaxZoomPreference(maxZoomPreference);
    }

    public void setLatLngBoundsForCameraTarget(LatLngBounds bounds) {
        delegate.setLatLngBoundsForCameraTarget(bounds);
    }

    public void setOnMapClickListener(GoogleMap.OnMapClickListener listener) {
        this.registeredMapClickListener = listener;
        delegate.setOnMapClickListener(listener);
    }

    public void setOnMapLongClickListener(GoogleMap.OnMapLongClickListener listener) {
        delegate.setOnMapLongClickListener(listener);
    }

    public void setOnMarkerClickListener(GoogleMap.OnMarkerClickListener listener) {
        delegate.setOnMarkerClickListener(listener);
    }

    public void setOnInfoWindowClickListener(GoogleMap.OnInfoWindowClickListener listener) {
        delegate.setOnInfoWindowClickListener(listener);
    }

    public void setOnCameraIdleListener(GoogleMap.OnCameraIdleListener listener) {
        delegate.setOnCameraIdleListener(listener);
    }

    public void setOnCameraMoveListener(GoogleMap.OnCameraMoveListener listener) {
        delegate.setOnCameraMoveListener(listener);
    }

    public void setOnCameraMoveStartedListener(GoogleMap.OnCameraMoveStartedListener listener) {
        delegate.setOnCameraMoveStartedListener(listener);
    }

    public void setMapType(int type) {
        delegate.setMapType(type);
    }

    public int getMapType() {
        return delegate.getMapType();
    }

    public void setTrafficEnabled(boolean enabled) {
        delegate.setTrafficEnabled(enabled);
    }

    public boolean isTrafficEnabled() {
        return delegate.isTrafficEnabled();
    }

    public boolean setIndoorEnabled(boolean enabled) {
        return delegate.setIndoorEnabled(enabled);
    }

    public boolean isIndoorEnabled() {
        return delegate.isIndoorEnabled();
    }

    @SuppressWarnings("MissingPermission")
    public void setMyLocationEnabled(boolean enabled) {
        delegate.setMyLocationEnabled(enabled);
    }

    public boolean isMyLocationEnabled() {
        return delegate.isMyLocationEnabled();
    }

    public UiSettings getUiSettings() {
        return delegate.getUiSettings();
    }

    public Projection getProjection() {
        return delegate.getProjection();
    }

    public CameraPosition getCameraPosition() {
        return delegate.getCameraPosition();
    }

    public float getMaxZoomLevel() {
        return delegate.getMaxZoomLevel();
    }

    public float getMinZoomLevel() {
        return delegate.getMinZoomLevel();
    }

    /** Aggressively resets all GoogleMap camera locks, styles, padding, UI settings, and event listeners. */
    public static void resetMapToDefaults(GoogleMap map) {
        if (map == null) return;
        map.clear();
        map.resetMinMaxZoomPreference();
        map.setLatLngBoundsForCameraTarget(null);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMapStyle(null);
        map.setPadding(0, 0, 0, 0);
        map.setTrafficEnabled(false);
        map.setIndoorEnabled(false);
        map.setBuildingsEnabled(true);

        UiSettings ui = map.getUiSettings();
        if (ui != null) {
            ui.setAllGesturesEnabled(true);
            ui.setZoomControlsEnabled(true);
            ui.setCompassEnabled(true);
            ui.setMyLocationButtonEnabled(true);
            ui.setMapToolbarEnabled(true);
            ui.setIndoorLevelPickerEnabled(true);
            ui.setScrollGesturesEnabledDuringRotateOrZoom(true);
        }

        map.setOnMapClickListener(null);
        map.setOnMapLongClickListener(null);
        map.setOnCameraMoveListener(null);
        map.setOnCameraMoveStartedListener(null);
        map.setOnCameraMoveCanceledListener(null);
        map.setOnCameraIdleListener(null);
        map.setOnMarkerClickListener(null);
        map.setOnMarkerDragListener(null);
        map.setOnInfoWindowClickListener(null);
        map.setOnInfoWindowLongClickListener(null);
        map.setOnInfoWindowCloseListener(null);
        map.setInfoWindowAdapter(null);
        map.setOnPoiClickListener(null);
        map.setOnPolygonClickListener(null);
        map.setOnPolylineClickListener(null);
        map.setOnGroundOverlayClickListener(null);
        map.setOnCircleClickListener(null);
    }
}
