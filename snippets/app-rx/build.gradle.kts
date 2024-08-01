/*
 * Copyright 2024 Google LLC
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
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.app_maps_rx"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app_maps_rx"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

// [START maps_android_maps_rx_install]
dependencies {
    // RxJava bindings for the Maps SDK
    implementation("com.google.maps.android:maps-rx:1.0.0")

    // RxJava bindings for the Places SDK
    implementation("com.google.maps.android:places-rx:1.0.0")

    // It is recommended to also include the latest Maps SDK, Places SDK and RxJava so you
    // have the latest features and bug fixes.
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.libraries.places:places:3.5.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")

    // [START_EXCLUDE silent]
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.trello.rxlifecycle4:rxlifecycle-android-lifecycle-kotlin:4.0.2")
    implementation("com.google.maps.android:maps-ktx:5.1.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")
    // [END_EXCLUDE silent]
}
// [END maps_android_maps_rx_install]

secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
}
