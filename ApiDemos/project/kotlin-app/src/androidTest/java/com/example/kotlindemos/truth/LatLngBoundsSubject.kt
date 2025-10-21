package com.example.kotlindemos.truth

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Truth

class LatLngBoundsSubject private constructor(
    failureMetadata: FailureMetadata,
    private val actual: LatLngBounds
) : Subject(failureMetadata, actual) {

    fun containsWithTolerance(expected: LatLng) {
        val tolerance = 1e-5
        val contains = actual.southwest.latitude - tolerance <= expected.latitude &&
            expected.latitude <= actual.northeast.latitude + tolerance &&
            isLongitudeWithinBounds(expected.longitude, actual, tolerance)
        if (!contains) {
            failWithActual("expected to contain", expected)
        }
    }

    private fun isLongitudeWithinBounds(longitude: Double, bounds: LatLngBounds, tolerance: Double): Boolean {
        return if (bounds.southwest.longitude <= bounds.northeast.longitude) {
            longitude >= bounds.southwest.longitude - tolerance &&
                longitude <= bounds.northeast.longitude + tolerance
        } else {
            // The bounds cross the antimeridian.
            longitude >= bounds.southwest.longitude - tolerance ||
                longitude <= bounds.northeast.longitude + tolerance
        }
    }

    companion object {
        fun assertThat(actual: LatLngBounds): LatLngBoundsSubject {
            return Truth.assertAbout(::LatLngBoundsSubject).that(actual)
        }
    }
}
