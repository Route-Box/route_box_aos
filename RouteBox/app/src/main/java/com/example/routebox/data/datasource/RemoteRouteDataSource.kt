package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.BuildConfig
import com.example.routebox.data.remote.RouteApiService
import com.example.routebox.domain.model.Activity
import com.example.routebox.domain.model.ActivityId
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.ActivityUpdateRequest
import com.example.routebox.domain.model.Insight
import com.example.routebox.domain.model.KakaoSearchResult
import com.example.routebox.domain.model.MyRoute
import com.example.routebox.domain.model.PlaceMeta
import com.example.routebox.domain.model.RegionInfo
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteRouteDataSource @Inject constructor(
    private val routeApiService: RouteApiService
) {
    suspend fun searchKakaoPlace(
        query: String,
        page: Int
    ): KakaoSearchResult {
        var kakaoSearchResult = KakaoSearchResult(
            meta = PlaceMeta(
                0, 0, true,
                RegionInfo(listOf(), "", "")
            ),
            documents = listOf()
        )
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.searchKakaoPlace("KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}", query, page)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "searchKakaoPlace Success\nmeta = ${it.meta}\ndocuments = ${it.documents}")
                kakaoSearchResult = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "searchKakaoPlace Fail\ne = $e")
            }
        }
        return kakaoSearchResult
    }

    suspend fun getSearchRouteList(
        page: Int,
        size: Int
    ): ArrayList<RoutePreview> {
        var routePreviewList = arrayListOf<RoutePreview>()
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.getSearchRouteList(page, size)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "getSearchRouteList Success\nroutePreviewList = ${routePreviewList}")
                routePreviewList = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "getSearchRouteList Fail\ne = $e")
            }
        }
        return routePreviewList
    }

    suspend fun getRouteDetailPreview(
        routeId: Int
    ): RoutePreview {
        var routePreview = RoutePreview(-1, -1, "", "", "", "",
            arrayListOf(), false, -1, -1, arrayListOf(), "", "", -1, "", "")
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.getRouteDetailPreview(routeId)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "getRouteDetailPreview Success\nroutePreview = ${routePreview}")
                routePreview = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "getRouteDetailPreview Fail\ne = $e")
            }
        }

        return routePreview
    }

    suspend fun getRouteDetail(
        routeId: Int
    ): RouteDetail {
        var routeDetail = RouteDetail(
            -1, -1, "", "", "", "", "", "", "",
            arrayListOf(), -1, "", "", "", arrayListOf(), arrayListOf(), false
        )
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.getRouteDetail(routeId)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "getRouteDetail Success\nrouteDetail = ${routeDetail}")
                routeDetail = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "getRouteDetail Fail\ne = $e")
            }
        }

        return routeDetail
    }

    suspend fun getMyRouteList(): ArrayList<MyRoute> {
        var myRouteList = arrayListOf<MyRoute>()
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.getMyRouteList()
            }.onSuccess {
                myRouteList = it.result
                Log.d("RemoteRouteDataSource", "getMyRouteList Success\nmyRouteList = ${myRouteList}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "getMyRouteList Fail\ne = $e")
            }
        }

        return myRouteList
    }

    suspend fun checkRouteIsRecording(
        checkRouteIsRecording: String
    ): RouteId {
        var routeId = RouteId(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.checkRouteIsRecording(checkRouteIsRecording)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "checkRouteIsRecording Success\nrouteId = ${routeId}")
                routeId = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "checkRouteIsRecording Fail\ne = $e")
            }
        }

        return routeId
    }

    suspend fun addRouteDot(
        routeId: Int
    ): RoutePointRequest {
        var routeDot = RoutePointRequest("", "", -1)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.addRouteDot(routeId)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "addRouteDot Success\nrouteDot = ${routeDot}")
                routeDot = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "addRouteDot Fail\ne = $e")
            }
        }

        return routeDot
    }

    suspend fun updateRoutePublic(
        routeId: Int,
        isPublicBody: RoutePublicRequest
    ): RoutePublicRequest {
        var isPublic = RoutePublicRequest(false)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.updateRoutePublic(routeId, isPublicBody)
            }.onSuccess {
                isPublic = it
                Log.d("RemoteRouteDataSource", "updateRoutePublic Success\nisPublic = ${isPublic}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "updateRoutePublic Fail\ne = $e")
            }
        }

        return isPublic
    }

    suspend fun createRoute(
        startTime: String,
        endTime: String
    ): RouteId {
        var routeId = RouteId(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.createRoute(RouteWriteTime(startTime, endTime))
            }.onSuccess {
                routeId = it
                Log.d("RemoteRouteDataSource", "createRoute Success\nrouteId = $routeId")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "createRoute Fail\ne = $e")
            }
        }

        return routeId
    }

    suspend fun createActivity(
        routeId: Int,
        activity: Activity
    ): ActivityResult {
        var activityResult = ActivityResult(
            -1, "", "", "", "", "", "", "", "", "", arrayListOf()
        )
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.createActivity(routeId, activity)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "createActivity Success\nactivityResult = ${activityResult}")
                activityResult = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "createActivity Fail\ne = $e")
            }
        }

        return activityResult
    }

    suspend fun updateRoute(
        routeId: Int,
        routeUpdateRequest: RouteUpdateRequest
    ): RouteUpdateResult {
        var routeUpdateResult = RouteUpdateResult(
            -1, "", "", "", -1, "", arrayListOf(), ""
        )
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.updateRoute(routeId, routeUpdateRequest)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "updateRoute Success\nrouteUpdateResult = ${routeUpdateResult}")
                routeUpdateResult = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "updateRoute Fail\ne = $e")
            }
        }

        return routeUpdateResult
    }

    suspend fun updateActivity(
        routeId: Int,
        activityId: Int,
        activityUpdateRequest: ActivityUpdateRequest
    ): ActivityResult {
        var activityUpdateResult = ActivityResult(
            -1, "", "", "", "", "", "", "", "", "", arrayListOf()
        )
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.updateActivity(routeId, activityId, activityUpdateRequest)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "updateActivity Success\nactivityUpdateResult = ${activityUpdateResult}")
                activityUpdateResult = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "updateActivity Fail\ne = $e")
            }
        }

        return activityUpdateResult
    }

    suspend fun deleteRoute(
        routeId: Int
    ): RouteId {
        var deleteRouteId = RouteId(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.deleteRoute(routeId)
            }.onSuccess {
                deleteRouteId = it
                Log.d("RemoteRouteDataSource", "deleteRoute Success\nrouteId = ${deleteRouteId}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "deleteRoute Fail\ne = $e")
            }
        }

        return deleteRouteId
    }

    suspend fun deleteActivity(
        routeId: Int,
        activityId: Int
    ): ActivityId {
        var deleteActivityId = ActivityId(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.deleteActivity(routeId, activityId)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "deleteActivity Success\nrouteId = ${deleteActivityId}")
                deleteActivityId = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "deleteActivity Fail\ne = $e")
            }
        }

        return deleteActivityId
    }

    suspend fun getInsight(): Insight {
        var insight = Insight(-1, -1, -1)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.getInsight()
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "getInsight Success\ninsight = $it")
                insight = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "getInsight Fail\ne = $e")
            }
        }

        return insight
    }

    suspend fun reportUser(
        reportUserBody: ReportUser
    ): ReportId {
        var reportId = ReportId(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.reportUser(reportUserBody)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "reportUser Success\nreportId = ${reportId}")
                reportId = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "reportUser Fail\ne = $e")
            }
        }

        return reportId
    }

    suspend fun reportRoute(
        reportRouteBody: ReportRoute
    ): ReportId {
        var reportId = ReportId(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.reportRoute(reportRouteBody)
            }.onSuccess {
                Log.d("RemoteRouteDataSource", "reportRoute Success\nreportId = ${reportId}")
                reportId = it
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "reportRoute Fail\ne = $e")
            }
        }

        return reportId
    }
}