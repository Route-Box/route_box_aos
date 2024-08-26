package com.example.routebox.domain.repositories

import com.example.routebox.domain.model.KakaoSearchResult

interface RouteRepository {
    /** 활동 추가하기 검색 */
    suspend fun searchKakaoPlace(
        query: String,
        page: Int
    ): KakaoSearchResult
}