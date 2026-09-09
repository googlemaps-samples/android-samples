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

import android.content.Intent;
import android.widget.FrameLayout;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.snippets.common.R;
import com.google.android.gms.maps.MapView;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class SnippetDiscoveryTest {

    @Test
    public void verifySnippetGroupsLoaded() {
        List<SnippetGroupInfo> groups = SnippetRegistry.getSnippetGroups();
        Assert.assertFalse("Snippet groups should not be empty", groups.isEmpty());
        for (SnippetGroupInfo group : groups) {
            Assert.assertFalse("Group title should not be blank", group.getTitle().trim().isEmpty());
            for (SnippetItemInfo item : group.getItems()) {
                Assert.assertFalse("Item title should not be blank", item.getTitle().trim().isEmpty());
                Assert.assertEquals("Item group title should match", group.getTitle(), item.getGroupTitle());
            }
        }
    }

    @Test
    public void verifyAllSnippetsLaunchWithoutCrash() throws Exception {
        Map<String, SnippetItemInfo> snippets = SnippetRegistry.snippets;
        Assert.assertFalse("Registry should contain snippets", snippets.isEmpty());

        for (Map.Entry<String, SnippetItemInfo> entry : snippets.entrySet()) {
            String key = entry.getKey();
            SnippetItemInfo snippet = entry.getValue();

            Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
            intent.putExtra(MapActivity.EXTRA_SNIPPET_TITLE, snippet.getTitle());
            intent.putExtra("group_title", snippet.getGroupTitle());

            final CountDownLatch latch = new CountDownLatch(1);
            final MapView[] mapViewHolder = new MapView[1];

            try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(intent)) {
                scenario.onActivity(activity -> {
                    FrameLayout holder = activity.findViewById(R.id.map_view_holder);
                    mapViewHolder[0] = activity.mapView != null ? activity.mapView : (MapView) (holder != null && holder.getChildCount() > 0 ? holder.getChildAt(0) : null);
                    latch.countDown();
                });

                boolean loaded = latch.await(5, TimeUnit.SECONDS);
                Assert.assertTrue("Timed out waiting for activity to launch for snippet: " + key, loaded);
                Assert.assertNotNull("MapView should not be null for snippet: " + key, mapViewHolder[0]);
            }
        }
    }
}
