/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kotlindemos.model

import com.google.android.libraries.maps.model.LatLng

/**
 * Helper to translate all given LatLngs by the same amount in one of the predefined directions.
 */
enum class MoveDirection(private val numLatSteps: Int, private val numLngSteps: Int) {
    UP(1, 0),
    DOWN(-1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    fun getLatDistance(stepSizeDeg: Double): Double {
        return numLatSteps * stepSizeDeg
    }

    fun getLngDistance(stepSizeDeg: Double): Double {
        return numLngSteps * stepSizeDeg
    }

    companion object {
        private fun movePoint(
            oldPoint: LatLng,
            latMoveDistance: Double,

            lngMoveDistance: Double
        ): LatLng {
            return LatLng(oldPoint.latitude + latMoveDistance, oldPoint.longitude + lngMoveDistance)
        }

        fun movePointsInList(
            oldPoints: List<LatLng>,
            latMoveDistance: Double,
            lngMoveDistance: Double
        ): List<LatLng> {
            val newPoints = mutableListOf<LatLng>()
            for (oldPoint in oldPoints) {
                newPoints.add(movePoint(oldPoint, latMoveDistance, lngMoveDistance))
            }
            return newPoints
        }

        fun movePointsInNestedList(
            oldPointLists: List<List<LatLng>>,
            latMoveDistance: Double,
            lngMoveDistance: Double
        ): List<List<LatLng>> {
            val newPointLists = mutableListOf<List<LatLng>>()
            for (oldPointList in oldPointLists) {
                newPointLists.add(movePointsInList(oldPointList, latMoveDistance, lngMoveDistance))
            }
            return newPointLists
        }
    }

}