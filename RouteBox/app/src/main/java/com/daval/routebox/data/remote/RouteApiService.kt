package com.daval.routebox.data.remote

import com.daval.routebox.domain.model.RoutePreviewResult
import com.daval.routebox.domain.model.ActivityId
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.ActivityUpdateRequest
import com.daval.routebox.domain.model.Insight
import com.daval.routebox.domain.model.MyRouteResponse
import com.daval.routebox.domain.model.ReportId
import com.daval.routebox.domain.model.ReportRoute
import com.daval.routebox.domain.model.ReportUser
import com.daval.routebox.domain.model.RouteDetail
import com.daval.routebox.domain.model.RouteId
import com.daval.routebox.domain.model.RoutePointRequest
import com.daval.routebox.domain.model.RoutePointResult
import com.daval.routebox.domain.model.RoutePreview
import com.daval.routebox.domain.model.RoutePublicRequest
import com.daval.routebox.domain.model.RouteUpdateRequest
import com.daval.routebox.domain.model.RouteUpdateResult
import com.daval.routebox.domain.model.RouteWriteTime
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RouteApiService {
    @GET("routes")
    suspend fun getSearchRouteList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): RoutePreviewResult

    @GET("routes/{routeId}")
    suspend fun getRouteDetailPreview(
        @Path("routeId") routeId: Int
    ): RoutePreview

    // 루트 상세보기
    @GET("routes/{routeId}/detail")
    suspend fun getRouteDetail(
        @Path("routeId") routeId: Int
    ): RouteDetail

    // 내 루트 목록 조회
    @GET("routes/my")
    suspend fun getMyRouteList(): MyRouteResponse

    // 기록 진행 중인 루트 여부 조회
    @GET("routes/progress")
    suspend fun checkRouteIsRecording(
        @Query("userLocalTime") userLocalTime: String
    ): RouteId

    @POST("routes/{routeId}/point")
    suspend fun addRouteDot(
        @Path("routeId") routeId: Int,
        @Body routePointRequest: RoutePointRequest
    ): RoutePointResult

    // 루트 공개여부 수정
    @PATCH("routes/{routeId}/public")
    suspend fun updateRoutePublic(
        @Path("routeId") routeId: Int,
        @Body isPublic: RoutePublicRequest
    ): RoutePublicRequest

    // 루트 생성 (루트 기록 시작)
    @POST("routes/start")
    suspend fun createRoute(
        @Body timeBody: RouteWriteTime
    ): RouteId

    @POST("routes/{routeId}/activity")
    @Multipart
    suspend fun createActivity(
        @Path("routeId") routeId: Int,
        @Part("locationName") locationName: RequestBody,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("visitDate") visitDate: RequestBody,
        @Part("startTime") startTime: RequestBody,
        @Part("endTime") endTime: RequestBody,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody?,
        @Part activityImages: List<MultipartBody.Part?>
    ): ActivityResult

    // 루트 수정
    @PUT("routes/{routeId}")
    suspend fun updateRoute(
        @Path("routeId") routeId: Int,
        @Body routeUpdateRequest: RouteUpdateRequest
    ): RouteUpdateResult

    @PUT("routes/{routeId}/activity/{activityId}")
    suspend fun updateActivity(
        @Path("routeId") routeId: Int,
        @Path("activityId") activityId: Int,
        @Body activityUpdateRequest: ActivityUpdateRequest
    ): ActivityResult

    // 루트 삭제
    @DELETE("routes/{routeId}")
    suspend fun deleteRoute(
        @Path("routeId") routeId: Int
    ): RouteId

    @DELETE("routes/{routeId}/activity/{activityId}")
    suspend fun deleteActivity(
        @Path("routeId") routeId: Int,
        @Path("activityId") activityId: Int
    ): ActivityId

    // 인사이트 조회
    @GET("routes/insight")
    suspend fun getInsight(): Insight

    @POST("reports/user")
    suspend fun reportUser(
        @Body reportUserBody: ReportUser
    ): ReportId

    @POST("reports/route")
    suspend fun reportRoute(
        @Body reportRouteBody: ReportRoute
    ): ReportId
}