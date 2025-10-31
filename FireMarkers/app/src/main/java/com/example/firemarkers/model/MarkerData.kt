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
package com.example.firemarkers.model

/**
 * A data class representing a single marker on the map.
 *
 * This class is used to store the data for each marker, including its location, label, and style.
 * It is used by the [com.example.firemarkers.viewmodel.MarkersViewModel] to manage the state of the markers and by the UI to
 * render the markers on the map.
 *
 * @property id The unique ID of the marker.
 * @property latitude The latitude of the marker's location.
 * @property longitude The longitude of the marker's location.
 * @property label The label to be displayed for the marker.
 * @property style The style of the marker.
 * @property color The color of the marker, represented as a hue value.
 */
data class MarkerData(
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val label: String = "",
    val style: String = "",
    val color: Float = 0.0f
)
