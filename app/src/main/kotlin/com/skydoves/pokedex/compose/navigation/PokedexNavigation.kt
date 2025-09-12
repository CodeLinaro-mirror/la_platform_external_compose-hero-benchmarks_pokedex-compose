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

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.skydoves.pokedex.compose.core.designsystem.utils.TraceAsync
import com.skydoves.pokedex.compose.core.navigation.PokedexScreen
import com.skydoves.pokedex.compose.feature.details.PokedexDetails
import com.skydoves.pokedex.compose.feature.home.PokedexHome

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.pokedexNavigation(
    sharedTransitionScope: SharedTransitionScope?,
    viewModelFactory: ViewModelProvider.Factory,
) {
    composable(PokedexScreen.Home.NAVIGATION_ROUTE) {
        TrackTransitionStatus("home")
        if (this.transition.isRunning) {
            TraceAsync("Pokedex Home Navigation Transition")
        }
        PokedexHome(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable,
            homeViewModel = viewModel(factory = viewModelFactory),
        )
    }

    composable(PokedexScreen.Details.NAVIGATION_ROUTE) { backStackEntry ->
        TrackTransitionStatus("details")
        if (this.transition.isRunning) {
            TraceAsync("Pokedex Details Navigation Transition")
        }
        PokedexDetails(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable,
            detailsViewModel = viewModel(factory = viewModelFactory),
        )
    }
}

@Composable
private fun AnimatedContentScope.TrackTransitionStatus(tag: String) {
    val status = "pokedex-$tag-transition-active-${this@TrackTransitionStatus.transition.isRunning}"
    Text(text = status, Modifier.semantics { testTag = status })
}
