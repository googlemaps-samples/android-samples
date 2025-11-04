package com.example.kotlindemos.utils

import com.google.android.gms.maps.GoogleMap

/**
 * Interface for activities that provide a GoogleMap instance.
 */
interface MapProvider {
    val map: GoogleMap
    val mapReady: Boolean
}