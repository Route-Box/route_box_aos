package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.EditCommentRequest
import com.daval.routebox.domain.model.GetCommentsResponse
import com.daval.routebox.domain.model.PostCommentRequest

interface CommentRepository {
    suspend fun getComments(routeId: Int): GetCommentsResponse

    suspend fun postComment(request: PostCommentRequest): BaseResponse

    suspend fun editComment(commentId: Int, editCommentRequest: EditCommentRequest): BaseResponse

    suspend fun deleteComment(commentId: Int): BaseResponse
}