
package com.samduka.dukacred.feature.invoicecapture.presentation.ui

/** Transient post-capture feedback shown on top of the preview. */
sealed interface CaptureWarning {
    data object LowLight : CaptureWarning
    data object Shaking  : CaptureWarning   // reserved for future real-time use
}