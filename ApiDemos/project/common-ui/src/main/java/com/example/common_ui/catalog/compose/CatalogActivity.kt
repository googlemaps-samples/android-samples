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

package com.example.common_ui.catalog.compose

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.SampleItem

/**
 * Clean, modern Jetpack Compose Catalog application for end-user developers.
 *
 * Provides multi-framework browsing, instant search, complexity filters, and sample expectation guides.
 */
open class CatalogActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CatalogTheme {
                CatalogScreen(
                    isReviewerMode = false,
                    onLaunchSample = { sample, framework ->
                        launchSample(sample, framework)
                    },
                    onSwitchMode = {
                        val intent = Intent().setClassName(packageName, "com.example.common_ui.catalog.compose.ReviewerActivity")
                        startActivity(intent)
                    }
                )
            }
        }
    }

    protected fun launchSample(sample: SampleItem, framework: Framework) {
        val className = sample.getActivityForFramework(framework)
        if (className.isNullOrBlank()) {
            Toast.makeText(this, "No ${framework.displayName} implementation available for ${sample.title}", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent().setClassName(packageName, className).apply {
                putExtra("extra_sample_id", sample.id)
                putExtra("extra_is_reviewer_mode", false)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not launch ${sample.title}: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
