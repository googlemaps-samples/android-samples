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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleCatalogRegistry
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.repository.SampleReviewRepository
import com.example.common_ui.catalog.ui.ReviewEvaluationDialog
import com.example.common_ui.catalog.ui.SampleExpectationsBottomSheet
import com.example.common_ui.catalog.ui.UnifiedCatalogActivity
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

open class SamplesBaseActivity : AppCompatActivity() {

    protected var currentSampleMetadata: SampleItem? = null
    protected val reviewRepository: SampleReviewRepository by lazy {
        SampleReviewRepository.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applyImmersiveStickyMode()
        resolveSampleMetadata()
        super.setContentView(com.example.common_ui.R.layout.activity_sample_base)
        setupEdgeToEdgeInsets()
        setupSampleToolbar()
    }

    override fun onResume() {
        super.onResume()
        applyImmersiveStickyMode()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            applyImmersiveStickyMode()
        }
    }

    private fun applyImmersiveStickyMode() {
        val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun setContentView(layoutResID: Int) {
        val inflated = layoutInflater.inflate(layoutResID, null)
        wrapAndSetContentView(inflated)
    }

    override fun setContentView(view: View?) {
        if (view == null) return
        wrapAndSetContentView(view)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if (view == null) return
        if (params != null) {
            view.layoutParams = params
        }
        wrapAndSetContentView(view)
    }

    override fun addContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if (view == null) return
        val container = findViewById<ViewGroup>(com.example.common_ui.R.id.sample_content_container)
        if (container != null) {
            if (params != null) container.addView(view, params) else container.addView(view)
        } else {
            super.addContentView(view, params)
        }
    }

    private fun wrapAndSetContentView(childView: View) {
        val existingTopBar = childView.findViewById<View>(com.example.common_ui.R.id.top_bar)
        if (existingTopBar != null) {
            super.setContentView(childView)
        } else {
            val baseView = layoutInflater.inflate(com.example.common_ui.R.layout.activity_sample_base, null)
            val container = baseView.findViewById<ViewGroup>(com.example.common_ui.R.id.sample_content_container)
            container.addView(
                childView,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            )
            super.setContentView(baseView)
        }
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

        setSupportActionBar(topBar)
        val metadata = currentSampleMetadata
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            if (metadata != null) {
                title = metadata.title
                subtitle = null
            }
        }
        topBar.setNavigationOnClickListener {
            navigateBackToCatalog()
        }
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val metadata = currentSampleMetadata
        if (metadata != null) {
            // 1. Good Job (Pass) - ALWAYS
            menu.add(0, 2003, 0, "Good Job (Pass)")
                .setIcon(com.example.common_ui.R.drawable.ic_thumb_up)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            // 2. Something's Wrong - ALWAYS
            menu.add(0, 2004, 1, "Something's Wrong")
                .setIcon(com.example.common_ui.R.drawable.ic_warning_bug)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            // 3. Next Unchecked - ALWAYS
            menu.add(0, 2005, 2, "Next Unchecked")
                .setIcon(com.example.common_ui.R.drawable.ic_skip_next)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            // 4. Criteria & Purpose
            menu.add(0, 2001, 3, "Criteria & Purpose")
                .setIcon(com.example.common_ui.R.drawable.ic_info_outline)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

            // 5. Previous Sample
            menu.add(0, 2006, 4, "Previous Sample")
                .setIcon(com.example.common_ui.R.drawable.ic_skip_previous)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

            // 6. Reset to Unchecked (Undo)
            menu.add(0, 2007, 5, "Reset to Unchecked")
                .setIcon(com.example.common_ui.R.drawable.ic_undo)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

            // 7. Switch to Java
            val javaActivity = metadata.javaActivity
            if (javaActivity != null) {
                menu.add(0, 2002, 6, "Switch to Java")
                    .setIcon(com.example.common_ui.R.drawable.ic_swap_framework)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
            }
        }
        return true
    }

    private fun navigateBackToCatalog() {
        if (isTaskRoot) {
            try {
                val intent = Intent().setClassName(packageName, "com.example.common_ui.catalog.compose.ReviewerActivity")
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback
            }
        }
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val metadata = currentSampleMetadata
        return when (item.itemId) {
            android.R.id.home -> {
                navigateBackToCatalog()
                true
            }
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
                    ReviewEvaluationDialog.show(
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
                    ReviewEvaluationDialog.show(
                        this,
                        metadata,
                        Framework.KOTLIN_VIEWS,
                        ReviewStatus.NEEDS_WORK
                    )
                }
                true
            }
            2005 -> {
                // Advance to Next Unchecked Sample
                if (metadata != null) {
                    lifecycleScope.launch {
                        val next = reviewRepository.getNextUncheckedSample(metadata.id, Framework.KOTLIN_VIEWS)
                        if (next != null) {
                            SampleReviewRepository.launchSample(this@SamplesBaseActivity, next, Framework.KOTLIN_VIEWS)
                        } else {
                            Toast.makeText(this@SamplesBaseActivity, "🎉 All Kotlin samples reviewed!", Toast.LENGTH_LONG).show()
                            navigateBackToCatalog()
                        }
                    }
                }
                true
            }
            2006 -> {
                // Return to Previous Sample
                if (metadata != null) {
                    lifecycleScope.launch {
                        val prev = reviewRepository.getPreviousSample(metadata.id, Framework.KOTLIN_VIEWS)
                        if (prev != null) {
                            SampleReviewRepository.launchSample(this@SamplesBaseActivity, prev, Framework.KOTLIN_VIEWS)
                        }
                    }
                }
                true
            }
            2007 -> {
                // Reset to Unchecked
                if (metadata != null) {
                    val targetFqcn = metadata.getTargetFqcn(Framework.KOTLIN_VIEWS)
                    reviewRepository.deleteEvaluation(targetFqcn) {
                        Toast.makeText(this, "Reverted ${metadata.title} to Unchecked", Toast.LENGTH_SHORT).show()
                    }
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
                val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
                view.setPadding(
                    cutout.left,
                    cutout.top,
                    cutout.right,
                    0
                )
                view.layoutParams.height = baseHeight + cutout.top
                view.requestLayout()
                insets
            }
        }

        val mapContainer = root.findViewById<View>(com.example.common_ui.R.id.map_container)
            ?: root.findViewById<View>(com.example.common_ui.R.id.sample_content_container)
        val bottomTarget = mapContainer ?: root
        ViewCompat.setOnApplyWindowInsetsListener(bottomTarget) { view, insets ->
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            view.setPadding(
                cutout.left,
                0,
                cutout.right,
                cutout.bottom
            )
            insets
        }
    }

    companion object {
        fun applyInsets(view: View?) {
            if (view == null) return
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
