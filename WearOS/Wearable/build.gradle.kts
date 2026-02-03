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

plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("android") // The Kotlin plugin is applied differently in Kotlin DSL
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.wearos"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    namespace = "com.example.wearosmap"

    lint {
        disable += setOf("MissingInflatedId")
        sarifOutput = layout.buildDirectory.file("reports/lint-results-debug.sarif").get().asFile
    }

    kotlin {
        jvmToolchain(21)
    }
}

// [START maps_wear_os_dependencies]
dependencies {
    // [START_EXCLUDE]
    implementation(libs.core.ktx)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.stdlib)
    // [END_EXCLUDE]
    // Modern Android projects use version catalogs to manage dependencies.  To include the necessary dependencies,
    // first add the following to your libs.versions.toml file:
    //
    // [versions]
    // playServicesMaps = "20.0.0"
    // wear = "1.3.0"
    // wearable = "2.9.0"
    //
    // [libraries]
    // play-services-maps = { group = "com.google.android.gms", name = "play-services-maps", version.ref = "playServicesMaps" }
    // wear = { group = "androidx.wear", name = "wear", version.ref = "wear" }
    // wearable-compile = { group = "com.google.android.wearable", name = "wearable", version.ref = "wearable" }
    // wearable-support = { group = "com.google.android.support", name = "wearable", version.ref = "wearable" }

    compileOnly(libs.wearable.compile)
    implementation(libs.wearable.support)
    implementation(libs.play.services.maps)

    // This dependency is necessary for ambient mode
    implementation(libs.wear)

    // Android Test Dependencies
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.uiautomator)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.junit)
    
    // If your project does not use a version catalog, you can use the following dependencies instead:
    //
    //    compileOnly("com.google.android.wearable:wearable:2.9.0")
    //    implementation("com.google.android.support:wearable:2.9.0")
    //    implementation("com.google.android.gms:play-services-maps:20.0.0")
    //    implementation("androidx.wear:wear:1.3.0")
    //    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    //    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
    //    androidTestImplementation("com.google.truth:truth:1.4.2")
    //    androidTestImplementation("junit:junit:4.13.2")
}
// [END maps_wear_os_dependencies]

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
