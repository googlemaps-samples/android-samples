// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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