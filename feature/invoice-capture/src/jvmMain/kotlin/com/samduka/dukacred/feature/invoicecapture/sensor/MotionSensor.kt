package com.samduka.dukacred.feature.invoicecapture.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun rememberIsShaking(thresholdG: Float): State<Boolean> {
    // Lazy Day Stub: Desktop monitors don't have accelerometers
    return remember { mutableStateOf(false) }
}