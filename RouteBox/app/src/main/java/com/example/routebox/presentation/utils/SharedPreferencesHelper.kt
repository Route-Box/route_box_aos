package com.example.routebox.presentation.utils

import android.content.SharedPreferences

class SharedPreferencesHelper(private val sharedPreferences: SharedPreferences) {
    // 최근 검색어 불러오기
    fun getRecentSearchWords(): Set<String>? {
        return sharedPreferences.getStringSet(RECENT_SEARCHWORD_KEY, null)
    }

    // 최근 검색어 저장
    fun setRecentSearchWords(wordSet: Set<String>?) {
        sharedPreferences.edit()
            .putStringSet(RECENT_SEARCHWORD_KEY, wordSet)
            .apply()
    }

    // 루트를 기록 중인지 확인
    fun getRouteTracking(): Boolean {
        return sharedPreferences.getBoolean(TRACKING_KEY, false)
    }

    // 루트 기록 여부 저장
    fun setRouteTracking(isTracking: Boolean) {
        sharedPreferences.edit()
            .putBoolean(TRACKING_KEY, isTracking)
            .apply()
    }

    companion object {
        const val APP_PREF_KEY = "app_pref"
        const val RECENT_SEARCHWORD_KEY = "recent_searchword"
        const val TRACKING_KEY = "route_tracking"
    }
}