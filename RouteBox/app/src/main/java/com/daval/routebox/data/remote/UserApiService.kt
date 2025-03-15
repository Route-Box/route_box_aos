package com.daval.routebox.data.remote

import com.daval.routebox.domain.model.EditProfileResponse
import com.daval.routebox.domain.model.MyInfoResponse
import com.daval.routebox.domain.model.NicknameAvailabilityResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApiService {
    // 닉네임 이용 가능 여부 확인
    @GET("users/nickname/{nickname}/availability")
    suspend fun getNicknameAvailability(
        @Path("nickname") nickname: String
    ): NicknameAvailabilityResponse

    // 내 정보 수정
    @PATCH("users/me")
    @Multipart
    suspend fun patchMyInfo(
        @Part("nickname") nickname: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("birthDay") birthDay: RequestBody?,
        @Part("introduction") introduction: RequestBody?,
        @Part profileImage: MultipartBody.Part?,
    ): EditProfileResponse

    @GET("users/me")
    suspend fun getMyInfo(): MyInfoResponse
}