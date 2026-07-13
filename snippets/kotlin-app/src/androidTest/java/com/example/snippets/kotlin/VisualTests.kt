/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snippets.kotlin

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VisualTests : BaseVisualTest() {

    @Test
    fun verifyCameraSnippet() {
        launchAndVerifySnippet(
            groupTitle = "Camera",
            snippetTitle = "1. Zoom Level Constraints",
            screenshotFilename = "camera_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible focused on Sydney or Mountain View with zoom constraints applied.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifySettingBoundariesSnippet() {
        kotlinx.coroutines.runBlocking {
            val intent = android.content.Intent(context, MapActivity::class.java).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("group_title", "Camera")
                putExtra(MapActivity.EXTRA_SNIPPET_TITLE, "2. Fit Camera To Bounds (Australia)")
            }
            context.startActivity(intent)

            uiDevice.wait(androidx.test.uiautomator.Until.hasObject(androidx.test.uiautomator.By.pkg(context.packageName).depth(0)), 10000)
            waitForMapRendering(60)
            kotlinx.coroutines.delay(3000) // Settle map camera on Australia bounds

            val screenshotBitmap = captureScreenshot("setting_boundaries_screenshot.png")
            verifyScreenshotWithGemini(
                screenshotBitmap,
                """
                    Please act as a UI tester and analyze this screenshot to verify the application is rendering geographic camera boundaries.
                    Check the image against the following criteria:
                    1. Confirm that a Google Map is visible with the camera framed around the continent of Australia (rect bounds SW -44, 113 to NE -10, 154).
                    2. Confirm that the camera is NOT zoomed in tightly on a single city or artificially blocked by zoom constraints, but rather shows the entire Australian continent fitting cleanly within the map viewport.

                    If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }

    @Test
    fun verifyEventsSnippet() {
        launchAndVerifySnippet(
            groupTitle = "Events",
            snippetTitle = "1. MapView Disable Click Event",
            screenshotFilename = "events_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible demonstrating click event handling on MapView.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyMapInitSnippet() {
        launchAndVerifySnippet(
            groupTitle = "Map Initialization",
            snippetTitle = "1. Basic Map Activity",
            screenshotFilename = "mapinit_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible initialized with basic map configuration.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyMarkerSnippet() {
        launchAndVerifySnippet(
            groupTitle = "Markers",
            snippetTitle = "1. Add a Marker",
            screenshotFilename = "marker_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible showing a red marker pin displayed near the center.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyMyLocationSnippet() {
        launchAndVerifySnippet(
            groupTitle = "My Location Layer",
            snippetTitle = "1. Enable My Location Layer",
            screenshotFilename = "mylocation_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible with the blue My Location dot enabled.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyOverlaySnippet() {
        launchAndVerifySnippet(
            groupTitle = "Overlays",
            snippetTitle = "1. Ground Overlays",
            screenshotFilename = "overlay_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible with a historical map image overlay placed on top.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyShapesSnippet() {
        launchAndVerifySnippet(
            groupTitle = "Shapes",
            snippetTitle = "1. Simple Polyline",
            screenshotFilename = "shapes_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible with a polyline drawing a rectangular boundary across the map.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyGeoJsonSnippet() {
        launchAndVerifySnippet(
            groupTitle = "Utility Library",
            snippetTitle = "5. Add GeoJSON Layer from File",
            screenshotFilename = "geojson_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible with a gray shaded polygon with a red border covering the United States of America.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyHeatmapSnippet() {
        launchAndVerifySnippet(
            groupTitle = "Utility Library",
            snippetTitle = "9. Simple Heatmap",
            screenshotFilename = "heatmap_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible focused on Melbourne, Australia displaying colored thermal heatmap intensity dots.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyClusteringSnippet() {
        launchAndVerifySnippet(
            groupTitle = "Utility Library",
            snippetTitle = "1. Marker Clustering Setup",
            screenshotFilename = "clustering_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible focused on London showing numbered marker cluster circles.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyKmlSnippet() {
        launchAndVerifySnippet(
            groupTitle = "Utility Library",
            snippetTitle = "7. KML Layer from File Resource",
            screenshotFilename = "kml_screenshot.png",
            prompt = """
                Please act as a UI tester and analyze this screenshot to verify the application is rendering correctly.
                Check the image against the following criteria:
                1. Confirm that a map is visible focused on Google Campus in Mountain View displaying imported KML 3D building polygons.

                If all elements are present, reply with "PASSED".
            """.trimIndent()
        )
    }

    @Test
    fun verifyMarkerDraggableSnippet() {
        kotlinx.coroutines.runBlocking {
            val intent = android.content.Intent(context, MapActivity::class.java).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("group_title", "Markers")
                putExtra(MapActivity.EXTRA_SNIPPET_TITLE, "2. Draggable Marker")
            }
            context.startActivity(intent)

            uiDevice.wait(androidx.test.uiautomator.Until.hasObject(androidx.test.uiautomator.By.pkg(context.packageName).depth(0)), 10000)
            waitForMapRendering(60)
            kotlinx.coroutines.delay(4000) // Settle map camera on Perth

            // Drag marker pin from location A (center of screen) to location B (top right)
            val centerX = uiDevice.displayWidth / 2
            val centerY = uiDevice.displayHeight / 2
            uiDevice.drag(centerX, centerY, centerX + 300, centerY - 400, 40)
            kotlinx.coroutines.delay(3000) // Wait for drag completion

            val screenshotBitmap = captureScreenshot("marker_draggable_screenshot.png")
            verifyScreenshotWithGemini(
                screenshotBitmap,
                """
                    Please act as a UI tester and analyze this screenshot to verify a draggable map marker interaction.
                    Confirm that a map is visible showing a marker pin located away from the center (at location B after being dragged).
                    If the marker pin is visible, reply with "PASSED".
                """.trimIndent()
            )
        }
    }
}
