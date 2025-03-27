package com.daval.routebox.domain.model

data class GetCommentsResponse(
    val comments: List<Comment>
)

data class Comment(
    val commentId: Int = 0,
    val content: String = "",
    val timeAgo: String = "",
    val userNickname: String,
    val userProfileImageUrl: String
)

data class EditCommentRequest(
    val content: String
)

data class PostCommentRequest(
    val routeId: Int,
    val content: String
)