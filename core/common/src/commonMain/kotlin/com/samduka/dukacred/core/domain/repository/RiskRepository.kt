package com.samduka.dukacred.core.domain.repository

import com.samduka.dukacred.core.common.error.RiskPolicyError
import com.samduka.dukacred.core.common.result.AppResult
import com.samduka.dukacred.core.domain.model.RiskAssessment
import com.samduka.dukacred.core.domain.model.RequestId

interface RiskRepository {
    suspend fun assess(requestId: RequestId): AppResult<RiskAssessment, RiskPolicyError>
}