package com.daval.routebox.domain.model

import com.daval.routebox.R

enum class Category(val categoryName: String, val categoryIcon: Int) {
    STAY("숙소", R.drawable.ic_category_stay),
    TOUR("관광지", R.drawable.ic_category_tour),
    FOOD("음식점", R.drawable.ic_category_food),
    CAFE("카페", R.drawable.ic_category_cafe),
    SNS("SNS 스팟", R.drawable.ic_category_sns),
    CULTURE("문화 공간", R.drawable.ic_category_culture),
    TOILET("화장실", R.drawable.ic_category_toilet),
    PARKING("주차장", R.drawable.ic_category_parking),
    ETC("기타", R.drawable.ic_more_horizontal)
}