package com.example.routebox.data.repositoriyImpl

import com.example.routebox.data.datasource.RemoteAuthDataSource
import com.example.routebox.domain.model.LoginRequest
import com.example.routebox.domain.model.LoginResponse
import com.example.routebox.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteAuthDataSource: RemoteAuthDataSource
) : AuthRepository {

    override suspend fun postKakaoLogin(body: LoginRequest): LoginResponse {
        return remoteAuthDataSource.postKakaoLogin(body)
    }
}