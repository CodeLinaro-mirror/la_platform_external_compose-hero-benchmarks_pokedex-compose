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

package com.skydoves.pokedex.compose.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.skydoves.pokedex.compose.core.navigation.PokedexScreen
import com.skydoves.pokedex.compose.core.navigation.navigationEnterTransition
import com.skydoves.pokedex.compose.core.navigation.navigationExitTransition
import com.skydoves.pokedex.compose.core.network.di.ModuleLocator
import com.skydoves.pokedex.compose.core.viewmodel.pokedexViewModelFactory

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokedexNavHost(navHostController: NavHostController, startDestination: PokedexScreen) {
    SharedTransitionLayout {
        PokedexNavigation(
            navHostController,
            sharedTransitionScope = this@SharedTransitionLayout,
            startDestination = startDestination,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PokedexNavigation(
    navHostController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    startDestination: PokedexScreen,
) {
    val viewModelFactory = remember { pokedexViewModelFactory(ModuleLocator.repositoryModule) }
    NavHost(
        navController = navHostController,
        startDestination = startDestination.asRoute(),
        enterTransition = { navigationEnterTransition },
        exitTransition = { navigationExitTransition },
    ) {
        pokedexNavigation(sharedTransitionScope = sharedTransitionScope, viewModelFactory)
    }
}
