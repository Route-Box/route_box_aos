package com.daval.routebox.data.remote

import com.daval.routebox.domain.model.ReportId
import com.daval.routebox.domain.model.ReportRoute
import com.daval.routebox.domain.model.ReportUser
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportApiService {
    // 유저 신고
    @POST("reports/user")
    suspend fun reportUser(
        @Body reportUserBody: ReportUser
    ): ReportId

    // 루트 신고
    @POST("reports/route")
    suspend fun reportRoute(
        @Body reportRouteBody: ReportRoute
    ): ReportId
}