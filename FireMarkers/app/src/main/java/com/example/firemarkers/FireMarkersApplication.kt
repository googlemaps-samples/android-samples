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
package com.example.firemarkers

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp
import java.util.Objects
import kotlin.text.isBlank

/**
 * The main application class for the FireMarkers app.
 *
 * This class is the entry point of the application and is responsible for initializing Hilt for
 * dependency injection.
 */
@HiltAndroidApp
class FireMarkersApplication : Application() {
    /**
     * Called when the application is starting, before any other application objects have been created.
     *
     * This method is used to initialize the application, including setting up Hilt for dependency
     * injection.
     */
    override fun onCreate() {
        super.onCreate()
        checkApiKey()
    }

    /**
     * Checks if the API key for Google Maps is properly configured in the application's metadata.
     *
     * This method retrieves the API key from the application's metadata, specifically looking for
     * a string value associated with the key "com.google.android.geo.maps.API_KEY".
     * The key must be present, not blank, and not set to the placeholder value "DEFAULT_API_KEY".
     *
     * If any of these checks fail, a Toast message is displayed indicating that the API key is missing or
     * incorrectly configured, and a RuntimeException is thrown.
     */
    private fun checkApiKey() {
        try {
            val appInfo =
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = Objects.requireNonNull(appInfo.metaData)

            val mapsApiKey =
                bundle.getString("com.google.android.geo.API_KEY") // Key name is important!

            if (mapsApiKey.isNullOrBlank() || mapsApiKey == "DEFAULT_API_KEY") {
                Toast.makeText(
                    this,
                    "Maps API Key was not set in secrets.properties",
                    Toast.LENGTH_LONG
                ).show()
                throw RuntimeException("Maps API Key was not set in secrets.properties")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Package name not found.", e)
            throw RuntimeException("Error getting package info.", e)
        } catch (e: NullPointerException) {
            Log.e(TAG, "Error accessing meta-data.", e) // Handle the case where meta-data is completely missing.
            throw RuntimeException("Error accessing meta-data in manifest", e)
        }
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}