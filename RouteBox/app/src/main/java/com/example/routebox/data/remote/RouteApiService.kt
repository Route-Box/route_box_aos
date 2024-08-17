package com.example.routebox.data.remote

import com.example.routebox.domain.model.KakaoSearchResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface RouteApiService {
    // 루트 카카오 장소 검색
    @GET("https://dapi.kakao.com/v2/local/search/keyword")
    suspend fun searchKakaoPlace(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): KakaoSearchResult
}