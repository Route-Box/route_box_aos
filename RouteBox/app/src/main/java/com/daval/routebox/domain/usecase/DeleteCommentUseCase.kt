package com.daval.routebox.domain.usecase

import android.net.Uri
import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.GetCommentsResponse
import com.daval.routebox.domain.repositories.CommentRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(
        commentId: Int
    ): BaseResponse {
        return commentRepository.deleteComment(commentId)
    }
}