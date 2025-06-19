package com.daval.routebox.domain.model

data class RecommendRoutes(
    val comment: String,
    val routes: List<RecommendRoute>
)

data class RecommendRoute(
    val id: Int,
    val routeName: String,
    val routeDescription: String,
    val routeImageUrl: String
)

data class PopularRoutes(
    val routes: List<PopularRoute>
)

data class PopularRoute(
    val id: Int,
    val name: String
)