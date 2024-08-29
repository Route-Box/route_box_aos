package com.example.routebox.data.remote

import com.example.routebox.domain.model.KakaoSearchResult
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface KakaoApiService {
    // 카카오 장소 검색
    @POST("search/keyword")
    suspend fun searchKakaoPlace(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): KakaoSearchResult
}