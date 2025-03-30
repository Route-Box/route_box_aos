package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.ReportCommentRequest
import com.daval.routebox.domain.model.ReportId
import com.daval.routebox.domain.model.ReportRoute
import com.daval.routebox.domain.model.ReportUser
import com.daval.routebox.domain.model.RouteReportId
import com.daval.routebox.domain.repositories.ReportRepository
import com.daval.routebox.data.datasource.RemoteReportDataSource
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val remoteReportDataSource: RemoteReportDataSource
): ReportRepository {
    override suspend fun reportUser(reportUserBody: ReportUser): ReportId {
        return remoteReportDataSource.reportUser(reportUserBody)
    }

    override suspend fun reportRoute(reportRouteBody: ReportRoute): RouteReportId {
        return remoteReportDataSource.reportRoute(reportRouteBody)
    }

    override suspend fun reportComment(reportCommentBody: ReportCommentRequest): BaseResponse {
        return remoteReportDataSource.reportComment(reportCommentBody)
    }
}