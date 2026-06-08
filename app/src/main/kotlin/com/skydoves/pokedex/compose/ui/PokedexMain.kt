/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.pokedex.compose.ui

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.text.LocalBackgroundTextMeasurementExecutor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.trace
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.skydoves.pokedex.compose.core.PokedexFeatureFlags
import com.skydoves.pokedex.compose.core.designsystem.theme.PokedexTheme
import com.skydoves.pokedex.compose.core.navigation.LocalComposeNavigator
import com.skydoves.pokedex.compose.core.navigation.PokedexScreen
import com.skydoves.pokedex.compose.core.navigation.rememberPokedexComposeNavigator
import com.skydoves.pokedex.compose.core.network.di.ModuleLocator
import com.skydoves.pokedex.compose.navigation.PokedexNavDisplay
import java.util.concurrent.Executors

@Composable
fun PokedexMain(startDestination: PokedexScreen) {
    val textMeasurementExecutor =
        if (PokedexFeatureFlags.UseBackgroundTextPrewarming) {
            remember {
                Executors.newSingleThreadExecutor { Thread(it, "ComposeBackgroundTextPrewarmer") }
            }
        } else {
            null
        }
    val navigator = rememberPokedexComposeNavigator(startDestination)
    PokedexTheme {
        var values: Array<ProvidedValue<*>> =
            arrayOf(
                LocalComposeNavigator provides navigator,
                LocalBackgroundTextMeasurementExecutor provides textMeasurementExecutor,
            )
        if (PokedexFeatureFlags.DisableOverscrollEffect) {
            val providedValue = (LocalOverscrollFactory provides null)
            values += providedValue
        }

        CompositionLocalProvider(*values) {
            val context = LocalContext.current
            DisposableEffect(context) {
                (context as? ComponentActivity)?.enableEdgeToEdge()
                onDispose { ModuleLocator.detach() }
            }
            trace("ModuleLocator.attach") { ModuleLocator.attach(context = { context }) }
            if (PokedexFeatureFlags.UseCoil) {
                ConfigureCoil()
            }
            PokedexNavDisplay(backStack = navigator.backStack)
        }
    }
}

@Composable
private fun ConfigureCoil() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = ModuleLocator.networkModule.okHttpClient.newBuilder().build()
                    )
                )
            }
            .build()
    }
}
