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

package com.skydoves.pokedex.compose.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.runtime.serialization.NavKeySerializer

@Composable
fun rememberPokedexComposeNavigator(startDestination: PokedexScreen): PokedexComposeNavigator {
    // rememberNavBackStack doesn't accept a generic nav key type, so we have our own small
    // version of it that gives us a NavBackStack<PokedexScreen>.
    val backStack: NavBackStack<PokedexScreen> =
        rememberSerializable(serializer = NavBackStackSerializer(NavKeySerializer())) {
            NavBackStack(startDestination)
        }
    return remember(backStack) { PokedexComposeNavigator(backStack) }
}

class PokedexComposeNavigator(override val backStack: NavBackStack<PokedexScreen>) :
    AppComposeNavigator<PokedexScreen>() {

    override fun navigate(route: PokedexScreen) {
        backStack.add(route)
    }

    override fun navigateAndClearBackStack(route: PokedexScreen) {
        backStack.clear()
        backStack.add(route)
    }

    override fun popUpTo(route: PokedexScreen, inclusive: Boolean) {
        val index = backStack.indexOf(route)
        if (index != -1) {
            val fromIndex = if (inclusive) index else index + 1
            while (backStack.size > fromIndex) {
                backStack.removeAt(backStack.lastIndex)
            }
        }
    }

    override fun navigateUp() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
}
