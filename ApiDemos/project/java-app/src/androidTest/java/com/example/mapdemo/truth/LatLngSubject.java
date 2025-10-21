package com.example.mapdemo.truth;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;

public final class LatLngSubject extends Subject {
    private final LatLng actual;
    private static final double TOLERANCE = 1e-6;

    private LatLngSubject(FailureMetadata metadata, LatLng actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public static Factory<LatLngSubject, LatLng> latLngs() {
        return LatLngSubject::new;
    }

    public static LatLngSubject assertThat(LatLng actual) {
        return Truth.assertAbout(latLngs()).that(actual);
    }

    public void isNear(LatLng expected) {
        if (Math.abs(actual.latitude - expected.latitude) > TOLERANCE ||
            Math.abs(actual.longitude - expected.longitude) > TOLERANCE) {
            failWithActual("expected to be near", expected);
        }
    }
}
