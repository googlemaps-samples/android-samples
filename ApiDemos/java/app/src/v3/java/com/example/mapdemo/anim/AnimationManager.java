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

package com.example.mapdemo.anim;

import android.os.Handler;
import android.os.Looper;

/** Simple manager for UI-thread animation. All methods must be invoked on the UI thread only. */
public class AnimationManager {
    private static final double INITIAL_FRAME_RATE_FPS = 60.0;

    private final Handler animationHandler;
    private final Runnable animationRunnable;

    private double frameRateFps;
    private boolean running;

    public AnimationManager(final Runnable frameRunnable) {
        frameRateFps = INITIAL_FRAME_RATE_FPS;
        running = false;
        animationHandler = new Handler(Looper.getMainLooper());

        animationRunnable =
            () -> {
                if (!running) {
                    return;
                }
                frameRunnable.run();
                requestAnimationFrame();
            };
    }

    private void requestAnimationFrame() {
        if (frameRateFps <= 0.0) {
            return;
        }

        long delayMs = (long) Math.floor(1000.0 / frameRateFps);
        animationHandler.postDelayed(animationRunnable, delayMs);
    }

    public void startAnimation() {
        if (running) {
            return;
        }

        requestAnimationFrame();
        running = true;
    }

    public void stopAnimation() {
        if (!running) {
            return;
        }

        animationHandler.removeCallbacks(animationRunnable);
        running = false;
    }

    public void setFrameRateFps(double frameRateFps) {
        this.frameRateFps = frameRateFps;
    }

    public double getFrameRateFps() {
        return frameRateFps;
    }
}