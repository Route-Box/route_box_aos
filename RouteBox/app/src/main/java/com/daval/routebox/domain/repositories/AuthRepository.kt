package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.LoginRequest
import com.daval.routebox.domain.model.LoginResponse
import com.daval.routebox.domain.model.RefreshRequest
import com.daval.routebox.domain.model.RefreshResponse

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