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

    companion object {
        const val APP_PREF_KEY = "app_pref"
        const val RECENT_SEARCHWORD_KEY = "recent_searchword"
    }
}