package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.BuyPointRequestResponse
import com.daval.routebox.domain.model.BuyRouteRequest
import com.daval.routebox.domain.model.PointHistoryResponse
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
    ): BaseResponse

    /** 내 포인트 구매 이력 조회 */
    suspend fun getPointHistories(
        page: Int,
        pageSize: Int
    ): PointHistoryResponse

    /** 포인트 구매하기 */
    suspend fun buyPoints(
        point: Int
    ): BuyPointRequestResponse
}