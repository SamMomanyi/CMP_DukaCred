
package com.samduka.dukacred.feature.invoicecapture.util

/**
 * Samples every 4th byte (R channel of RGBA) across the image.
 * Returns 0.0 (black) → 1.0 (white). Threshold: < 0.25 = too dark.
 */

fun analyzeBrightness(imageBytes: ByteArray): Float {
    if (imageBytes.isEmpty()) return 1f
    var sum = 0L
    var count = 0
    var i = 0
    while (i < imageBytes.size) {
        sum += (imageBytes[i].toInt() and 0xFF)
        count++
        i += 16 // sample 1 in every 16 bytes — fast enough for validation
    }
    return if (count == 0) 1f else (sum.toFloat() / count) / 255f
}

const val BRIGHTNESS_THRESHOLD = 0.25f