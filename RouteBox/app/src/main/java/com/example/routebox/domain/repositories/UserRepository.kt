package com.example.routebox.domain.repositories

import com.example.routebox.domain.model.EditProfileResponse
import com.example.routebox.domain.model.NicknameAvailabilityResponse

interface UserRepository {
    // 닉네임 이용 가능 여부 확인
    suspend fun getNicknameAvailability(
        nickname: String
    ): NicknameAvailabilityResponse

    // 회원가입
    suspend fun signup(
        nickname: String,
        birth: String,
        gender: String
    ): EditProfileResponse
}