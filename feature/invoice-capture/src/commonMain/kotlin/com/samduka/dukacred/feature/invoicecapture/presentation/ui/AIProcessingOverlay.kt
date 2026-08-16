// presentation/ui/AIProcessingOverlay.kt — NOT wired into this flow, see flag above
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.components.dukaSkeletonBrush
import com.samduka.dukacred.core.designsystem.theme.dukaShapes

enum class AIProcessingStage(val title: String, val subtitle: String) {
    SECURING_RECEIPT("Securing Receipt", "Encrypting document for processing…"),
    READING_SUPPLIER("Reading Supplier Data", "Detecting KRA ETR serial & merchant details…"),
    ANALYZING_INVOICE("Analyzing Invoice", "Extracting line items…"),
}

@Composable
fun AIProcessingOverlay(stage: AIProcessingStage, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stage.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(stage.subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
            Spacer(Modifier.height(28.dp))

            Crossfade(targetState = stage, label = "ai_processing_stage") { current ->
                when (current) {
                    AIProcessingStage.SECURING_RECEIPT -> SecuringReceiptGlyph()
                    AIProcessingStage.READING_SUPPLIER -> DocumentSkeleton(fullDetail = false)
                    AIProcessingStage.ANALYZING_INVOICE -> DocumentSkeleton(fullDetail = true)
                }
            }

            Spacer(Modifier.height(28.dp))
            SecureProcessingPill()
        }
    }
}

@Composable
private fun SecuringReceiptGlyph() {
    val transition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow_alpha",
    )
    Box(
        modifier = Modifier.size(160.dp).clip(RoundedCornerShape(28.dp)).background(Color(0xFF013220).copy(alpha = glowAlpha)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(96.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF013220)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Description, contentDescription = null, tint = Color(0xFFA2D1B7), modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun DocumentSkeleton(fullDetail: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Box(Modifier.width(160.dp).height(14.dp).dukaSkeletonBrush())
                Spacer(Modifier.height(6.dp))
                Box(Modifier.width(90.dp).height(10.dp).dukaSkeletonBrush())
            }
            if (fullDetail) Box(Modifier.size(40.dp).dukaSkeletonBrush(shape = RoundedCornerShape(8.dp)))
        }
        Spacer(Modifier.height(16.dp))
        if (fullDetail) {
            repeat(4) { index ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Box(Modifier.width((140 - index * 10).dp).height(12.dp).dukaSkeletonBrush())
                        Spacer(Modifier.height(4.dp))
                        Box(Modifier.width(60.dp).height(8.dp).dukaSkeletonBrush())
                    }
                    Box(Modifier.width(70.dp).height(12.dp).dukaSkeletonBrush())
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Box(Modifier.width(90.dp).height(16.dp).dukaSkeletonBrush())
            }
        } else {
            repeat(2) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(Modifier.width(90.dp).height(10.dp).dukaSkeletonBrush())
                    Box(Modifier.width(60.dp).height(10.dp).dukaSkeletonBrush())
                }
            }
        }
    }
}

@Composable
private fun SecureProcessingPill() {
    Surface(
        shape = MaterialTheme.dukaShapes.full,
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color(0xFFA2D1B7), modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text("SECURE PROCESSING", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
        }
    }
}