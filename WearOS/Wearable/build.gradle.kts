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
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("android") // The Kotlin plugin is applied differently in Kotlin DSL
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wearos"
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    namespace = "com.example.wearosmap"

    kotlinOptions {
        jvmTarget = "21"
    }

    kotlin {
        jvmToolchain(21)
    }
}

// [START maps_wear_os_dependencies]
dependencies {
    // [START_EXCLUDE]
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.0")
    // [END_EXCLUDE]
    compileOnly("com.google.android.wearable:wearable:2.9.0")
    implementation("com.google.android.support:wearable:2.9.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // This dependency is necessary for ambient mode
    implementation("androidx.wear:wear:1.3.0")
}
// [END maps_wear_os_dependencies]

secrets {
    defaultPropertiesFileName = "local.defaults.properties"
}
