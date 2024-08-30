package com.example.routebox.domain.model

data class ReportId(
    var reportId: Int
)

// 사용자 신고
data class ReportUser(
    var userId: Int,
    var content: String
)

// 루트 신고
data class ReportRoute(
    var routeId: Int,
    var content: String
)