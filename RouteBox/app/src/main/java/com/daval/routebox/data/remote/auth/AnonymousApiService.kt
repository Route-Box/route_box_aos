package com.daval.routebox.data.remote.auth

import com.daval.routebox.domain.model.LoginRequest
import com.daval.routebox.domain.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AnonymousApiService {
    // 로그인
    @POST("auth/login/kakao")
    suspend fun postKakaoLogin(
        @Body body: LoginRequest
    ): LoginResponse
}