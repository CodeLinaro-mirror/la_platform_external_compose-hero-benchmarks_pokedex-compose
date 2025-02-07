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

package com.skydoves.pokedex.compose.core.network.service

import com.skydoves.pokedex.compose.core.model.FakeRandomizedNames
import com.skydoves.pokedex.compose.core.model.fakePokemonInfo
import com.skydoves.pokedex.compose.core.network.model.fakePokemonResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.intellij.lang.annotations.Language

/**
 * A [okhttp3.mockwebserver.MockWebServer] with a [Dispatcher] that sends responses with fake data
 * for our API.
 */
fun pokedexMockWebServer(json: Json) =
    MockWebServer().apply {
        val pokemonEndpointRegex = Regex(PokemonEndpointPattern)
        val pokemonInfoEndpointRegex = Regex(PokemonInfoEndpointPattern)
        dispatcher =
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    val requestPath = request.path
                    if (requestPath == null) return MockResponse().setResponseCode(404)
                    return when {
                        pokemonEndpointRegex.matches(requestPath) -> {
                            val mockWebServerUrl = this@apply.url("/api/v2/")
                            val responseData = fakePokemonResponse(mockWebServerUrl)
                            MockResponse()
                                .setResponseCode(200)
                                .setBody(json.encodeToString(responseData))
                        }
                        pokemonInfoEndpointRegex.matches(requestPath) -> {
                            val requestUrl = request.requestUrl
                            if (requestUrl == null) return MockResponse().setResponseCode(404)
                            val pokemonName = requestUrl.pathSegments.last()
                            val fakePokemonInfo =
                                json.encodeToString(
                                    fakePokemonInfo(
                                        id = FakeRandomizedNames.indexOf(pokemonName),
                                        name = pokemonName
                                    )
                                )
                            return MockResponse().setResponseCode(200).setBody(fakePokemonInfo)
                        }
                        else -> MockResponse().setResponseCode(404)
                    }
                }
            }
    }

@Language("RegExp") private const val PokemonEndpointPattern = "/api/v2/pokemon(\\?(?<query>(.*)))"

@Language("RegExp")
private const val PokemonInfoEndpointPattern = "/api/v2/pokemon/(?<name>\\w*)(/?)"
