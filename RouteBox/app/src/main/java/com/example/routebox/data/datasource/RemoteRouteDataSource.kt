package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.BuildConfig
import com.example.routebox.data.remote.KakaoApiService
import com.example.routebox.data.remote.RouteApiService
import com.example.routebox.domain.model.RoutePreviewResult
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class RemoteRouteDataSource @Inject constructor(
    private val routeApiService: RouteApiService,
    private val kakaoApiService: KakaoApiService
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
                kakaoApiService.searchKakaoPlace("KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}", query, page)
            }.onSuccess {
                kakaoSearchResult = it
                Log.d("RemoteRouteDataSource", "searchKakaoPlace Success\nmeta = ${it.meta}\ndocuments = ${it.documents}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "searchKakaoPlace Fail\ne = $e")
            }
        }
        return kakaoSearchResult
    }

    suspend fun getSearchRouteList(
        page: Int,
        size: Int
    ): RoutePreviewResult {
        var routePreviewList = RoutePreviewResult()
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.getSearchRouteList(page, size)
            }.onSuccess {
                routePreviewList = it
                Log.d("RemoteRouteDataSource", "getSearchRouteList Success\nroutePreviewList = ${routePreviewList}\npage = $page size = $size")
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
                routePreview = it
                Log.d("RemoteRouteDataSource", "getRouteDetailPreview Success\nroutePreview = ${routePreview}")
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
                routeDetail = it
                Log.d("RemoteRouteDataSource", "getRouteDetail Success\nrouteDetail = ${routeDetail}")
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
                myRouteList = it
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
                routeDot = it
                Log.d("RemoteRouteDataSource", "addRouteDot Success\nrouteDot = ${routeDot}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "addRouteDot Fail\ne = $e")
            }
        }

        return routeDot
    }

    suspend fun updateRoutePublic(
        routeId: Int
    ): RoutePublicRequest {
        var isPublic = RoutePublicRequest(false)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.updateRoutePublic(routeId)
            }.onSuccess {
                isPublic = it
                Log.d("RemoteRouteDataSource", "updateRoutePublic Success\nisPublic = ${isPublic}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "updateRoutePublic Fail\ne = $e")
            }
        }

        return isPublic
    }

    suspend fun createRoute(): RouteWriteTime {
        var writeTime = RouteWriteTime("", "")
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.createRoute()
            }.onSuccess {
                writeTime = it
                Log.d("RemoteRouteDataSource", "createRoute Success\nwriteTime = ${writeTime}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "createRoute Fail\ne = $e")
            }
        }

        return writeTime
    }

    suspend fun createActivity(
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
        var activityResult = ActivityResult(
            -1, "", "", "", "", "", "", "", "", "", arrayListOf()
        )

        var activityImagesPart = mutableListOf<MultipartBody.Part?>()
        if (activityImages.isNotEmpty()) {
            for (i in 0 until activityImages.size) {
                activityImagesPart.add(MultipartBody.Part.createFormData("activityImages",
                    activityImages.get(i)!!.name, activityImages.get(i)!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                ))
            }
        }

        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.createActivity(
                    createPartFromString(routeId.toString())!!, createPartFromString(locationName)!!, createPartFromString(address)!!, createPartFromString(latitude)!!, createPartFromString(longitude)!!,
                        createPartFromString(visitDate)!!, createPartFromString(startTime)!!, createPartFromString(endTime)!!, createPartFromString(category)!!, createPartFromString(description), activityImagesPart
                )
            }.onSuccess {
                activityResult = it
                Log.d("RemoteRouteDataSource", "createActivity Success\nactivityResult = ${activityResult}")
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
                routeUpdateResult = it
                Log.d("RemoteRouteDataSource", "updateRoute Success\nrouteUpdateResult = ${routeUpdateResult}")
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
                activityUpdateResult = it
                Log.d("RemoteRouteDataSource", "updateActivity Success\nactivityUpdateResult = ${activityUpdateResult}")
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
                deleteActivityId = it
                Log.d("RemoteRouteDataSource", "deleteActivity Success\nrouteId = ${deleteActivityId}")
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
                insight = it
                Log.d("RemoteRouteDataSource", "getInsight Success\ninsight = ${insight}")
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
                reportId = it
                Log.d("RemoteRouteDataSource", "reportUser Success\nreportId = ${reportId}")
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
                reportId = it
                Log.d("RemoteRouteDataSource", "reportRoute Success\nreportId = ${reportId}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "reportRoute Fail\ne = $e")
            }
        }

        return reportId
    }

    private fun createPartFromString(value: String?): RequestBody? {
        return value?.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}