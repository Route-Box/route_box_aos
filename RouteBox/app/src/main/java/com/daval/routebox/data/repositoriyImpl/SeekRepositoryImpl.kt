package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.data.datasource.RemoteSeekDataSource
import com.daval.routebox.domain.model.BuyRouteRequest
import com.daval.routebox.domain.model.SearchRoute
import com.daval.routebox.domain.repositories.SeekRepository
import javax.inject.Inject

class SeekRepositoryImpl @Inject constructor(
    private val remoteSeekDataSource: RemoteSeekDataSource
): SeekRepository {
    override suspend fun searchRoute(
        searchWord: String?,
        sortBy: String,
        withWhom: List<String>?,
        numberOfPeople: List<Int>?,
        numberOfDays: List<String>?,
        routeStyle: List<String>?,
        transportation: List<String>?
    ): List<SearchRoute> {
        return remoteSeekDataSource.searchRoute(searchWord, sortBy, withWhom, numberOfPeople, numberOfDays, routeStyle, transportation)
            .routes
    }

    override suspend fun buyRoute(routeId: Int, buyRouteRequest: BuyRouteRequest): String {
        return remoteSeekDataSource.buyRoute(routeId, buyRouteRequest)
    }
}