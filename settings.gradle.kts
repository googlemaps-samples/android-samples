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

// [START maps_android_settings_plugin_management]
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
// [END maps_android_settings_plugin_management]

// [START maps_android_settings_dependency_resolution_management]
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}
// [END maps_android_settings_dependency_resolution_management]

rootProject.name = "android-samples"

// ApiDemos
include(":ApiDemos:common-ui")
project(":ApiDemos:common-ui").projectDir = file("ApiDemos/project/common-ui")
include(":ApiDemos:java-app")
project(":ApiDemos:java-app").projectDir = file("ApiDemos/project/java-app")
include(":ApiDemos:kotlin-app")
project(":ApiDemos:kotlin-app").projectDir = file("ApiDemos/project/kotlin-app")

// FireMarkers
include(":FireMarkers:app")
project(":FireMarkers:app").projectDir = file("FireMarkers/app")

// WearOS
include(":WearOS:Wearable")
project(":WearOS:Wearable").projectDir = file("WearOS/Wearable")

// Snippets
include(":snippets:app")
project(":snippets:app").projectDir = file("snippets/app")
include(":snippets:app-ktx")
project(":snippets:app-ktx").projectDir = file("snippets/app-ktx")
include(":snippets:app-utils-ktx")
project(":snippets:app-utils-ktx").projectDir = file("snippets/app-utils-ktx")
include(":snippets:app-compose")
project(":snippets:app-compose").projectDir = file("snippets/app-compose")
include(":snippets:app-places-ktx")
project(":snippets:app-places-ktx").projectDir = file("snippets/app-places-ktx")
include(":snippets:app-utils")
project(":snippets:app-utils").projectDir = file("snippets/app-utils")

// Tutorials
include(":tutorials:kotlin:Polygons")
project(":tutorials:kotlin:Polygons").projectDir = file("tutorials/kotlin/Polygons/app")
// Add others as needed, starting with these for now
