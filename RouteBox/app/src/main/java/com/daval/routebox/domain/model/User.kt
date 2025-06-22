package com.daval.routebox.domain.model

data class User(
    var id: Int,
    var profileImageUrl: String,
    var nickname: String,
    var gender: String,
    var birthDay: String,
    var introduction: String,
    var numOfRoutes: Int,
    var mostVisitedLocation: String,
    var mostTaggedRouteStyles: String
)

data class NicknameAvailabilityResponse(
    var nickname: String,
    var isAvailable: Boolean
)

data class EditProfileResponse(
    var id: Int = 0,
    var profileImage: String = "",
    var point: Int = 0,
    var gender: String = "",
    var birthDay: String = "",
    var introduction: String = ""
)

data class UserRoutes(
    var routes: List<UserRoute>
)

data class UserRoute(
    var routeId: Int,
    var routeName: String,
    var routeDescription: String,
    var routeImageUrl: String,
    var purchaseCount: Int,
    var commentCount: Int,
    var createdAt: String
)