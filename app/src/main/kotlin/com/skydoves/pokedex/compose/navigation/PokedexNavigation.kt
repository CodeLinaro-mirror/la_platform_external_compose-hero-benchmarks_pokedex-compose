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

import android.os.Bundle
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.defaultViewModelCreationExtras
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.skydoves.pokedex.compose.core.designsystem.utils.TraceAsync
import com.skydoves.pokedex.compose.core.navigation.PokedexScreen
import com.skydoves.pokedex.compose.feature.details.PokedexDetails
import com.skydoves.pokedex.compose.feature.home.PokedexHome

@OptIn(ExperimentalSharedTransitionApi::class)
fun pokedexNavigation(
    sharedTransitionScope: SharedTransitionScope?,
    viewModelFactory: ViewModelProvider.Factory,
): (PokedexScreen) -> NavEntry<PokedexScreen> = entryProvider {
    entry<PokedexScreen.Home> {
        val animatedContentScope = LocalNavAnimatedContentScope.current
        animatedContentScope.TrackTransitionStatus("home")
        val isTransitionRunning = animatedContentScope.transition.isRunning
        if (isTransitionRunning) {
            TraceAsync("Pokedex Home Navigation Transition")
        }
        PokedexHome(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedContentScope,
            homeViewModel = viewModel(factory = viewModelFactory),
        )
    }

    entry<PokedexScreen.Details> { detailsKey ->
        val animatedContentScope = LocalNavAnimatedContentScope.current
        animatedContentScope.TrackTransitionStatus("details")
        val isTransitionRunning = animatedContentScope.transition.isRunning
        if (isTransitionRunning) {
            TraceAsync("Pokedex Details Navigation Transition")
        }
        val defaultExtras =
            LocalViewModelStoreOwner.current?.defaultViewModelCreationExtras ?: CreationExtras.Empty
        val extras =
            MutableCreationExtras(defaultExtras).apply {
                set(DEFAULT_ARGS_KEY, Bundle().apply { putString("name", detailsKey.pokemon.name) })
            }
        PokedexDetails(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedContentScope,
            detailsViewModel = viewModel(factory = viewModelFactory, extras = extras),
        )
    }
}

@Composable
private fun AnimatedContentScope.TrackTransitionStatus(tag: String) {
    val isRunning = this@TrackTransitionStatus.transition.isRunning
    val status = "pokedex-$tag-transition-active-$isRunning"
    Text(text = status, Modifier.semantics { testTag = status })
}
