package com.daval.routebox.domain.usecase.report

import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.ReportCommentRequest
import com.daval.routebox.domain.repositories.CommentRepository
import com.daval.routebox.domain.repositories.ReportRepository
import javax.inject.Inject

class ReportCommentUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(
        commentId: Int
    ): BaseResponse {
        return reportRepository.reportComment(ReportCommentRequest(commentId))
    }
}