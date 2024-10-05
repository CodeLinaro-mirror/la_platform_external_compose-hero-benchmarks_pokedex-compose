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

package com.skydoves.pokedex.compose.core.data.repository.details

import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.skydoves.pokedex.compose.core.database.PokemonInfoDao
import com.skydoves.pokedex.compose.core.database.entitiy.mapper.asDomain
import com.skydoves.pokedex.compose.core.database.entitiy.mapper.asEntity
import com.skydoves.pokedex.compose.core.network.Dispatcher
import com.skydoves.pokedex.compose.core.network.PokedexAppDispatchers
import com.skydoves.pokedex.compose.core.network.service.PokedexClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

@VisibleForTesting
class DetailsRepositoryImpl @Inject constructor(
  private val pokedexClient: PokedexClient,
  private val pokemonInfoDao: PokemonInfoDao,
  @Dispatcher(PokedexAppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : DetailsRepository {

  @WorkerThread
  override fun fetchPokemonInfo(name: String, onComplete: () -> Unit, onError: (String?) -> Unit) =
    flow {
      val pokemonInfo = pokemonInfoDao.getPokemonInfo(name)
      if (pokemonInfo == null) {
        val response = pokedexClient.fetchPokemonInfo(name = name)
        response
          .onSuccess { data ->
            pokemonInfoDao.insertPokemonInfo(data.asEntity())
            emit(data)
          }
          .onFailure { throwable ->
            onError(throwable.message)
          }
      } else {
        emit(pokemonInfo.asDomain())
      }
    }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}
