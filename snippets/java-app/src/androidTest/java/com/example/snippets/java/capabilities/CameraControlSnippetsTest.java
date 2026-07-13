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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Falsifiable Java capability tests for Camera Controls (`2a3e0c25`, `0e6b228f`).
 * Proves that breaking or removing zoom constraints or panning restrictions strictly causes these tests to fail.
 */
@RunWith(AndroidJUnit4.class)
public class CameraControlSnippetsTest {

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

            verification.verify(googleMapHolder[0]);
        }
    }

    @Test
    public void verifyCameraMovementsAndZoomConstraints_falsifiable() throws InterruptedException {
        // Capability 2a3e0c25: Zoom Level Constraints
        runWithMap("Camera", "1. Zoom Level Constraints", map -> {
            final float[] clampedZoomHolder = new float[2]; // [0] = min, [1] = max

            // Run camera mutations and reads strictly on UI main thread using runOnMainSync
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                // Attempt to zoom out below minZoom (6.0f)
                map.moveCamera(CameraUpdateFactory.zoomTo(2.0f));
                clampedZoomHolder[0] = map.getCameraPosition().zoom;

                // Attempt to zoom in above maxZoom (14.0f)
                map.moveCamera(CameraUpdateFactory.zoomTo(21.0f));
                clampedZoomHolder[1] = map.getCameraPosition().zoom;
            });

            float clampedMinZoom = clampedZoomHolder[0];
            float clampedMaxZoom = clampedZoomHolder[1];

            Assert.assertTrue(
                "Zoom level (" + clampedMinZoom + ") must strictly clamp to >= 6.0f when minZoomPreference is enabled! " +
                "If setMinZoomPreference(6.0f) is removed or broken in the snippet, this test fails.",
                clampedMinZoom >= 5.9f
            );
            Assert.assertTrue(
                "Zoom level (" + clampedMaxZoom + ") must strictly clamp to <= 14.0f when maxZoomPreference is enabled! " +
                "If setMaxZoomPreference(14.0f) is removed or broken in the snippet, this test fails.",
                clampedMaxZoom <= 14.1f
            );
        });
    }

    @Test
    public void verifyCameraClampingToAustralia_falsifiable() throws InterruptedException {
        // Capability 0e6b228f: Panning Restrictions (Constrain target to geographic bounds)
        runWithMap("Camera", "4. Panning Restrictions", map -> {
            final LatLng[] targetHolder = new LatLng[1];

            // Run camera mutations strictly on UI main thread using runOnMainSync
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                // Attempt to pan camera to London, UK (51.5074, -0.1278)
                LatLng london = new LatLng(51.5074, -0.1278);
                map.moveCamera(CameraUpdateFactory.newLatLng(london));
                targetHolder[0] = map.getCameraPosition().target;
            });

            LatLng currentTarget = targetHolder[0];
            Assert.assertNotNull("Camera target must not be null", currentTarget);
            // Adelaide bounds are LatLng(-35.0, 138.58) to LatLng(-34.9, 138.61).
            // When clamped, target latitude MUST NOT be near London (51.5), but clamped inside/near Adelaide (-35 to -34.9).
            Assert.assertTrue(
                "Camera target latitude (" + currentTarget.latitude + ") must be clamped near Adelaide bounds (-35.0 to -34.9) and NOT allowed to move to London (51.5)! " +
                "If setLatLngBoundsForCameraTarget() is removed from the snippet, this test fails.",
                currentTarget.latitude < -30.0
            );
            Assert.assertTrue(
                "Camera target longitude (" + currentTarget.longitude + ") must be clamped near Adelaide bounds (138.58 to 138.61) and NOT London (-0.12)! " +
                "If setLatLngBoundsForCameraTarget() is removed from the snippet, this test fails.",
                currentTarget.longitude > 130.0
            );
        });
    }
}
