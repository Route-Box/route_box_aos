package com.daval.routebox.domain.model

import com.daval.routebox.R

enum class Convenience(val title: String, val markerIcon: Int, val typeList: List<String>) {
    CAFE("카페 ☕\uFE0F", R.drawable.ic_marker_cafe, listOf("cafe", "coffee_shop")),
    FOOD("음식점 \uD83C\uDF7D\uFE0F", R.drawable.ic_marker_food, listOf("restaurant")),
    TOILET("공용 화장실 \uD83D\uDEBB", R.drawable.ic_marker_toilet, listOf("public_bath", "public_bathroom", "restroom")),
    TOUR("관광지 \uD83D\uDCCD", R.drawable.ic_marker_tour, listOf("tourist_attraction", "amusement_park", "natural_feature", "place_of_worship", "campground")),
    STAY("숙소 \uD83C\uDFE0", R.drawable.ic_marker_stay, listOf("lodging", "motel", "hotel")),
    SHOPPING("쇼핑 \uD83D\uDECD", R.drawable.ic_marker_shopping, listOf("convenience_store", "supermarket")),
    HOSPITAL("병원 \uD83C\uDFE5", R.drawable.ic_marker_hospital, listOf("hospital", "pharmacy", "health")),
    PUBLIC_TRANSPORTATION("대중교통 \uD83D\uDE87", R.drawable.ic_marker_transportation, listOf("station", "train_station", "taxi_stand", "subway_station", "park_and_ride", "bus_station", "bus_stop")),
    CAR("자동차 \uD83D\uDE97", R.drawable.ic_marker_parking, listOf("car_rental", "gas_station", "charging_station", "parking", "car_repair")),
    BANK("은행 \uD83D\uDCB5", R.drawable.ic_marker_bank, listOf("atm", "bank"))
}