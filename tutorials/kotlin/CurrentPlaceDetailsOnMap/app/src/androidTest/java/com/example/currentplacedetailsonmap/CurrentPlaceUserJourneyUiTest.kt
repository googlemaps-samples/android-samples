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

import android.Manifest
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.example.currentplacedetailsonmap.MapsActivityCurrentPlace.PlaceItemUiModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI journey test testing the dialog presentation and marker selection flow.
 */
@RunWith(AndroidJUnit4::class)
class CurrentPlaceUserJourneyUiTest {

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    private val chautauquaLocation = LatLng(39.9989, -105.2828)
    private val chautauquaName = "Chautauqua Park"
    private val chautauquaAddress = "900 Baseline Rd, Boulder, CO 80302"
    private val chautauquaAttribution = "City of Boulder Open Space and Mountain Parks"

    @Test
    fun selectChautauquaParkFromDialog_placesMarkerAndDismisses() {
        val scenario = ActivityScenario.launch(MapsActivityCurrentPlace::class.java)

        val chautauquaPlace = Place.builder()
            .setLocation(chautauquaLocation)
            .setDisplayName(chautauquaName)
            .setFormattedAddress(chautauquaAddress)
            .setAttributions(listOf(chautauquaAttribution))
            .build()

        val likelyPlaces: List<PlaceItemUiModel> = listOfNotNull(
            toPlaceItemUiModel(chautauquaPlace, "Default Title"),
        )

        // Show dialog with candidates
        scenario.onActivity { activity ->
            activity.openPlacesDialog(likelyPlaces)
        }

        // Verify dialog opens with "Choose a place"
        onView(withText(R.string.pick_place))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        // Verify the list item contains the expected place name
        onData(anything())
            .inRoot(isDialog())
            .atPosition(0)
            .check(matches(withText(chautauquaName)))

        // Select the place item from the dialog list
        onData(anything())
            .inRoot(isDialog())
            .atPosition(0)
            .perform(click())

        // Confirm dialog is dismissed
        onView(withText(R.string.pick_place)).check(doesNotExist())

        // Verify the map marker and custom info window UI contents
        scenario.onActivity { activity ->
            val infoView = activity.layoutInflater.inflate(
                R.layout.custom_info_contents,
                activity.findViewById(R.id.map),
                false,
            )
            val titleView = infoView.findViewById<TextView>(R.id.title)
            val snippetView = infoView.findViewById<TextView>(R.id.snippet)

            val selectedPlace = likelyPlaces[0]
            titleView.text = selectedPlace.title
            snippetView.text = selectedPlace.snippet

            assertThat(titleView.text.toString()).isEqualTo(chautauquaName)
            assertThat(snippetView.text.toString()).isEqualTo(
                "$chautauquaAddress\n$chautauquaAttribution",
            )
        }
    }
}
