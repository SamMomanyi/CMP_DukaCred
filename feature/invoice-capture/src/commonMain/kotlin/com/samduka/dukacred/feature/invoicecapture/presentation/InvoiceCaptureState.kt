package com.samduka.dukacred.feature.invoicecapture.presentation

import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice
import com.samduka.dukacred.feature.invoicecapture.domain.model.ParsedInvoice

/**
 * Represents the various states of the OCR scanning screen.
 */
sealed interface InvoiceCaptureState {
    // Waiting for the user to take a picture
    data object Idle : InvoiceCaptureState

    // Image captured, currently sending to AWS Bedrock/Gemini
    data object Scanning : InvoiceCaptureState

    // AI successfully read the invoice
    data class Success(val invoice: ParsedInvoice) : InvoiceCaptureState

    // Something went wrong (network error, blurry image)
    data class Error(val message: String) : InvoiceCaptureState
}