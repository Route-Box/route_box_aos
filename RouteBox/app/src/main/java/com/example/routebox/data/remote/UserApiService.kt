package com.example.routebox.data.remote

import com.example.routebox.domain.model.NicknameAvailabilityResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApiService {
    // 닉네임 이용 가능 여부 확인
    @GET("users/nickname/{nickname}/availability")
    suspend fun getNicknameAvailability(
        @Path("nickname") nickname: String
    ): NicknameAvailabilityResponse
}