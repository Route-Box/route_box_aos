package com.daval.routebox.data.datasource

import android.util.Log
import com.daval.routebox.data.remote.CommentApiService
import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.EditCommentRequest
import com.daval.routebox.domain.model.GetCommentsResponse
import com.daval.routebox.domain.model.PostCommentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteCommentDataSource @Inject constructor(
    private val commentApiService: CommentApiService
) {
    suspend fun getComments(routeId: Int): GetCommentsResponse {
        var response = GetCommentsResponse(emptyList())

        withContext(Dispatchers.IO) {
            runCatching {
                commentApiService.getComments(routeId)
            }.onSuccess {
                Log.d("RemoteCommentDataSource", "getComments Success $it")
                response = it
            }.onFailure {
                Log.d("RemoteCommentDataSource", "getComments Fail $it")
            }
        }

        return response
    }

    suspend fun postComment(request: PostCommentRequest): BaseResponse {
        var response = BaseResponse()

        withContext(Dispatchers.IO) {
            runCatching {
                commentApiService.postComment(request)
            }.onSuccess {
                Log.d("RemoteCommentDataSource", "postComment Success $it")
                response = it
            }.onFailure {
                Log.d("RemoteCommentDataSource", "postComment Fail $it")
            }
        }

        return response
    }

    suspend fun editComment(commentId: Int, request: EditCommentRequest): BaseResponse {
        var response = BaseResponse()

        withContext(Dispatchers.IO) {
            runCatching {
                commentApiService.editComment(commentId, request)
            }.onSuccess {
                Log.d("RemoteCommentDataSource", "editComment Success $it")
                response = it
            }.onFailure {
                Log.d("RemoteCommentDataSource", "editComment Fail $it")
            }
        }

        return response
    }

    suspend fun deleteComment(commentId: Int): BaseResponse {
        var response = BaseResponse()
        withContext(Dispatchers.IO) {
            runCatching {
                commentApiService.deleteComment(commentId)
            }.onSuccess {
                Log.d("RemoteCommentDataSource", "deleteComment Success $it")
                response = it
            }.onFailure {
                Log.d("RemoteCommentDataSource", "deleteComment Fail $it")
            }
        }

        return response
    }
}