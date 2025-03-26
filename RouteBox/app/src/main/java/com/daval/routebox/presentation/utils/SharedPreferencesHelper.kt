package com.daval.routebox.presentation.utils

import android.content.SharedPreferences
import com.daval.routebox.domain.model.Activity
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.RoutePointRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kakao.vectormap.LatLng


class SharedPreferencesHelper(private val sharedPreferences: SharedPreferences) {
    // 최근 검색어 저장
    fun setRecentSearchWords(wordSet: Set<String>?) {
        sharedPreferences.edit()
            .putStringSet(RECENT_SEARCHWORD_KEY, wordSet)
            .apply()
    }

    // 최근 검색어 불러오기
    fun getRecentSearchWords(): Set<String>? {
        return sharedPreferences.getStringSet(RECENT_SEARCHWORD_KEY, null)
    }

    // 루트 기록 여부 저장
    fun setRouteTracking(isTracking: Boolean) {
        sharedPreferences.edit()
            .putBoolean(TRACKING_KEY, isTracking)
            .apply()
    }

    // 루트를 기록 중인지 확인
    fun getRouteTracking(): Boolean {
        return sharedPreferences.getBoolean(TRACKING_KEY, false)
    }

    // 기록 중인 루트 활동 데이터 저장
    fun setRouteActivity(activity: ActivityResult?) {
        sharedPreferences.edit()
            .putString(ROUTE_ACTIVITY, Gson().toJson(activity))
            .apply()
    }

    // 기록 중인 루트 활동 데이터 불러오기
    fun getRouteActivity(): ActivityResult? {
        return Gson().fromJson(sharedPreferences.getString(ROUTE_ACTIVITY, ""), object : TypeToken<ActivityResult?>() {}.type)
    }

    // 기록하기 화면인지 아닌지를 확인하기 위함
    fun setIsBackground(isBackground: Boolean) {
        sharedPreferences.edit()
            .putBoolean(TRACKING_IS_BACKGROUND, isBackground)
            .apply()
    }

    fun getIsBackground(): Boolean {
        return sharedPreferences.getBoolean(TRACKING_IS_BACKGROUND, true)
    }

    // 지도 좌표 저장
    fun setLocationCoordinate(coordinate: ArrayList<Double?>) {
        sharedPreferences.edit()
            .putString(TRACKING_COORDINATE, Gson().toJson(coordinate))
            .apply()
    }

    // 지도 좌표 불러오기
    fun getLocationCoordinate(): ArrayList<Double?> {
        return Gson().fromJson(sharedPreferences.getString(TRACKING_COORDINATE, ""), ArrayList<Double?>()::class.java)
    }

    // 앱이 종료되었을 때 기록되는 점들을 저장
    fun setBackgroundCoordinate(coordinate: ArrayList<RoutePointRequest?>?) {
        sharedPreferences.edit()
            .putString(TRACKING_BACKGROUND, Gson().toJson(coordinate))
            .apply()
    }

    // 앱이 종료되었을 때 기록된 점 불러오기
    fun getBackgroundCoordinate(): ArrayList<RoutePointRequest?>? {
        return Gson().fromJson(sharedPreferences.getString(TRACKING_BACKGROUND, ""), object : TypeToken<ArrayList<RoutePointRequest?>?>() {}.type)
    }

    // DataChangedListener 추가
    fun registerPreferences(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    // DataChangedListener 해제
    fun unregisterPreferences(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    // 루트 기록 EndTime 저장
    fun setEndTime(endTime: String) {
        sharedPreferences.edit()
            .putString(ROUTE_END_TIME, endTime)
            .apply()
    }

    // 루트 기록 EndTime 불러오기
    fun getEndTime(): String? {
        return sharedPreferences.getString(ROUTE_END_TIME, null)
    }

    companion object {
        const val APP_PREF_KEY = "app_pref"
        const val RECENT_SEARCHWORD_KEY = "recent_searchword"
        const val TRACKING_KEY = "route_tracking"
        const val ROUTE_ACTIVITY = "route_activity"
        const val ROUTE_ACTIVITY_IMAGES = "route_activity_images"
        const val TRACKING_COORDINATE = "tracking_coordinate"
        const val TRACKING_BACKGROUND = "tracking_background"
        const val TRACKING_IS_BACKGROUND = "tracking_is_background"
        const val ROUTE_END_TIME = "route_end_time"
    }
}