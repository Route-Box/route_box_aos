package com.example.routebox.data.remote

import com.example.routebox.domain.model.SearchRoute
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
        @Query("withWhom") withWhom: ArrayList<String>?,
        @Query("numberOfPeople") numberOfPeople: Int?,
        @Query("numberOfDays") numberOfDays: ArrayList<String>?,
        @Query("style") routeStyle: ArrayList<String>?,
        @Query("transportation") transportation: ArrayList<String>?
    ): List<SearchRoute>
}