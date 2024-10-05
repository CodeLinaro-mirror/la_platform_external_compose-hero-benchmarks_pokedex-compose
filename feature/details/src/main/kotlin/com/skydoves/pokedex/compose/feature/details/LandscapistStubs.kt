@file:Suppress("unused", "UNUSED_PARAMETER", "FunctionName")
package com.skydoves.pokedex.compose.feature.details

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
fun GlideImage(
    imageModel: () -> Any?,
    modifier: Modifier = Modifier,
    glideRequestType: Any = "GlideRequestType.DRAWABLE",
    requestBuilder: @Composable () -> Any = {
        "LocalGlideProvider.getGlideRequestBuilder()"
    },
    requestOptions: @Composable () -> Any = {
        "LocalGlideProvider.getGlideRequestOptions()"
    },
    requestListener: (() -> Any)? = null,
    component: Any = "rememberImageComponent {}",
    imageOptions: Any? = "ImageOptions()",
    clearTarget: Boolean = false,
    onImageStateChanged: (Any) -> Unit = {},
    previewPlaceholder: Painter? = null,
    loading: @Composable (BoxScope.(imageState: Any) -> Unit)? = null,
    success: @Composable (
    BoxScope.(
        imageState: Any,
        painter: Painter,
    ) -> Unit
    )? = null,
    failure: @Composable (BoxScope.(imageState: Any) -> Unit)? = null,
) {
    TODO("Stub for Landscapist composable")
}

fun ImageOptions(contentScale: ContentScale) {
    TODO("Stub for Landscapist composable")
}

class ImageComponentStubReceiver {
    fun CrossfadePlugin() {
        TODO("Stub for Landscapist composable")
    }

    fun ShimmerPlugin(param: Any) {
        TODO("Stub for Landscapist composable")
    }

    fun PalettePlugin(
        imageModel: Any?,
        useCache: Any,
        paletteLoadedListener: (Any) -> Unit
    ) {
        TODO("Stub for Landscapist composable")
    }

    object Shimmer {
        fun Resonate(baseColor: Any, highlightColor: Any) {
            TODO("Stub for Landscapist composable")
        }
    }

    operator fun Unit.unaryPlus() {
        TODO("Stub for Landscapist composable")
    }
}

fun rememberImageComponent(block: @Composable ImageComponentStubReceiver.() -> Unit) {
    TODO("Stub for Landscapist composable")
}