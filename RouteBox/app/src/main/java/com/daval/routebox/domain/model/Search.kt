package com.daval.routebox.domain.model

import android.graphics.drawable.Drawable

data class Bank(
    var bankName: String,
    var bankImg: Drawable
)

data class History(
    var thumbNailImg: String?,
    var title: String,
    var date: String,
    var point: Int
)

data class OwnPoint(
    var nickname: String,
    var point: Int
)

data class SearchRouteResponse(
    val routes: List<SearchRoute>
)

data class SearchRoute(
    var routeId: Int,
    var userId: Int,
    var profileImageUrl: String = "",
    var nickname: String = "",
    var routeName: String = "",
    var routeDescription: String = "",
    var routeImageUrl: String = "",
    var purchaseCount: Int = -1,
    var commentCount: Int = -1,
    var createdAt: String = ""
)

const val loadingType = 1
const val routeType = 2