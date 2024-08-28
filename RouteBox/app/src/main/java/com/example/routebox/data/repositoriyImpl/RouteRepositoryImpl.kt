package com.example.routebox.data.repositoriyImpl

import com.example.routebox.data.datasource.RemoteRouteDataSource
import com.example.routebox.domain.model.Activity
import com.example.routebox.domain.model.ActivityId
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.ActivityUpdateRequest
import com.example.routebox.domain.model.Insight
import com.example.routebox.domain.model.KakaoSearchResult
import com.example.routebox.domain.model.MyRoute
import com.example.routebox.domain.model.ReportId
import com.example.routebox.domain.model.ReportRoute
import com.example.routebox.domain.model.ReportUser
import com.example.routebox.domain.model.RouteDetail
import com.example.routebox.domain.model.RouteId
import com.example.routebox.domain.model.RoutePointRequest
import com.example.routebox.domain.model.RoutePreview
import com.example.routebox.domain.model.RoutePublicRequest
import com.example.routebox.domain.model.RouteUpdateRequest
import com.example.routebox.domain.model.RouteUpdateResult
import com.example.routebox.domain.model.RouteWriteTime
import com.example.routebox.domain.repositories.RouteRepository
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val remoteRouteDataSource: RemoteRouteDataSource
) : RouteRepository {
    override suspend fun searchKakaoPlace(query: String, page: Int): KakaoSearchResult {
        return remoteRouteDataSource.searchKakaoPlace(query, page)
    }

    override suspend fun getSearchRouteList(page: Int, size: Int): ArrayList<RoutePreview> {
        return remoteRouteDataSource.getSearchRouteList(page, size)
    }

    override suspend fun getRouteDetailPreview(routeId: Int): RoutePreview {
        return remoteRouteDataSource.getRouteDetailPreview(routeId)
    }

    override suspend fun getRouteDetail(routeId: Int): RouteDetail {
        return remoteRouteDataSource.getRouteDetail(routeId)
    }

    override suspend fun getMyRouteList(): ArrayList<MyRoute> {
        return remoteRouteDataSource.getMyRouteList()
    }

    override suspend fun checkRouteIsRecording(userLocalTime: String): RouteId {
        return remoteRouteDataSource.checkRouteIsRecording(userLocalTime)
    }

    override suspend fun addRouteDot(routeId: Int): RoutePointRequest {
        return remoteRouteDataSource.addRouteDot(routeId)
    }

    override suspend fun updateRoutePublic(routeId: Int, isPublic: RoutePublicRequest): RoutePublicRequest {
        return remoteRouteDataSource.updateRoutePublic(routeId, isPublic)
    }

    override suspend fun createRoute(): RouteWriteTime {
        return remoteRouteDataSource.createRoute()
    }

    override suspend fun createActivity(routeId: Int, activity: Activity): ActivityResult {
        return remoteRouteDataSource.createActivity(routeId, activity)
    }

    override suspend fun updateRoute(
        routeId: Int,
        routeUpdateRequest: RouteUpdateRequest
    ): RouteUpdateResult {
        return remoteRouteDataSource.updateRoute(routeId, routeUpdateRequest)
    }

    override suspend fun updateActivity(
        routeId: Int,
        activityId: Int,
        activityUpdateRequest: ActivityUpdateRequest
    ): ActivityResult {
        return remoteRouteDataSource.updateActivity(routeId, activityId, activityUpdateRequest)
    }

    override suspend fun deleteRoute(routeId: Int): RouteId {
        return remoteRouteDataSource.deleteRoute(routeId)
    }

    override suspend fun deleteActivity(routeId: Int, activityId: Int): ActivityId {
        return remoteRouteDataSource.deleteActivity(routeId, activityId)
    }

    override suspend fun getInsight(): Insight {
        return remoteRouteDataSource.getInsight()
    }

    override suspend fun reportUser(reportUserBody: ReportUser): ReportId {
        return remoteRouteDataSource.reportUser(reportUserBody)
    }

    override suspend fun reportRoute(reportRouteBody: ReportRoute): ReportId {
        return remoteRouteDataSource.reportRoute(reportRouteBody)
    }
}