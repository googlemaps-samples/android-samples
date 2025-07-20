/*
 * Copyright 2025 Google LLC
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

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.kotlindemos"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.16.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions.add("version")

    compileOptions {
    }

    lint {
        abortOnError = false
    }

    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = JavaVersion.VERSION_21.toString()
    }

    namespace = "com.example.kotlindemos"
}

kotlin {
    jvmToolchain(21) // Specify the JVM toolchain version
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.kotlinStdlib)
    implementation(libs.cardview)
    implementation(libs.recyclerview)
    implementation(libs.multidex)
    implementation(libs.volley)
    implementation(libs.material)

    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.mapsKtx)

    implementation(libs.activity)

    // Below is used to run the easypermissions library to manage location permissions
    // EasyPermissions is needed to help us request for permission to access location
    implementation(libs.easypermissions)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.espressoCore)

    implementation(project(":common-ui"))
}

secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
}