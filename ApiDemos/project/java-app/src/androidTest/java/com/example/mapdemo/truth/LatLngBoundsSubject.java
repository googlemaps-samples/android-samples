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
package com.example.mapdemo.truth;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;

/**
 * A custom Truth subject for asserting properties of {@link LatLngBounds} objects.
 * <p>
 * To use, static import {@link #assertThat(LatLngBounds)} and call assertions on the subject.
 */
public final class LatLngBoundsSubject extends Subject {
    private final LatLngBounds actual;
    private static final double TOLERANCE = 1e-5;

    private LatLngBoundsSubject(FailureMetadata metadata, LatLngBounds actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    /**
     * The factory for creating {@link LatLngBoundsSubject} instances.
     */
    public static Factory<LatLngBoundsSubject, LatLngBounds> latLngBounds() {
        return LatLngBoundsSubject::new;
    }

    /**
     * Creates a {@link LatLngBoundsSubject} for asserting on the given {@link LatLngBounds}.
     */
    public static LatLngBoundsSubject assertThat(LatLngBounds actual) {
        return Truth.assertAbout(latLngBounds()).that(actual);
    }

    /**
     * Asserts that the given {@link LatLng} is contained within the actual bounds, allowing for
     * a small tolerance to account for floating-point inaccuracies.
     */
    public void containsWithTolerance(LatLng expected) {
        boolean contains = actual.southwest.latitude - TOLERANCE <= expected.latitude &&
            expected.latitude <= actual.northeast.latitude + TOLERANCE &&
            isLongitudeWithinBounds(expected.longitude, actual);
        if (!contains) {
            failWithActual("expected to contain", expected);
        }
    }

    /**
     * Checks if a longitude is within the bounds, correctly handling cases where the bounds
     * cross the antimeridian (e.g., from 160 to -160).
     */
    private boolean isLongitudeWithinBounds(double longitude, LatLngBounds bounds) {
        if (bounds.southwest.longitude <= bounds.northeast.longitude) {
            // Standard case (e.g., from 10 to 20).
            return longitude >= bounds.southwest.longitude - TOLERANCE &&
                longitude <= bounds.northeast.longitude + TOLERANCE;
        } else {
            // Case where the bounds cross the antimeridian (e.g., from 160 to -160).
            return longitude >= bounds.southwest.longitude - TOLERANCE ||
                longitude <= bounds.northeast.longitude + TOLERANCE;
        }
    }
}
