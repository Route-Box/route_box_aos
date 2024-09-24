package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.data.remote.SeekApiService
import com.example.routebox.domain.model.SearchRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteSeekDataSource @Inject constructor(
    private val seekApiService: SeekApiService
) {
    suspend fun searchRoute(
        searchWord: String?,
        sortBy: String,
        withWhom: ArrayList<String>?,
        numberOfPeople: Int?,
        numberOfDays: ArrayList<String>?,
        routeStyle: ArrayList<String>?,
        transportation: ArrayList<String>?
    ): List<SearchRoute> {
        var searchResult = emptyList<SearchRoute>()
        withContext(Dispatchers.IO) {
            runCatching {
                seekApiService.searchRoute(0, 10, searchWord, sortBy, withWhom, numberOfPeople, numberOfDays, routeStyle, transportation)
            }.onSuccess {
                searchResult = it
                Log.d("RemoteSeekDataSource", "searchRoute Success\nresult = ${searchResult}")
            }.onFailure { e ->
                Log.d("RemoteSeekDataSource", "searchRoute Fail\ne = $e")
            }
        }
        return searchResult
    }
}