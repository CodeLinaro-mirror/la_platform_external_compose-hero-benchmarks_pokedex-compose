import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.serialization")
  id("dagger.hilt.android.plugin")
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.skydoves.pokedex.compose.core.navigation"

  buildFeatures.compose = true

  extensions.configure<ComposeCompilerGradlePluginExtension> {
    enableStrongSkippingMode = true
  }
}

dependencies {
  implementation(projects.core.model)

  implementation(libs.androidx.core)
  implementation(libs.kotlinx.coroutines.android)

  api(libs.androidx.navigation.compose)
  
  // di
  implementation(libs.hilt.android)
  implementation(libs.androidx.hilt.navigation.compose)
  ksp(libs.hilt.compiler)

  // json parsing
  implementation(libs.kotlinx.serialization.json)
}