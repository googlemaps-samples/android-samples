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

package com.example.currentplacedetailsonmap;

import static com.google.common.truth.Truth.assertThat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import java.util.Collections;
import org.junit.Test;

public class PlaceItemUiModelTest {

    private final LatLng testLatLng = new LatLng(-33.8523341, 151.2106085);
    private final String defaultTitle = "Default Location";

    @Test
    public void toUiModel_withAllFields_mapsCorrectly() {
        Place place =
                Place.builder()
                        .setLocation(testLatLng)
                        .setDisplayName("Sydney Opera House")
                        .setFormattedAddress("Bennelong Point, Sydney NSW 2000")
                        .setAttributions(Collections.singletonList("Photo by John Doe"))
                        .build();

        MapsActivityCurrentPlace.PlaceItemUiModel uiModel =
                MapsActivityCurrentPlace.toUiModel(place, defaultTitle);

        assertThat(uiModel).isNotNull();
        assertThat(uiModel.title).isEqualTo("Sydney Opera House");
        assertThat(uiModel.position).isEqualTo(testLatLng);
        assertThat(uiModel.snippet)
                .isEqualTo("Bennelong Point, Sydney NSW 2000\nPhoto by John Doe");
    }

    @Test
    public void toUiModel_withoutLocation_returnsNull() {
        Place place =
                Place.builder()
                        .setDisplayName("Sydney Opera House")
                        .setFormattedAddress("Bennelong Point, Sydney NSW 2000")
                        .build();

        MapsActivityCurrentPlace.PlaceItemUiModel uiModel =
                MapsActivityCurrentPlace.toUiModel(place, defaultTitle);

        assertThat(uiModel).isNull();
    }

    @Test
    public void toUiModel_withoutDisplayName_fallsBackToDefaultTitle() {
        Place place =
                Place.builder()
                        .setLocation(testLatLng)
                        .setFormattedAddress("Bennelong Point, Sydney NSW 2000")
                        .build();

        MapsActivityCurrentPlace.PlaceItemUiModel uiModel =
                MapsActivityCurrentPlace.toUiModel(place, defaultTitle);

        assertThat(uiModel).isNotNull();
        assertThat(uiModel.title).isEqualTo(defaultTitle);
        assertThat(uiModel.position).isEqualTo(testLatLng);
    }

    @Test
    public void toUiModel_withoutAttributions_formatsSnippetWithAddressOnly() {
        Place place =
                Place.builder()
                        .setLocation(testLatLng)
                        .setDisplayName("Sydney Opera House")
                        .setFormattedAddress("Bennelong Point, Sydney NSW 2000")
                        .build();

        MapsActivityCurrentPlace.PlaceItemUiModel uiModel =
                MapsActivityCurrentPlace.toUiModel(place, defaultTitle);

        assertThat(uiModel).isNotNull();
        assertThat(uiModel.snippet).isEqualTo("Bennelong Point, Sydney NSW 2000");
    }

    @Test
    public void toUiModel_withAttributionsOnly_formatsSnippetWithoutLeadingNewline() {
        Place place =
                Place.builder()
                        .setLocation(testLatLng)
                        .setDisplayName("Sydney Opera House")
                        .setAttributions(Collections.singletonList("Photo by John Doe"))
                        .build();

        MapsActivityCurrentPlace.PlaceItemUiModel uiModel =
                MapsActivityCurrentPlace.toUiModel(place, defaultTitle);

        assertThat(uiModel).isNotNull();
        assertThat(uiModel.snippet).isEqualTo("Photo by John Doe");
    }
}
