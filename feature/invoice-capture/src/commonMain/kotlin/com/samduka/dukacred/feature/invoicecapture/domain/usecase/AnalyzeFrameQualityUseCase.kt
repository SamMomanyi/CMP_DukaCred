package com.samduka.dukacred.feature.invoicecapture.domain.usecase

import com.samduka.dukacred.feature.invoicecapture.domain.CaptureGuidance
import com.samduka.dukacred.feature.invoicecapture.domain.FrameQualityMetrics
import com.samduka.dukacred.feature.invoicecapture.domain.FrameQualityResult

/** Converts platform camera/ML Kit measurements into a deterministic capture decision. */
class AnalyzeFrameQualityUseCase {
    operator fun invoke(metrics: FrameQualityMetrics): Result<FrameQualityResult> = runCatching {
        require(metrics.lightingScore in 0f..1f) { "lightingScore must be between 0 and 1" }
        require(metrics.sharpnessScore in 0f..1f) { "sharpnessScore must be between 0 and 1" }
        require(metrics.textDensityScore in 0f..1f) { "textDensityScore must be between 0 and 1" }

        val guidance = when {
            metrics.lightingScore < MIN_LIGHTING -> CaptureGuidance.LOW_LIGHT
            metrics.sharpnessScore < MIN_SHARPNESS -> CaptureGuidance.HOLD_STEADY
            metrics.textDensityScore < MIN_TEXT_DENSITY -> CaptureGuidance.MOVE_CLOSER
            else -> CaptureGuidance.READY
        }
        FrameQualityResult(shouldAutoCapture = guidance == CaptureGuidance.READY, guidance = guidance)
    }

    private companion object {
        const val MIN_LIGHTING = 0.45f
        const val MIN_SHARPNESS = 0.65f
        const val MIN_TEXT_DENSITY = 0.35f
    }
}
