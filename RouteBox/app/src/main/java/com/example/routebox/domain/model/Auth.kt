package com.example.routebox.domain.model

data class LoginRequest(
    val kakaoAccessToken: String,
)

data class LoginResponse(
    val isNew: Boolean,
    val loginType: String,
    val accessToken: TokenResult,
    val refreshToken: TokenResult,
)

data class TokenResult(
    val token: String,
    val expiresAt: String
)