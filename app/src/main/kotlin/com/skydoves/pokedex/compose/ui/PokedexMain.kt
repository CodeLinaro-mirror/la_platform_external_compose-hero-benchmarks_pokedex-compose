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

import android.os.Trace
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.skydoves.pokedex.compose.core.PokedexFeatureFlags
import com.skydoves.pokedex.compose.core.designsystem.theme.PokedexTheme
import com.skydoves.pokedex.compose.core.navigation.AppComposeNavigator
import com.skydoves.pokedex.compose.core.navigation.LocalComposeNavigator
import com.skydoves.pokedex.compose.core.navigation.PokedexComposeNavigator
import com.skydoves.pokedex.compose.core.navigation.PokedexScreen
import com.skydoves.pokedex.compose.core.network.di.ModuleLocator
import com.skydoves.pokedex.compose.navigation.PokedexNavHost

@Composable
fun PokedexMain(
    composeNavigator: AppComposeNavigator<PokedexScreen> = remember { PokedexComposeNavigator() },
    startDestination: PokedexScreen,
) {
    PokedexTheme {
        CompositionLocalProvider(LocalComposeNavigator provides composeNavigator) {
            val context = LocalContext.current
            DisposableEffect(context) {
                (context as? ComponentActivity)?.enableEdgeToEdge()
                onDispose { ModuleLocator.detach() }
            }
            Trace.beginSection("ModuleLocator.attach")
            ModuleLocator.attach(context = { context })
            Trace.endSection()
            if (PokedexFeatureFlags.UseCoil) {
                ConfigureCoil()
            }
            val navHostController = rememberNavController()
            LaunchedEffect(Unit) { composeNavigator.handleNavigationCommands(navHostController) }
            PokedexNavHost(
                navHostController = navHostController,
                startDestination = startDestination,
            )
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
