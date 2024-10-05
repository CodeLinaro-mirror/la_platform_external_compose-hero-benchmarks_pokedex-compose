import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("dagger.hilt.android.plugin")
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.skydoves.pokedex.compose.feature.home"
  compileSdk = 35

  defaultConfig {
    minSdk = 21
    buildFeatures.compose = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  extensions.configure<ComposeCompilerGradlePluginExtension> {
    enableStrongSkippingMode = true
  }
}

dependencies {
  implementation(project(":core:designsystem"))
  implementation(project(":core:navigation"))
  implementation(project(":core:viewmodel"))
  implementation(project(":core:data"))
  compileOnly(project(":core:preview"))

  implementation("androidx.palette:palette:1.0.0")

  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
}