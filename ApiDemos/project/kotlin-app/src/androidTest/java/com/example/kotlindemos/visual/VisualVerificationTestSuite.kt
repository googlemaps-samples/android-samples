/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos.visual

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite grouping all on-device visual verification tests for the 9 calibrated samples.
 *
 * Run with:
 * ./gradlew :ApiDemos:kotlin-app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.kotlindemos.visual.VisualVerificationTestSuite
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    VerifiedSamplesVisualTest::class
)
class VisualVerificationTestSuite
