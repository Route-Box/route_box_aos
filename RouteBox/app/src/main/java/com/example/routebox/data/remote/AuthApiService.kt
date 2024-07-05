package com.example.routebox.data.remote

import com.example.routebox.domain.model.LoginRequest
import com.example.routebox.domain.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    // 로그인
    @POST("auths/kakao/signup")
    suspend fun postKakaoSDK(
        @Body body: LoginRequest
    ): LoginResponse

    // 토큰 재발급
    @POST("auths/reissuance")
    suspend fun refreshToken(
        @Body body: LoginRequest
    ): LoginResponse
}