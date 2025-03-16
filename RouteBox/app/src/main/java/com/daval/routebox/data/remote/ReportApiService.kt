package com.daval.routebox.data.remote

import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.ReportCommentRequest
import com.daval.routebox.domain.model.ReportId
import com.daval.routebox.domain.model.ReportRoute
import com.daval.routebox.domain.model.ReportUser
import com.daval.routebox.domain.model.RouteReportId
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
    ): RouteReportId

    // 댓글 신고
    @POST("reports/comment")
    suspend fun reportComment(
        @Body reportRouteBody: ReportCommentRequest
    ): BaseResponse
}