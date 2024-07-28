package com.example.routebox.domain.model

import android.graphics.drawable.Drawable

data class RoutePreview(
    var profileImg: String?,
    val nickname: String,
    val createAt: String,
    val img: ArrayList<String>?,
    val title: String,
    val content: String,
    val save: Int,
    val comment: Int
)

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