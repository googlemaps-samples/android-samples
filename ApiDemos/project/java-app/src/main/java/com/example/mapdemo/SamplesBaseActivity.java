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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.common_ui.catalog.Framework;
import com.example.common_ui.catalog.SampleCatalogRegistry;
import com.example.common_ui.catalog.SampleItem;
import com.example.common_ui.catalog.ui.SampleExpectationsBottomSheet;
import com.example.common_ui.catalog.ui.UnifiedCatalogActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class SamplesBaseActivity extends AppCompatActivity {

    protected SampleItem currentSampleMetadata;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        applyImmersiveStickyMode();
        resolveSampleMetadata();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyImmersiveStickyMode();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            applyImmersiveStickyMode();
        }
    }

    private void applyImmersiveStickyMode() {
        androidx.core.view.WindowInsetsControllerCompat insetsController =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        insetsController.setSystemBarsBehavior(
                androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        insetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupEdgeToEdgeInsets();
        setupSampleToolbar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setupEdgeToEdgeInsets();
        setupSampleToolbar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setupEdgeToEdgeInsets();
        setupSampleToolbar();
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
        setupEdgeToEdgeInsets();
        setupSampleToolbar();
    }

    private void resolveSampleMetadata() {
        String sampleId = getIntent().getStringExtra(UnifiedCatalogActivity.EXTRA_SAMPLE_ID);
        if (sampleId != null && !sampleId.isEmpty()) {
            for (SampleItem s : SampleCatalogRegistry.INSTANCE.getSAMPLES()) {
                if (s.getId().equals(sampleId)) {
                    currentSampleMetadata = s;
                    break;
                }
            }
        } else {
            String myClass = getClass().getName();
            for (SampleItem s : SampleCatalogRegistry.INSTANCE.getSAMPLES()) {
                if (myClass.equals(s.getJavaActivity()) || myClass.equals(s.getKotlinActivity())) {
                    currentSampleMetadata = s;
                    break;
                }
            }
        }
    }

    private void setupSampleToolbar() {
        View root = findViewById(android.R.id.content);
        if (root == null) return;
        MaterialToolbar topBar = root.findViewById(com.example.common_ui.R.id.top_bar);
        if (topBar != null) {
            setSupportActionBar(topBar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
            }
            topBar.setNavigationOnClickListener(v -> navigateBackToCatalog());
            if (currentSampleMetadata != null) {
                topBar.setTitle(currentSampleMetadata.getTitle());
                topBar.setSubtitle(null); // Clean single-line title for maximum space and clean presentation
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (currentSampleMetadata != null) {
            // 1. Criteria & Purpose
            menu.add(0, 2001, 0, "Criteria & Purpose")
                .setIcon(com.example.common_ui.R.drawable.ic_info_outline)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            // 2. Good Job (Pass)
            menu.add(0, 2003, 1, "Good Job (Pass)")
                .setIcon(com.example.common_ui.R.drawable.ic_thumb_up)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            // 3. Something's Wrong (Needs Work)
            menu.add(0, 2004, 2, "Something's Wrong")
                .setIcon(com.example.common_ui.R.drawable.ic_warning_bug)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            // 4. Switch to Kotlin
            if (currentSampleMetadata.getKotlinActivity() != null) {
                menu.add(0, 2002, 3, "Switch to Kotlin")
                    .setIcon(com.example.common_ui.R.drawable.ic_swap_framework)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
        }
        return true;
    }

    private void navigateBackToCatalog() {
        if (isTaskRoot()) {
            try {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "com.example.common_ui.catalog.compose.ReviewerActivity");
                startActivity(intent);
            } catch (Exception e) {
                // Fallback to finish
            }
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateBackToCatalog();
            return true;
        } else if (item.getItemId() == 2001 && currentSampleMetadata != null) {
            SampleExpectationsBottomSheet sheet = SampleExpectationsBottomSheet.Companion.newInstance(
                currentSampleMetadata,
                Framework.JAVA_VIEWS,
                (sampleItem, framework) -> {
                    String targetClass = sampleItem.getActivityForFramework(framework);
                    if (targetClass != null && !targetClass.equals(getClass().getName())) {
                        finish();
                        Intent intent = new Intent();
                        intent.setClassName(getPackageName(), targetClass);
                        startActivity(intent);
                    }
                    return kotlin.Unit.INSTANCE;
                }
            );
            sheet.show(getSupportFragmentManager(), "SampleExpectationsBottomSheet");
            return true;
        } else if (item.getItemId() == 2003 && currentSampleMetadata != null) {
            com.example.common_ui.catalog.ui.ReviewEvaluationDialog.show(
                this,
                currentSampleMetadata,
                Framework.JAVA_VIEWS,
                com.example.common_ui.catalog.ReviewStatus.PASSING
            );
            return true;
        } else if (item.getItemId() == 2004 && currentSampleMetadata != null) {
            com.example.common_ui.catalog.ui.ReviewEvaluationDialog.show(
                this,
                currentSampleMetadata,
                Framework.JAVA_VIEWS,
                com.example.common_ui.catalog.ReviewStatus.NEEDS_WORK
            );
            return true;
        } else if (item.getItemId() == 2002 && currentSampleMetadata != null && currentSampleMetadata.getKotlinActivity() != null) {
            finish();
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), currentSampleMetadata.getKotlinActivity());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupEdgeToEdgeInsets() {
        View root = findViewById(android.R.id.content);
        if (root == null) return;
        View topBar = root.findViewById(com.example.common_ui.R.id.top_bar);
        if (topBar != null) {
            android.util.TypedValue typedValue = new android.util.TypedValue();
            int baseHeight;
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
                baseHeight = android.util.TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
            } else {
                baseHeight = (int) (56 * getResources().getDisplayMetrics().density);
            }
            ViewCompat.setOnApplyWindowInsetsListener(topBar, (view, insets) -> {
                Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
                view.setPadding(cutout.left, cutout.top, cutout.right, 0);
                view.getLayoutParams().height = baseHeight + cutout.top;
                view.requestLayout();
                return insets;
            });
        }

        View mapContainer = root.findViewById(com.example.common_ui.R.id.map_container);
        View bottomTarget = mapContainer != null ? mapContainer : root;
        ViewCompat.setOnApplyWindowInsetsListener(bottomTarget, (view, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            view.setPadding(cutout.left, 0, cutout.right, cutout.bottom);
            return insets;
        });
    }

    protected static void applyInsets(View container) {
        // Handled automatically in SamplesBaseActivity
    }
}
