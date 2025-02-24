package com.daval.routebox.data.datasource

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.daval.routebox.BuildConfig
import com.daval.routebox.data.remote.KakaoApiService
import com.daval.routebox.data.remote.RouteApiService
import com.daval.routebox.domain.model.Activity
import com.daval.routebox.domain.model.ActivityId
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.BaseResponse
import com.daval.routebox.domain.model.CategoryGroupCode
import com.daval.routebox.domain.model.Insight
import com.daval.routebox.domain.model.KakaoSearchResult
import com.daval.routebox.domain.model.MyRoute
import com.daval.routebox.domain.model.PlaceMeta
import com.daval.routebox.domain.model.RegionInfo
import com.daval.routebox.domain.model.RouteDetail
import com.daval.routebox.domain.model.RouteFinishRequest
import com.daval.routebox.domain.model.RouteFinishResult
import com.daval.routebox.domain.model.RouteId
import com.daval.routebox.domain.model.RoutePointRequest
import com.daval.routebox.domain.model.RoutePointResult
import com.daval.routebox.domain.model.RoutePreview
import com.daval.routebox.domain.model.RoutePreviewResult
import com.daval.routebox.domain.model.RoutePublicRequest
import com.daval.routebox.domain.model.RouteUpdateRequest
import com.daval.routebox.domain.model.RouteUpdateResult
import com.daval.routebox.domain.model.RouteWriteTime
import com.daval.routebox.domain.model.WeatherRegionDocuments
import com.daval.routebox.domain.model.WeatherRegionMeta
import com.daval.routebox.domain.model.WeatherRegionResponse
import com.daval.routebox.presentation.utils.ImageConverter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class RemoteRouteDataSource @Inject constructor(
    private val routeApiService: RouteApiService,
    private val kakaoApiService: KakaoApiService
) {
    suspend fun searchKakaoCategory(
        categoryGroupCode: CategoryGroupCode,
        y: String,
        x: String,
        page: Int,
        radius: Int
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
                kakaoApiService.searchKakaoCategory("KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}", categoryGroupCode, y, x, page, radius)
            }.onSuccess {
                kakaoSearchResult = it
                Log.d("RemoteRouteDataSource", "searchKakaoCategory Success\nmeta = ${it.meta}\ndocuments = ${it.documents}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "searchKakaoCategory Fail\ne = $e")
            }
        }

        return kakaoSearchResult
    }

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

    suspend fun getKakaoRegionCode(
        latitude: String,
        longitude: String
    ): WeatherRegionResponse {
        var regionResponse = WeatherRegionResponse(
            WeatherRegionMeta(-1),
            mutableListOf()
        )
        withContext(Dispatchers.IO) {
            runCatching {
                kakaoApiService.getKakaoRegionCode("KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}", longitude, latitude)
            }.onSuccess {
                regionResponse = it
                Log.d("RemoteRouteDataSource", "getKakaoRegionCode Success\nregionResponse = ${regionResponse}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "getKakaoRegionCode Fail\ne = $e")
            }
        }
        return regionResponse
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
        var routePreview = RoutePreview()
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
        var routeId = RouteId(null)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.checkRouteIsRecording(checkRouteIsRecording)
            }.onSuccess {
                routeId = it
                Log.d("RemoteRouteDataSource", "checkRouteIsRecording Success\nrouteId = ${routeId}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "checkRouteIsRecording Fail\ne = $e")
            }
        }

        return routeId
    }

    suspend fun addRouteDot(
        routeId: Int,
        routePointRequest: RoutePointRequest
    ): RoutePointResult {
        var routeDot = RoutePointResult(-1)
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.addRouteDot(routeId, routePointRequest)
            }.onSuccess {
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
        var activityResult = ActivityResult()

        // 이미지 문자열을 MultipartBody.Part 형태로 변환
        val activityImagesPart = ImageConverter.getMultipartImgList(context, activityImages!!.toMutableList())
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.createActivity(
                    routeId, createPartFromString(locationName)!!, createPartFromString(address)!!, createPartFromString(latitude)!!, createPartFromString(longitude)!!,
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

    suspend fun finishRoute(
        routeId: Int,
        routeFinishRequest: RouteFinishRequest
    ): RouteFinishResult {
        var routeFinishResult = RouteFinishResult(-1, "", "", "")
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                Log.d("RemoteRouteDataSource", "routeId = $routeId / routeFinishRequest = $routeFinishRequest")
                routeApiService.finishRoute(routeId, routeFinishRequest)
            }.onSuccess {
                routeFinishResult = it
                Log.d("RemoteRouteDataSource", "finishRoute Success\nrouteFinishResult = ${routeFinishResult}")
            }.onFailure { e ->
                Log.d("RemoteRouteDataSource", "finishRoute Fail\ne = ${e}")
            }
        }

        return routeFinishResult
    }


    @SuppressLint("Range")
    fun getUriFromPath(filePath: String, context: Context): Uri {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, "_data = '$filePath'", null, null
        )
        cursor!!.moveToNext()
        val id = cursor!!.getInt(cursor!!.getColumnIndex("_id"))
        val uri =
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong())

        return uri
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
        context: Context,
        routeId: Int,
        activityId: Int,
        request: Activity,
        addedImageList: List<String>?,
        deletedActivityImageIds: List<Int>?
    ): ActivityResult {
        var activityUpdateResult = ActivityResult()
        // 이미지 문자열을 MultipartBody.Part 형태로 변환
        val activityImagesPart = ImageConverter.getMultipartImgList(context, addedImageList!!.toMutableList())
        withContext(Dispatchers.IO) {
            runCatching {
                routeApiService.updateActivity(
                    routeId, activityId,
                    createPartFromString(request.locationName)!!, createPartFromString(request.address)!!, createPartFromString(request.latitude), createPartFromString(request.longitude),
                    createPartFromString(request.visitDate)!!, createPartFromString(request.startTime)!!, createPartFromString(request.endTime)!!,
                    createPartFromString(request.category)!!, createPartFromString(request.description),
                    createRequestBodyFromList(deletedActivityImageIds), activityImagesPart
                )
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

    private fun createPartFromString(value: String?): RequestBody? {
        return value?.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun createRequestBodyFromList(imageIds: List<Int>?): RequestBody? {
        // List<Int>를 쉼표로 구분된 문자열로 변환
        val imageIdsString = imageIds?.joinToString(separator = ",")

        // 문자열을 RequestBody로 변환
        return imageIdsString?.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    // 이미지 파일을 MultipartBody.Part로 변환하는 함수
    fun createMultipartBodyPart(file: File, partName: String): MultipartBody.Part {
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }
}