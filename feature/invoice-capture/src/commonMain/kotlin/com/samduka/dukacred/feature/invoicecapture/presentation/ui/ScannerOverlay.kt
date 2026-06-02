package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    isDocumentDetected: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserFraction"
    )

    // Turn green when an invoice is perfectly aligned and lit
    val activeColor by animateColorAsState(
        targetValue = if (isDocumentDetected) Color(0xFF4CAF50) else Color(0xFFE5A93C),
        animationSpec = tween(durationMillis = 400),
        label = "cutout_color",
    )
    val overlayColor = Color(0x99000000) // 60% Black

    Canvas(
        modifier = modifier
            .fillMaxSize()
            // Isolates the BlendMode.Clear to just this layer to prevent black screen bug
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    ) {
        val cutoutW = size.width  * 0.90f
        val cutoutH = size.height * 0.72f

        val cutoutL = (size.width  - cutoutW) / 2f
        val cutoutT = (size.height - cutoutH) / 2f
        val cornerR = 20.dp.toPx()

        // 1. Draw the dark transparent scrim over the entire screen
        drawRect(color = overlayColor)

        // 2. Punch out the clear hole for the camera to show through
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(cutoutL, cutoutT),
            size = Size(cutoutW, cutoutH),
            cornerRadius = CornerRadius(cornerR),
            blendMode = BlendMode.Clear
        )

        // 3. Draw the glowing border around the cutout using the ANIMATED activeColor
        drawRoundRect(
            color = activeColor.copy(alpha = 0.3f),
            topLeft = Offset(cutoutL, cutoutT),
            size = Size(cutoutW, cutoutH),
            cornerRadius = CornerRadius(cornerR),
            style = Stroke(width = 8.dp.toPx())
        )
        drawRoundRect(
            color = activeColor,
            topLeft = Offset(cutoutL, cutoutT),
            size = Size(cutoutW, cutoutH),
            cornerRadius = CornerRadius(cornerR),
            style = Stroke(width = 2.dp.toPx())
        )

        // 4. Draw the animated scanning laser using the ANIMATED activeColor
        val laserY = cutoutT + laserFraction * cutoutH
        val laserBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                activeColor.copy(alpha = 0.9f),
                activeColor,
                activeColor.copy(alpha = 0.9f),
                Color.Transparent,
            ),
            startX = cutoutL,
            endX   = cutoutL + cutoutW
        )
        drawLine(
            brush       = laserBrush,
            start       = Offset(cutoutL, laserY),
            end         = Offset(cutoutL + cutoutW, laserY),
            strokeWidth = 2.5.dp.toPx(),
            cap         = StrokeCap.Round
        )
    }
}