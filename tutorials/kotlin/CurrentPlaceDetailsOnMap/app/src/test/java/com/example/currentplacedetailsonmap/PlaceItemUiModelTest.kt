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

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaceItemUiModelTest {

    private val testLatLng = LatLng(-33.8523341, 151.2106085)
    private val defaultTitle = "Default Location"

    @Test
    fun toUiModel_withAllFields_mapsCorrectly() {
        val place = Place.builder()
            .setLocation(testLatLng)
            .setDisplayName("Sydney Opera House")
            .setFormattedAddress("Bennelong Point, Sydney NSW 2000")
            .setAttributions(listOf("Photo by John Doe"))
            .build()

        val uiModel = toPlaceItemUiModel(place, defaultTitle)

        assertThat(uiModel).isNotNull()
        assertThat(uiModel?.title).isEqualTo("Sydney Opera House")
        assertThat(uiModel?.position).isEqualTo(testLatLng)
        assertThat(uiModel?.snippet).isEqualTo("Bennelong Point, Sydney NSW 2000\nPhoto by John Doe")
    }

    @Test
    fun toUiModel_withoutLocation_returnsNull() {
        val place = Place.builder()
            .setDisplayName("Sydney Opera House")
            .setFormattedAddress("Bennelong Point, Sydney NSW 2000")
            .build()

        val uiModel = toPlaceItemUiModel(place, defaultTitle)

        assertThat(uiModel).isNull()
    }

    @Test
    fun toUiModel_withoutDisplayName_fallsBackToDefaultTitle() {
        val place = Place.builder()
            .setLocation(testLatLng)
            .setFormattedAddress("Bennelong Point, Sydney NSW 2000")
            .build()

        val uiModel = toPlaceItemUiModel(place, defaultTitle)

        assertThat(uiModel).isNotNull()
        assertThat(uiModel?.title).isEqualTo(defaultTitle)
        assertThat(uiModel?.position).isEqualTo(testLatLng)
        assertThat(uiModel?.snippet).isEqualTo("Bennelong Point, Sydney NSW 2000")
    }

    @Test
    fun toUiModel_withoutAttributions_formatsSnippetWithAddressOnly() {
        val place = Place.builder()
            .setLocation(testLatLng)
            .setDisplayName("Sydney Opera House")
            .setFormattedAddress("Bennelong Point, Sydney NSW 2000")
            .build()

        val uiModel = toPlaceItemUiModel(place, defaultTitle)

        assertThat(uiModel).isNotNull()
        assertThat(uiModel?.snippet).isEqualTo("Bennelong Point, Sydney NSW 2000")
    }

    @Test
    fun toUiModel_withAttributionsOnly_formatsSnippetWithoutLeadingNewline() {
        val place = Place.builder()
            .setLocation(testLatLng)
            .setDisplayName("Sydney Opera House")
            .setAttributions(listOf("Photo by John Doe", "Photo by Jane Smith"))
            .build()

        val uiModel = toPlaceItemUiModel(place, defaultTitle)

        assertThat(uiModel).isNotNull()
        assertThat(uiModel?.snippet).isEqualTo("Photo by John Doe\nPhoto by Jane Smith")
    }
}
