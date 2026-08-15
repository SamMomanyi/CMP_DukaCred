package com.samduka.dukacred.feature.invoicecapture.domain

/** Platform analyzer measurements normalized to the 0.0..1.0 range. */
data class FrameQualityMetrics(
    val lightingScore: Float,
    val sharpnessScore: Float,
    val textDensityScore: Float,
)

enum class CaptureGuidance {
    HOLD_STEADY,
    MOVE_CLOSER,
    LOW_LIGHT,
    READY,
}

data class FrameQualityResult(
    val shouldAutoCapture: Boolean,
    val guidance: CaptureGuidance,
)
