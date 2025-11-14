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

package com.skydoves.pokedex.compose.core

/** Contains feature flags for the Pokedex hero benchmark target */
object PokedexFeatureFlags {
    /**
     * Whether to configure and use Coil for image loading of all images, or Glide if false. Please
     * note that Glide will always be configured.
     */
    var UseCoil = true

    /**
     * Whether [androidx.compose.animation.SharedTransitionScope] should be used or replaced by
     * simpler layouts instead. If false, shared element transitions will be off too.
     */
    var EnableSharedTransitionScope = true

    /**
     * Whether to enable shared element transitions between the activities.
     * [EnableSharedTransitionScope] must be set to true, otherwise this flag will be false.
     */
    var EnableSharedElementTransitions = true
        get() = EnableSharedTransitionScope && field

    /**
     * Whether to fetch pokemon images from disk instead of the network. Requires storing images in
     * the app's files directory before the startup, e.g. through PokedexSetupActivity.
     *
     * Disabling this flag means loading images from the network. If using the local mock web
     * server, that means generating the gradient images on the fly, which can incur significant
     * performance impact. Disable the flag to simulate a worse performance of the app.
     */
    var FetchPokemonImagesFromDisk = true
}
