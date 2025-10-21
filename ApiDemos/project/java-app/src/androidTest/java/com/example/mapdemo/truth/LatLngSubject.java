package com.example.mapdemo.truth;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;

/**
 * A custom Truth subject for asserting properties of {@link LatLng} objects.
 * <p>
 * To use, static import {@link #assertThat(LatLng)} and call assertions on the subject.
 * <pre>{@code
 * LatLng actual = new LatLng(10.0, 20.0);
 * LatLng expected = new LatLng(10.000001, 20.000001);
 * assertThat(actual).isNear(expected);
 * }</pre>
 */
public final class LatLngSubject extends Subject {
    private final LatLng actual;
    private static final double TOLERANCE = 1e-6;

    private LatLngSubject(FailureMetadata metadata, LatLng actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    /**
     * The factory for creating {@link LatLngSubject} instances.
     */
    public static Factory<LatLngSubject, LatLng> latLngs() {
        return LatLngSubject::new;
    }

    /**
     * Creates a {@link LatLngSubject} for asserting on the given {@link LatLng}.
     */
    public static LatLngSubject assertThat(LatLng actual) {
        return Truth.assertAbout(latLngs()).that(actual);
    }

    /**
     * Asserts that the actual {@link LatLng} is within a small tolerance of the expected value.
     * This is useful for accounting for floating-point inaccuracies.
     */
    public void isNear(LatLng expected) {
        if (Math.abs(actual.latitude - expected.latitude) > TOLERANCE ||
            Math.abs(actual.longitude - expected.longitude) > TOLERANCE) {
            failWithActual("expected to be near", expected);
        }
    }
}
