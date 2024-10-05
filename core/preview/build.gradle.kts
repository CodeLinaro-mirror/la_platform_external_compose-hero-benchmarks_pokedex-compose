import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("dagger.hilt.android.plugin")
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.skydoves.pokedex.compose.feature.preview"

  buildFeatures.compose = true

  extensions.configure<ComposeCompilerGradlePluginExtension> {
    enableStrongSkippingMode = true
  }
}

dependencies {
  // core
  implementation(projects.core.designsystem)
  implementation(projects.core.navigation)
  implementation(projects.core.model)
  
  // di
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
}