package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.data.datasource.RemoteCommentDataSource
import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.EditCommentRequest
import com.daval.routebox.domain.model.GetCommentsResponse
import com.daval.routebox.domain.model.PostCommentRequest
import com.daval.routebox.domain.repositories.CommentRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val remoteCommentDataSource: RemoteCommentDataSource
) : CommentRepository {
    override suspend fun getComments(routeId: Int): GetCommentsResponse {
        return remoteCommentDataSource.getComments(routeId)
    }

    override suspend fun postComment(request: PostCommentRequest): BaseResponse {
        return remoteCommentDataSource.postComment(request)
    }

    override suspend fun editComment(commentId: Int, request: EditCommentRequest): BaseResponse {
        return remoteCommentDataSource.editComment(commentId, request)
    }

    override suspend fun deleteComment(commentId: Int): BaseResponse {
        return remoteCommentDataSource.deleteComment(commentId)
    }

}