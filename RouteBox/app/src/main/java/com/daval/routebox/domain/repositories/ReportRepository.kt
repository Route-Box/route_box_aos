package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.ReportCommentRequest
import com.daval.routebox.domain.model.ReportId
import com.daval.routebox.domain.model.ReportRoute
import com.daval.routebox.domain.model.ReportUser
import com.daval.routebox.domain.model.RouteReportId

interface ReportRepository {
    /** 사용자 신고 */
    suspend fun reportUser(
        reportUserBody: ReportUser
    ): ReportId

    /** 루트 신고 */
    suspend fun reportRoute(
        reportRouteBody: ReportRoute
    ): RouteReportId

    /** 댓글 신고 */
    suspend fun reportComment(
        reportCommentBody: ReportCommentRequest
    ): BaseResponse
}