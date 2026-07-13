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

package com.example.snippets.java.capabilities;

import android.content.Intent;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.snippets.common.R;
import com.example.snippets.java.MapActivity;
import com.example.snippets.java.TrackedMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Falsifiable Java capability tests for Map Initialization (`232ecd00`, `20793ebb`, `25bf9dfd`, `c511ea57`).
 * Proves that breaking or mutating the Java sample code strictly causes these tests to fail.
 */
@RunWith(AndroidJUnit4.class)
public class MapInitSnippetsTest {

    private interface MapVerification {
        void verify(@NonNull GoogleMap googleMap);
    }

    private Marker getFirstMarker() {
        if (TrackedMap.lastInstance == null || TrackedMap.lastInstance.getItems() == null) return null;
        for (Object item : TrackedMap.lastInstance.getItems()) {
            if (item instanceof Marker) {
                return (Marker) item;
            }
        }
        return null;
    }

    private void runWithMap(String groupTitle, String snippetTitle, MapVerification verification) throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        intent.putExtra("group_title", groupTitle);
        intent.putExtra(MapActivity.EXTRA_SNIPPET_TITLE, snippetTitle);

        CountDownLatch latch = new CountDownLatch(1);
        final GoogleMap[] googleMapHolder = new GoogleMap[1];

        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                FrameLayout holder = activity.findViewById(R.id.map_view_holder);
                MapView mapView = activity.mapView != null ? activity.mapView : (MapView) (holder != null && holder.getChildCount() > 0 ? holder.getChildAt(0) : null);
                if (mapView != null) {
                    mapView.getMapAsync(map -> {
                        googleMapHolder[0] = map;
                        latch.countDown();
                    });
                }
            });

            boolean loaded = latch.await(10, TimeUnit.SECONDS);
            Assert.assertTrue("Timed out waiting for Java GoogleMap initialization for: " + snippetTitle, loaded);
            Assert.assertNotNull("GoogleMap must not be null for snippet: " + snippetTitle, googleMapHolder[0]);

            // Allow map layout and camera animations to settle while UI main thread is unblocked
            Thread.sleep(1000);

            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                verification.verify(googleMapHolder[0]);
            });
        }
    }

    @Test
    public void verifyBasicMapActivity_falsifiable() throws InterruptedException {
        // Capability 232ecd00: Add a customizable, interactive map to a web page or mobile app
        runWithMap("Map Initialization", "1. Basic Map Activity", map -> {
            LatLng sydney = new LatLng(-34.0, 151.0);
            Marker marker = getFirstMarker();
            Assert.assertNotNull("Basic Map Activity snippet must add Marker in Sydney", marker);
            Assert.assertEquals("Marker title must be exactly 'Marker in Sydney'", "Marker in Sydney", marker.getTitle());
            Assert.assertEquals("Marker latitude must exactly match Sydney (-34.0)", sydney.latitude, marker.getPosition().latitude, 0.001);
            Assert.assertEquals("Marker longitude must exactly match Sydney (151.0)", sydney.longitude, marker.getPosition().longitude, 0.001);
        });
    }

    @Test
    public void verifyEnableTrafficLayer_falsifiable() throws InterruptedException {
        // Capability 20793ebb: Add a traffic layer to a map
        runWithMap("Map Initialization", "11. Enable Traffic Layer", map -> {
            // Falsifiability Check: If snippet code is removed or changed to isTrafficEnabled = false, this test STRICTLY FAILS!
            Assert.assertTrue("googleMap.isTrafficEnabled() MUST be true after Java snippet execution", map.isTrafficEnabled());
        });
    }

    @Test
    public void verifySetMapTypeToHybrid_falsifiable() throws InterruptedException {
        // Capability c511ea57: Change the map type
        runWithMap("Map Initialization", "3. Set Map Type", map -> {
            // Falsifiability Check: If changed to MAP_TYPE_NORMAL or commented out, this test STRICTLY FAILS!
            Assert.assertEquals("GoogleMap type must be exactly MAP_TYPE_HYBRID (4)", GoogleMap.MAP_TYPE_HYBRID, map.getMapType());
        });
    }
}
