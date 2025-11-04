package com.example.kotlindemos.truth

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Truth
import kotlin.math.abs

/**
 * A custom Truth subject for asserting properties of [LatLng] objects.
 *
 * To use, static import [assertThat] and call assertions on the subject.
 * ```
 * val actual = LatLng(10.0, 20.0)
 * val expected = LatLng(10.000001, 20.000001)
 * assertThat(actual).isNear(expected)
 * ```
 */
class LatLngSubject private constructor(
    failureMetadata: FailureMetadata,
    private val actual: LatLng
) : Subject(failureMetadata, actual) {

    /**
     * Asserts that the actual [LatLng] is within a small tolerance of the expected value.
     * This is useful for accounting for floating-point inaccuracies.
     */
    fun isNear(expected: LatLng) {
        val tolerance = 1e-6
        if (abs(actual.latitude - expected.latitude) > tolerance ||
            abs(actual.longitude - expected.longitude) > tolerance
        ) {
            failWithActual("expected to be near", expected)
        }
    }

    companion object {
        /**
         * Creates a [LatLngSubject] for asserting on the given [LatLng].
         */
        fun assertThat(actual: LatLng): LatLngSubject {
            return Truth.assertAbout(::LatLngSubject).that(actual)
        }
    }
}
