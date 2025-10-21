package com.example.kotlindemos.truth

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Truth

class LatLngSubject private constructor(
    failureMetadata: FailureMetadata,
    private val actual: LatLng
) : Subject(failureMetadata, actual) {

    fun isNear(expected: LatLng) {
        val tolerance = 1e-6
        if (Math.abs(actual.latitude - expected.latitude) > tolerance ||
            Math.abs(actual.longitude - expected.longitude) > tolerance
        ) {
            failWithActual("expected to be near", expected)
        }
    }

    companion object {
        fun assertThat(actual: LatLng): LatLngSubject {
            return Truth.assertAbout(::LatLngSubject).that(actual)
        }
    }
}
