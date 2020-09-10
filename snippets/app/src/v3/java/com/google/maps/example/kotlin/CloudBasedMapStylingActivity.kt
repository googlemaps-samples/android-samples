package com.google.maps.example.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.maps.GoogleMapOptions
import com.google.android.libraries.maps.MapFragment
import com.google.maps.example.R

class CloudBasedMapStylingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START maps_android_cloud_based_map_styling]
        val mapFragment = MapFragment.newInstance(
            GoogleMapOptions()
                .mapId(resources.getString(R.string.map_id))
        )
        // [END maps_android_cloud_based_map_styling]
    }
}