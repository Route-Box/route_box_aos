package com.daval.routebox.data.datasource

import android.util.Log
import com.daval.routebox.data.remote.ReportApiService
import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.ReportCommentRequest
import com.daval.routebox.domain.model.ReportId
import com.daval.routebox.domain.model.ReportRoute
import com.daval.routebox.domain.model.ReportUser
import com.daval.routebox.domain.model.RouteReportId
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
                Log.d("RemoteReportDataSource", "reportUser Success\nreportId = ${reportId}")
            }.onFailure { e ->
                Log.d("RemoteReportDataSource", "reportUser Fail\ne = $e")
            }
        }

        return reportId
    }

    suspend fun reportRoute(
        reportRouteBody: ReportRoute
    ): RouteReportId {
        var reportId = RouteReportId(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                reportApiService.reportRoute(reportRouteBody)
            }.onSuccess {
                reportId = it
                Log.d("RemoteReportDataSource", "reportRoute Success\nreportId = ${reportId}")
            }.onFailure { e ->
                Log.d("RemoteReportDataSource", "reportRoute Fail\ne = $e")
            }
        }

        return reportId
    }

    suspend fun reportComment(
        reportCommentBody: ReportCommentRequest
    ): BaseResponse {
        var response = BaseResponse()
        withContext(Dispatchers.IO) {
            runCatching {
                reportApiService.reportComment(reportCommentBody)
            }.onSuccess {
                response = it
                Log.d("RemoteReportDataSource", "reportComment Success\nreportId = ${response}")
            }.onFailure { e ->
                Log.d("RemoteReportDataSource", "reportComment Fail\ne = $e")
            }
        }

        return response
    }
}