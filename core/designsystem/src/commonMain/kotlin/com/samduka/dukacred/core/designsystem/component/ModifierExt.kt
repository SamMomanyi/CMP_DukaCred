package com.samduka.dukacred.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.samduka.dukacred.core.designsystem.DukaCredColors

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateX by transition.animateFloat(
        initialValue = -2f * size.width,
        targetValue = 2f * size.width,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1400,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_translate_x",
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                DukaCredColors.ForestGreen800,
                DukaCredColors.ForestGreen700,
                DukaCredColors.ForestGreen800,
            ),
            start = Offset(x = translateX - size.width, y = 0f),
            end = Offset(x = translateX, y = size.height.toFloat()),
        ),
    ).onGloballyPositioned { coordinates ->
        size = coordinates.size
    }
}
