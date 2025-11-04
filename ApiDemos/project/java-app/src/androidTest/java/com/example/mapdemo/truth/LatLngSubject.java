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