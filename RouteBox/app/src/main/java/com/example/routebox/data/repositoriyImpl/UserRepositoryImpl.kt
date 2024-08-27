package com.example.routebox.data.repositoriyImpl

import com.example.routebox.data.datasource.RemoteUserDataSource
import com.example.routebox.domain.model.NicknameAvailabilityResponse
import com.example.routebox.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteUserDataSource: RemoteUserDataSource
) : UserRepository {

    override suspend fun getNicknameAvailability(nickname: String): NicknameAvailabilityResponse {
        return remoteUserDataSource.getNicknameAvailability(nickname)
    }
}