package com.samduka.dukacred.feature.invoicecapture.sensor


import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * Returns a State<Boolean> — true when the device is shaking.
 * Platform actuals wire up accelerometer; stub returns false on Desktop/iOS-stub.
 */
@Composable
expect fun rememberIsShaking(thresholdG: Float = 1.2f): State<Boolean>