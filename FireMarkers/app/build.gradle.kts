/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    // ---------------------------------------------------------------------------------------------
    //                                    Core App Plugins
    //
    // These plugins are essential for building an Android application with Kotlin and Jetpack
    // Compose. They handle the core compilation, packaging, and resource processing.
    // ---------------------------------------------------------------------------------------------
    alias(libs.plugins.android.application) // Applies the core Android application plugin.
    alias(libs.plugins.kotlin.android)      // Enables Kotlin language support for Android.
    alias(libs.plugins.kotlin.compose)      // Provides support for the Jetpack Compose compiler.

    // ---------------------------------------------------------------------------------------------
    //                                  Dependency Injection
    //
    // These plugins set up Hilt for dependency injection, which simplifies the management of
    // dependencies and improves the app's architecture. KSP (Kotlin Symbol Processing) is used
    // by Hilt for efficient code generation.
    // ---------------------------------------------------------------------------------------------
    alias(libs.plugins.ksp)                 // Enables the Kotlin Symbol Processing tool.
    alias(libs.plugins.hilt.android)        // Integrates Hilt for dependency injection.

    // ---------------------------------------------------------------------------------------------
    //                                     Utility Plugins
    //
    // These plugins provide additional functionalities, such as managing API keys with the
    // Secrets Gradle Plugin and enabling Kotlin serialization for data conversion.
    // ---------------------------------------------------------------------------------------------
    alias(libs.plugins.secrets.gradle.plugin) // Manages secrets and API keys.
    alias(libs.plugins.kotlin.serialization)  // Provides Kotlin serialization capabilities.
}

if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
    println("Applied Google Services plugin.")
} else {
    println("google-services.json not found — skipping Google Services plugin")
}

android {
    namespace = "com.example.firemarkers"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.firemarkers"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    lint {
        sarifOutput = layout.buildDirectory.file("reports/lint-results-debug.sarif").get().asFile
    }
}

dependencies {
    // ---------------------------------------------------------------------------------------------
    //                                  AndroidX & Jetpack
    //
    // These dependencies from the AndroidX library are fundamental for modern Android development.
    // They provide core functionalities, lifecycle-aware components, and the Jetpack Compose UI
    // toolkit for building the app's user interface.
    // ---------------------------------------------------------------------------------------------
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.hilt.navigation.compose)

    // ---------------------------------------------------------------------------------------------
    //                                  Dependency Injection
    //
    // Hilt is used for dependency injection, which helps in managing the object graph and
    // providing dependencies to different parts of the application.
    // ---------------------------------------------------------------------------------------------
    implementation(libs.dagger)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)

    // ---------------------------------------------------------------------------------------------
    //                                        Firebase
    //
    // The Firebase Bill of Materials (BOM) ensures that all Firebase libraries use compatible
    // versions. The `firebase-database` dependency is included for real-time data synchronization.
    // ---------------------------------------------------------------------------------------------
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)

    // ---------------------------------------------------------------------------------------------
    //                                     Google Maps
    //
    // These dependencies provide the necessary components for integrating Google Maps into the
    // application using Jetpack Compose. This includes the Maps Compose library, extended material
    // icons, and navigation components for Compose.
    // ---------------------------------------------------------------------------------------------
    implementation(libs.maps.compose)
    implementation(libs.maps.utils.ktx)

    // ---------------------------------------------------------------------------------------------
    //                                    Kotlin Libraries
    //
    // Provides additional Kotlin functionalities, such as the `kotlinx-datetime` library for
    // handling dates and times in a more idiomatic and multiplatform-friendly way.
    // ---------------------------------------------------------------------------------------------
    implementation(libs.kotlinx.datetime)

    // ---------------------------------------------------------------------------------------------
    //                                    Unit Testing
    //
    // These dependencies are used for running local unit tests on the JVM. They include JUnit for
    // the core testing framework, Robolectric for running Android-specific code without an
    // emulator, and coroutines-test for testing asynchronous code.
    // ---------------------------------------------------------------------------------------------
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.kotlin)

    // ---------------------------------------------------------------------------------------------
    //                                     Flow Testing
    //
    // Turbine is a small testing library for Kotlin Flows. It makes it easy to test Flows by
    // providing a simple and concise API for asserting on the items that a Flow emits. This is
    // particularly useful for testing ViewModels that expose data using StateFlow or SharedFlow.
    // ---------------------------------------------------------------------------------------------
    testImplementation(libs.turbine)

    // ---------------------------------------------------------------------------------------------
    //                                Instrumentation Testing
    //
    // These dependencies are for running tests on an Android device or emulator. They include
    // AndroidX Test libraries for JUnit and Espresso, as well as Compose-specific testing tools
    // for UI testing.
    // ---------------------------------------------------------------------------------------------
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)

    // ---------------------------------------------------------------------------------------------
    //                                    Debug Tools
    //
    // These dependencies are only included in the debug build. They provide tools for inspecting
    // and debugging the application's UI and other components.
    // ---------------------------------------------------------------------------------------------
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}