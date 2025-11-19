package com.example.kotlindemos

import android.graphics.Bitmap
import android.os.Environment
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.ScreenCapture
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.io.IOException
import java.util.HashSet

class ScreenshotTestWatcher : TestWatcher() {

    private val SCREENSHOT_DIR = "screenshots"

    override fun failed(e: Throwable?, description: Description?) {
        super.failed(e, description)
        if (description != null) {
            val filename = "${description.className}-${description.methodName}"
            captureScreenshot(filename)
        }
    }

    private fun captureScreenshot(filename: String) {
        try {
            val capture: ScreenCapture = Screenshot.capture()
            capture.name = filename
            capture.format = Bitmap.CompressFormat.PNG

            val processors: MutableSet<ScreenCapture.Processor> = HashSet()
            processors.add(CustomScreenCaptureProcessor())

            capture.process(processors)
            System.out.println("Screenshot captured: $filename")
        } catch (e: Exception) {
            System.err.println("Failed to capture screenshot: ${e.message}")
            e.printStackTrace()
        }
    }

    private class CustomScreenCaptureProcessor : BasicScreenCaptureProcessor() {
        private val outputDirectory: File

        init {
            outputDirectory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                SCREENSHOT_DIR
            )
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs()
            }
        }

        override fun getOutputDirectory(filename: String?): File {
            return outputDirectory
        }
    }
}