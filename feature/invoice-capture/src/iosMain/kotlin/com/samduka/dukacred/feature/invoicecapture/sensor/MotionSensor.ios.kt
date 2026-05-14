package com.samduka.dukacred.feature.invoicecapture.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

// iosMain — stub; wire CMMotionManager here when ready
@Composable
actual fun rememberIsShaking(thresholdG: Float): State<Boolean> =
    remember { mutableStateOf(false) }