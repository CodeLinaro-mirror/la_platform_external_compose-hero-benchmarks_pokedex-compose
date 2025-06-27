/*
 * Copyright 2025 The Android Open Source Project
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

package com.skydoves.pokedex.compose.core.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable @Serializable class PokemonNetworkModel(val name: String)

fun fakePokemonNetworkModels() = FakeRandomizedNames.map { name -> PokemonNetworkModel(name) }

val FakePokemonNames =
    listOf(
        "Jason",
        "Jack",
        "Anna",
        "Bubir",
        "Xanto",
        "Vistesia",
        "Ulint-y",
        "Lapesareba",
        "Nemo",
        "Masurap"
    )
val FakeRandomizedNames by lazy {
    FakePokemonNames.flatMap { name ->
        val nameChars = name.toCharArray()
        val shuffledChars = nameChars.apply { shuffle() }
        listOf(
            shuffledChars.joinToString(""),
            shuffledChars.apply { reverse() }.joinToString(""),
            nameChars.apply { reverse() }.joinToString("")
        )
    }
}
