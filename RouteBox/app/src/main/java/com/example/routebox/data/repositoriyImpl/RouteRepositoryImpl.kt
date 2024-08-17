package com.example.routebox.data.repositoriyImpl

import com.example.routebox.data.datasource.RemoteRouteDataSource
import com.example.routebox.domain.model.KakaoSearchResult
import com.example.routebox.domain.repositories.RouteRepository
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val remoteRouteDataSource: RemoteRouteDataSource
) : RouteRepository {
    override suspend fun searchKakaoPlace(query: String, page: Int): KakaoSearchResult {
        return remoteRouteDataSource.searchKakaoPlace(query, page)
    }
}