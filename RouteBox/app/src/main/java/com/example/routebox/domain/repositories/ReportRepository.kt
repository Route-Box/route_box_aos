package com.example.routebox.domain.repositories

import com.example.routebox.domain.model.ReportId
import com.example.routebox.domain.model.ReportRoute
import com.example.routebox.domain.model.ReportUser
import com.example.routebox.domain.model.RouteReportId

interface ReportRepository {
    /** 사용자 신고 */
    suspend fun reportUser(
        reportUserBody: ReportUser
    ): ReportId

    /** 루트 신고 */
    suspend fun reportRoute(
        reportRouteBody: ReportRoute
    ): RouteReportId
}