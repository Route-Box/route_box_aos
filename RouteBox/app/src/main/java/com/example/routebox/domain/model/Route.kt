package com.example.routebox.domain.model

// 내 루트
data class Route(
    val title: String,
    val content: String,
    val isPrivate: Boolean
    //TODO: 구매 수, 댓글 수, 추가 날짜 등 서버 데이터 추가
)