package com.example.routebox.presentation.utils

import android.util.Log
import android.webkit.JavascriptInterface
import com.example.routebox.domain.model.MessageType
import com.example.routebox.domain.model.WebViewPage
import org.json.JSONException
import org.json.JSONObject

interface NativeMessageCallback {
    fun onMessageReceived(type: MessageType, page: WebViewPage, id: String)
}

abstract class JavaScriptBridge(private val callback: NativeMessageCallback) {

    @JavascriptInterface
    fun sendMessageToNative(json: String) {
        try {
            // 전달된 문자열을 JSONObject로 변환
            val jsonObject = JSONObject(json)

            // type 필드 값 가져오기
            val type = jsonObject.getString("type")

            // payload 객체 가져오기
            val payload = jsonObject.getJSONObject("payload")

            // 페이지 식별자와 선택적 ID 가져오기
            val page = payload.getString("page")
            val id = if (payload.has("id")) payload.getString("id") else "No ID"

            // 메시지를 구성하여 Toast로 표시
            val message = "Type: $type, Page: $page, ID: $id"
            Log.d("JavaScriptBridge", "msg: $message")

            // 콜백 호출하여 데이터를 전달
            callback.onMessageReceived(MessageType.findMessageType(type), WebViewPage.findPage(page), id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val INTF = "Android"
    }
}