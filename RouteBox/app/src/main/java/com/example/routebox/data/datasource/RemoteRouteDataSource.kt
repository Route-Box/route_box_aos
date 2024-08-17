package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.BuildConfig
import com.example.routebox.data.remote.RouteApiService
import com.example.routebox.domain.model.KakaoSearchResult
import com.example.routebox.domain.model.PlaceMeta
import com.example.routebox.domain.model.RegionInfo
import com.example.routebox.domain.model.SearchActivityResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteRouteDataSource @Inject constructor(
    private val routeApiService: RouteApiService
) {
    suspend fun searchKakaoPlace(
        query: String,
        page: Int
    ): KakaoSearchResult {
        var KakaoSearchResult = KakaoSearchResult(
            meta = PlaceMeta(
                0, 0, true,
                RegionInfo(listOf(), "", "")
            ),
            documents = listOf()
        )
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.searchKakaoPlace("KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}", query, page)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "searchKakaoPlace Success\nmeta = ${it.meta}\ndocuments = ${it.documents}")
                KakaoSearchResult = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "searchKakaoPlace Fail\ne = $e")
            }
        }
        return KakaoSearchResult
    }
}