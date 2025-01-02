package com.daval.routebox.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.daval.routebox.R
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.Category
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles

object MapUtil {
    const val DEFAULT_ZOOM_LEVEL = 10f // 루트를 표시하는 기본 줌 레벨

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
    fun createMarkerBitmap(context: Context, category: Category, activityNumber: Int): BitmapDescriptor {
        // 커스텀 마커 레이아웃 생성
        val markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker, null)

        // 배경 이미지뷰 설정
        val backgroundImageView = markerView.findViewById<ImageView>(R.id.marker_background)
        val drawable = ContextCompat.getDrawable(context, category.categoryMarkerIcon)?.mutate()
        backgroundImageView.setImageDrawable(drawable)

        // 숫자 텍스트 설정 및 스타일 적용
        val numberTextView = markerView.findViewById<TextView>(R.id.marker_number)
        numberTextView.text = activityNumber.toString()

        // 마커 뷰 크기 설정
        markerView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

        // 마커 뷰를 비트맵으로 변환
        val bitmap = Bitmap.createBitmap(
            markerView.measuredWidth,
            markerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // RouteLine
    fun setRoutePathStyle(context: Context): RouteLineStyles {
        return RouteLineStyles.from(
            RouteLineStyle.from(6f, ContextCompat.getColor(context, R.color.main))
        )
    }
}