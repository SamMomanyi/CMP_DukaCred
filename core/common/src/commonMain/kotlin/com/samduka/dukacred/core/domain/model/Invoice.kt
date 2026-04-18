package com.samduka.dukacred.core.domain.model

data class Merchant(
    val id: MerchantId,
    val name: String,
    val phoneNumber: String,
    val businessName: String,
    val location: String,
    val averageDailySalesKes: Double,
    val hasOverdueContracts: Boolean = false,
    val activeContractCount: Int = 0,
)