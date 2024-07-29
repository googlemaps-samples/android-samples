plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kotlindemos"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    lint {
        abortOnError = false
    }

    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    namespace = "com.example.kotlindemos"
}

dependencies {
    api(libs.appcompat)
    implementation(libs.kotlinStdlib)
    implementation(libs.cardview)
    implementation(libs.recyclerview)
    implementation(libs.multidex)
    implementation(libs.volley)

    // GMS
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.mapsKtx)

    // Below is used to run the easypermissions library to manage location permissions
    // EasyPermissions is needed to help us request for permission to access location
    implementation(libs.easypermissions)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.espressoCore)
}

secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
}
