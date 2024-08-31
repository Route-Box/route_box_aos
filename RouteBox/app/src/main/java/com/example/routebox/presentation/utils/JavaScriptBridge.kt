package com.example.routebox.presentation.utils

import android.webkit.JavascriptInterface
import com.google.gson.Gson

abstract class JavaScriptBridge() {

    private var form: WebViewDataForm? = null

    // 웹에서 들어오는 모든 처리는 dispatcher 메서드에서 이루어짐
    @JavascriptInterface
    fun dispatcher(jsonString: String) {
        form = Gson().fromJson(jsonString, WebViewDataForm::class.java)

        val method = this::class.java.methods.first { it.name == form!!.getFunction() }

        method.invoke(this, form!!.getParameters())
    }

    // 웹에서 전송된 파라미터 값들에 대해 Object화 시킨 것
    private class WebViewDataForm {
        private var funcName: String = ""
        private var data: HashMap<String, @JvmSuppressWildcards Any>? = null

        fun getFunction() = funcName
        fun getParameters() = data
    }

    companion object {
        const val INTF = "BridgeRouter"
    }
}