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

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class SamplesBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupEdgeToEdgeInsets()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setupEdgeToEdgeInsets()
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        setupEdgeToEdgeInsets()
    }

    override fun addContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.addContentView(view, params)
        setupEdgeToEdgeInsets()
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
        /**
         * Applies insets to the container view to properly handle window insets.
         *
         * @param container the container view to apply insets to
         */
        fun applyInsets(container: View? = null) {
            // Handled automatically in SamplesBaseActivity
        }
    }
}