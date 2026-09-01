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

package com.example.kotlindemos

import android.os.Bundle
import android.widget.Toast
import com.example.common_ui.catalog.compose.ReviewerActivity

/**
 * The main entry activity of the Google Maps API demo gallery.
 *
 * Provides the unified Compose sample catalog with real-time tag search,
 * collapsible filters, quick grading buttons, and on-device manual Reviewer Mode
 * with Room persistence and Airing of Grievances export.
 */
class MainActivity : ReviewerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.MAPS_API_KEY.isEmpty()) {
            Toast.makeText(
                this,
                "Add your own API key in secrets.properties as MAPS_API_KEY=YOUR_API_KEY",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
