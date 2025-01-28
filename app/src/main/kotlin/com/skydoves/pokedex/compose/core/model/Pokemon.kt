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

package com.skydoves.pokedex.compose.core.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.HttpUrl

@SuppressLint("BanParcelableUsage") // TODO(b/374318532): Migrate to VersionedParcelable
@Immutable
@Serializable
data class Pokemon(
    var page: Int = 0,
    @SerialName(value = "name") val nameField: String,
    @SerialName(value = "url") val url: String,
    val imageUrl: String = imageUrlFromPokemonInfoUrl(url)
) : Parcelable {

    val name: String
        get() = nameField.replaceFirstChar { it.uppercase() }

    constructor(
        parcel: Parcel
    ) : this(parcel.readInt(), parcel.readString()!!, parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(page)
        parcel.writeString(nameField)
        parcel.writeString(url)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Pokemon> {
        override fun createFromParcel(parcel: Parcel) = Pokemon(parcel)

        override fun newArray(size: Int): Array<Pokemon?> = arrayOfNulls(size)
    }
}

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

/**
 * Create a list of [Pokemon] with fake names and corresponding URLs.
 *
 * @param apiBaseUrl The base URL of the API to fetch data from
 */
fun fakePokemons(apiBaseUrl: HttpUrl): List<Pokemon> =
    FakeRandomizedNames.mapIndexed { index, name ->
        Pokemon(nameField = name, url = "${apiBaseUrl}pokemon/${index}/")
    }

/**
 * Derive the URL for a Pokemon's image from the Pokemon's URL
 *
 * For example, `https://localhost:3000/api/v2/pokemon/0` would become:
 * `https://localhost:3000/api/v2/pokemon/0/image`
 *
 * @param pokemonInfoUrl The URL of the pokemon details
 * @return A string with the URL for the image
 */
private fun imageUrlFromPokemonInfoUrl(pokemonInfoUrl: String): String {
    val separator = if (pokemonInfoUrl.endsWith("/")) "" else "/"
    return "${pokemonInfoUrl}${separator}image"
}
