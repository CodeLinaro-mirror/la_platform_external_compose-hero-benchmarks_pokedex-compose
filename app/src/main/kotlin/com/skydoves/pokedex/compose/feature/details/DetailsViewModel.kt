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

package com.skydoves.pokedex.compose.feature.details

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.skydoves.pokedex.compose.core.data.repository.details.DetailsRepository
import com.skydoves.pokedex.compose.core.model.PokemonInfo
import com.skydoves.pokedex.compose.core.viewmodel.BaseViewModel
import com.skydoves.pokedex.compose.core.viewmodel.ViewModelStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class DetailsViewModel(detailsRepository: DetailsRepository, savedStateHandle: SavedStateHandle) :
    BaseViewModel() {

    internal val uiState: ViewModelStateFlow<DetailsUiState> =
        viewModelStateFlow(DetailsUiState.Loading)

    val pokemonName = savedStateHandle.getStateFlow<String?>("name", null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val pokemonInfo: StateFlow<PokemonInfo?> =
        pokemonName
            .filterNotNull()
            .flatMapLatest { pokemon ->
                detailsRepository.fetchPokemonInfo(
                    name = pokemon.replaceFirstChar { it.lowercase() },
                    onComplete = { uiState.tryEmit(key, DetailsUiState.Idle) },
                    onError = { uiState.tryEmit(key, DetailsUiState.Error(it)) },
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )
}

@Stable
internal sealed interface DetailsUiState {

    object Idle : DetailsUiState

    object Loading : DetailsUiState

    data class Error(val message: String?) : DetailsUiState
}
