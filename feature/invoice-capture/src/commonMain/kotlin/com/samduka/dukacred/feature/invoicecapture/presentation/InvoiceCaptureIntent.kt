// commonMain/.../presentation/InvoiceCaptureIntent.kt
package com.samduka.dukacred.feature.invoicecapture.presentation

sealed interface InvoiceCaptureIntent {
    // Manual shutter
    data object TakePictureClicked    : InvoiceCaptureIntent

    // Image delivery
    data class  ImageCaptured(val imageBytes: ByteArray) : InvoiceCaptureIntent
    data object CaptureFailed         : InvoiceCaptureIntent

    // User actions on review screen
    data object RetakeClicked         : InvoiceCaptureIntent
    data object ConfirmInvoiceClicked : InvoiceCaptureIntent

    // Auto-capture signals from the Screen
    data object AutoCaptureReady      : InvoiceCaptureIntent  // all conditions met
    data object AutoCaptureCancelled  : InvoiceCaptureIntent  // a condition was lost
}