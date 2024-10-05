import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.skydoves.pokedex.compose.designsystem"
  buildFeatures.compose = true

  extensions.configure<ComposeCompilerGradlePluginExtension> {
    enableStrongSkippingMode = true
  }
}

dependencies {

  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui)
  api(libs.androidx.compose.ui.tooling)
  api(libs.androidx.compose.ui.tooling.preview)
  api(libs.androidx.compose.animation)
  api(libs.androidx.compose.material3)
  api(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
}
