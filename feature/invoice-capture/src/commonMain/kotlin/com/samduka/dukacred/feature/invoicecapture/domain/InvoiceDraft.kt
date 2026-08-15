package com.samduka.dukacred.feature.invoicecapture.domain

/**
 * A merchant-reviewable invoice extracted from a receipt image.
 *
 * The persisted model already represents this shape; the alias makes the
 * capture workflow explicit without duplicating serialization or validation
 * models.
 */
typealias InvoiceDraft = ParsedInvoice
