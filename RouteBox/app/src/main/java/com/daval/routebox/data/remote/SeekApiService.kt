package com.daval.routebox.data.remote

import com.daval.routebox.domain.model.BuyRouteResponse
import com.daval.routebox.domain.model.SearchRouteResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SeekApiService {
    // 루트 검색
    @GET("search")
    suspend fun searchRoute(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("query") searchWord: String?, // 검색어
        @Query("sortBy") sortBy: String, // 정렬
        @Query("whoWith") withWhom: List<String>?, // 누구와
        @Query("numberOfPeople") numberOfPeople: List<Int>?, // 몇 명과
        @Query("numberOfDays") numberOfDays: List<String>?, // 며칠 동안
        @Query("style") routeStyle: List<String>?, // 루트 스타일
        @Query("transportation") transportation: List<String>? // 이동 수단
    ): SearchRouteResponse

    @POST("routes/{routeId}/purchase")
    suspend fun buyRoute(
        @Path("routeId") routeId: Int
    ): BuyRouteResponse
}