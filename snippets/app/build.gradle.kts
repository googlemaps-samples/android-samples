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

// [START maps_android_secrets_gradle_plugin]
plugins {
    // [START_EXCLUDE]
    id("com.android.application")
    id("kotlin-android")
    // [END_EXCLUDE]
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}
// [END maps_android_secrets_gradle_plugin]

android {
    compileSdk = 36
    defaultConfig {
        applicationId = "com.google.maps.example"
        minSdk = 24
        targetSdk = 36
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

    flavorDimensions.add("version")

    productFlavors {
        create("gms") {
            dimension = "version"
            applicationIdSuffix = ".gms"
            versionNameSuffix = "-gms"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    namespace = "com.google.maps.example"
}

// [START maps_android_play_services_maps_dependency]
dependencies {
    // [START_EXCLUDE silent]
    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.volley)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.places)
    // [END_EXCLUDE]

    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:19.2.0")
}
// [END maps_android_play_services_maps_dependency]

// [START maps_android_secrets_gradle_plugin_config]
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
// [END maps_android_secrets_gradle_plugin_config]