package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.BuyRouteRequest
import com.daval.routebox.domain.model.SearchRoute

interface SeekRepository {
    /** 루트 검색 */
    suspend fun searchRoute(
        searchWord: String?,
        sortBy: String,
        withWhom: List<String>? = null,
        numberOfPeople: List<Int>? = null,
        numberOfDays: List<String>? = null,
        routeStyle: List<String>? = null,
        transportation: List<String>? = null
    ): List<SearchRoute>

    /** 루트 구매하기 */
    suspend fun buyRoute(
        routeId: Int,
        buyRouteRequest: BuyRouteRequest
    ): String
}