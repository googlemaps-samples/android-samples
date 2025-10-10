package com.example.mapdemo;

import androidx.test.espresso.IdlingResource;

import com.google.android.gms.maps.GoogleMap;

public class MapIdlingResource implements IdlingResource, GoogleMap.OnMapLoadedCallback {
    private ResourceCallback callback;
    private boolean isIdle;

    public MapIdlingResource(GoogleMap map) {
        if (map != null) {
            map.setOnMapLoadedCallback(this);
        }
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
    public void onMapLoaded() {
        isIdle = true;
        if (callback != null) {
            callback.onTransitionToIdle();
        }
    }
}
