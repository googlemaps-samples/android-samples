// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.mapdemo;

import androidx.test.espresso.IdlingResource;
import com.google.android.gms.maps.GoogleMap;

public class MapIdlingResource implements IdlingResource, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {
    private final GoogleMap map;
    private ResourceCallback callback;
    private boolean isIdle = true;

    public MapIdlingResource(GoogleMap map) {
        this.map = map;
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
    }

    @Override
    public String getName() {
        return MapIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCameraIdle() {
        isIdle = true;
        if (callback != null) {
            callback.onTransitionToIdle();
        }
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        isIdle = false;
    }

    public void waitForIdle() {
        // Wait for the map to become idle.
        while (!isIdleNow()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitForIdle(long additionalDelay) {
        // Wait for the map to become idle.
        while (!isIdleNow()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (additionalDelay > 0) {
            try {
                Thread.sleep(additionalDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}