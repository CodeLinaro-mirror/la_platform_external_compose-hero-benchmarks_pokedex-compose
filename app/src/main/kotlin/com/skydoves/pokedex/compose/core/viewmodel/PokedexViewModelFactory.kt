/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.pokedex.compose.core.viewmodel

import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skydoves.pokedex.compose.core.database.di.DatabaseModule
import com.skydoves.pokedex.compose.core.di.RepositoryModule
import com.skydoves.pokedex.compose.core.network.di.DispatchersModule
import com.skydoves.pokedex.compose.core.network.di.NetworkModule
import com.skydoves.pokedex.compose.core.network.di.SerializationModule
import com.skydoves.pokedex.compose.feature.details.DetailsViewModel
import com.skydoves.pokedex.compose.feature.home.HomeViewModel

val LocalPokedexViewModelFactory = compositionLocalWithComputedDefaultOf {
    val serializationModule = SerializationModule()
    val networkModule = NetworkModule(serializationModule.json)
    val databaseModule = DatabaseModule(LocalContext.currentValue, serializationModule.json)
    val dispatchersModule = DispatchersModule()
    val repositoryModule =
        RepositoryModule(
            networkModule.pokedexClient,
            databaseModule.pokemonDao,
            databaseModule.pokemonInfoDao,
            dispatchersModule.io
        )
    PokedexViewModelFactory(repositoryModule)
}

fun PokedexViewModelFactory(repositoryModule: RepositoryModule) = viewModelFactory {
    initializer { DetailsViewModel(repositoryModule.detailsRepository, createSavedStateHandle()) }
    initializer { HomeViewModel(repositoryModule.homeRepository) }
}
