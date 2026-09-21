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
include(":snippets:common")
project(":snippets:common").projectDir = file("snippets/common")
include(":snippets:java-app")
project(":snippets:java-app").projectDir = file("snippets/java-app")
include(":snippets:kotlin-app")
project(":snippets:kotlin-app").projectDir = file("snippets/kotlin-app")
include(":snippets:app-compose")
project(":snippets:app-compose").projectDir = file("snippets/app-compose")
include(":snippets:app-places-ktx")
project(":snippets:app-places-ktx").projectDir = file("snippets/app-places-ktx")

// Tutorials
include(":tutorials:kotlin:Polygons")
project(":tutorials:kotlin:Polygons").projectDir = file("tutorials/kotlin/Polygons/app")
// Add others as needed, starting with these for now

// Visual Testing
include(":visual-testing")
project(":visual-testing").projectDir = file("visual-testing")

