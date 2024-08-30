package com.example.routebox.data.remote

import com.example.routebox.domain.model.RoutePreviewResult
import com.example.routebox.domain.model.Activity
import com.example.routebox.domain.model.ActivityId
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.ActivityUpdateRequest
import com.example.routebox.domain.model.Insight
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

    @GET("routes/{routeId}/detail")
    suspend fun getRouteDetail(
        @Path("routeId") routeId: Int
    ): RouteDetail

    @GET("routes/my")
    suspend fun getMyRouteList(): ArrayList<MyRoute>

    @GET("routes/progress")
    suspend fun checkRouteIsRecording(
        @Query("userLocalTime") userLocalTime: String
    ): RouteId

    @POST("routes/{routeId}/point")
    suspend fun addRouteDot(
        @Path("routeId") routeId: Int
    ): RoutePointRequest

    @PATCH("routes/{routeId}/public")
    suspend fun updateRoutePublic(
        @Path("routeId") routeId: Int
    ): RoutePublicRequest

    @POST("routes/start")
    suspend fun createRoute(): RouteWriteTime

    @POST("routes/{routeId}/activity")
    @Multipart
    suspend fun createActivity(
        @Path("routeId") routeId: RequestBody,
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

    @DELETE("routes/{routeId}")
    suspend fun deleteRoute(
        @Path("routeId") routeId: Int
    ): RouteId

    @DELETE("routes/{routeId}/activity/{activityId}")
    suspend fun deleteActivity(
        @Path("routeId") routeId: Int,
        @Path("activityId") activityId: Int
    ): ActivityId

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