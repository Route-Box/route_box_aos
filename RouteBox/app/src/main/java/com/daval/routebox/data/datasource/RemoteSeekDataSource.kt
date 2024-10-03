package com.daval.routebox.data.datasource

import android.util.Log
import com.daval.routebox.data.remote.SeekApiService
import com.daval.routebox.domain.model.SearchRouteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteSeekDataSource @Inject constructor(
    private val seekApiService: SeekApiService
) {
    suspend fun searchRoute(
        searchWord: String?,
        sortBy: String,
        withWhom: List<String>?,
        numberOfPeople: List<Int>?,
        numberOfDays: List<String>?,
        routeStyle: List<String>?,
        transportation: List<String>?
    ): SearchRouteResponse {
        var searchResult = SearchRouteResponse(emptyList())
        withContext(Dispatchers.IO) {
            runCatching {
                seekApiService.searchRoute(0, 10, searchWord, sortBy, withWhom, numberOfPeople, numberOfDays, routeStyle, transportation)
            }.onSuccess {
                searchResult = it
                Log.d("RemoteSeekDataSource", "searchRoute Success\nresult = $searchResult")
            }.onFailure { e ->
                Log.d("RemoteSeekDataSource", "searchRoute Fail\ne = $e")
            }
        }
        return searchResult
    }
}