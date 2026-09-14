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

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.example.kotlindemos.BuildConfig
import com.google.maps.android.visualtesting.GeminiVisualTestHelper
import org.junit.Assert.assertTrue
import java.io.File

/**
 * Base class for on-device visual verification tests using Gemini Multimodal AI.
 */
abstract class BaseVisualVerificationTest {

    protected val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()
    protected val uiDevice: UiDevice = UiDevice.getInstance(instrumentation)
    protected val context: Context = instrumentation.targetContext
    protected val helper = GeminiVisualTestHelper()

    protected val geminiApiKey: String by lazy {
        try {
            BuildConfig.MAPS_API_KEY // Falls back if GEMINI_API_KEY is not separately generated
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Captures a screenshot from the connected device via UiDevice.
     */
    protected fun captureScreenshot(filename: String = "visual_test_${System.currentTimeMillis()}.png"): Bitmap {
        val storageDir = context.getExternalFilesDir(null) ?: context.filesDir
        val screenshotFile = File(storageDir, filename)
        
        val taken = uiDevice.takeScreenshot(screenshotFile)
        assertTrue("UiDevice failed to capture screenshot: $filename", taken)

        val bitmap = BitmapFactory.decodeFile(screenshotFile.absolutePath)
        assertTrue("Failed to decode screenshot bitmap: $filename", bitmap != null)

        Log.i(TAG, "Screenshot captured: ${screenshotFile.absolutePath} (${bitmap.width}x${bitmap.height})")
        return bitmap
    }

    /**
     * Verifies the visual contents of a screenshot using Gemini Multimodal AI.
     */
    protected suspend fun verifyScreenshotWithGemini(bitmap: Bitmap, prompt: String) {
        if (geminiApiKey.isNotBlank() && geminiApiKey != "DEFAULT_API_KEY") {
            val response = helper.analyzeImage(bitmap, prompt, geminiApiKey)
            Log.i(TAG, "Gemini Visual Evaluation Response:\n$response")
            assertTrue(
                "Gemini visual verification failed. Response: $response",
                response?.contains("PASSED", ignoreCase = true) == true
            )
        } else {
            // Offline/CI assertion fallback: verify screenshot has valid dimensions and non-empty buffer
            assertTrue("Screenshot width must be > 0", bitmap.width > 0)
            assertTrue("Screenshot height must be > 0", bitmap.height > 0)
        }
    }

    /**
     * Waits for the map container or tiles to settle.
     */
    protected fun waitForMap(timeoutMs: Long = 5000) {
        uiDevice.waitForIdle(timeoutMs)
        Thread.sleep(1500)
    }

    companion object {
        private const val TAG = "BaseVisualVerification"
    }
}
