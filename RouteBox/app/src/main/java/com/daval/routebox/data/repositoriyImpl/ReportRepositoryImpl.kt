package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.data.datasource.RemoteReportDataSource
import com.daval.routebox.domain.model.ReportId
import com.daval.routebox.domain.model.ReportRoute
import com.daval.routebox.domain.model.ReportUser
import com.daval.routebox.domain.repositories.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val remoteReportDataSource: RemoteReportDataSource
): ReportRepository {
    override suspend fun reportUser(reportUserBody: ReportUser): ReportId {
        return remoteReportDataSource.reportUser(reportUserBody)
    }

    override suspend fun reportRoute(reportRouteBody: ReportRoute): ReportId {
        return remoteReportDataSource.reportRoute(reportRouteBody)
    }
}