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

package com.example.mapdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Xml;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.example.common_ui.R;
import com.example.common_ui.catalog.Complexity;
import com.example.common_ui.catalog.Framework;
import com.example.common_ui.catalog.Sample;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.button.MaterialButton;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

/**
 * Demonstrates feeding programmatic coordinates from a GPX track into the GoogleMap location layer
 * using a custom {@link LocationSource}.
 */
// [START maps_android_sample_location_source]
@Sample(
    id = "com.example.kotlindemos.LocationSourceDemoActivity",
    title = "Custom LocationSource",
    description = "Providing a custom mock LocationSource for simulated GPS navigation playback along a trail.",
    category = "Location & Sensors",
    complexity = Complexity.ADVANCED,
    tags = {"#location", "#locationsource", "#mock", "#simulation", "#gpx", "#navigation"},
    apiCalls = {
        "GoogleMap.setLocationSource(LocationSource)",
        "LocationSource.activate(OnLocationChangedListener)",
        "LocationSource.deactivate()",
        "GoogleMap.setMyLocationEnabled(Boolean)",
        "GoogleMap.addPolyline(PolylineOptions)",
        "CameraUpdateFactory.newLatLngBounds(LatLngBounds, Int)"
    },
    purpose = "Shows how to feed programmatic coordinates from a GPX track into the GoogleMap location layer using a custom LocationSource.",
    successCriteria = "The map bounds to the trail, draws a polyline, and the blue dot animates smoothly along the route.",
    failureIndicators = "Blue dot fails to move or location updates cause memory leaks.",
    framework = Framework.JAVA_VIEWS
)
public class LocationSourceDemoActivity extends SamplesBaseActivity implements OnMapReadyCallback {

    private GpxLocationSource mLocationSource;
    private LatLngBounds mTrackBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_source_demo);

        Toolbar toolbar = findViewById(R.id.top_bar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.location_source_demo_label);
        }

        List<LatLng> trackPoints = parseGpxTrack(getResources().openRawResource(R.raw.fowler_rattlesnake));
        mLocationSource = new GpxLocationSource(trackPoints, 60L);

        MaterialButton toggleButton = findViewById(R.id.btn_toggle_playback);
        if (toggleButton != null) {
            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isPlaying = mLocationSource.togglePlayback();
                    toggleButton.setText(isPlaying ? R.string.location_source_pause : R.string.location_source_play);
                }
            });
        }

        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        applyInsets(findViewById(R.id.map_container));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationSource != null) {
            mLocationSource.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationSource != null) {
            mLocationSource.onPause();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        List<LatLng> trackPoints = mLocationSource.getTrackPoints();
        if (trackPoints.isEmpty()) {
            return;
        }

        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (LatLng point : trackPoints) {
            boundsBuilder.include(point);
        }
        mTrackBounds = boundsBuilder.build();

        // 1. Draw route polyline with outer casing and core color
        map.addPolyline(
            new PolylineOptions()
                .addAll(trackPoints)
                .color(0xFF0D47A1)
                .width(16f)
                .jointType(JointType.ROUND)
                .startCap(new RoundCap())
                .endCap(new RoundCap())
        );
        map.addPolyline(
            new PolylineOptions()
                .addAll(trackPoints)
                .color(0xFF2196F3)
                .width(10f)
                .jointType(JointType.ROUND)
                .startCap(new RoundCap())
                .endCap(new RoundCap())
        );

        // 2. Center and bound camera
        int padding = (int) (getResources().getDisplayMetrics().density * 56);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mTrackBounds.getCenter(), 14.5f));
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(mTrackBounds, padding));
            }
        });

        MaterialButton recenterButton = findViewById(R.id.btn_recenter_bounds);
        if (recenterButton != null) {
            recenterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTrackBounds != null) {
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(mTrackBounds, padding));
                    }
                }
            });
        }

        // 3. Connect custom location source
        map.setLocationSource(mLocationSource);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
    }

    private List<LatLng> parseGpxTrack(InputStream inputStream) {
        List<LatLng> points = new ArrayList<>();
        try (InputStream stream = inputStream) {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "trkpt".equals(parser.getName())) {
                    String latStr = parser.getAttributeValue(null, "lat");
                    String lonStr = parser.getAttributeValue(null, "lon");
                    if (latStr != null && lonStr != null) {
                        try {
                            double lat = Double.parseDouble(latStr);
                            double lon = Double.parseDouble(lonStr);
                            points.add(new LatLng(lat, lon));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception ignored) {
        }
        return points;
    }

    private static class GpxLocationSource implements LocationSource {
        private final List<LatLng> trackPoints;
        private final long intervalMs;
        private OnLocationChangedListener listener;
        private boolean isRunning = true;
        private int currentIndex;
        private final Handler handler = new Handler(Looper.getMainLooper());

        private final Runnable stepRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning || listener == null || trackPoints.isEmpty()) {
                    return;
                }
                emitCurrentPoint();
                currentIndex = (currentIndex + 1) % trackPoints.size();
                handler.postDelayed(this, intervalMs);
            }
        };

        public GpxLocationSource(List<LatLng> trackPoints, long intervalMs) {
            this.trackPoints = trackPoints;
            this.intervalMs = intervalMs;
        }

        public List<LatLng> getTrackPoints() {
            return trackPoints;
        }

        @Override
        public void activate(OnLocationChangedListener listener) {
            this.listener = listener;
            if (isRunning) {
                emitCurrentPoint();
                handler.postDelayed(stepRunnable, intervalMs);
            }
        }

        @Override
        public void deactivate() {
            handler.removeCallbacks(stepRunnable);
            this.listener = null;
        }

        public boolean togglePlayback() {
            isRunning = !isRunning;
            if (isRunning) {
                handler.post(stepRunnable);
            } else {
                handler.removeCallbacks(stepRunnable);
            }
            return isRunning;
        }

        private void emitCurrentPoint() {
            if (listener == null || trackPoints.isEmpty()) {
                return;
            }
            LatLng p1 = trackPoints.get(currentIndex);
            LatLng p2 = trackPoints.get((currentIndex + 1) % trackPoints.size());

            Location loc1 = new Location("GpxTrackLocationSource");
            loc1.setLatitude(p1.latitude);
            loc1.setLongitude(p1.longitude);

            Location loc2 = new Location("GpxTrackLocationSource");
            loc2.setLatitude(p2.latitude);
            loc2.setLongitude(p2.longitude);

            float bearing = loc1.bearingTo(loc2);

            Location location = new Location("GpxTrackLocationSource");
            location.setLatitude(p1.latitude);
            location.setLongitude(p1.longitude);
            location.setAccuracy(6.0f);
            location.setBearing(bearing);
            location.setSpeed(4.5f);
            location.setTime(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }

            listener.onLocationChanged(location);
        }

        public void onPause() {
            handler.removeCallbacks(stepRunnable);
        }

        public void onResume() {
            if (isRunning && listener != null) {
                handler.post(stepRunnable);
            }
        }
    }
}
// [END maps_android_sample_location_source]
