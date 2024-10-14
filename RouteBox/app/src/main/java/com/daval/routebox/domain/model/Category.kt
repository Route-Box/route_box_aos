package com.daval.routebox.domain.model

import com.daval.routebox.R

enum class Category(val categoryName: String, val categoryIcon: Int, val categoryMarkerIcon: Int) {
    STAY("숙소", R.drawable.ic_category_stay, R.drawable.activity_accommodation),
    TOUR("관광지", R.drawable.ic_category_tour, R.drawable.activity_tourist),
    FOOD("음식점", R.drawable.ic_category_food, R.drawable.activity_restaurant),
    CAFE("카페", R.drawable.ic_category_cafe, R.drawable.activity_cafe),
    SNS("SNS 스팟", R.drawable.ic_category_sns, R.drawable.activity_sns_spot),
    CULTURE("문화 공간", R.drawable.ic_category_culture, R.drawable.activity_culture),
    TOILET("화장실", R.drawable.ic_category_toilet, R.drawable.activity_toilet),
    PARKING("주차장", R.drawable.ic_category_parking, R.drawable.activity_parking),
    ETC("기타", R.drawable.ic_more_horizontal, R.drawable.activity_etc);

    companion object {
        fun getAllCategories(): List<Category> {
            return entries
        }

        fun getCategoryByName(name: String): Category {
            return entries.find { it.categoryName == name } ?: ETC
        }
    }
}