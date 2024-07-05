package com.example.routebox.domain.repositories

import com.example.routebox.domain.model.LoginRequest
import com.example.routebox.domain.model.LoginResponse

interface AuthRepository {
    /** 로그인 */
    // 카카오
    suspend fun postKakaoLogin(
        body: LoginRequest
    ): LoginResponse
}