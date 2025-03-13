package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.data.datasource.RemoteCommentDataSource
import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.GetCommentsResponse
import com.daval.routebox.domain.repositories.CommentRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val remoteCommentDataSource: RemoteCommentDataSource
) : CommentRepository {
    override suspend fun getComments(routeId: Int): GetCommentsResponse {
        return remoteCommentDataSource.getComments(routeId)
    }

    override suspend fun postComment(commentId: Int, content: String): BaseResponse {
        return remoteCommentDataSource.postComment(commentId, content)
    }

    override suspend fun editComment(commentId: Int, content: String): BaseResponse {
        return remoteCommentDataSource.editComment(commentId, content)
    }

    override suspend fun deleteComment(commentId: Int): BaseResponse {
        return remoteCommentDataSource.deleteComment(commentId)
    }

}