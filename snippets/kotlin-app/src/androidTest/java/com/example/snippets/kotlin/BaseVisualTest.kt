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

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.maps.android.visualtesting.GeminiVisualTestHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import java.io.File

abstract class BaseVisualTest {

    protected val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()
    protected val uiDevice = UiDevice.getInstance(instrumentation)
    protected val context: Context = instrumentation.targetContext
    protected val helper = GeminiVisualTestHelper()

    protected val geminiApiKey: String by lazy {
        BuildConfig.GEMINI_API_KEY
    }

    protected fun captureScreenshot(filename: String = "screenshot_${System.currentTimeMillis()}.png"): Bitmap {
        val rawFile = File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), "raw_$filename")
        val screenshotTaken = uiDevice.takeScreenshot(rawFile)
        assertTrue("Failed to take screenshot: $filename", screenshotTaken)

        val rawBitmap = BitmapFactory.decodeFile(rawFile.absolutePath)
        assertTrue("Failed to decode screenshot file: $filename", rawBitmap != null)

        val scaledBitmap = Bitmap.createScaledBitmap(rawBitmap, rawBitmap.width / 4, rawBitmap.height / 4, true)
        val screenshotFile = File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), filename)
        screenshotFile.outputStream().use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        android.util.Log.i("BaseVisualTest", "Scaled 25% screenshot saved to device: ${screenshotFile.absolutePath}")

        return scaledBitmap
    }

    /**
     * Waits for the map to render.
     */
    protected fun waitForMapRendering(timeoutSeconds: Long = 30) {
        val found = uiDevice.wait(Until.hasObject(By.desc("MapLoaded")), timeoutSeconds * 1000)
        assertTrue("Map did not load within $timeoutSeconds seconds", found)
    }

    protected suspend fun verifyScreenshotWithGemini(screenshotBitmap: Bitmap, prompt: String) {
        if (geminiApiKey != "YOUR_GEMINI_API_KEY") {
            val geminiResponse = helper.analyzeImage(screenshotBitmap, prompt, geminiApiKey)
            android.util.Log.i("BaseVisualTest", "Gemini's analysis: $geminiResponse")
            assertTrue(
                "Visual verification failed. Gemini response: $geminiResponse",
                geminiResponse?.contains("PASSED", ignoreCase = true) == true
            )
        } else {
            assertTrue("Captured screenshot should be valid", screenshotBitmap.width > 0 && screenshotBitmap.height > 0)
        }
    }

    protected fun launchAndVerifySnippet(
        groupTitle: String,
        snippetTitle: String,
        screenshotFilename: String,
        prompt: String
    ) {
        runBlocking {
            val intent = Intent(context, MapActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("group_title", groupTitle)
                putExtra(MapActivity.EXTRA_SNIPPET_TITLE, snippetTitle)
            }
            context.startActivity(intent)

            uiDevice.wait(Until.hasObject(By.pkg(context.packageName).depth(0)), 10000)
            waitForMapRendering(60)
            kotlinx.coroutines.delay(5000) // Pause for map tiles, camera animations, and UI overlays to settle

            val screenshotBitmap = captureScreenshot(screenshotFilename)
            verifyScreenshotWithGemini(screenshotBitmap, prompt)
        }
    }
}
