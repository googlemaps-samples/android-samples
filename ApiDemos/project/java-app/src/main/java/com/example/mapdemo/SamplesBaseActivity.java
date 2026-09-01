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
        resolveSampleMetadata();
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
            topBar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
            if (currentSampleMetadata != null) {
                topBar.setSubtitle(currentSampleMetadata.getComplexity().getBadge() + " " + currentSampleMetadata.getCategory());
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 2001 && currentSampleMetadata != null) {
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
                Insets statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.displayCutout());
                view.setPadding(statusBar.left, statusBar.top, statusBar.right, 0);
                view.getLayoutParams().height = baseHeight + statusBar.top;
                view.requestLayout();
                return insets;
            });
        }

        View mapContainer = root.findViewById(com.example.common_ui.R.id.map_container);
        View bottomTarget = mapContainer != null ? mapContainer : root;
        ViewCompat.setOnApplyWindowInsetsListener(bottomTarget, (view, insets) -> {
            Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars() | WindowInsetsCompat.Type.displayCutout());
            int topInsets = (topBar == null) ? insets.getInsets(WindowInsetsCompat.Type.statusBars()).top : 0;
            view.setPadding(navBars.left, topInsets, navBars.right, navBars.bottom);
            return insets;
        });
    }

    protected static void applyInsets(View container) {
        // Handled automatically in SamplesBaseActivity
    }
}
