package com.example.routebox.domain.model

// 내 루트
data class Route(
    val title: String,
    val content: String,
    val isPrivate: Boolean,
    val tags: List<String>, // 루트 스타일
    val activities: List<Activity> // 활동 목록
    //TODO: 구매 수, 댓글 수, 생성 날짜 등 서버 데이터 추가
)

// 활동
data class Activity(
    val name: String,
    val type: String, // 음식점, 관광명소 등
    val address: String,
    val startTime: String,
    val endTime: String,
    val imgUrls: List<String>?,
    val description: String?
)