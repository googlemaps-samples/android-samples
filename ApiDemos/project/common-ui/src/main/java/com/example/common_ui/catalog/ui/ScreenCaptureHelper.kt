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

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility for capturing high-fidelity screenshots of hardware-accelerated Maps and UI surfaces
 * using PixelCopy with software fallback, and saving to disk.
 */
object ScreenCaptureHelper {

    @JvmStatic
    fun captureActivity(activity: Activity, onCaptured: (Bitmap?) -> Unit) {
        val window = activity.window
        if (window == null) {
            onCaptured(null)
            return
        }

        val decorView = window.decorView
        val width = decorView.width
        val height = decorView.height

        if (width <= 0 || height <= 0) {
            onCaptured(null)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val location = IntArray(2)
            decorView.getLocationInWindow(location)
            val rect = Rect(location[0], location[1], location[0] + width, location[1] + height)

            try {
                PixelCopy.request(
                    window,
                    rect,
                    bitmap,
                    { result ->
                        if (result == PixelCopy.SUCCESS) {
                            activity.runOnUiThread { onCaptured(bitmap) }
                        } else {
                            activity.runOnUiThread { onCaptured(captureViewFallback(decorView, width, height)) }
                        }
                    },
                    Handler(Looper.getMainLooper())
                )
            } catch (e: Exception) {
                onCaptured(captureViewFallback(decorView, width, height))
            }
        } else {
            onCaptured(captureViewFallback(decorView, width, height))
        }
    }

    private fun captureViewFallback(view: View, width: Int, height: Int): Bitmap? {
        return try {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun saveBitmap(context: Context, bitmap: Bitmap, sampleId: String): File {
        val cleanName = sampleId.substringAfterLast('.').replace(Regex("[^a-zA-Z0-9_]"), "_")
        val dir = File(context.getExternalFilesDir("screenshots") ?: context.filesDir, "").apply {
            mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(dir, "issue_${cleanName}_${timeStamp}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }
}
