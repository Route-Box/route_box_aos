package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.data.remote.UserApiService
import com.example.routebox.domain.model.NicknameAvailabilityResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteUserDataSource @Inject constructor(
    private val userApiService: UserApiService
) {
    // 닉네임 중복 여부 확인
    suspend fun getNicknameAvailability(
        nickname: String
    ): NicknameAvailabilityResponse {
        var response = NicknameAvailabilityResponse(
            nickname = nickname,
            isAvailable = false
        )
        withContext(Dispatchers.IO) {
            runCatching {
                userApiService.getNicknameAvailability(nickname)
            }.onSuccess {
                Log.d("RemoteAuthDataSource", "getNicknameAvailability Success $it")
                response = it
            }.onFailure {
                Log.d("RemoteAuthDataSource", "getNicknameAvailability Fail $it")
            }
        }
        return response
    }
}