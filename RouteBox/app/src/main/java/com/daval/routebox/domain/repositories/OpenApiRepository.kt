package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.TourApiResult
import com.daval.routebox.domain.model.WeatherApiResult
import com.daval.routebox.presentation.config.Constants.OPEN_API_SERVICE_KEY
import com.daval.routebox.presentation.ui.route.write.MapCameraRadius

interface OpenApiRepository {
    /** 편의기능 관광지 */
    // 카카오
    suspend fun getTourList(
        mobileOs: String = "AND",
        mobileApp: String = "Route Box",
        serviceKey: String = OPEN_API_SERVICE_KEY,
        mapX: String,
        mapY: String,
        radius: String = MapCameraRadius.toString(),
        contentTypeId: String = "12",
        _type: String = "json"
    ): TourApiResult

    suspend fun getWeatherList(
        ServiceKey: String,
        pageNo : Int,
        numOfRows : Int,
        dataType : String,
        base_date : String,
        base_time : String,
        nx : Int,
        ny : Int
    ): Result<WeatherApiResult>
}