package com.example.mapdemo.utils;

import com.google.android.gms.maps.GoogleMap;

/**
 * An interface for activities that provide a GoogleMap object.
 */
public interface MapProvider {
    GoogleMap getMap();
    boolean isMapReady();
}
