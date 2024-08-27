package com.example.routebox.domain.repositories

import com.example.routebox.domain.model.NicknameAvailabilityResponse

interface UserRepository {
    /** 유저 */
    // 닉네임 이용 가능 여부 확인
    suspend fun getNicknameAvailability(
        nickname: String
    ): NicknameAvailabilityResponse
}