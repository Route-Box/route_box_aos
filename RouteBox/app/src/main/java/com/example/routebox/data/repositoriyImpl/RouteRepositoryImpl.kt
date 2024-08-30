package com.example.routebox.data.repositoriyImpl

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.routebox.data.datasource.RemoteRouteDataSource
import com.example.routebox.domain.model.RoutePreviewResult
import com.example.routebox.domain.model.Activity
import com.example.routebox.domain.model.ActivityId
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.ActivityUpdateRequest
import com.example.routebox.domain.model.Insight
import com.example.routebox.domain.model.KakaoSearchResult
import com.example.routebox.domain.model.MyRoute
import com.example.routebox.domain.model.RouteDetail
import com.example.routebox.domain.model.RouteId
import com.example.routebox.domain.model.RoutePointRequest
import com.example.routebox.domain.model.RoutePreview
import com.example.routebox.domain.model.RoutePublicRequest
import com.example.routebox.domain.model.RouteUpdateRequest
import com.example.routebox.domain.model.RouteUpdateResult
import com.example.routebox.domain.repositories.RouteRepository
import retrofit2.http.Part
import java.io.File
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class RouteRepositoryImpl @Inject constructor(
    private val remoteRouteDataSource: RemoteRouteDataSource
) : RouteRepository {
    override suspend fun searchKakaoPlace(query: String, page: Int): KakaoSearchResult {
        return remoteRouteDataSource.searchKakaoPlace(query, page)
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

    override suspend fun addRouteDot(routeId: Int): RoutePointRequest {
        return remoteRouteDataSource.addRouteDot(routeId)
    }

    override suspend fun updateRoutePublic(routeId: Int, isPublic: RoutePublicRequest): RoutePublicRequest {
        return remoteRouteDataSource.updateRoutePublic(routeId, isPublic)
    }

    override suspend fun createRoute(startTime: String, endTime: String): RouteId {
        return remoteRouteDataSource.createRoute(startTime, endTime)
    }

    override suspend fun createActivity(
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
        activityImages: ArrayList<File?>
    ): ActivityResult {
        return remoteRouteDataSource.createActivity(
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