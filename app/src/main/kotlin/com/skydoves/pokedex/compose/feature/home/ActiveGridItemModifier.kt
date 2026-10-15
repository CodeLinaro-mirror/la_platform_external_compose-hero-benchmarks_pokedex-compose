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

package com.skydoves.pokedex.compose.feature.home

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.tracing.Trace
import com.skydoves.pokedex.compose.core.PokedexFeatureFlags

/**
 * Tracks the number of active grid items via the `pokedex:grid:items:active` trace counter when
 * [PokedexFeatureFlags.TrackActiveGridItems] is enabled.
 *
 * We track this to compare the number of active items held by Compose vs Views. This lets us verify
 * the benchmarks are structurally equivalent.
 *
 * This uses a [Modifier.Node] rather than [androidx.compose.runtime.RememberObserver] because
 * [Modifier.Node] adds less overhead in this case.
 */
internal fun Modifier.trackActiveGridItem(pokemonName: String): Modifier =
    if (PokedexFeatureFlags.TrackActiveGridItems) {
        this.then(ActiveGridItemElement(pokemonName))
    } else {
        this
    }

// Do not override inspectableProperties: this is a metric measurement code path that should not
// add any overhead.
@Suppress("ModifierNodeInspectableProperties")
private data class ActiveGridItemElement(private val pokemonName: String) :
    ModifierNodeElement<ActiveGridItemNode>() {
    override fun create(): ActiveGridItemNode = ActiveGridItemNode(pokemonName)

    override fun update(node: ActiveGridItemNode) {
        node.pokemonName = pokemonName
    }
}

private class ActiveGridItemNode(var pokemonName: String) : Modifier.Node() {
    override val shouldAutoInvalidate: Boolean
        get() = false

    override fun onAttach() {
        activeItems++
        Trace.setCounter(ACTIVE_GRID_ITEMS_COUNTER_NAME, activeItems)
    }

    override fun onDetach() {
        activeItems--
        Trace.setCounter(ACTIVE_GRID_ITEMS_COUNTER_NAME, activeItems)
    }

    private companion object {
        const val ACTIVE_GRID_ITEMS_COUNTER_NAME = "pokedex:grid:items:active"
        var activeItems = 0
    }
}
