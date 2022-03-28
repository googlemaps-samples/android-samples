package com.google.maps.example.kotlin

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