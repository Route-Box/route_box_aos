package com.example.routebox.domain.repositories

import com.example.routebox.domain.model.LoginRequest
import com.example.routebox.domain.model.LoginResponse
import com.example.routebox.domain.model.RefreshRequest
import com.example.routebox.domain.model.RefreshResponse

interface AuthRepository {
    /** 로그인 */
    // 카카오
    suspend fun postKakaoLogin(
        body: LoginRequest
    ): LoginResponse

    /** 토큰 재발급 */
    suspend fun postRefreshToken(
        body: RefreshRequest
    ): RefreshResponse
}