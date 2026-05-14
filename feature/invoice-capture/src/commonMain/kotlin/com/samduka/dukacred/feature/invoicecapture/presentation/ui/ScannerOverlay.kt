// feature/invoicecapture/src/commonMain/kotlin/.../ui/ScannerOverlay.kt
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp

private val CutoutColor    = Color(0xFFE5A93C)
private val OverlayColor   = Color(0x99000000)  // Black 60%
private val LaserColor     = Color(0xFFE5A93C)

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
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

    Canvas(modifier = modifier.fillMaxSize()) {
        val cutoutW = size.width  * 0.82f
        val cutoutH = size.height * 0.55f
        val cutoutL = (size.width  - cutoutW) / 2f
        val cutoutT = (size.height - cutoutH) / 2f
        val cornerR = 20.dp.toPx()

        val cutoutRect = RoundRect(
            left         = cutoutL,
            top          = cutoutT,
            right        = cutoutL + cutoutW,
            bottom       = cutoutT + cutoutH,
            cornerRadius = CornerRadius(cornerR)
        )

        // ── Dark overlay with punched-out cutout ──────────────────────
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = OverlayColor
                blendMode = BlendMode.SrcOver
            }
            // Fill the whole screen
            canvas.drawRect(Rect(Offset.Zero, size), paint)

            // Clear the cutout
            val clearPaint = Paint().apply {
                blendMode = BlendMode.Clear
            }
            canvas.drawRoundRect(
                left   = cutoutL, top    = cutoutT,
                right  = cutoutL + cutoutW, bottom = cutoutT + cutoutH,
                radiusX = cornerR, radiusY = cornerR,
                paint  = clearPaint
            )
        }

        // ── Glowing amber border ──────────────────────────────────────
        val path = Path().apply { addRoundRect(cutoutRect) }
        // outer soft glow
        drawPath(path, CutoutColor.copy(alpha = 0.3f), style = Stroke(width = 8.dp.toPx()))
        // crisp inner line
        drawPath(path, CutoutColor, style = Stroke(width = 2.dp.toPx()))

        // ── Laser line ───────────────────────────────────────────────
        val laserY = cutoutT + laserFraction * cutoutH
        val laserBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                LaserColor.copy(alpha = 0.9f),
                LaserColor,
                LaserColor.copy(alpha = 0.9f),
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