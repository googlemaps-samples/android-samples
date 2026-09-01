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
package com.example.kotlindemos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleCatalogRegistry
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.ui.ReviewEvaluationDialog
import com.example.common_ui.catalog.ui.SampleExpectationsBottomSheet
import com.example.common_ui.catalog.ui.UnifiedCatalogActivity
import com.google.android.material.appbar.MaterialToolbar

open class SamplesBaseActivity : AppCompatActivity() {

    protected var currentSampleMetadata: SampleItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        resolveSampleMetadata()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupEdgeToEdgeInsets()
        setupSampleToolbar()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setupEdgeToEdgeInsets()
        setupSampleToolbar()
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        setupEdgeToEdgeInsets()
        setupSampleToolbar()
    }

    override fun addContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.addContentView(view, params)
        setupEdgeToEdgeInsets()
        setupSampleToolbar()
    }

    private fun resolveSampleMetadata() {
        val sampleId = intent.getStringExtra(UnifiedCatalogActivity.EXTRA_SAMPLE_ID)
        currentSampleMetadata = if (!sampleId.isNullOrBlank()) {
            SampleCatalogRegistry.SAMPLES.find { it.id == sampleId }
        } else {
            val myClass = this::class.java.name
            SampleCatalogRegistry.SAMPLES.find { it.kotlinActivity == myClass || it.javaActivity == myClass }
        }
    }

    private fun setupSampleToolbar() {
        val root = findViewById<View>(android.R.id.content) ?: return
        val topBar = root.findViewById<MaterialToolbar>(com.example.common_ui.R.id.top_bar) ?: return

        topBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val metadata = currentSampleMetadata
        if (metadata != null) {
            topBar.subtitle = "${metadata.complexity.badge} ${metadata.category}"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val metadata = currentSampleMetadata
        if (metadata != null) {
            // 1. Info & Criteria Button
            menu.add(0, 2001, 0, "Criteria & Purpose")
                .setIcon(com.example.common_ui.R.drawable.ic_info_outline)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            // 2. Good Job (Pass) Button
            menu.add(0, 2003, 1, "Good Job (Pass)")
                .setIcon(com.example.common_ui.R.drawable.ic_thumb_up)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            // 3. Something's Wrong (Needs Work) Button
            menu.add(0, 2004, 2, "Something's Wrong")
                .setIcon(com.example.common_ui.R.drawable.ic_warning_bug)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            // 4. Switch Framework Button
            val javaActivity = metadata.javaActivity
            if (javaActivity != null) {
                menu.add(0, 2002, 3, "Switch to Java")
                    .setIcon(com.example.common_ui.R.drawable.ic_swap_framework)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val metadata = currentSampleMetadata
        return when (item.itemId) {
            2001 -> {
                if (metadata != null) {
                    val sheet = SampleExpectationsBottomSheet.newInstance(
                        sample = metadata,
                        framework = Framework.KOTLIN_VIEWS,
                        onLaunch = { s, fw ->
                            val targetClass = s.getActivityForFramework(fw)
                            if (targetClass != null && targetClass != this::class.java.name) {
                                finish()
                                startActivity(Intent().setClassName(packageName, targetClass))
                            }
                        }
                    )
                    sheet.show(supportFragmentManager, "SampleExpectationsBottomSheet")
                }
                true
            }
            2003 -> {
                if (metadata != null) {
                    com.example.common_ui.catalog.ui.ReviewEvaluationDialog.show(
                        this,
                        metadata,
                        Framework.KOTLIN_VIEWS,
                        ReviewStatus.PASSING
                    )
                }
                true
            }
            2004 -> {
                if (metadata != null) {
                    com.example.common_ui.catalog.ui.ReviewEvaluationDialog.show(
                        this,
                        metadata,
                        Framework.KOTLIN_VIEWS,
                        ReviewStatus.NEEDS_WORK
                    )
                }
                true
            }
            2002 -> {
                val javaActivity = metadata?.javaActivity
                if (javaActivity != null) {
                    finish()
                    startActivity(Intent().setClassName(packageName, javaActivity))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupEdgeToEdgeInsets() {
        val root = findViewById<View>(android.R.id.content) ?: return
        val topBar = root.findViewById<View>(com.example.common_ui.R.id.top_bar)
        if (topBar != null) {
            val typedValue = android.util.TypedValue()
            val baseHeight = if (theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
                android.util.TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
            } else {
                (56 * resources.displayMetrics.density).toInt()
            }
            ViewCompat.setOnApplyWindowInsetsListener(topBar) { view, insets ->
                val statusBar = insets.getInsets(
                    WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
                )
                view.setPadding(
                    statusBar.left,
                    statusBar.top,
                    statusBar.right,
                    0
                )
                view.layoutParams.height = baseHeight + statusBar.top
                view.requestLayout()
                insets
            }
        }

        val mapContainer = root.findViewById<View>(com.example.common_ui.R.id.map_container)
        val bottomTarget = mapContainer ?: root
        ViewCompat.setOnApplyWindowInsetsListener(bottomTarget) { view, insets ->
            val navBars = insets.getInsets(
                WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val topInsets = if (topBar == null) {
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            } else {
                0
            }
            view.setPadding(
                navBars.left,
                topInsets,
                navBars.right,
                navBars.bottom
            )
            insets
        }
    }

    companion object {
        fun applyInsets(view: View) {
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val navBars = insets.getInsets(
                    WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.displayCutout()
                )
                val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                v.setPadding(
                    navBars.left,
                    statusBars.top,
                    navBars.right,
                    navBars.bottom
                )
                insets
            }
        }
    }
}
