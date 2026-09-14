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

package com.example.kotlindemos.visual

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kotlindemos.CameraDemoActivity
import com.example.kotlindemos.CloudBasedMapStylingDemoActivity
import com.example.kotlindemos.DataDrivenBoundariesActivity
import com.example.kotlindemos.DataDrivenDatasetStylingActivity
import com.example.kotlindemos.GroundOverlayDemoActivity
import com.example.kotlindemos.MapColorSchemeActivity
import com.example.kotlindemos.MarkerDemoActivity
import com.example.kotlindemos.TileOverlayDemoActivity
import com.example.kotlindemos.VisibleRegionDemoActivity
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

/**
 * On-device visual verification test suite covering the 9 calibrated samples.
 *
 * Exercises the multi-state gestures calibrated during evaluation descent spelunk_2026y09m14d_13h59m56s
 * and asserts visual correctness against declared @Sample contracts using Gemini Multimodal AI.
 */
@RunWith(AndroidJUnit4::class)
class VerifiedSamplesVisualTest : BaseVisualVerificationTest() {

    @Test
    fun testCameraDemoVisuals() = runBlocking {
        ActivityScenario.launch(CameraDemoActivity::class.java).use {
            waitForMap()
            // Click Stop button to interrupt initial scroll
            uiDevice.click(115, 338)
            Thread.sleep(800)
            // Jump to Bondi
            uiDevice.click(810, 716)
            Thread.sleep(1500)
            // Jump to Sydney
            uiDevice.click(270, 716)
            Thread.sleep(1500)

            val bitmap = captureScreenshot("camera_demo_verified.png")
            verifyScreenshotWithGemini(
                bitmap,
                """
                Please act as a QA visual verifier. Analyze this screenshot of CameraDemoActivity.
                Verify that:
                1. A Google Map is rendered showing Sydney or Bondi.
                2. Camera control buttons (Bondi, Sydney, Stop) are visible and responsive.
                3. The catalog screen did not leak through or obscure the view.
                If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }

    @Test
    fun testVisibleRegionDemoVisuals() = runBlocking {
        ActivityScenario.launch(VisibleRegionDemoActivity::class.java).use {
            waitForMap()
            // Tap "Actions ▾" popup menu button
            uiDevice.click(539, 529)
            Thread.sleep(1000)
            // Tap first action (Sydney Opera House)
            uiDevice.click(539, 650)
            Thread.sleep(2000)

            val bitmap = captureScreenshot("visible_region_verified.png")
            verifyScreenshotWithGemini(
                bitmap,
                """
                Please act as a QA visual verifier. Analyze this screenshot of VisibleRegionDemoActivity.
                Verify that:
                1. The map is visible and framed on Sydney / Opera House bounds.
                2. The HUD telemetry card displays projection bounds and camera coordinates.
                3. The "Actions ▾" button is clearly positioned below the toolbar.
                If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }

    @Test
    fun testMarkerDemoVisuals() = runBlocking {
        ActivityScenario.launch(MarkerDemoActivity::class.java).use {
            waitForMap()
            // Toggle Flat checkbox
            uiDevice.click(304, 396)
            Thread.sleep(800)
            // Slide rotation seekbar
            uiDevice.swipe(350, 498, 900, 498, 15)
            Thread.sleep(800)
            // Click custom info contents button
            uiDevice.click(278, 2190)
            Thread.sleep(1200)

            val bitmap = captureScreenshot("marker_demo_verified.png")
            verifyScreenshotWithGemini(
                bitmap,
                """
                Please act as a QA visual verifier. Analyze this screenshot of MarkerDemoActivity.
                Verify that:
                1. Multiple markers are displayed on the map with custom rotation applied.
                2. An InfoWindow or custom info contents popup is visible above a marker.
                3. The controls at the top and bottom are visible without crashing or overlapping.
                If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }

    @Test
    fun testDataDrivenBoundariesVisuals() = runBlocking {
        ActivityScenario.launch(DataDrivenBoundariesActivity::class.java).use {
            waitForMap(4000)
            // Tap "US" button
            uiDevice.click(472, 391)
            Thread.sleep(3500)
            // Tap on US state polygon
            uiDevice.click(480, 1450)
            Thread.sleep(1500)

            val bitmap = captureScreenshot("data_driven_boundaries_verified.png")
            verifyScreenshotWithGemini(
                bitmap,
                """
                Please act as a QA visual verifier. Analyze this screenshot of DataDrivenBoundariesActivity.
                Verify that:
                1. The map is centered on the United States.
                2. State administrative boundaries are rendered with choropleth styling or outlines.
                3. A selected state polygon exhibits highlight styling.
                If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }

    @Test
    fun testDataDrivenDatasetStylingVisuals() = runBlocking {
        ActivityScenario.launch(DataDrivenDatasetStylingActivity::class.java).use {
            waitForMap(4000)
            // Tap "New York" button
            uiDevice.click(560, 391)
            Thread.sleep(3500)
            // Tap "Kyoto" button
            uiDevice.click(856, 391)
            Thread.sleep(3500)

            val bitmap = captureScreenshot("data_driven_datasets_verified.png")
            verifyScreenshotWithGemini(
                bitmap,
                """
                Please act as a QA visual verifier. Analyze this screenshot of DataDrivenDatasetStylingActivity.
                Verify that:
                1. The map is centered on Kyoto with custom dataset feature styling applied.
                2. Dataset polygons/features are visibly rendered over the base map.
                If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }

    @Test
    fun testCloudBasedMapStylingVisuals() = runBlocking {
        ActivityScenario.launch(CloudBasedMapStylingDemoActivity::class.java).use {
            waitForMap()
            // Tap Satellite button
            uiDevice.click(435, 2324)
            Thread.sleep(2500)
            // Tap Hybrid button
            uiDevice.click(716, 2324)
            Thread.sleep(2500)
            // Tap Terrain button
            uiDevice.click(967, 2324)
            Thread.sleep(2500)

            val bitmap = captureScreenshot("cloud_styling_verified.png")
            verifyScreenshotWithGemini(
                bitmap,
                """
                Please act as a QA visual verifier. Analyze this screenshot of CloudBasedMapStylingDemoActivity.
                Verify that:
                1. The map renders Cloud-based map styling with terrain contours or photographic layers.
                2. The styling mode buttons (Normal, Satellite, Hybrid, Terrain) are visible along the bottom.
                If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }

    @Test
    fun testMapColorSchemeVisuals() = runBlocking {
        ActivityScenario.launch(MapColorSchemeActivity::class.java).use {
            waitForMap()
            // Tap Light mode
            uiDevice.click(105, 338)
            Thread.sleep(1500)
            // Tap Dark mode
            uiDevice.click(294, 338)
            Thread.sleep(1500)
            // Tap Follow System
            uiDevice.click(578, 338)
            Thread.sleep(1500)

            val bitmap = captureScreenshot("map_color_scheme_verified.png")
            verifyScreenshotWithGemini(
                bitmap,
                """
                Please act as a QA visual verifier. Analyze this screenshot of MapColorSchemeActivity.
                Verify that:
                1. The map is rendered with the selected color scheme applied.
                2. Mode buttons (Light, Dark, Follow System) are visible and responsive at the top.
                If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }

    @Test
    fun testGroundOverlayVisuals() = runBlocking {
        ActivityScenario.launch(GroundOverlayDemoActivity::class.java).use {
            waitForMap()
            // Drag transparency seekbar
            uiDevice.swipe(450, 367, 950, 367, 15)
            Thread.sleep(1000)
            // Tap Switch Image (1922 historical map)
            uiDevice.click(268, 481)
            Thread.sleep(1500)

            val bitmap = captureScreenshot("ground_overlay_verified.png")
            verifyScreenshotWithGemini(
                bitmap,
                """
                Please act as a QA visual verifier. Analyze this screenshot of GroundOverlayDemoActivity.
                Verify that:
                1. The historical 1922 Newark map ground overlay is pinned to geographic coordinates.
                2. Transparency is applied to the overlay.
                3. The transparency slider and Switch Image button are visible.
                If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }

    @Test
    fun testTileOverlayVisuals() = runBlocking {
        ActivityScenario.launch(TileOverlayDemoActivity::class.java).use {
            waitForMap()
            // Toggle Fade In checkbox
            uiDevice.click(912, 338)
            Thread.sleep(800)
            // Drag transparency seekbar
            uiDevice.swipe(780, 482, 920, 482, 15)
            Thread.sleep(1000)

            val bitmap = captureScreenshot("tile_overlay_verified.png")
            verifyScreenshotWithGemini(
                bitmap,
                """
                Please act as a QA visual verifier. Analyze this screenshot of TileOverlayDemoActivity.
                Verify that:
                1. Lunar surface tiles from the custom TileProvider are rendered over the map surface.
                2. The Fade In checkbox and transparency slider are functional.
                3. No bottom sheet modal obscures the view.
                If all criteria are met, reply with "PASSED".
                """.trimIndent()
            )
        }
    }
}
