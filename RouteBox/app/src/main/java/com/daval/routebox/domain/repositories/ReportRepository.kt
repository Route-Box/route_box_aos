package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.ReportId
import com.daval.routebox.domain.model.ReportRoute
import com.daval.routebox.domain.model.ReportUser

interface ReportRepository {
    /** 사용자 신고 */
    suspend fun reportUser(
        reportUserBody: ReportUser
    ): ReportId

    /** 루트 신고 */
    suspend fun reportRoute(
        reportRouteBody: ReportRoute
    ): ReportId
}