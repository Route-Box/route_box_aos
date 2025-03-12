package com.daval.routebox.domain.model

data class Comment (
    val content: String
)

data class EditCommentRequest(
    val comment: String
)

data class PostCommentRequest(
    val routeId: Int,
    val content: String
)