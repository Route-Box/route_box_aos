package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.GetCommentsResponse

interface CommentRepository {
    suspend fun getComments(routeId: Int): GetCommentsResponse

    suspend fun postComment(commentId: Int, content: String): BaseResponse

    suspend fun editComment(commentId: Int, content: String): BaseResponse

    suspend fun deleteComment(commentId: Int): BaseResponse
}