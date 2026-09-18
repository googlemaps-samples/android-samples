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
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.snippets.common.R;
import com.example.snippets.java.MapActivity;
import com.example.snippets.java.TrackedMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PointOfInterest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Falsifiable Java capability tests for Map Events & Interactions (`b34458f3`).
 * Proves that breaking or removing click disabling or POI event registration strictly causes these tests to fail.
 */
@RunWith(AndroidJUnit4.class)
public class EventsSnippetsTest {

    private interface MapVerification {
        void verify(@NonNull GoogleMap googleMap, @NonNull ActivityScenario<MapActivity> scenario);
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

            verification.verify(googleMapHolder[0], scenario);
        }
    }

    @Test
    public void verifyMapClickListenerAndEvents_falsifiable() throws InterruptedException {
        // Capability b34458f3 Check 1: MapView Disable Click Event (maps_android_events_disable_clicks_mapview)
        runWithMap("Events", "1. MapView Disable Click Event", (map, scenario) -> {
            final boolean[] isClickableHolder = new boolean[] { true };
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                scenario.onActivity(activity -> {
                    FrameLayout holder = activity.findViewById(R.id.map_view_holder);
                    MapView mapView = activity.mapView != null ? activity.mapView : (MapView) (holder != null && holder.getChildCount() > 0 ? holder.getChildAt(0) : null);
                    isClickableHolder[0] = mapView != null ? mapView.isClickable() : true;
                });
            });
            Assert.assertFalse(
                "MapView.isClickable must be strictly false when '1. MapView Disable Click Event' is executed! " +
                "If mapView.setClickable(false) is removed or broken in EventsSnippets.java, this test fails.",
                isClickableHolder[0]
            );
        });

        // Capability b34458f3 Check 2: POI Click Listener (maps_android_on_poi_click_demo) & Event Simulation (Pillar 3)
        runWithMap("Events", "4. POI Click Listener", (map, scenario) -> {
            final GoogleMap.OnPoiClickListener[] listenerHolder = new GoogleMap.OnPoiClickListener[1];
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                listenerHolder[0] = TrackedMap.lastInstance != null ? TrackedMap.lastInstance.registeredPoiClickListener : null;
                if (listenerHolder[0] != null) {
                    listenerHolder[0].onPoiClick(new PointOfInterest(new LatLng(-33.88, 151.21), "ChIJN1t_tDeuEmsRUsoyG83frY4", "Sydney Opera House"));
                }
            });

            Assert.assertNotNull(
                "OnPoiClickListener must be registered in TrackedMap when '4. POI Click Listener' is run! " +
                "If map.setOnPoiClickListener is removed or broken in EventsSnippets.java, this test fails.",
                listenerHolder[0]
            );
        });
    }
}
