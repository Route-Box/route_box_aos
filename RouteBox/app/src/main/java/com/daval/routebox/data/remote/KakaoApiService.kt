package com.daval.routebox.data.remote

import com.daval.routebox.domain.model.CategoryGroupCode
import com.daval.routebox.domain.model.KakaoSearchResult
import retrofit2.http.GET
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

    @GET("search/category")
    suspend fun searchKakaoCategory(
        @Header("Authorization") authorization: String,
        @Query("category_group_code") categoryGroupCode: CategoryGroupCode,
        @Query("y") latitude: String,
        @Query("x") longitude: String,
        @Query("page") page: Int,
        @Query("radius") radius: Int
    ): KakaoSearchResult
}