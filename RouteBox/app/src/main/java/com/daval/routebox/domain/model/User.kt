package com.daval.routebox.domain.model

data class User(
    val id: Int,
    val profileImageUrl: String,
    val nickname: String,
    val gender: String,
    val birthDay: String,
    val introduction: String,
    val numOfRoutes: Int,
    val mostVisitedLocation: String,
    val mostTaggedRouteStyles: String
)

data class NicknameAvailabilityResponse(
    val nickname: String,
    val isAvailable: Boolean
)

data class EditProfileResponse(
    val id: Int = 0,
    val profileImage: String = "",
    val point: Int = 0,
    val gender: String = "",
    val birthDay: String = "",
    val introduction: String = ""
)

data class UserRoutes(
    val routes: List<UserRoute>
)

data class UserRoute(
    val routeId: Int,
    val routeName: String,
    val routeDescription: String,
    val routeImageUrl: String,
    val purchaseCount: Int,
    val commentCount: Int,
    val createdAt: String
)