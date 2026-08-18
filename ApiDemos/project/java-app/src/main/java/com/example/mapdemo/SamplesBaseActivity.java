// Copyright 2025 Google LLC
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


import android.os.Bundle;
import android.view.View;

import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SamplesBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupEdgeToEdgeInsets();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setupEdgeToEdgeInsets();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setupEdgeToEdgeInsets();
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
        setupEdgeToEdgeInsets();
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

    /**
     * Applies insets to the container view to properly handle window insets.
     *
     * @param container the container view to apply insets to
     */
    protected static void applyInsets(View container) {
        // Handled automatically in SamplesBaseActivity
    }
}