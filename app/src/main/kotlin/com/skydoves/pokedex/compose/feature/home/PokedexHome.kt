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

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.skydoves.pokedex.compose.feature.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.skydoves.pokedex.compose.R
import com.skydoves.pokedex.compose.core.PokedexFeatureFlags
import com.skydoves.pokedex.compose.core.data.repository.home.FakeHomeRepository
import com.skydoves.pokedex.compose.core.designsystem.component.PokedexAppBar
import com.skydoves.pokedex.compose.core.designsystem.component.PokedexCircularProgress
import com.skydoves.pokedex.compose.core.designsystem.component.pokedexSharedElement
import com.skydoves.pokedex.compose.core.designsystem.theme.PokedexTheme
import com.skydoves.pokedex.compose.core.model.Pokemon
import com.skydoves.pokedex.compose.core.navigation.PokedexScreen
import com.skydoves.pokedex.compose.core.navigation.boundsTransform
import com.skydoves.pokedex.compose.core.navigation.currentComposeNavigator
import com.skydoves.pokedex.compose.core.preview.PokedexPreviewTheme
import com.skydoves.pokedex.compose.core.preview.PreviewUtils
import com.skydoves.pokedex.compose.core.viewmodel.LocalPokedexViewModelFactory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun PokedexHome(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeViewModel: HomeViewModel = viewModel(factory = LocalPokedexViewModelFactory.current),
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val pokemonList by homeViewModel.pokemonList.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        PokedexAppBar()

        HomeContent(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            uiState = uiState,
            pokemonList = pokemonList.toImmutableList(),
            fetchNextPokemonList = homeViewModel::fetchNextPokemonList,
        )
    }
}

@Composable
private fun HomeContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    uiState: HomeUiState,
    pokemonList: ImmutableList<Pokemon>,
    fetchNextPokemonList: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val gridState = rememberLazyGridState()
        LaunchedEffect(gridState, pokemonList) {
            val paginationThreshold = pokemonList.size - PaginationBufferSize
            snapshotFlow { gridState.firstVisibleItemIndex >= paginationThreshold }
                .collect { shouldFetchNewItems ->
                    if (shouldFetchNewItems) {
                        fetchNextPokemonList()
                    }
                }
        }
        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier.testTag("PokedexList"),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(6.dp),
        ) {
            items(items = pokemonList, key = { pokemon -> pokemon.name }) { pokemon ->
                PokemonCard(
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    pokemon = pokemon,
                )
            }
        }

        if (uiState == HomeUiState.Loading) {
            PokedexCircularProgress()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PokemonCard(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    pokemon: Pokemon,
) {
    val composeNavigator = currentComposeNavigator
    val palette by remember { mutableStateOf<Palette?>(null) }
    val backgroundColor by palette.paletteBackgroundColor()

    Card(
        modifier =
            Modifier.padding(6.dp).fillMaxWidth().testTag("Pokemon").clickable {
                composeNavigator.navigate(PokedexScreen.Details(pokemon = pokemon))
            },
        shape = RoundedCornerShape(14.dp),
        colors =
            CardColors(
                containerColor = backgroundColor,
                contentColor = backgroundColor,
                disabledContainerColor = backgroundColor,
                disabledContentColor = backgroundColor,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        PokemonCardImage(
            modifier =
                Modifier.align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
                    .size(120.dp)
                    .pokedexSharedElement(
                        sharedTransitionScope = sharedTransitionScope,
                        isLocalInspectionMode = LocalInspectionMode.current,
                        state =
                            sharedTransitionScope.rememberSharedContentState(
                                key = "image-${pokemon.name}"
                            ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = boundsTransform,
                    ),
            pokemon = pokemon
        )

        Text(
            modifier =
                Modifier.align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .pokedexSharedElement(
                        sharedTransitionScope = sharedTransitionScope,
                        isLocalInspectionMode = LocalInspectionMode.current,
                        state =
                            sharedTransitionScope.rememberSharedContentState(
                                key = "name-${pokemon.name}"
                            ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = boundsTransform,
                    )
                    .padding(12.dp),
            text = pokemon.name,
            color = PokedexTheme.colors.black,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
private fun PokemonCardImage(pokemon: Pokemon, modifier: Modifier = Modifier) {
    if (PokedexFeatureFlags.UseCoil) {
        AsyncImage(
            modifier = modifier,
            contentDescription = pokemon.name,
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(pokemon.imageUrl)
                    .crossfade(PokemonCardImageCrossfadeDurationMillis)
                    .build(),
            contentScale = ContentScale.Inside,
            placeholder = painterResource(id = R.drawable.pokemon_preview),
        )
    } else {
        GlideImage(
            modifier = modifier,
            contentDescription = pokemon.name,
            model = pokemon.imageUrl,
            contentScale = ContentScale.Inside,
            transition = CrossFade(tween(PokemonCardImageCrossfadeDurationMillis)),
            loading = placeholder(painterResource(id = R.drawable.pokemon_preview)),
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PokedexHomePreview() {
    PokedexTheme {
        SharedTransitionScope { modifier ->
            AnimatedVisibility(modifier = modifier, visible = true, label = "") {
                PokedexHome(
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionScope,
                    homeViewModel =
                        viewModel { HomeViewModel(homeRepository = FakeHomeRepository()) }
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentPreview() {
    PokedexPreviewTheme { scope ->
        val homeViewModel = viewModel { HomeViewModel(homeRepository = FakeHomeRepository()) }
        HomeContent(
            animatedVisibilityScope = scope,
            sharedTransitionScope = this@PokedexPreviewTheme,
            uiState = HomeUiState.Idle,
            pokemonList = PreviewUtils.mockPokemonList().toImmutableList(),
            fetchNextPokemonList = { homeViewModel.fetchNextPokemonList() },
        )
    }
}

private const val PaginationBufferSize = 14
private const val PokemonCardImageCrossfadeDurationMillis = 250
