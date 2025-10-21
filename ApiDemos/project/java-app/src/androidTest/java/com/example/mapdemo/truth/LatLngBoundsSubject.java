package com.example.mapdemo.truth;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;

public final class LatLngBoundsSubject extends Subject {
    private final LatLngBounds actual;
    private static final double TOLERANCE = 1e-5;

    private LatLngBoundsSubject(FailureMetadata metadata, LatLngBounds actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public static Factory<LatLngBoundsSubject, LatLngBounds> latLngBounds() {
        return LatLngBoundsSubject::new;
    }

    public static LatLngBoundsSubject assertThat(LatLngBounds actual) {
        return Truth.assertAbout(latLngBounds()).that(actual);
    }

    public void containsWithTolerance(LatLng expected) {
        boolean contains = actual.southwest.latitude - TOLERANCE <= expected.latitude &&
            expected.latitude <= actual.northeast.latitude + TOLERANCE &&
            isLongitudeWithinBounds(expected.longitude, actual);
        if (!contains) {
            failWithActual("expected to contain", expected);
        }
    }

    private boolean isLongitudeWithinBounds(double longitude, LatLngBounds bounds) {
        if (bounds.southwest.longitude <= bounds.northeast.longitude) {
            return longitude >= bounds.southwest.longitude - TOLERANCE &&
                longitude <= bounds.northeast.longitude + TOLERANCE;
        } else {
            // The bounds cross the antimeridian.
            return longitude >= bounds.southwest.longitude - TOLERANCE ||
                longitude <= bounds.northeast.longitude + TOLERANCE;
        }
    }
}
