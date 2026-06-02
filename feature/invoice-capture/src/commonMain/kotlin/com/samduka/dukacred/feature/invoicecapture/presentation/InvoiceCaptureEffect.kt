// commonMain/.../presentation/InvoiceCaptureEffect.kt
package com.samduka.dukacred.feature.invoicecapture.presentation

sealed interface InvoiceCaptureEffect {
    data object TriggerCapture      : InvoiceCaptureEffect
    data object NavigateToDashboard : InvoiceCaptureEffect
}