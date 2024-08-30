package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.data.remote.ReportApiService
import com.example.routebox.domain.model.ReportId
import com.example.routebox.domain.model.ReportRoute
import com.example.routebox.domain.model.ReportUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteReportDataSource @Inject constructor(
    private val reportApiService: ReportApiService
) {
    suspend fun reportUser(
        reportUserBody: ReportUser
    ): ReportId {
        var reportId = ReportId(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                reportApiService.reportUser(reportUserBody)
            }.onSuccess {
                reportId = it
                Log.d("RemoteRouteDataSource", "reportUser Success\nreportId = ${reportId}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "reportUser Fail\ne = $e")
            }
        }

        return reportId
    }

    suspend fun reportRoute(
        reportRouteBody: ReportRoute
    ): ReportId {
        var reportId = ReportId(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                reportApiService.reportRoute(reportRouteBody)
            }.onSuccess {
                reportId = it
                Log.d("RemoteRouteDataSource", "reportRoute Success\nreportId = ${reportId}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "reportRoute Fail\ne = $e")
            }
        }

        return reportId
    }
}