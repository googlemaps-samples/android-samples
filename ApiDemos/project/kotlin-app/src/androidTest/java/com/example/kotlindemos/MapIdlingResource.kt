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
package com.example.kotlindemos

import androidx.test.espresso.IdlingResource
import com.google.android.gms.maps.GoogleMap

class MapIdlingResource(private val map: GoogleMap?) : IdlingResource, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {
    private var callback: IdlingResource.ResourceCallback? = null
    private var isIdle = true

    init {
        map?.setOnCameraIdleListener(this)
        map?.setOnCameraMoveStartedListener(this)
    }

    override fun getName(): String {
        return MapIdlingResource::class.java.name
    }

    override fun isIdleNow(): Boolean {
        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    override fun onCameraIdle() {
        isIdle = true
        callback?.onTransitionToIdle()
    }

    override fun onCameraMoveStarted(reason: Int) {
        isIdle = false
    }

    fun waitForIdle() {
        // Wait for the map to become idle.
        while (!isIdleNow) {
            Thread.sleep(10)
        }
    }
}