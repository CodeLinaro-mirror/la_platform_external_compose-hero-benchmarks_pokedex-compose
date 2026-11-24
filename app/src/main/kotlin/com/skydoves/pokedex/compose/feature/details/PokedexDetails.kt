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

package com.skydoves.pokedex.compose.feature.details

import android.content.res.Configuration
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.nonInteractiveScrollbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.skydoves.pokedex.compose.R
import com.skydoves.pokedex.compose.core.PokedexFeatureFlags
import com.skydoves.pokedex.compose.core.data.repository.details.FakeDetailsRepository
import com.skydoves.pokedex.compose.core.database.entitiy.mapper.getPokemonImageUrlByName
import com.skydoves.pokedex.compose.core.designsystem.component.PokedexCircularProgress
import com.skydoves.pokedex.compose.core.designsystem.component.PokedexText
import com.skydoves.pokedex.compose.core.designsystem.component.pokedexSharedElement
import com.skydoves.pokedex.compose.core.designsystem.theme.PokedexTheme
import com.skydoves.pokedex.compose.core.designsystem.utils.getPokemonTypeColor
import com.skydoves.pokedex.compose.core.model.PokemonInfo
import com.skydoves.pokedex.compose.core.navigation.boundsTransform
import com.skydoves.pokedex.compose.core.navigation.currentComposeNavigator
import com.skydoves.pokedex.compose.core.network.di.ModuleLocator
import com.skydoves.pokedex.compose.core.preview.PokedexPreviewTheme
import com.skydoves.pokedex.compose.core.preview.PreviewUtils

@Composable
fun PokedexDetails(
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope,
    detailsViewModel: DetailsViewModel,
) {
    val uiState by detailsViewModel.uiState.collectAsStateWithLifecycle()
    val pokemonName by detailsViewModel.pokemonName.collectAsStateWithLifecycle()
    val pokemonInfo by detailsViewModel.pokemonInfo.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val scrollIndicatorState = scrollState.scrollIndicatorState
    val scrollbarModifier =
        if (PokedexFeatureFlags.EnableScrollbar && scrollIndicatorState != null) {
            Modifier.nonInteractiveScrollbar(
                state = scrollIndicatorState,
                orientation = Orientation.Vertical,
            )
        } else {
            Modifier
        }

    ReportDrawnWhen { uiState == DetailsUiState.Idle && pokemonInfo != null }

    Column(
        modifier =
            Modifier.fillMaxSize()
                .then(scrollbarModifier)
                .verticalScroll(scrollState)
                .testTag("PokedexDetails")
    ) {
        DetailsHeader(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            pokemonName = pokemonName,
            pokemonInfo = pokemonInfo,
        )
        if (sharedTransitionScope != null) {
            val statusText =
                "pokedex-details-transition-active-${sharedTransitionScope.isTransitionActive}"
            Text(statusText, Modifier.semantics { testTag = statusText })
        }

        if (uiState == DetailsUiState.Idle && pokemonInfo != null) {
            DetailsInfo(pokemonInfo = pokemonInfo!!)

            DetailsStatus(pokemonInfo = pokemonInfo!!)
        } else {
            Box(modifier = Modifier.fillMaxSize()) { PokedexCircularProgress() }
        }
    }
}

@Composable
private fun DetailsHeader(
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope,
    pokemonName: String?,
    pokemonInfo: PokemonInfo?,
) {
    val composeNavigator = currentComposeNavigator
    val palette by remember { mutableStateOf<Palette?>(null) }
    val shape =
        RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 64.dp, bottomEnd = 64.dp)

    val backgroundBrush by palette.paletteBackgroundBrush()

    Box(
        modifier =
            Modifier.fillMaxWidth()
                .height(290.dp)
                .shadow(elevation = 9.dp, shape = shape)
                .background(brush = backgroundBrush, shape = shape)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier =
                    Modifier.testTag("pokedexDetailsBack").padding(end = 6.dp).clickable {
                        composeNavigator.navigateUp()
                    },
                painter = painterResource(id = R.drawable.ic_arrow),
                tint = PokedexTheme.colors.absoluteBlack,
                contentDescription = null,
            )

            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = pokemonName.orEmpty(),
                color = PokedexTheme.colors.black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
        }

        PokedexText(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).statusBarsPadding(),
            text = pokemonInfo?.getIdString().orEmpty(),
            previewText = "#001",
            color = PokedexTheme.colors.black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        PokemonHeaderImage(
            pokemonName,
            modifier =
                Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .size(190.dp)
                    .then(
                        if (
                            sharedTransitionScope != null &&
                                PokedexFeatureFlags.EnableSharedElementTransitions
                        ) {
                            Modifier.pokedexSharedElement(
                                sharedTransitionScope = sharedTransitionScope,
                                isLocalInspectionMode = LocalInspectionMode.current,
                                state =
                                    sharedTransitionScope.rememberSharedContentState(
                                        key = "image-$pokemonName"
                                    ),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = boundsTransform,
                            )
                        } else Modifier
                    ),
        )
    }

    PokedexText(
        modifier =
            Modifier.padding(top = 24.dp)
                .fillMaxWidth()
                .then(
                    if (
                        sharedTransitionScope != null &&
                            PokedexFeatureFlags.EnableSharedElementTransitions
                    ) {
                        Modifier.pokedexSharedElement(
                            sharedTransitionScope = sharedTransitionScope,
                            isLocalInspectionMode = LocalInspectionMode.current,
                            state =
                                sharedTransitionScope.rememberSharedContentState(
                                    key = "name-$pokemonName"
                                ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform,
                        )
                    } else Modifier
                ),
        text = pokemonName.orEmpty(),
        previewText = "skydoves",
        color = PokedexTheme.colors.black,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontSize = 36.sp,
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PokemonHeaderImage(pokemonName: String?, modifier: Modifier) {
    val imageModel =
        if (pokemonName != null) {
            getPokemonImageUrlByName(
                    name = pokemonName,
                    apiUrl = ModuleLocator.networkModule.baseUrl,
                )
                .toString()
        } else null
    if (PokedexFeatureFlags.UseCoil) {
        AsyncImage(
            modifier = modifier,
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(imageModel)
                    .crossfade(PokemonHeaderImageCrossfadeDurationMillis)
                    .build(),
            contentDescription = pokemonName,
            contentScale = ContentScale.Inside,
            placeholder = painterResource(id = R.drawable.pokemon_preview),
        )
    } else {
        GlideImage(
            modifier = modifier,
            model = imageModel,
            contentScale = ContentScale.Inside,
            contentDescription = pokemonName,
            loading = placeholder(R.drawable.pokemon_preview),
            failure = placeholder(R.drawable.pokemon_preview),
        )
    }
}

@Composable
private fun DetailsInfo(pokemonInfo: PokemonInfo) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally),
    ) {
        pokemonInfo.types.forEach { typeInfo ->
            Text(
                modifier =
                    Modifier.background(
                            color = getPokemonTypeColor(type = typeInfo.type.name),
                            shape = RoundedCornerShape(64.dp),
                        )
                        .padding(horizontal = 40.dp, vertical = 4.dp),
                text = typeInfo.type.name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = PokedexTheme.colors.absoluteWhite,
                maxLines = 1,
                fontSize = 16.sp,
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        PokemonInfoItem(
            title = pokemonInfo.getWeightString(),
            content = stringResource(id = R.string.weight),
        )

        PokemonInfoItem(
            title = pokemonInfo.getHeightString(),
            content = stringResource(id = R.string.height),
        )
    }
}

@Composable
private fun DetailsStatus(pokemonInfo: PokemonInfo) {
    Text(
        modifier = Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 16.dp),
        text = stringResource(id = R.string.base_stats),
        textAlign = TextAlign.Center,
        color = PokedexTheme.colors.black,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
    )

    Column {
        pokemonInfo.toPokedexStatusList().forEach { pokemonStatus ->
            PokemonStatusItem(
                modifier = Modifier.padding(bottom = 12.dp),
                pokedexStatus = pokemonStatus,
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PokedexDetailsPreview() {
    PokedexPreviewTheme { animatedVisibilityScope ->
        PokedexDetails(
            sharedTransitionScope = this,
            animatedVisibilityScope = animatedVisibilityScope,
            detailsViewModel =
                viewModel {
                    DetailsViewModel(
                        detailsRepository = FakeDetailsRepository(),
                        savedStateHandle = SavedStateHandle(),
                    )
                },
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PokedexDetailsInfoPreview() {
    PokedexPreviewTheme { DetailsInfo(pokemonInfo = PreviewUtils.mockPokemonInfo()) }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PokedexDetailsStatusPreview() {
    PokedexPreviewTheme { DetailsStatus(pokemonInfo = PreviewUtils.mockPokemonInfo()) }
}

private const val PokemonHeaderImageCrossfadeDurationMillis = 250
