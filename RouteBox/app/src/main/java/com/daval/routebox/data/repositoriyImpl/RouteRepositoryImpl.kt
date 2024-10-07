package com.daval.routebox.data.repositoriyImpl

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.daval.routebox.data.datasource.RemoteRouteDataSource
import com.daval.routebox.domain.model.RoutePreviewResult
import com.daval.routebox.domain.model.ActivityId
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.ActivityUpdateRequest
import com.daval.routebox.domain.model.CategoryGroupCode
import com.daval.routebox.domain.model.Insight
import com.daval.routebox.domain.model.KakaoSearchResult
import com.daval.routebox.domain.model.MyRoute
import com.daval.routebox.domain.model.RouteDetail
import com.daval.routebox.domain.model.RouteId
import com.daval.routebox.domain.model.RoutePointRequest
import com.daval.routebox.domain.model.RoutePointResult
import com.daval.routebox.domain.model.RoutePreview
import com.daval.routebox.domain.model.RoutePublicRequest
import com.daval.routebox.domain.model.RouteUpdateRequest
import com.daval.routebox.domain.model.RouteUpdateResult
import com.daval.routebox.domain.model.WeatherRegionResponse
import com.daval.routebox.domain.repositories.RouteRepository
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class RouteRepositoryImpl @Inject constructor(
    private val remoteRouteDataSource: RemoteRouteDataSource
) : RouteRepository {
    override suspend fun searchKakaoCategory(categoryGroupCode: CategoryGroupCode, y: String, x: String, page: Int, radius: Int): KakaoSearchResult {
        return remoteRouteDataSource.searchKakaoCategory(categoryGroupCode, y, x, page, radius)
    }

    override suspend fun searchKakaoPlace(query: String, page: Int): KakaoSearchResult {
        return remoteRouteDataSource.searchKakaoPlace(query, page)
    }

    override suspend fun getKakaoRegionCode(latitude: String, longitude: String): WeatherRegionResponse {
        return remoteRouteDataSource.getKakaoRegionCode(latitude, longitude)
    }

    override suspend fun getSearchRouteList(page: Int, size: Int): RoutePreviewResult {
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

    override suspend fun addRouteDot(routeId: Int, routePointRequest: RoutePointRequest): RoutePointResult {
        return remoteRouteDataSource.addRouteDot(routeId, routePointRequest)
    }

    override suspend fun updateRoutePublic(routeId: Int, isPublic: RoutePublicRequest): RoutePublicRequest {
        return remoteRouteDataSource.updateRoutePublic(routeId, isPublic)
    }

    override suspend fun createRoute(startTime: String, endTime: String): RouteId {
        return remoteRouteDataSource.createRoute(startTime, endTime)
    }

    override suspend fun createActivity(
        context: Context,
        routeId: Int,
        locationName: String,
        address: String,
        latitude: String?,
        longitude: String?,
        visitDate: String,
        startTime: String,
        endTime: String,
        category: String,
        description: String?,
        activityImages: List<String>?
    ): ActivityResult {
        return remoteRouteDataSource.createActivity(
            context,
            routeId, locationName, address, latitude, longitude,
            visitDate, startTime, endTime, category, description, activityImages
        )
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
}