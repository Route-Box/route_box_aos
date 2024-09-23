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

const val loadingType = 1
const val routeType = 2