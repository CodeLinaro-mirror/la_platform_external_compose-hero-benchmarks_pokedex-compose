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

package com.skydoves.pokedex.compose.core.designsystem.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.tracing.Trace
import kotlin.random.Random

/**
 * Trace the lifetime of this composable with an async trace block. Begins tracing when added to
 * composition and ends tracing when removed. This is useful for tracking how long a composable is
 * in a certain state, e.g. animations.
 */
@Composable
fun TraceAsync(methodName: String) {
    DisposableEffect(methodName) {
        val tracingCookie = Random.nextInt()
        Trace.beginAsyncSection(methodName, tracingCookie)
        onDispose { Trace.endAsyncSection(methodName, tracingCookie) }
    }
}
