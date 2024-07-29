plugins {
    id("com.android.application")
    id("project-report")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mapdemo"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
    }

    buildFeatures {
        compose = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    namespace = "com.example.mapdemo"
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    implementation(libs.volley)
    implementation(platform(libs.kotlinBom))

    // GMS
    implementation(libs.playServicesMaps)

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
