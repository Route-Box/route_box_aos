package com.example.routebox.data.remote.auth

import com.example.routebox.domain.model.BaseResponse
import com.example.routebox.domain.model.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApiService {
    // 토큰 재발급
    @POST("auth/tokens/refresh")
    suspend fun refreshToken(
        @Body body: LoginRequest
    ): BaseResponse
}