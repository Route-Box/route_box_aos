package com.daval.routebox.domain.usecase.comment

import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.EditCommentRequest
import com.daval.routebox.domain.repositories.CommentRepository
import javax.inject.Inject

class EditCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(
        commentId: Int,
        content: String
    ): BaseResponse {
        return commentRepository.editComment(commentId, EditCommentRequest(content))
    }
}