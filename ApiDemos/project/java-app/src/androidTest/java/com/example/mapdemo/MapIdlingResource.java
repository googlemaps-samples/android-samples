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
}