package com.daval.routebox.presentation.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import com.daval.routebox.R
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.Category
import com.google.android.gms.maps.model.LatLng
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles

object MapUtil {
    const val DEFAULT_ZOOM_LEVEL = 10f // 루트를 표시하는 기본 줌 레벨

    private const val RANK_INTERVAL = 10 // activity 번호에 따른 rank 차이 (더 높은 activityNumber를 가졌다면 핀을 더 위에 표시)
    private const val RANK_OFFSET = 1 // 아이콘-텍스트 간 rank 차이 (기본적으로 텍스트는 아이콘 위에 표시)

    private const val ICON_SIZE = 60f // IconLabel의 크기를 가정
    const val TEXT_OFFSET_Y = - (ICON_SIZE / (2.3)).toFloat() // 텍스트를 이동할 offset (아이콘 중심에서 약간 위로 이동)

    // 루트 경로의 평균 좌표로 지도 중심에 위치할 지점을 반환
    fun getRoutePathCenterPoint(activities: List<ActivityResult>): LatLng {
        val routeActivityList = getLatLngRoutePath(activities)
        val avgLat = routeActivityList.map { it.latitude }.average()
        val avgLng = routeActivityList.map { it.longitude }.average()
        Log.d("MapUtil", "latitude: $avgLat, longitude: $avgLng")
        return LatLng(
            avgLat, avgLng
        )
    }

    // 루트 경로를 그릴 LatLng 리스트 반환
    fun getLatLngRoutePath(activities: List<ActivityResult>): List<LatLng> {
        Log.d("MapUtil", "activities: $activities")
        //TODO: 활동 경로 외에도 점들로 기록한 routePath 추가
        return activities.map {
            LatLng(it.latitude.toDouble(), it.longitude.toDouble())
        }
    }

    /** 스타일 관련 */
    // IconLabel
    private fun setMapIconLabelStyles(category: Category): LabelStyles {
        return LabelStyles.from(
            LabelStyle.from(category.categoryMarkerIcon)
        )
    }

    fun getMapActivityIconLabelOptions(latLng: com.kakao.vectormap.LatLng, category: Category, activityNumber: Int): LabelOptions {
        return LabelOptions.from(latLng)
            .setStyles(setMapIconLabelStyles(category))
            .setRank((activityNumber * RANK_INTERVAL).toLong()) // activityNumber가 클수록 높은 rank를 가짐
    }

    // TextLabel
    private fun setMapTextLabelStyle(): LabelStyles {
        return LabelStyles.from(
            LabelStyle.from(LabelTextStyle.from(28, Color.WHITE))
        )
    }

    fun getMapActivityNumberLabelOptions(latLng: com.kakao.vectormap.LatLng, activityNumber: Int): LabelOptions {
        return LabelOptions.from(latLng)
            .setStyles(setMapTextLabelStyle())
            .setTexts(LabelTextBuilder().setTexts(activityNumber.toString()))
            .setRank((activityNumber * RANK_INTERVAL + RANK_OFFSET).toLong()) // 텍스트는 아이콘보다 높은 rank를 가짐
    }

    // RouteLine
    fun setRoutePathStyle(context: Context): RouteLineStyles {
        return RouteLineStyles.from(
            RouteLineStyle.from(6f, ContextCompat.getColor(context, R.color.main))
        )
    }
}