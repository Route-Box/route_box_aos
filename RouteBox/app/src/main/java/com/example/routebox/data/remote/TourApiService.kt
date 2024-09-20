package com.example.routebox.data.remote

import com.example.routebox.domain.model.TourApiResult
import com.example.routebox.presentation.config.Constants.TOUR_SERVICE_KEY
import com.example.routebox.presentation.ui.route.write.MapCameraRadius
import retrofit2.http.GET
import retrofit2.http.Query

interface TourApiService {
    /* 편의기능 관광지 정보 가져오기 */
    @GET("/locationBasedList1")
    fun getTourList(
        @Query("MobileOS") mobileOs: String = "AND",
        @Query("MobileApp") mobileApp: String = "Route Box",
        @Query("serviceKey") serviceKey: String = TOUR_SERVICE_KEY,
        @Query("mapX") mapX: String,
        @Query("mapY") mapY: String,
        @Query("radius") radius: String = MapCameraRadius.toString(),
        @Query("contentTypeId") contentTypeId: String = "12"
    ): TourApiResult
}