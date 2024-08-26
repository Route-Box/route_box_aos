package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.data.remote.auth.AnonymousApiService
import com.example.routebox.domain.model.LoginRequest
import com.example.routebox.domain.model.LoginResponse
import com.example.routebox.domain.model.TokenResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteAuthDataSource @Inject constructor(
    private val anonymousApiService: AnonymousApiService,
) {
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
}