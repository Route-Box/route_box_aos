package com.example.routebox.data.remote

import com.example.routebox.domain.model.Activity
import com.example.routebox.domain.model.ActivityId
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.ActivityUpdateRequest
import com.example.routebox.domain.model.Insight
import com.example.routebox.domain.model.KakaoSearchResult
import com.example.routebox.domain.model.MyRouteResponse
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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RouteApiService {
    // 루트 카카오 장소 검색
    @GET("https://dapi.kakao.com/v2/local/search/keyword")
    suspend fun searchKakaoPlace(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): KakaoSearchResult

    @GET("/api/v1/routes")
    suspend fun getSearchRouteList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ArrayList<RoutePreview>

    @GET("/api/v1/routes/{routeId}")
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

    @GET("/api/v1/routes/progress")
    suspend fun checkRouteIsRecording(
        @Query("userLocalTime") userLocalTime: String
    ): RouteId

    @POST("/api/v1/routes/{routeId}/point")
    suspend fun addRouteDot(
        @Path("routeId") routeId: Int
    ): RoutePointRequest

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

    @POST("/api/v1/routes/{routeId}/activity")
    suspend fun createActivity(
        @Path("routeId") routeId: Int,
        @Body activity: Activity
    ): ActivityResult

    @PUT("/api/v1/routes/{routeId}")
    suspend fun updateRoute(
        @Path("routeId") routeId: Int,
        @Body routeUpdateRequest: RouteUpdateRequest
    ): RouteUpdateResult

    @PUT("/api/v1/routes/{routeId}/activity/{activityId}")
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

    @DELETE("/api/v1/routes/{routeId}/activity/{activityId}")
    suspend fun deleteActivity(
        @Path("routeId") routeId: Int,
        @Path("activityId") activityId: Int
    ): ActivityId

    // 인사이트 조회
    @GET("routes/insight")
    suspend fun getInsight(): Insight

    @POST("/api/v1/reports/user")
    suspend fun reportUser(
        @Body reportUserBody: ReportUser
    ): ReportId

    @POST("/api/v1/reports/route")
    suspend fun reportRoute(
        @Body reportRouteBody: ReportRoute
    ): ReportId
}