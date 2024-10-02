package com.daval.routebox.data.remote

import com.daval.routebox.domain.model.SearchRouteResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SeekApiService {
    // 루트 검색
    @GET("search")
    suspend fun searchRoute(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("query") searchWord: String?,
        @Query("sortBy") sortBy: String,
        @Query("whoWith") withWhom: List<String>?,
        @Query("numberOfPeople") numberOfPeople: List<Int>?,
        @Query("numberOfDays") numberOfDays: List<String>?,
        @Query("style") routeStyle: List<String>?,
        @Query("transportation") transportation: List<String>?
    ): SearchRouteResponse
}