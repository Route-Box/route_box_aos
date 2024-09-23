package com.daval.routebox.data.repositoriyImpl

import com.example.routebox.data.datasource.RemoteReportDataSource
import com.example.routebox.domain.model.ReportId
import com.example.routebox.domain.model.ReportRoute
import com.example.routebox.domain.model.ReportUser
import com.example.routebox.domain.model.RouteReportId
import com.example.routebox.domain.repositories.ReportRepository
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

    override suspend fun reportRoute(reportRouteBody: ReportRoute): RouteReportId {
        return remoteReportDataSource.reportRoute(reportRouteBody)
    }
}