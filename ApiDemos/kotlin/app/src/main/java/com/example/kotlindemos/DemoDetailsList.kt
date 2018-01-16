/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos

/**
 * A list of all the demos we have available.
 */
class DemoDetailsList {
    companion object {
        val DEMOS = listOf<DemoDetails>(
                DemoDetails(R.string.basic_demo_label, R.string.basic_demo_details,
                        BasicMapDemoActivity::class.java),
                DemoDetails(R.string.close_info_window_demo_label,
                        R.string.close_info_window_demo_details,
                        CloseInfoWindowDemoActivity::class.java),
                DemoDetails(R.string.circle_demo_label, R.string.circle_demo_details,
                        CircleDemoActivity::class.java),
                DemoDetails(
                        R.string.street_view_panorama_navigation_demo_label,
                        R.string.street_view_panorama_navigation_demo_details,
                        StreetViewPanoramaNavigationDemoActivity::class.java),
                DemoDetails(R.string.ui_settings_demo_label, R.string.ui_settings_demo_details,
                        UiSettingsDemoActivity::class.java)
        )
    }
}