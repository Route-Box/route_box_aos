package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.data.datasource.RemoteAuthDataSource
import com.daval.routebox.domain.model.LoginRequest
import com.daval.routebox.domain.model.LoginResponse
import com.daval.routebox.domain.model.RefreshRequest
import com.daval.routebox.domain.model.RefreshResponse
import com.daval.routebox.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteAuthDataSource: RemoteAuthDataSource
) : AuthRepository {

    override suspend fun postKakaoLogin(body: LoginRequest): LoginResponse {
        return remoteAuthDataSource.postKakaoLogin(body)
    }

    override suspend fun postRefreshToken(body: RefreshRequest): RefreshResponse {
        return remoteAuthDataSource.postTokenRefresh(body)
    }
}