package com.example.routebox.data.datasource

import android.util.Log
import com.example.routebox.data.remote.UserApiService
import com.example.routebox.domain.model.EditProfileResponse
import com.example.routebox.domain.model.NicknameAvailabilityResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
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
                Log.d("RemoteUserDataSource", "getNicknameAvailability Success $it")
                response = it
            }.onFailure {
                Log.d("RemoteUserDataSource", "getNicknameAvailability Fail $it")
            }
        }
        return response
    }

    // 내 정보 수정
    suspend fun patchMyInfo(
        nickname: String?,
        birth: String?,
        gender: String?,
        introduction: String?,
        profileImage: File?
    ): EditProfileResponse {
        var response = EditProfileResponse()

        val profileImagePart = profileImage?.let {
            MultipartBody.Part.createFormData(
                "profileImage",
                it.name,
                it.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
        }

        withContext(Dispatchers.IO) {
            runCatching {
                userApiService.patchMyInfo(createPartFromString(nickname), createPartFromString(gender), createPartFromString(birth), createPartFromString(introduction), profileImagePart)
            }.onSuccess {
                Log.d("RemoteUserDataSource", "patchMyInfo Success $it")
                response = it
            }.onFailure {
                Log.d("RemoteUserDataSource", "patchMyInfo Fail $it")
            }
        }
        return response
    }

    private fun createPartFromString(value: String?): RequestBody? {
        return value?.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}