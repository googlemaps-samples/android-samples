/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snippets.java.snippets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.GoogleMap;

@SnippetGroup(
        title = "My Location Layer",
        description = "Snippets demonstrating my location layer setup and button clicks."
)
public class MyLocationSnippets {

    private final Context context;
    private final TrackedMap map;

    public MyLocationSnippets(Context context, TrackedMap map) {
        this.context = context;
        this.map = map;
    }

    @SuppressLint("MissingPermission")
    @SnippetItem(
            title = "1. Enable My Location Layer",
            description = "Enables the my location layer and registers click listeners."
    )
    public void myLocationLayer() {
        // [START maps_android_my_location]
        // TODO: Before enabling the My Location layer, you must request
        // location permission from the user. This sample does not include
        // a request for location permission.
        map.setMyLocationEnabled(true);
        map.getDelegate().setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
                // Return false so that we don't consume the event and the default behavior still occurs
                // (the camera animates to the user's current position).
                return false;
            }
        });
        map.getDelegate().setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                Toast.makeText(context, "Current location:\n" + location, Toast.LENGTH_LONG).show();
            }
        });
        // [END maps_android_my_location]
    }
}
