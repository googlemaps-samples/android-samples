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

package com.example.currentplacedetailsonmap

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.currentplacedetailsonmap.MapsActivityCurrentPlace.PlaceItemUiModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration test connecting directly to the Google Places API server.
 *
 * Queries around Chautauqua Park in Boulder, CO and validates that live results
 * map cleanly to the tutorial's [PlaceItemUiModel].
 */
@RunWith(AndroidJUnit4::class)
class PlacesServerIntegrationTest {

    private lateinit var placesClient: PlacesClient

    // Coordinates of Chautauqua Park, Boulder, CO
    private val chautauquaCenter = LatLng(39.9992, -105.2815)
    private val searchRadiusMeters = 500.0

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.MAPS_API_KEY)
        }
        placesClient = Places.createClient(context)
    }

    @Test
    fun searchNearby_atChautauquaPark_returnsLivePlacesAndMapsToUiModels() {
        val searchArea = CircularBounds.newInstance(chautauquaCenter, searchRadiusMeters)
        val placeFields = listOf(
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
        )

        val request = SearchNearbyRequest.builder(searchArea, placeFields)
            .setMaxResultCount(5)
            .setRankPreference(SearchNearbyRequest.RankPreference.DISTANCE)
            .build()

        // Synchronously await the live Places API server response (15s timeout)
        val response = Tasks.await(placesClient.searchNearby(request), 15, TimeUnit.SECONDS)

        assertNotNull(response)
        assertTrue(response.places.isNotEmpty())

        // Pass server models through the tutorial's UI model mapper
        val likelyPlaces: List<PlaceItemUiModel> = response.places.mapNotNull {
            toPlaceItemUiModel(it, defaultTitle = "Unknown Location")
        }

        assertTrue(likelyPlaces.isNotEmpty())

        // Verify Chautauqua Park is present among the closest places returned by the server
        val placeNames = likelyPlaces.map { it.title }
        assertTrue("Chautauqua Park" in placeNames)

        // Assert that the UI model contains complete presentation data
        val chautauquaUiModel = likelyPlaces.first { it.title == "Chautauqua Park" }
        assertTrue(abs(chautauquaUiModel.position.latitude - 39.9992) < 0.02)
        assertTrue(abs(chautauquaUiModel.position.longitude - -105.2815) < 0.02)
        assertTrue("Boulder, CO" in chautauquaUiModel.snippet)
    }
}
