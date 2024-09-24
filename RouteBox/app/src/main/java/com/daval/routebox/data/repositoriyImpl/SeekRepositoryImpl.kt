package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.data.datasource.RemoteSeekDataSource
import com.daval.routebox.domain.model.SearchRoute
import com.daval.routebox.domain.repositories.SeekRepository
import javax.inject.Inject

class SeekRepositoryImpl @Inject constructor(
    private val remoteSeekDataSource: RemoteSeekDataSource
): SeekRepository {
    override suspend fun searchRoute(
        searchWord: String?,
        sortBy: String,
        withWhom: ArrayList<String>?,
        numberOfPeople: Int?,
        numberOfDays: ArrayList<String>?,
        routeStyle: ArrayList<String>?,
        transportation: ArrayList<String>?
    ): List<SearchRoute> {
        return  remoteSeekDataSource.searchRoute(searchWord, sortBy, withWhom, numberOfPeople, numberOfDays, routeStyle, transportation)
    }
}