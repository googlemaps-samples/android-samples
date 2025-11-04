package com.example.kotlindemos.truth

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Truth

/**
 * A custom Truth subject for asserting properties of [LatLngBounds] objects.
 *
 * To use, static import [assertThat] and call assertions on the subject.
 */
class LatLngBoundsSubject private constructor(
    failureMetadata: FailureMetadata,
    private val actual: LatLngBounds
) : Subject(failureMetadata, actual) {

    /**
     * Asserts that the given [LatLng] is contained within the actual bounds, allowing for
     * a small tolerance to account for floating-point inaccuracies.
     */
    fun containsWithTolerance(expected: LatLng) {
        val tolerance = 1e-5
        val contains = actual.southwest.latitude - tolerance <= expected.latitude &&
            expected.latitude <= actual.northeast.latitude + tolerance &&
            isLongitudeWithinBounds(expected.longitude, actual, tolerance)
        if (!contains) {
            failWithActual("expected to contain", expected)
        }
    }

    /**
     * Checks if a longitude is within the bounds, correctly handling cases where the bounds
     * cross the antimeridian (e.g., from 160 to -160).
     */
    private fun isLongitudeWithinBounds(longitude: Double, bounds: LatLngBounds, tolerance: Double): Boolean {
        return if (bounds.southwest.longitude <= bounds.northeast.longitude) {
            // Standard case (e.g., from 10 to 20).
            longitude >= bounds.southwest.longitude - tolerance &&
                longitude <= bounds.northeast.longitude + tolerance
        } else {
            // Case where the bounds cross the antimeridian (e.g., from 160 to -160).
            longitude >= bounds.southwest.longitude - tolerance ||
                longitude <= bounds.northeast.longitude + tolerance
        }
    }

    companion object {
        /**
         * Creates a [LatLngBoundsSubject] for asserting on the given [LatLngBounds].
         */
        fun assertThat(actual: LatLngBounds): LatLngBoundsSubject {
            return Truth.assertAbout(::LatLngBoundsSubject).that(actual)
        }
    }
}
