package com.daval.routebox.data.remote.auth

import com.daval.routebox.domain.model.RefreshRequest
import com.daval.routebox.domain.model.RefreshResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApiService {
    // 토큰 재발급
    @POST("auth/tokens/refresh")
    suspend fun refreshToken(
        @Body body: RefreshRequest
    ): RefreshResponse
}