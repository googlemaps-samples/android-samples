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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.snippets.common.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import android.widget.FrameLayout;
import com.google.android.gms.maps.GoogleMapOptions;

public class MapActivity extends AppCompatActivity {

    public static final String EXTRA_SNIPPET_TITLE = "snippet_title";

    public MapView mapView;
    private String snippetTitle;
    private String groupTitle;
    public GoogleMap googleMap;
    private int currentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#E76F00")));
        }
        getWindow().setStatusBarColor(android.graphics.Color.parseColor("#C05D00"));
        android.view.View mainView = findViewById(R.id.map_container);
        if (mainView != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        // MapView will be dynamically initialized and added to map_view_holder in runSnippet()
        try {
            android.content.pm.ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
                    getPackageName(), android.content.pm.PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                String apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY");
                if (apiKey == null || apiKey.isEmpty() || apiKey.equals("DEFAULT_API_KEY") || apiKey.equals("YOUR_API_KEY") || !apiKey.startsWith("AIza")) {
                    Toast.makeText(this, "ERROR: Invalid Google Maps API Key configured in secrets.properties", Toast.LENGTH_LONG).show();
                    Log.e("MapActivity", "Invalid MAPS_API_KEY: '" + apiKey + "'");
                    finish();
                    return;
                }
            }
        } catch (Exception e) {
            Log.e("MapActivity", "Failed to verify API key metadata", e);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.snapshot_button)
                .setOnClickListener(
                        v -> {
                            if (googleMap != null) {
                                CameraPosition cam = googleMap.getCameraPosition();
                                LatLng target = cam.target;
                                double rawBearing = cam.bearing;
                                double bearing = (rawBearing % 360.0 + 360.0) % 360.0;

                                String codeSnippet = String.format(
                                        "CameraPosition.builder()\n    .target(new LatLng(%.6f, %.6f))\n    .zoom(%.1ff)\n    .tilt(%.1ff)\n    .bearing(%.1ff)\n    .build()",
                                        target.latitude,
                                        target.longitude,
                                        cam.zoom,
                                        cam.tilt,
                                        bearing);

                                android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                                if (cm != null) {
                                    cm.setPrimaryClip(android.content.ClipData.newPlainText("CameraPosition", codeSnippet));
                                }
                                Toast.makeText(MapActivity.this, "📷 Camera Pose copied to clipboard!\n" + codeSnippet, Toast.LENGTH_LONG).show();
                                Log.d("MapActivity", "Camera Pose copied:\n" + codeSnippet);
                            } else {
                                Toast.makeText(MapActivity.this, "Map not initialized yet", Toast.LENGTH_SHORT).show();
                            }
                        });

        findViewById(R.id.reset_view_button).setOnClickListener(v -> runSnippet());

        groupTitle = getIntent().getStringExtra("group_title");
        snippetTitle = getIntent().getStringExtra(EXTRA_SNIPPET_TITLE);
        final List<SnippetItemInfo> snippetList = new ArrayList<>();
        for (SnippetGroupInfo group : SnippetRegistry.getSnippetGroups()) {
            snippetList.addAll(group.getItems());
        }
        for (int i = 0; i < snippetList.size(); i++) {
            SnippetItemInfo item = snippetList.get(i);
            if (item.getTitle().equals(snippetTitle)
                    && (groupTitle == null || item.getGroupTitle().equals(groupTitle))) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1 && !snippetList.isEmpty()) {
            currentIndex = 0;
        }
        if (currentIndex >= 0 && currentIndex < snippetList.size()) {
            SnippetItemInfo item = snippetList.get(currentIndex);
            groupTitle = item.getGroupTitle();
            snippetTitle = item.getTitle();
        }

        findViewById(R.id.purpose_button)
                .setOnClickListener(
                        v -> {
                            String desc = "Demonstrates " + snippetTitle;
                            if (currentIndex >= 0 && currentIndex < snippetList.size()) {
                                SnippetItemInfo item = snippetList.get(currentIndex);
                                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                                    desc = item.getDescription();
                                }
                            }
                            new androidx.appcompat.app.AlertDialog.Builder(MapActivity.this)
                                    .setTitle("ℹ️ " + (snippetTitle != null ? snippetTitle : "Snippet Purpose"))
                                    .setMessage("🎯 Purpose & Point:\n" + desc + "\n\n👀 What you should see / do:\nInteract with the live map on your device screen to test this capability.")
                                    .setPositiveButton("Got It", null)
                                    .show();
                        });

        findViewById(R.id.previous_button)
                .setOnClickListener(
                        v -> {
                            if (snippetList.isEmpty()) return;
                            if (currentIndex > 0) {
                                currentIndex--;
                            } else {
                                currentIndex = snippetList.size() - 1;
                            }
                            SnippetItemInfo item = snippetList.get(currentIndex);
                            groupTitle = item.getGroupTitle();
                            snippetTitle = item.getTitle();
                            runSnippet();
                        });

        findViewById(R.id.next_button)
                .setOnClickListener(
                        v -> {
                            if (snippetList.isEmpty()) return;
                            if (currentIndex < snippetList.size() - 1) {
                                currentIndex++;
                            } else {
                                currentIndex = 0;
                            }
                            SnippetItemInfo item = snippetList.get(currentIndex);
                            groupTitle = item.getGroupTitle();
                            snippetTitle = item.getTitle();
                            runSnippet();
                        });

        runSnippet();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            android.content.Intent intent = new android.content.Intent(this, JavaSnippetsActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(@NonNull android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        groupTitle = intent.getStringExtra("group_title");
        snippetTitle = intent.getStringExtra(EXTRA_SNIPPET_TITLE);
        runSnippet();
    }

    private void recreateMapView(String mapId) {
        FrameLayout holder = findViewById(R.id.map_view_holder);
        if (holder == null) return;

        if (this.mapView != null) {
            this.mapView.onPause();
            this.mapView.onStop();
            this.mapView.onDestroy();
            holder.removeView(this.mapView);
            this.mapView = null;
            this.googleMap = null;
        }

        GoogleMapOptions options = new GoogleMapOptions();
        if (mapId != null && !mapId.isEmpty() && !mapId.equals("YOUR_MAP_ID")) {
            options.mapId(mapId);
            Log.d("MapActivity", "Recreating MapView with Map ID: " + mapId);
        } else {
            Log.d("MapActivity", "Recreating MapView with default options (No Map ID)");
        }

        this.mapView = new MapView(this, options);
        holder.addView(this.mapView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // Sync lifecycle
        this.mapView.onCreate(null);
        this.mapView.onStart();
        this.mapView.onResume();
    }

    protected void runSnippet() {
        if (snippetTitle != null) {
            String key = groupTitle != null ? groupTitle + " - " + snippetTitle : snippetTitle;
            SnippetItemInfo snippet = SnippetRegistry.snippets.get(key);
            if (snippet == null) {
                for (SnippetGroupInfo g : SnippetRegistry.getSnippetGroups()) {
                    for (SnippetItemInfo item : g.getItems()) {
                        if (item.getTitle().equals(snippetTitle)) {
                            snippet = item;
                            break;
                        }
                    }
                }
            }
            final SnippetItemInfo finalSnippet = snippet;
            if (finalSnippet != null) {
                final List<SnippetItemInfo> allSnippets = new ArrayList<>();
                for (SnippetGroupInfo g : SnippetRegistry.getSnippetGroups()) {
                    allSnippets.addAll(g.getItems());
                }
                for (int i = 0; i < allSnippets.size(); i++) {
                    if (allSnippets.get(i).getTitle().equals(finalSnippet.getTitle()) &&
                        (groupTitle == null || allSnippets.get(i).getGroupTitle().equals(finalSnippet.getGroupTitle()))) {
                        currentIndex = i;
                        break;
                    }
                }

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("☕ " + finalSnippet.getTitle());
                }
                android.view.View topBar = findViewById(androidx.appcompat.R.id.action_bar);
                if (topBar != null) {
                    topBar.setOnClickListener(v -> {
                        android.view.View pb = findViewById(R.id.purpose_button);
                        if (pb != null) pb.performClick();
                    });
                }
                
                // Aggressively recreate MapView
                recreateMapView(BuildConfig.MAP_ID);
                SnippetRegistry.clearTrackedItems();
                android.widget.LinearLayout controlsContainer = findViewById(R.id.custom_controls_container);
                if (controlsContainer != null) {
                    controlsContainer.removeAllViews();
                    controlsContainer.setVisibility(android.view.View.GONE);
                }

                this.mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap map) {
                        MapActivity.this.googleMap = map;
                        TrackedMap.resetMapToDefaults(map);
                        try {
                            finalSnippet.getAction().execute(MapActivity.this, map);
                            map.setOnMapLoadedCallback(() -> {
                                if (mapView != null) mapView.setContentDescription("MapLoaded");
                            });
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (mapView != null) mapView.setContentDescription("MapLoaded");
                            }, 2000);
                        } catch (Exception e) {
                            Toast.makeText(MapActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Toast.makeText(
                                MapActivity.this,
                                "Snippet not found: " + snippetTitle,
                                Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        if (mapView != null) mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mapView != null) mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }
}
