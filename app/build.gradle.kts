import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import java.io.FileInputStream
import java.util.Properties

plugins {
  id("dagger.hilt.android.plugin")
  id("com.google.devtools.ksp")
  id("org.jetbrains.kotlin.plugin.compose")
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("androidx.baselineprofile")
}

android {
  namespace = "com.skydoves.pokedex.compose"

  compileSdk = 35

  defaultConfig {
    minSdk = 21
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  lint {
    abortOnError = false
  }

  defaultConfig {
    applicationId = "com.skydoves.pokedex.compose"
    versionCode = 1
    versionName = "1"
    testInstrumentationRunner = "com.skydoves.pokedex.compose.AppTestRunner"
    targetSdk = 35
  }

  signingConfigs {
    val properties = Properties()
    val localPropertyFile = project.rootProject.file("local.properties")
    if (localPropertyFile.canRead()) {
      properties.load(FileInputStream("$rootDir/local.properties"))
    }
    create("release") {
      storeFile = file(properties["RELEASE_KEYSTORE_PATH"] ?: "../keystores/pokedex.jks")
      keyAlias = properties["RELEASE_KEY_ALIAS"].toString()
      keyPassword = properties["RELEASE_KEY_PASSWORD"].toString()
      storePassword = properties["RELEASE_KEYSTORE_PASSWORD"].toString()
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles("proguard-rules.pro",)
      signingConfig = signingConfigs.getByName("release")

      kotlinOptions {
        freeCompilerArgs += listOf(
          "-Xno-param-assertions",
          "-Xno-call-assertions",
          "-Xno-receiver-assertions"
        )
      }

      packaging {
        resources {
          excludes += listOf(
            "DebugProbesKt.bin",
            "kotlin-tooling-metadata.json",
            "kotlin/**",
            "META-INF/*.version"
          )
        }
      }
    }
  }

  buildFeatures {
    buildConfig = true
    compose = true
  }

  hilt {
    enableAggregatingTask = true
  }

  kotlin {
    sourceSets.configureEach {
      kotlin.srcDir(layout.buildDirectory.files("generated/ksp/$name/kotlin/"))
    }
    sourceSets.all {
      languageSettings {
        languageVersion = "2.0"
      }
    }
  }

  testOptions.unitTests {
    isIncludeAndroidResources = true
    isReturnDefaultValues = true
  }

  extensions.configure<ComposeCompilerGradlePluginExtension> {
    enableStrongSkippingMode = true
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
  }
}

dependencies {
  // features
  implementation(projects.feature.home)
  implementation(projects.feature.details)

  // cores
  implementation(projects.core.model)
  implementation(projects.core.designsystem)
  implementation(projects.core.navigation)

  // compose
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.foundation)

  // di
  implementation(libs.hilt.android)
  implementation(libs.androidx.hilt.navigation.compose)
  ksp(libs.hilt.compiler)
  androidTestImplementation(libs.hilt.testing)
  kspAndroidTest(libs.hilt.compiler)

  // baseline profile
  implementation(libs.profileinstaller)
  baselineProfile(project(":baselineprofile"))

  // unit test
  testImplementation(libs.junit)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.kotlinx.coroutines.test)
  androidTestImplementation(libs.truth)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso)
//  androidTestImplementation(libs.android.test.runner)
}