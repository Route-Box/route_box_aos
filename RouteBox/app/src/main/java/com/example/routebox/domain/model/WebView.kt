package com.example.routebox.domain.model

enum class MessageType(val text: String) {
    TOKEN("TOKEN"),
    PAGE_CHANGE("PAGE_CHANGE"),
    TOKEN_EXPIRED("TOKEN_EXPIRED")
}

// 토큰 페이로드 데이터 클래스
data class TokenPayload(
    val token: String
)

// 네이티브 메시지 데이터 클래스
data class NativeTokenRequestMessage(
    val type: String,
    val payload: TokenPayload
)