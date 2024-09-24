package com.example.routebox.domain.repositories

import com.example.routebox.domain.model.SearchRoute

interface SeekRepository {
    /** 루트 검색 */
    suspend fun searchRoute(
        searchWord: String?,
        sortBy: String,
        withWhom: ArrayList<String>?,
        numberOfPeople: Int?,
        numberOfDays: ArrayList<String>?,
        routeStyle: ArrayList<String>?,
        transportation: ArrayList<String>?
    ): List<SearchRoute>
}