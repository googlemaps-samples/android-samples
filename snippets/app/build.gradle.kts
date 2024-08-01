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
    compileSdk = 34
    defaultConfig {
        applicationId = "com.google.maps.example"
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
    implementation("com.google.android.gms:play-services-maps:19.0.0")
}
// [END maps_android_play_services_maps_dependency]

// [START maps_android_secrets_gradle_plugin_config]
secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}
// [END maps_android_secrets_gradle_plugin_config]
