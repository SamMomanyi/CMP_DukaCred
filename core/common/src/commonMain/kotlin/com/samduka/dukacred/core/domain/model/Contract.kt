package com.samduka.dukacred.core.domain.model

data class FinancingContract(
    val id: ContractId,
    val requestId: RequestId,
    val merchantId: MerchantId,
    val supplierId: SupplierId,
    val financedAmount: Money,
    val totalRepayable: Money,
    val status: ContractStatus,
    val disbursement: Disbursement?,
    val repaymentSchedule: RepaymentSchedule?,
    val createdAtEpochMs: Long,
    val dueDateEpochMs: Long,
)

data class Disbursement(
    val contractId: ContractId,
    val amount: Money,
    val recipientTillNumber: String,
    val status: DisbursementStatus,
    val sentAtEpochMs: Long? = null,
)

data class RepaymentSchedule(
    val contractId: ContractId,
    val totalAmount: Money,
    val installments: List<RepaymentInstallment>,
)

data class RepaymentInstallment(
    val dueEpochMs: Long,
    val amount: Money,
    val isPaid: Boolean = false,
    val paidAtEpochMs: Long? = null,
)