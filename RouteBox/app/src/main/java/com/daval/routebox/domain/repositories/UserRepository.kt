package com.daval.routebox.domain.repositories

import com.daval.routebox.domain.model.EditProfileResponse
import com.daval.routebox.domain.model.MyInfoResponse
import com.daval.routebox.domain.model.NicknameAvailabilityResponse

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

    /** 내 유저 정보 조회 */
    suspend fun getMyInfo(): MyInfoResponse
}