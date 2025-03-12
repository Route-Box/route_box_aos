package com.daval.routebox.data.remote

import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.EditCommentRequest
import com.daval.routebox.domain.model.PostCommentRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApiService {
    @POST("comments")
    suspend fun postComment(
        @Body postCommentRequest: PostCommentRequest
    ): BaseResponse

    @GET("comments/{commentId}")
    suspend fun getComments(
        @Path("routeId") routeId: Int
    ): BaseResponse

    @PATCH("comments/{commentId}")
    suspend fun editComment(
        @Path("routeId") routeId: Int,
        @Body editCommentRequest: EditCommentRequest
    ): BaseResponse

    @DELETE("comments/{commentId}")
    suspend fun deleteComment(
        @Path("commentId") commentId: Int
    ): BaseResponse
}