package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.SearchRoute

interface SeekRepository {
    /** 루트 검색 */
    suspend fun searchRoute(
        searchWord: String?,
        sortBy: String,
        withWhom: ArrayList<String>? = null,
        numberOfPeople: Int? = null,
        numberOfDays: ArrayList<String>? = null,
        routeStyle: ArrayList<String>? = null,
        transportation: ArrayList<String>? = null
    ): List<SearchRoute>
}