// androidMain
package com.samduka.dukacred.feature.invoicecapture.sensor

import android.content.Context
import android.hardware.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

@Composable
actual fun rememberIsShaking(thresholdG: Float): State<Boolean> {
    val context = LocalContext.current
    val isShaking = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(s: Sensor?, a: Int) = Unit
            override fun onSensorChanged(event: SensorEvent) {
                val (x, y, z) = event.values
                // Remove gravity (~9.8) with a rough high-pass, compare magnitude
                val magnitude = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH
                isShaking.value = magnitude > thresholdG
            }
        }
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { manager.unregisterListener(listener) }
    }

    return isShaking
}