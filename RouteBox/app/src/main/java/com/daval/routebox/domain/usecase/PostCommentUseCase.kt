package com.daval.routebox.domain.usecase

import android.net.Uri
import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.GetCommentsResponse
import com.daval.routebox.domain.model.PostCommentRequest
import com.daval.routebox.domain.repositories.CommentRepository
import javax.inject.Inject

class PostCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(
        routeId: Int,
        content: String
    ): BaseResponse {
        return commentRepository.postComment(PostCommentRequest(routeId, content))
    }
}