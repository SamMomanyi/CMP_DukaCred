package com.samduka.dukacred.core.domain.model

enum class SupplierTrustTier { KNOWN, NEW, FLAGGED }

data class Supplier(
    val id: SupplierId,
    val name: String,
    val tillNumber: String?,
    val paybillNumber: String?,
    val trustTier: SupplierTrustTier = SupplierTrustTier.KNOWN,
)