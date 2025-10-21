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
}