/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kotlindemos.anim

import android.os.Handler
import android.os.Looper
import kotlin.math.floor

/** Simple manager for UI-thread animation. All methods must be invoked on the UI thread only.  */
class AnimationManager(frameRunnable: Runnable) {

    private val animationHandler: Handler
    private val animationRunnable: Runnable
    var frameRateFps: Double
    private var running: Boolean

    init {
        frameRateFps = INITIAL_FRAME_RATE_FPS
        running = false
        animationHandler = Handler(Looper.getMainLooper())
        animationRunnable = object : Runnable {
            override fun run() {
                if (!running) {
                    return
                }
                frameRunnable.run()
                requestAnimationFrame()
            }
        }
    }

    private fun requestAnimationFrame() {
        if (frameRateFps <= 0.0) {
            return
        }
        val delayMs = floor(1000.0 / frameRateFps) as Long
        animationHandler.postDelayed(animationRunnable, delayMs)
    }

    fun startAnimation() {
        if (running) {
            return
        }
        requestAnimationFrame()
        running = true
    }

    fun stopAnimation() {
        if (!running) {
            return
        }
        animationHandler.removeCallbacks(animationRunnable)
        running = false
    }

    companion object {
        private const val INITIAL_FRAME_RATE_FPS = 60.0
    }
}