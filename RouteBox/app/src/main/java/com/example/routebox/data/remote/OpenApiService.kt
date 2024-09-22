package com.example.routebox.data.remote

import com.example.routebox.domain.model.TourApiResult
import com.example.routebox.domain.model.WeatherApiResult
import com.example.routebox.presentation.config.Constants.OPEN_API_SERVICE_KEY
import com.example.routebox.presentation.ui.route.write.MapCameraRadius
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenApiService {
    /* 편의기능 관광지 정보 가져오기 */
    @GET("B551011/KorService1/locationBasedList1")
    fun getTourList(
        @Query("MobileOS") mobileOs: String = "AND",
        @Query("MobileApp") mobileApp: String = "Route Box",
        @Query("serviceKey") serviceKey: String = OPEN_API_SERVICE_KEY,
        @Query("mapX") mapX: String,
        @Query("mapY") mapY: String,
        @Query("radius") radius: String = MapCameraRadius.toString(),
        @Query("contentTypeId") contentTypeId: String = "12",
        @Query("_type") _type: String = "json"
    ): TourApiResult

    @GET("/1360000/VilageFcstInfoService_2.0/getVilageFcst")
    suspend fun getWeatherList(
        @Query("ServiceKey") serviceKey: String = OPEN_API_SERVICE_KEY,
        @Query("pageNo") pageNo : Int,
        @Query("numOfRows") numOfRows : Int,
        @Query("dataType") dataType : String = "JSON",
        @Query("base_date") baseDate : String,
        @Query("base_time") baseTime : String,
        @Query("nx") nx : Int,
        @Query("ny") ny : Int
    ) : Response<WeatherApiResult>
}