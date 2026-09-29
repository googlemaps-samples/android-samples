/*
 * Copyright 2023 Google LLC
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

package com.example.app_rx

import android.util.Log
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.rx.places.fetchPlace

internal class PlacesRx {
  fun fetchPlace(placesClient: PlacesClient) {
    // [START maps_android_places_rx_marker_click_events]
    placesClient.fetchPlace(
      placeId = "thePlaceId",
      placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS),
      actions = {}
    ).subscribe(
      { response ->
        Log.d("PlacesRx", "Successfully got place ${response.place.id}")
      },
      { error ->
        Log.e("PlacesRx", "Could not get place: ${error.message}")
      }
    )
  }
  // [END maps_android_places_rx_marker_click_events]
}