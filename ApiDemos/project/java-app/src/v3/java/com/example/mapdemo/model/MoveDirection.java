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

package com.example.mapdemo.model;

import com.google.android.libraries.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper to translate all given LatLngs by the same amount in one of the predefined directions.
 */
public enum MoveDirection {
    UP(1, 0),
    DOWN(-1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int numLatSteps;
    private final int numLngSteps;

    MoveDirection(int numLatSteps, int numLngSteps) {
        this.numLatSteps = numLatSteps;
        this.numLngSteps = numLngSteps;
    }

    public double getLatDistance(double stepSizeDeg) {
        return numLatSteps * stepSizeDeg;
    }

    public double getLngDistance(double stepSizeDeg) {
        return numLngSteps * stepSizeDeg;
    }

    private static LatLng movePoint(LatLng oldPoint, double latMoveDistance,
        double lngMoveDistance) {
        return new LatLng(oldPoint.latitude + latMoveDistance,
            oldPoint.longitude + lngMoveDistance);
    }

    public static List<LatLng> movePointsInList(
        List<LatLng> oldPoints, double latMoveDistance, double lngMoveDistance) {
        List<LatLng> newPoints = new ArrayList<>(oldPoints.size());
        for (LatLng oldPoint : oldPoints) {
            newPoints.add(movePoint(oldPoint, latMoveDistance, lngMoveDistance));
        }

        return newPoints;
    }

    public static List<List<LatLng>> movePointsInNestedList(
        List<List<LatLng>> oldPointLists, double latMoveDistance, double lngMoveDistance) {
        List<List<LatLng>> newPointLists = new ArrayList<>(oldPointLists.size());
        for (List<LatLng> oldPointList : oldPointLists) {
            newPointLists.add(movePointsInList(oldPointList, latMoveDistance, lngMoveDistance));
        }

        return newPointLists;
    }
}
