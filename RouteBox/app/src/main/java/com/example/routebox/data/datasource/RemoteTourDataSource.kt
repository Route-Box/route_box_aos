package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.data.remote.TourApiService
import com.example.routebox.domain.model.TourApiBody
import com.example.routebox.domain.model.TourApiHeader
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
        mapX: String,
        mapY: String
    ): TourApiResult {
        var response = TourApiResult(
            response = TourApiResponse(
                header = TourApiHeader("", ""),
                body = TourApiBody(arrayListOf(), -1, -1, -1)
            )
        )
        withContext(Dispatchers.IO) {
            runCatching {
                tourApiService.getTourList(mapX = mapX, mapY = mapY)
            }.onSuccess {
                Log.d("RemoteTourDataSource", "getTourList Success $it")
                response = it
            }.onFailure {
                Log.d("RemoteTourDataSource", "getTourList Fail $it")
            }
        }
        return response
    }
}