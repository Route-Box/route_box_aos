package com.example.routebox.presentation.ui.my

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.routebox.databinding.FragmentMyBinding
import com.example.routebox.domain.model.MessageType
import com.example.routebox.domain.model.NativeTokenRequestMessage
import com.example.routebox.domain.model.TokenPayload
import com.example.routebox.presentation.config.ApplicationClass.Companion.dsManager
import com.example.routebox.presentation.config.Constants.ENDPOINT_MY
import com.example.routebox.presentation.config.Constants.WEB_BASE_URL
import com.example.routebox.presentation.utils.JavaScriptBridge
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentMyBinding.inflate(inflater, container, false)

        initWebViewSetting()

        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSetting() {
        binding.myWebView.settings.apply {
            this.javaScriptEnabled = true // 자바스크립트 허용
            this.loadWithOverviewMode = true //
        }

        // JavaScript 인터페이스 추가
        binding.myWebView.addJavascriptInterface(object : JavaScriptBridge() {}, JavaScriptBridge.INTF)

        binding.myWebView.apply {
            // 웹뷰 설정
            binding.myWebView.apply {
                this.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // 웹뷰가 완전히 로드된 후에 토큰 전송
                        sendTokenToWebView(this@apply)
                    }
                }
                this.webChromeClient = WebChromeClient()
                this.loadUrl("$WEB_BASE_URL$ENDPOINT_MY") // 웹 주소
            }
        }
    }

    // 네이티브 앱에서 웹뷰로 TOKEN 메시지 보내기
    private fun sendTokenToWebView(webView: WebView) {
        val nativeMessage = NativeTokenRequestMessage(
            type = MessageType.TOKEN.text,
            payload = TokenPayload("Bearer " + getSavedAccessToken())
        )

        val messageJson = Gson().toJson(nativeMessage)
        Log.d("MyFragment", "messageJson: $messageJson")

        // 웹뷰가 로드된 후에 JavaScript 함수를 호출
        webView.evaluateJavascript("javascript:sendMessageToWebView($messageJson)", null)
    }

    // 앱 내 저장된 토큰 정보 가져오기
    private fun getSavedAccessToken(): String = runBlocking {
        dsManager.getAccessToken().first().orEmpty()
    }
}