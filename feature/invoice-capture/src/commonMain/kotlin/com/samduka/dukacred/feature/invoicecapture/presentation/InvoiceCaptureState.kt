package com.samduka.dukacred.feature.invoicecapture.presentation

import com.samduka.dukacred.feature.invoicecapture.domain.ParsedInvoice


/**
 * Represents the various states of the OCR scanning screen.
 */
sealed interface InvoiceCaptureState {
    data object Idle                                     : InvoiceCaptureState
    data class  AutoCapturing(val countdown: Int)        : InvoiceCaptureState
    data object Scanning                                 : InvoiceCaptureState
    data class  Success(val invoice: ParsedInvoice)      : InvoiceCaptureState
    data class  Error(val message: String)               : InvoiceCaptureState
}