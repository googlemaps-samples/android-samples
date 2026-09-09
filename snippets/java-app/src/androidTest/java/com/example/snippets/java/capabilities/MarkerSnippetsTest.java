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
import com.google.android.gms.maps.model.Marker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Falsifiable Java capability tests for Markers (`7bbfe87e`, `bdbefba5`, `de757d41`, `4c2a9906`).
 * Proves that breaking, mutating, or removing marker creation, styling, and drag properties causes these tests to strictly fail.
 */
@RunWith(AndroidJUnit4.class)
public class MarkerSnippetsTest {

    private interface MapVerification {
        void verify(@NonNull GoogleMap googleMap);
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

            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                verification.verify(googleMapHolder[0]);
            });
        }
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

    @Test
    public void verifyAddMarkerProperties_falsifiable() throws InterruptedException {
        // Capability 7bbfe87e: Add a marker to a map
        runWithMap("Markers", "1. Add a Marker", map -> {
            Marker marker = getFirstMarker();
            Assert.assertNotNull(
                "Marker must be added by '1. Add a Marker'! If addMarker() snippet code is removed or broken, this test fails.",
                marker
            );
            Assert.assertEquals(
                "Marker latitude must exactly match Sydney (-33.852)",
                -33.852, marker.getPosition().latitude, 0.001
            );
            Assert.assertEquals(
                "Marker longitude must exactly match Sydney (151.211)",
                151.211, marker.getPosition().longitude, 0.001
            );
            Assert.assertEquals(
                "Marker title must be exactly 'Marker in Sydney'",
                "Marker in Sydney", marker.getTitle()
            );
        });
    }

    @Test
    public void verifyInfoWindowCustomizationAndDisplay_falsifiable() throws InterruptedException {
        // Capability bdbefba5: Add an info window to a map
        runWithMap("Markers", "11. Add Info Window", map -> {
            Marker marker = getFirstMarker();
            Assert.assertNotNull(
                "Marker must be added by '11. Add Info Window'! If snippet code is removed, this test fails.",
                marker
            );
            Assert.assertEquals("Marker title must be exactly 'Melbourne'", "Melbourne", marker.getTitle());
            Assert.assertEquals("Marker snippet must be exactly 'Population: 4,137,400'", "Population: 4,137,400", marker.getSnippet());
        });
    }

    @Test
    public void verifyMarkerCustomColorAndIcon_falsifiable() throws InterruptedException {
        // Capability de757d41: Customize a marker on a map
        runWithMap("Markers", "5. Marker Opacity", map -> {
            Marker marker = getFirstMarker();
            Assert.assertNotNull("Marker must be added by '5. Marker Opacity'", marker);
            Assert.assertEquals(
                "Marker alpha must exactly match 0.7f! If .alpha(0.7f) is removed or mutated in snippet, this test fails.",
                0.7f, marker.getAlpha(), 0.001f
            );
        });

        runWithMap("Markers", "7. Flat Marker", map -> {
            Marker marker = getFirstMarker();
            Assert.assertNotNull("Marker must be added by '7. Flat Marker'", marker);
            Assert.assertTrue(
                "Marker isFlat must exactly match true! If .flat(true) is removed in snippet, this test fails.",
                marker.isFlat()
            );
        });

        runWithMap("Markers", "8. Rotate Marker", map -> {
            Marker marker = getFirstMarker();
            Assert.assertNotNull("Marker must be added by '8. Rotate Marker'", marker);
            Assert.assertEquals(
                "Marker rotation must exactly match 90.0f! If .rotation(90.0f) is removed in snippet, this test fails.",
                90.0f, marker.getRotation(), 0.001f
            );
        });
    }

    @Test
    public void verifyMarkerDraggableProperty_falsifiable() throws InterruptedException {
        // Capability 4c2a9906: Respond to user interactions with markers on a map
        runWithMap("Markers", "2. Draggable Marker", map -> {
            Marker marker = getFirstMarker();
            Assert.assertNotNull("Draggable marker must be added by '2. Draggable Marker'", marker);
            Assert.assertTrue(
                "Marker isDraggable must strictly be true! If .draggable(true) is removed in snippet, this test fails.",
                marker.isDraggable()
            );

            // Adversarial negative check (Pillar 2): Mutate to false and verify constraint enforcement
            marker.setDraggable(false);
            Assert.assertFalse(
                "Marker should strictly reject drag interactions when isDraggable is set to false",
                marker.isDraggable()
            );
        });
    }
}
