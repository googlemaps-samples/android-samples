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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwnerKt;

import com.example.common_ui.catalog.Framework;
import com.example.common_ui.catalog.ReviewStatus;
import com.example.common_ui.catalog.SampleCatalogRegistry;
import com.example.common_ui.catalog.SampleItem;
import com.example.common_ui.catalog.repository.SampleReviewRepository;
import com.example.common_ui.catalog.ui.ReviewEvaluationDialog;
import com.example.common_ui.catalog.ui.SampleExpectationsBottomSheet;
import com.example.common_ui.catalog.ui.UnifiedCatalogActivity;
import com.google.android.material.appbar.MaterialToolbar;

import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Dispatchers;

public class SamplesBaseActivity extends AppCompatActivity {

    protected SampleItem currentSampleMetadata;
    protected SampleReviewRepository reviewRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        applyImmersiveStickyMode();
        reviewRepository = SampleReviewRepository.Companion.getInstance(this);
        resolveSampleMetadata();
        super.setContentView(com.example.common_ui.R.layout.activity_sample_base);
        setupEdgeToEdgeInsets();
        setupSampleToolbar();
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
        View inflated = getLayoutInflater().inflate(layoutResID, null);
        wrapAndSetContentView(inflated);
    }

    @Override
    public void setContentView(View view) {
        if (view == null) return;
        wrapAndSetContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (view == null) return;
        if (params != null) {
            view.setLayoutParams(params);
        }
        wrapAndSetContentView(view);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (view == null) return;
        ViewGroup container = findViewById(com.example.common_ui.R.id.sample_content_container);
        if (container != null) {
            if (params != null) {
                container.addView(view, params);
            } else {
                container.addView(view);
            }
        } else {
            super.addContentView(view, params);
        }
    }

    private void wrapAndSetContentView(View childView) {
        View existingTopBar = childView.findViewById(com.example.common_ui.R.id.top_bar);
        if (existingTopBar != null) {
            super.setContentView(childView);
        } else {
            View baseView = getLayoutInflater().inflate(com.example.common_ui.R.layout.activity_sample_base, null);
            ViewGroup container = baseView.findViewById(com.example.common_ui.R.id.sample_content_container);
            container.addView(childView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            super.setContentView(baseView);
        }
        setupEdgeToEdgeInsets();
        setupSampleToolbar();
    }

    private void resolveSampleMetadata() {
        String sampleId = getIntent().getStringExtra(UnifiedCatalogActivity.EXTRA_SAMPLE_ID);
        if (sampleId != null && !sampleId.trim().isEmpty()) {
            for (SampleItem item : SampleCatalogRegistry.INSTANCE.getSAMPLES()) {
                if (sampleId.equals(item.getId())) {
                    currentSampleMetadata = item;
                    return;
                }
            }
        } else {
            String myClass = getClass().getName();
            for (SampleItem item : SampleCatalogRegistry.INSTANCE.getSAMPLES()) {
                if (myClass.equals(item.getJavaActivity()) || myClass.equals(item.getKotlinActivity())) {
                    currentSampleMetadata = item;
                    return;
                }
            }
        }
    }

    private void setupSampleToolbar() {
        View root = findViewById(android.R.id.content);
        if (root == null) return;
        MaterialToolbar topBar = root.findViewById(com.example.common_ui.R.id.top_bar);
        if (topBar == null) return;

        setSupportActionBar(topBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            if (currentSampleMetadata != null) {
                getSupportActionBar().setTitle(currentSampleMetadata.getTitle());
                getSupportActionBar().setSubtitle(null);
            }
        }
        topBar.setNavigationOnClickListener(v -> navigateBackToCatalog());
        topBar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        invalidateOptionsMenu();
    }

    protected boolean isReviewerMode() {
        return getIntent().getBooleanExtra("extra_is_reviewer_mode", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (currentSampleMetadata != null) {
            if (isReviewerMode()) {
                menu.add(0, 2003, 0, "Good Job (Pass)")
                        .setIcon(com.example.common_ui.R.drawable.ic_thumb_up)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

                menu.add(0, 2004, 1, "Something's Wrong")
                        .setIcon(com.example.common_ui.R.drawable.ic_warning_bug)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

                menu.add(0, 2005, 2, "Next Unchecked")
                        .setIcon(com.example.common_ui.R.drawable.ic_skip_next)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

                menu.add(0, 2001, 3, "Criteria & Purpose")
                        .setIcon(com.example.common_ui.R.drawable.ic_info_outline)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

                menu.add(0, 2006, 4, "Previous Sample")
                        .setIcon(com.example.common_ui.R.drawable.ic_skip_previous)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

                menu.add(0, 2007, 5, "Reset to Unchecked")
                        .setIcon(com.example.common_ui.R.drawable.ic_undo)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            } else {
                menu.add(0, 2001, 0, "About & APIs")
                        .setIcon(com.example.common_ui.R.drawable.ic_info_outline)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }

            if (currentSampleMetadata.getKotlinActivity() != null) {
                menu.add(0, 2002, 6, "Switch to Kotlin")
                        .setIcon(com.example.common_ui.R.drawable.ic_swap_framework)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
        return true;
    }

    private void navigateBackToCatalog() {
        if (isTaskRoot()) {
            try {
                String targetActivity = isReviewerMode()
                        ? "com.example.common_ui.catalog.compose.ReviewerActivity"
                        : "com.example.common_ui.catalog.compose.CatalogActivity";
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), targetActivity);
                startActivity(intent);
            } catch (Exception ignored) {
            }
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateBackToCatalog();
            return true;
        } else if (item.getItemId() == 2001) {
            if (currentSampleMetadata != null) {
                SampleExpectationsBottomSheet sheet = SampleExpectationsBottomSheet.Companion.newInstance(
                        currentSampleMetadata,
                        Framework.JAVA_VIEWS,
                        isReviewerMode(),
                        (sampleItem, framework) -> {
                            String targetClass = sampleItem.getActivityForFramework(framework);
                            if (targetClass != null && !targetClass.equals(getClass().getName())) {
                                finish();
                                Intent intent = new Intent();
                                intent.setClassName(getPackageName(), targetClass);
                                intent.putExtra("extra_sample_id", sampleItem.getId());
                                intent.putExtra("extra_is_reviewer_mode", isReviewerMode());
                                startActivity(intent);
                            }
                            return kotlin.Unit.INSTANCE;
                        }
                );
                sheet.show(getSupportFragmentManager(), "SampleExpectationsBottomSheet");
            }
            return true;
        } else if (item.getItemId() == 2003) {
            if (currentSampleMetadata != null) {
                ReviewEvaluationDialog.show(
                        this,
                        currentSampleMetadata,
                        Framework.JAVA_VIEWS,
                        ReviewStatus.PASSING
                );
            }
            return true;
        } else if (item.getItemId() == 2004) {
            if (currentSampleMetadata != null) {
                ReviewEvaluationDialog.show(
                        this,
                        currentSampleMetadata,
                        Framework.JAVA_VIEWS,
                        ReviewStatus.NEEDS_WORK
                );
            }
            return true;
        } else if (item.getItemId() == 2005) {
            if (currentSampleMetadata != null) {
                reviewRepository.getNextUncheckedSampleAsync(currentSampleMetadata.getId(), Framework.JAVA_VIEWS, next -> {
                    if (next != null) {
                        SampleReviewRepository.Companion.launchSample(SamplesBaseActivity.this, next, Framework.JAVA_VIEWS);
                    } else {
                        Toast.makeText(SamplesBaseActivity.this, "🎉 All Java samples reviewed!", Toast.LENGTH_LONG).show();
                        navigateBackToCatalog();
                    }
                    return kotlin.Unit.INSTANCE;
                });
            }
            return true;
        } else if (item.getItemId() == 2006) {
            if (currentSampleMetadata != null) {
                reviewRepository.getPreviousSampleAsync(currentSampleMetadata.getId(), Framework.JAVA_VIEWS, prev -> {
                    if (prev != null) {
                        SampleReviewRepository.Companion.launchSample(SamplesBaseActivity.this, prev, Framework.JAVA_VIEWS);
                    }
                    return kotlin.Unit.INSTANCE;
                });
            }
            return true;
        } else if (item.getItemId() == 2007) {
            if (currentSampleMetadata != null) {
                String targetFqcn = currentSampleMetadata.getTargetFqcn(Framework.JAVA_VIEWS);
                reviewRepository.deleteEvaluation(targetFqcn, () -> {
                    Toast.makeText(this, "Reverted " + currentSampleMetadata.getTitle() + " to Unchecked", Toast.LENGTH_SHORT).show();
                    return kotlin.Unit.INSTANCE;
                });
            }
            return true;
        } else if (item.getItemId() == 2002) {
            if (currentSampleMetadata != null && currentSampleMetadata.getKotlinActivity() != null) {
                finish();
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), currentSampleMetadata.getKotlinActivity());
                intent.putExtra("extra_sample_id", currentSampleMetadata.getId());
                intent.putExtra("extra_is_reviewer_mode", isReviewerMode());
                startActivity(intent);
            }
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
        if (mapContainer == null) {
            mapContainer = root.findViewById(com.example.common_ui.R.id.sample_content_container);
        }
        View bottomTarget = mapContainer != null ? mapContainer : root;
        ViewCompat.setOnApplyWindowInsetsListener(bottomTarget, (view, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            view.setPadding(cutout.left, 0, cutout.right, cutout.bottom);
            return insets;
        });
    }

    protected static void applyInsets(View container) {
        if (container == null) return;
        ViewCompat.setOnApplyWindowInsetsListener(container, (v, insets) -> {
            Insets navBars = insets.getInsets(
                    WindowInsetsCompat.Type.navigationBars() | WindowInsetsCompat.Type.displayCutout()
            );
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(
                navBars.left,
                statusBars.top,
                navBars.right,
                navBars.bottom
            );
            return insets;
        });
    }
}
