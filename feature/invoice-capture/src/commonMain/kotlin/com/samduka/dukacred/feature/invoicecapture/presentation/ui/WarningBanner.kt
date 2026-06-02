// feature/invoicecapture/src/commonMain/kotlin/.../ui/WarningBanner.kt
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WarningBanner(warning: CaptureWarning?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible  = warning != null,
        enter    = slideInVertically { -it } + fadeIn(),
        exit     = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        val (bg, text) = when (warning) {
            CaptureWarning.LowLight -> Color(0xCC1A0000) to "⚠️  Low light detected — move to a brighter area and retake"
            CaptureWarning.Shaking  -> Color(0xCCD94F4F) to "📵  Hold phone steady"
            CaptureWarning.NoTextDetected ->
                Color(0xCC0D2B3E) to "🔍  Point at an invoice to begin scanning"
            null                    -> Color.Transparent to ""
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(bg, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}