import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
  alias(libs.plugins.android.library)
}

android {
  namespace = "com.example.library"
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
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
  lint {
    abortOnError = false
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  lint {
    disable += setOf("MissingInflatedId", "OnClick")
    sarifOutput = layout.buildDirectory.file("reports/lint-results-debug.sarif").get().asFile
  }
  kotlin {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
      javaParameters.set(true)
    }
  }
}

dependencies {
  implementation(libs.appcompat)
  implementation(libs.core.ktx)
  implementation(libs.material)
  implementation(libs.startup.runtime)
  implementation(libs.play.services.maps)
  testImplementation(libs.junit)
  testImplementation(libs.robolectric)
  testImplementation(libs.mockk)
  testImplementation(libs.espresso.core)
}


abstract class GenerateArtifactIdTask : DefaultTask() {
  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  @get:Input
  abstract val version: Property<String>

  @TaskAction
  fun generate() {
    val dir = outputDir.get().asFile
    val packageName = "com.example.library.utils.meta"
    val packagePath = packageName.replace('.', '/')
    val outputFile = File(dir, "$packagePath/ArtifactId.kt")
    outputFile.parentFile.mkdirs()
    val attributionId = "gmp_git_androidsamples_v${version.get()}"
    outputFile.writeText(
      """
            package $packageName

            /**
             * Automatically generated object containing the library's attribution ID.
             * This is used to track library usage for analytics.
             */
            public object AttributionId {
                public const val VALUE: String = "$attributionId"
            }
            """.trimIndent()
    )
  }
}

val generateArtifactIdFile = tasks.register<GenerateArtifactIdTask>("generateArtifactIdFile") {
  outputDir.set(layout.buildDirectory.dir("generated/source/artifactId"))
  version.set(libs.versions.versionName.get())
}

androidComponents {
  onVariants { variant ->
    variant.sources.java?.addGeneratedSourceDirectory(
      generateArtifactIdFile,
      GenerateArtifactIdTask::outputDir
    )
  }
}
