package com.daval.routebox.domain.model

enum class Point(val pointIndex: Int, val pointId: String) {
    POINT4500(0, "point_01_4500"),
    POINT9500(1, "point_02_9500"),
    POINT14500(2, "point_03_14500"),
    POINT19500(3, "point_04_19500"),
    POINT24500(4, "point_05_24500"),
    POINT49500(5, "point_06_49500");

    companion object {
        fun getPointIdList(): List<String> {
            return Point.entries.map { it.pointId }
        }
    }
}

data class PointHistoryResponse(
    val content: List<PointHistory> = listOf(),
    val page: PointHistoryPage
)

data class PointHistory(
    val id: Int,
    val userId: Int,
    val route: PointRoute,
    val transactionType: String,
    val amount: Int
)

data class PointRoute(
    val id: Int,
    val name: String,
    val thumbnailImageUrl: String = ""
)

data class PointHistoryPage(
    val size: Int,
    val number: Int,
    val totalElements: Int,
    val totalPages: Int
)