package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.data.datasource.RemoteUserDataSource
import com.daval.routebox.domain.model.EditProfileResponse
import com.daval.routebox.domain.model.NicknameAvailabilityResponse
import com.daval.routebox.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteUserDataSource: RemoteUserDataSource
) : UserRepository {

    override suspend fun getNicknameAvailability(nickname: String): NicknameAvailabilityResponse {
        return remoteUserDataSource.getNicknameAvailability(nickname)
    }

    override suspend fun signup(
        nickname: String,
        birth: String,
        gender: String
    ): EditProfileResponse {
        return remoteUserDataSource.patchMyInfo(nickname, birth, gender, null, null)
    }
}