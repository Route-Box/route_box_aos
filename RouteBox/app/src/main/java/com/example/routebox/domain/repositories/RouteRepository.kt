package com.example.routebox.domain.repositories

import android.content.Context
import com.example.routebox.domain.model.RoutePreviewResult
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
import java.io.File

interface RouteRepository {
    /** 활동 추가하기 장소 검색 */
    suspend fun searchKakaoPlace(
        query: String,
        page: Int
    ): KakaoSearchResult

    /** 루트 탐색 */
    suspend fun getSearchRouteList(
        page: Int,
        size: Int
    ): RoutePreviewResult

    /** 루트 미리보기 상세 조회 */
    suspend fun getRouteDetailPreview(
        routeId: Int
    ): RoutePreview

    /** 구매한 루트 / 내 루트 상세 조회 */
    suspend fun getRouteDetail(
        routeId: Int
    ): RouteDetail

    /** 내 루트 목록 조회 */
    suspend fun getMyRouteList(): ArrayList<MyRoute>

    /** 기록 진행 중인 루트 여부 조회 */
    suspend fun checkRouteIsRecording(
        userLocalTime: String
    ): RouteId

    /** 루트 경로 (점) 기록 */
    suspend fun addRouteDot(
        routeId: Int
    ): RoutePointRequest

    /** 루트 공개 여부 수정 */
    suspend fun updateRoutePublic(
        routeId: Int,
        isPublic: RoutePublicRequest
    ): RoutePublicRequest

    /** 루트 생성 (루트 기록 시작) */
    suspend fun createRoute(
        startTime: String,
        endTime: String
    ): RouteId

    /** 루트 활동 추가 */
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
        activityImages: MutableList<String>
    ): ActivityResult

    /** 루트 수정 */
    suspend fun updateRoute(
        routeId: Int,
        routeUpdateRequest: RouteUpdateRequest
    ): RouteUpdateResult

    /** 루트 활동 수정 */
    suspend fun updateActivity(
        routeId: Int,
        activityId: Int,
        activityUpdateRequest: ActivityUpdateRequest
    ): ActivityResult

    /** 루트 삭제 */
    suspend fun deleteRoute(
        routeId: Int
    ): RouteId

    /** 루트 활동 삭제 */
    suspend fun deleteActivity(
        routeId: Int,
        activityId: Int
    ): ActivityId

    /** 인사이트 조회 */
    suspend fun getInsight(): Insight
}