package com.daval.routebox.domain.model

enum class MessageType(val text: String) {
    TOKEN("TOKEN"),
    PAGE_CHANGE("PAGE_CHANGE"),
    TOKEN_EXPIRED("TOKEN_EXPIRED");

    companion object {
        fun findMessageType(messageTypeString: String): MessageType {
            return MessageType.entries.find { it.text == messageTypeString }
                ?: throw IllegalArgumentException("Invalid message type: $messageTypeString")
        }
    }
}

enum class WebViewPage(val viewName: String) {
    MY_ROUTE("MY_ROUTE"),
    SEARCH("SEARCH"),
    ROUTE("ROUTE"),
    COUPON("COUPON");

    companion object {
        fun findPage(pageString: String): WebViewPage {
            return entries.find { it.viewName == pageString }
                ?: throw IllegalArgumentException("Invalid page name: $pageString")
        }
    }
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