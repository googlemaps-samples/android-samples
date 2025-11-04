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
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;
import com.google.maps.android.SphericalUtil;

public class LatLngSubject extends Subject {
    private final LatLng actual;

    private LatLngSubject(FailureMetadata metadata, LatLng actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public static LatLngSubject assertThat(LatLng latLng) {
        return Truth.assertAbout(LatLngSubject::new).that(latLng);
    }

    public void isNear(LatLng expected) {
        double tolerance = 1e-6;
        if (Math.abs(actual.latitude - expected.latitude) > tolerance ||
            Math.abs(actual.longitude - expected.longitude) > tolerance) {
            failWithActual("expected to be near", expected);
        }
    }

    public LatLngDistanceSubject isWithin(double tolerance) {
        return new LatLngDistanceSubject(tolerance);
    }

    public class LatLngDistanceSubject {
        private final double tolerance;

        LatLngDistanceSubject(double tolerance) {
            this.tolerance = tolerance;
        }

        public void of(LatLng expected) {
            double distance = SphericalUtil.computeDistanceBetween(actual, expected);
            if (distance > tolerance) {
                failWithActual("expected to be within " + tolerance + " meters of", expected);
            }
        }
    }
}