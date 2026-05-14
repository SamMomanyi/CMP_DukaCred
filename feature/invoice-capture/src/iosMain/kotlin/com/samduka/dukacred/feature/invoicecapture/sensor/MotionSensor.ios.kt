package com.samduka.dukacred.feature.invoicecapture.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

// Stub — returns false (never shaking) until CMMotionManager is wired.
// To implement: inject CMMotionManager, call startAccelerometerUpdates,
// read CMAccelerometerData.acceleration, compute magnitude vs thresholdG,
// update the mutableStateOf inside a coroutine loop.
@Composable
actual fun rememberIsShaking(thresholdG: Float): State<Boolean> =
    remember { mutableStateOf(false) }