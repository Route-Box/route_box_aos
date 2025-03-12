package com.daval.routebox.data.datasource

import com.daval.routebox.data.remote.CommentApiService
import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.EditCommentRequest
import com.daval.routebox.domain.model.PostCommentRequest
import javax.inject.Inject

class RemoteCommentDataSource @Inject constructor(
    private val commentApiService: CommentApiService
) {
    suspend fun getComments(): BaseResponse {
        val response = BaseResponse()
        return commentApiService.getComments(0)
    }

    suspend fun postComment(): BaseResponse {
        val response = BaseResponse()
        return commentApiService.postComment(postCommentRequest = PostCommentRequest(0, ""))
    }

    suspend fun editComment(commentId: Int, content: String): BaseResponse {
        val response = BaseResponse()
        return commentApiService.editComment(0, editCommentRequest = EditCommentRequest(""))
    }

    suspend fun deleteComment(commentId: Int): BaseResponse {
        val response = BaseResponse()
        return commentApiService.deleteComment(0)
    }
}