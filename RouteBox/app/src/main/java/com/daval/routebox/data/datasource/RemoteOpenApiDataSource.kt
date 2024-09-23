package com.daval.routebox.data.datasource

import android.util.Log
import com.daval.routebox.data.remote.OpenApiService
import com.daval.routebox.domain.model.TourApiBody
import com.daval.routebox.domain.model.TourApiHeader
import com.daval.routebox.domain.model.TourApiItems
import com.daval.routebox.domain.model.TourApiResponse
import com.daval.routebox.domain.model.TourApiResult
import com.daval.routebox.domain.model.WeatherApiResult
import com.daval.routebox.presentation.config.Constants.OPEN_API_BASE_URL
import com.daval.routebox.presentation.config.Constants.OPEN_API_SERVICE_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteOpenApiDataSource @Inject constructor(
    private val openApiService: OpenApiService
) {
    // 편의기능 관광지 정보 가져오기
    suspend fun getTourList(
        mobileOs: String,
        mobileApp: String,
        serviceKey: String,
        mapX: String,
        mapY: String,
        radius: String,
        contentTypeId: String,
        _type: String
    ): TourApiResult {
        var response = TourApiResult(
            response = TourApiResponse(
                header = TourApiHeader("", ""),
                body = TourApiBody(TourApiItems(mutableListOf()), -1, -1, -1)
            )
        )
        withContext(Dispatchers.IO) {
            runCatching {
                openApiService.getTourList(mobileOs, mobileApp, serviceKey, mapX, mapY, radius, contentTypeId, _type)
            }.onSuccess {
                response = it
                Log.d("RemoteTourDataSource", "getTourList Success $it")
            }.onFailure {
                Log.d("RemoteTourDataSource", "getTourList Fail $it")
            }
        }
        return response
    }

    // 편의기능 관광지 정보 가져오기
    suspend fun getWeatherList(
        ServiceKey: String = OPEN_API_BASE_URL,
        pageNo: Int,
        numOfRows: Int,
        dataType: String = "JSON",
        base_date: String,
        base_time: String,
        nx: Int,
        ny: Int
    ): Result<WeatherApiResult> {
        return try {
            val response = openApiService.getWeatherList(OPEN_API_SERVICE_KEY, pageNo, numOfRows, "JSON", "20240923", "0500", nx, ny)
            Log.d("RemoteTourDataSource", "response = $response")

            val rawResponseBody = response.errorBody()?.string() ?: response.body().toString()
            Log.d("RemoteTourDataSource", "Response: $rawResponseBody")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.errorBody() != null) {
                // 에러 응답 처리
                val errorBody = response.errorBody()?.string()
                Log.e("RemoteTourDataSource", "Error Response: $errorBody")
                Result.failure(Throwable("API Error: $errorBody"))
            } else {
                // 그 외의 에러 처리
                Result.failure(Throwable("Unknown error"))
//                Log.e("RemoteTourDataSource", "unknown error")
            }
        } catch (e: Exception) {
            Log.d("RemoteTourDataSource", "e = $e")
            Result.failure(e)
        }
//        var response = WeatherApiResult(
//            response = WeatherApiResponse(
//                header = WeatherApiHeader("", ""),
//                body = WeatherApiBody("", WeatherApiItems(arrayListOf()))
//            )
//        )
//        withContext(Dispatchers.IO) {
//            runCatching {
//                openApiService.getWeatherList(OPEN_API_SERVICE_KEY, pageNo, numOfRows, "JSON", base_date, base_time, nx, ny)
//            }.onSuccess {
//                    Log.d("RemoteTourDataSource", "response = $it")
//
//                    if (it.body() == null) {
//                        Log.d("RemoteTourDataSource", "body null")
//                    }
//                    if (it.body() != null) {
//                        // 1. 응답이 JSON 문자열일 가능성에 대비해 Gson을 사용해 직접 파싱
//                        val gson = Gson()
//                        val rawResponse = it.body().toString() // 응답을 문자열로 변환
//
//                        // 2. 문자열로 변환된 응답을 다시 WeatherApiResult로 변환
//                        response = gson.fromJson(rawResponse, WeatherApiResult::class.java)
//
//                        Log.d("RemoteTourDataSource", "Parsed response: $response")
//                    }
//                    Log.d("RemoteTourDataSource", "getTourList Success $it")
//            }.onFailure { throwable ->
//                // 에러 메시지와 스택 트레이스 출력
//                Log.e("RemoteTourDataSource", "getTourList Fail: ${throwable.message}")
//                throwable.printStackTrace() // 스택 트레이스 출력
//
//                // 에러에 대한 추가 처리 (필요시)
//                Log.e("RemoteTourDataSource", "StackTrace: ${Log.getStackTraceString(throwable)}")
//            }
//        }
//        return response
    }
}