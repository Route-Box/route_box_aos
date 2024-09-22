package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.data.remote.TourApiService
import com.example.routebox.domain.model.TourApiBody
import com.example.routebox.domain.model.TourApiHeader
import com.example.routebox.domain.model.TourApiItems
import com.example.routebox.domain.model.TourApiResponse
import com.example.routebox.domain.model.TourApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteTourDataSource @Inject constructor(
    private val tourApiService: TourApiService
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
                tourApiService.getTourList(mobileOs, mobileApp, serviceKey, mapX, mapY, radius, contentTypeId, _type)
            }.onSuccess {
                response = it
                Log.d("RemoteTourDataSource", "getTourList Success $it")
            }.onFailure {
                Log.d("RemoteTourDataSource", "getTourList Fail $it")
            }
        }
        return response
    }
}