package com.example.routebox.domain.model

data class LoginResponse(
    // 베이스 리스폰스를 상속 받았으므로, 아래 내용은 포함이 되었음
//    @SerializedName("code") val code: Int = 0,
//    @SerializedName("message") val message: String = ""
    val result: LoginResult
) : BaseResponse()

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
    val newUser: Boolean
)

data class LoginRequest(
    val accessToken: String,
    val refreshToken: String
)