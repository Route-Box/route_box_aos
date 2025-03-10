package com.daval.routebox.domain.repositories

import android.content.Context
import com.daval.routebox.domain.model.Activity
import com.daval.routebox.domain.model.RoutePreviewResult
import com.daval.routebox.domain.model.ActivityId
import com.daval.routebox.domain.model.CategoryGroupCode
import com.daval.routebox.domain.model.Insight
import com.daval.routebox.domain.model.KakaoSearchResult
import com.daval.routebox.domain.model.MyRoute
import com.daval.routebox.domain.model.RouteDetail
import com.daval.routebox.domain.model.RouteFinishRequest
import com.daval.routebox.domain.model.RouteFinishResult
import com.daval.routebox.domain.model.RouteId
import com.daval.routebox.domain.model.RoutePointRequest
import com.daval.routebox.domain.model.RoutePointResult
import com.daval.routebox.domain.model.RoutePreview
import com.daval.routebox.domain.model.RoutePublicRequest
import com.daval.routebox.domain.model.RouteUpdateRequest
import com.daval.routebox.domain.model.RouteUpdateResult
import com.daval.routebox.domain.model.WeatherRegionResponse

interface RouteRepository {
    /** 편의 기능 카테고리 검색 */
    suspend fun searchKakaoCategory(
        categoryGroupCode: CategoryGroupCode,
        y: String,
        x: String,
        page: Int,
        radius: Int
    ): KakaoSearchResult

    /** 활동 추가하기 장소 검색 */
    suspend fun searchKakaoPlace(
        query: String,
        page: Int
    ): KakaoSearchResult

    /** 날씨 행정구역 조회 */
    suspend fun getKakaoRegionCode(
        latitude: String,
        longitude: String
    ): WeatherRegionResponse

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
        routeId: Int,
        routePointRequest: RoutePointRequest
    ): RoutePointResult

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
        activityImages: List<String>?
    ): Boolean

    /** 루트 마무리하기 */
    suspend fun finishRoute(
        routeId: Int,
        routeFinishRequest: RouteFinishRequest
    ): RouteFinishResult

    /** 루트 수정 */
    suspend fun updateRoute(
        routeId: Int,
        routeUpdateRequest: RouteUpdateRequest
    ): RouteUpdateResult

    /** 루트 활동 수정 */
    suspend fun updateActivity(
        context: Context,
        routeId: Int,
        activityId: Int,
        activityRequest: Activity,
        addedImageList: List<String>?,
        deletedActivityImageIds: List<Int>?
    ): Boolean

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