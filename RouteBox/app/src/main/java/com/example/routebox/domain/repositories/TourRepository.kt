package com.example.routebox.domain.repositories

import com.example.routebox.domain.model.TourApiResult
import com.example.routebox.presentation.config.Constants.TOUR_SERVICE_KEY
import com.example.routebox.presentation.ui.route.write.MapCameraRadius

interface TourRepository {
    /** 편의기능 관광지 */
    // 카카오
    suspend fun getTourList(
        mobileOs: String = "AND",
        mobileApp: String = "Route Box",
        serviceKey: String = TOUR_SERVICE_KEY,
        mapX: String,
        mapY: String,
        radius: String = MapCameraRadius.toString(),
        contentTypeId: String = "12"
    ): TourApiResult
}