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

// [START maps_android_secrets_gradle_plugin]
plugins {
    // [START_EXCLUDE]
    alias(libs.plugins.android.application)
    // [END_EXCLUDE]
    alias(libs.plugins.secrets.gradle.plugin)
}
// [END maps_android_secrets_gradle_plugin]

apply(from = rootProject.file("check_api_key.gradle.kts"))

android {
    namespace = "com.example.snippets.java"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.snippets.java"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
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
}

// [START maps_android_play_services_maps_dependency]
dependencies {
    // [START_EXCLUDE silent]
    implementation(project(":snippets:common"))
    implementation(libs.volley)
    implementation(libs.constraintlayout)
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.material)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // [END_EXCLUDE]

    // Maps SDK for Android
    implementation(libs.play.services.maps)
}
// [END maps_android_play_services_maps_dependency]

// [START maps_android_utils_install_snippet]
dependencies {
    // Utility Library for Maps SDK for Android
    // You do not need to add a separate dependency for the Maps SDK for Android
    // since this library builds in the compatible version of the Maps SDK.
    implementation(libs.maps.utils)
}
// [END maps_android_utils_install_snippet]

// [START maps_android_secrets_gradle_plugin_config]
secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the root directory (the same folder as the root local.properties file).
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
}
// [END maps_android_secrets_gradle_plugin_config]
