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

package com.example.common_ui.catalog.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common_ui.R
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.SampleCatalogRegistry
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.db.SampleEvaluationEntity
import com.example.common_ui.catalog.repository.GrievanceReportExporter
import com.example.common_ui.catalog.repository.SampleReviewRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

open class UnifiedCatalogActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var repository: SampleReviewRepository
    private lateinit var adapter: SampleCardAdapter

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tabFramework: TabLayout
    private lateinit var searchView: SearchView
    private lateinit var chipGroupComplexity: ChipGroup
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var recyclerSamples: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var fabGrievances: ExtendedFloatingActionButton

    private var currentFramework: Framework = Framework.KOTLIN_VIEWS
    private var selectedComplexity: Complexity? = null
    private val selectedTags: MutableSet<String> = mutableSetOf()
    private var searchQuery: String = ""
    private var isReviewerMode: Boolean = true

    companion object {
        private const val PREFS_NAME = "gmp_catalog_prefs"
        private const val KEY_FRAMEWORK = "selected_framework"
        private const val KEY_REVIEWER_MODE = "reviewer_mode"
        const val EXTRA_SAMPLE_ID = "extra_sample_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unified_catalog)

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        repository = SampleReviewRepository.getInstance(this)

        currentFramework = Framework.fromId(prefs.getString(KEY_FRAMEWORK, Framework.KOTLIN_VIEWS.id))
        isReviewerMode = prefs.getBoolean(KEY_REVIEWER_MODE, true)

        initViews()
        setupEdgeToEdge()
        setupFrameworkTabs()
        setupFilters()
        setupRecyclerView()
        setupGrievancesFab()
        observeEvaluations()
        applyFilters()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.catalog_toolbar)
        tabFramework = findViewById(R.id.tab_framework)
        searchView = findViewById(R.id.search_view)
        chipGroupComplexity = findViewById(R.id.chip_group_complexity)
        chipGroupTags = findViewById(R.id.chip_group_tags)
        recyclerSamples = findViewById(R.id.recycler_samples)
        textEmpty = findViewById(R.id.text_empty)
        fabGrievances = findViewById(R.id.fab_grievances)

        setSupportActionBar(toolbar)
    }

    private fun setupEdgeToEdge() {
        val root = findViewById<View>(R.id.catalog_root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
    }

    private fun setupFrameworkTabs() {
        when (currentFramework) {
            Framework.KOTLIN_VIEWS -> tabFramework.getTabAt(0)?.select()
            Framework.JAVA_VIEWS -> tabFramework.getTabAt(1)?.select()
            Framework.COMPOSE -> tabFramework.getTabAt(2)?.select()
        }

        tabFramework.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentFramework = when (tab?.position) {
                    1 -> Framework.JAVA_VIEWS
                    2 -> Framework.COMPOSE
                    else -> Framework.KOTLIN_VIEWS
                }
                prefs.edit().putString(KEY_FRAMEWORK, currentFramework.id).apply()
                adapter.currentFramework = currentFramework
                applyFilters()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupFilters() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query.orEmpty()
                applyFilters()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText.orEmpty()
                applyFilters()
                return true
            }
        })

        chipGroupComplexity.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedComplexity = when (checkedIds.firstOrNull()) {
                R.id.chip_complexity_snippet -> Complexity.SNIPPET
                R.id.chip_complexity_simple -> Complexity.SIMPLE
                R.id.chip_complexity_advanced -> Complexity.ADVANCED
                else -> null
            }
            applyFilters()
        }

        // Build Hashtag Filter Chips dynamically
        val allTags = SampleCatalogRegistry.getAllTags()
        chipGroupTags.removeAllViews()
        allTags.forEach { tag ->
            val chip = Chip(this).apply {
                text = tag
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedTags.add(tag)
                    } else {
                        selectedTags.remove(tag)
                    }
                    applyFilters()
                }
            }
            chipGroupTags.addView(chip)
        }
    }

    private fun setupRecyclerView() {
        adapter = SampleCardAdapter(
            onSampleClick = { sample -> launchSample(sample, currentFramework) },
            onInfoClick = { sample -> showExpectationsSheet(sample) },
            onStatusClick = { sample, _ -> showExpectationsSheet(sample) }
        ).apply {
            this.currentFramework = this@UnifiedCatalogActivity.currentFramework
            this.isReviewerMode = this@UnifiedCatalogActivity.isReviewerMode
        }

        recyclerSamples.layoutManager = LinearLayoutManager(this)
        recyclerSamples.adapter = adapter
    }

    private fun setupGrievancesFab() {
        fabGrievances.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val file = repository.exportAiringOfGrievances(this@UnifiedCatalogActivity)
                    val snackbar = Snackbar.make(
                        recyclerSamples,
                        "📢 Airing of Grievances saved to: ${file.name}",
                        Snackbar.LENGTH_LONG
                    ).setAction("Share") {
                        val shareIntent = GrievanceReportExporter.createShareIntent(this@UnifiedCatalogActivity, file)
                        startActivity(Intent.createChooser(shareIntent, "Share Airing of Grievances"))
                    }
                    snackbar.show()
                } catch (e: Exception) {
                    Toast.makeText(this@UnifiedCatalogActivity, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeEvaluations() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.allEvaluationsFlow.collect { evaluations ->
                    adapter.updateEvaluations(evaluations)
                }
            }
        }
    }

    private fun applyFilters() {
        val filtered = SampleCatalogRegistry.filter(
            framework = currentFramework,
            complexity = selectedComplexity,
            selectedTags = selectedTags,
            searchQuery = searchQuery
        )
        adapter.submitList(filtered)
        textEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showExpectationsSheet(sample: SampleItem) {
        val sheet = SampleExpectationsBottomSheet.newInstance(
            sample = sample,
            framework = currentFramework,
            onLaunch = { s, fw -> launchSample(s, fw) }
        )
        sheet.show(supportFragmentManager, "SampleExpectationsBottomSheet")
    }

    private fun launchSample(sample: SampleItem, framework: Framework) {
        val className = sample.getActivityForFramework(framework)
        if (className.isNullOrBlank()) {
            Toast.makeText(this, "No ${framework.displayName} implementation available for ${sample.title}", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent().setClassName(packageName, className).apply {
                putExtra(EXTRA_SAMPLE_ID, sample.id)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not launch ${sample.title}: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1001, 0, if (isReviewerMode) "Hide Reviewer Mode" else "Show Reviewer Mode")
            ?.setIcon(R.drawable.ic_status_passing)
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu?.add(0, 1002, 1, "Export Grievances Report")
            ?.setIcon(R.drawable.ic_grievances)
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            1001 -> {
                isReviewerMode = !isReviewerMode
                prefs.edit().putBoolean(KEY_REVIEWER_MODE, isReviewerMode).apply()
                adapter.isReviewerMode = isReviewerMode
                invalidateOptionsMenu()
                Toast.makeText(this, if (isReviewerMode) "Reviewer Mode Enabled" else "Reviewer Mode Disabled", Toast.LENGTH_SHORT).show()
                true
            }
            1002 -> {
                fabGrievances.performClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
