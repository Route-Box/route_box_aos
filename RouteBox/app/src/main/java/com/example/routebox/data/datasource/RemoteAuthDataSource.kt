package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.data.remote.auth.AnonymousApiService
import com.example.routebox.data.remote.auth.RefreshApiService
import com.example.routebox.domain.model.LoginRequest
import com.example.routebox.domain.model.LoginResponse
import com.example.routebox.domain.model.RefreshRequest
import com.example.routebox.domain.model.RefreshResponse
import com.example.routebox.domain.model.TokenResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteAuthDataSource @Inject constructor(
    private val anonymousApiService: AnonymousApiService,
    private val refreshApiService: RefreshApiService,
) {
    // 카카오 로그인
    suspend fun postKakaoLogin(
        tokenBody: LoginRequest
    ): LoginResponse {
        var loginResponse = LoginResponse(
            isNew = false,
            loginType = "",
            accessToken = TokenResult("", ""),
            refreshToken = TokenResult("", "")
        )
        withContext(Dispatchers.IO) {
            runCatching {
                anonymousApiService.postKakaoLogin(tokenBody)
            }.onSuccess {
                Log.d("RemoteAuthDataSource", "login Success $it")
                loginResponse = it
            }.onFailure {
                Log.d("RemoteAuthDataSource", "login Fail $it")
            }
        }
        return loginResponse
    }

    // 토큰 재발급
    suspend fun postTokenRefresh(
        tokenBody: RefreshRequest
    ): RefreshResponse {
        var response = RefreshResponse(
            accessToken = TokenResult("", ""),
            refreshToken = TokenResult("", "")
        )
        withContext(Dispatchers.IO) {
            runCatching {
                refreshApiService.refreshToken(tokenBody)
            }.onSuccess {
                Log.d("RemoteAuthDataSource", "tokenRefresh Success $it")
                response = it
            }.onFailure {
                Log.d("RemoteAuthDataSource", "tokenRefresh Fail $it")
            }
        }
        return response
    }
}