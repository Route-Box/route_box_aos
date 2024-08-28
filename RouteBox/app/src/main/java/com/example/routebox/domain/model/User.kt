package com.example.routebox.domain.model

data class NicknameAvailabilityResponse(
    val nickname: String,
    val isAvailable: Boolean
)

data class EditProfileResponse(
    val id: Int = 0,
    val profileImage: String = "",
    val point: Int = 0,
    val gender: String = "",
    val birthDay: String = "",
    val introduction: String = ""
)