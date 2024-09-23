package com.example.routebox.presentation.ui.my

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.routebox.databinding.FragmentMyBinding
import com.example.routebox.domain.model.MessageType
import com.example.routebox.domain.model.NativeTokenRequestMessage
import com.example.routebox.domain.model.TokenPayload
import com.example.routebox.domain.model.WebViewPage
import com.example.routebox.presentation.config.ApplicationClass.Companion.dsManager
import com.example.routebox.presentation.config.Constants.ENDPOINT_MY
import com.example.routebox.presentation.config.Constants.WEB_BASE_URL
import com.example.routebox.presentation.utils.JavaScriptBridge
import com.example.routebox.presentation.utils.NativeMessageCallback
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MyFragment : Fragment(), NativeMessageCallback {
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
        // 웹뷰 설정
        binding.myWebView.apply {
            // 세팅
            settings.javaScriptEnabled = true // JavaScript를 사용한 웹뷰를 로드한다면 활성화 필요
            settings.loadWithOverviewMode = true // 컨텐츠의 크기가 WebView 보다 클 경우, 스크린에 맞게 자동 조정
            settings.domStorageEnabled = true // 웹뷰에서 LocalStorage를 사용해야 하는 경우 활성화 필요

            addJavascriptInterface(object : JavaScriptBridge(this@MyFragment) {}, JavaScriptBridge.INTF)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // 웹뷰가 완전히 로드된 후에 토큰 전송
                    sendTokenToWebView(this@apply)
                }
            }
            webChromeClient = WebChromeClient()
            this.loadUrl("$WEB_BASE_URL$ENDPOINT_MY") // 웹 주소

            // WebView 뒤로가기 설정
            setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (event.action != KeyEvent.ACTION_DOWN) return@OnKeyListener true
                // 뒤로가기 버튼을 눌렀을 때, WebView에서 뒤로가기가 된다면 뒤로가고 아니라면 종료
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (this.canGoBack()) {
                        this.goBack()
                    } else {
                        requireActivity().onBackPressedDispatcher.addCallback(object :
                            OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {}
                        })
                    }
                    return@OnKeyListener true
                }
                false
            })
        }
    }

    // 네이티브 앱에서 웹뷰로 TOKEN 메시지 보내기
    private fun sendTokenToWebView(webView: WebView) {
        val nativeMessage = NativeTokenRequestMessage(
            type = MessageType.TOKEN.text,
            payload = TokenPayload(getSavedAccessToken())
        )

        val messageJson = Gson().toJson(nativeMessage)
        Log.d("MyFragment", "messageJson: $messageJson")

        // 웹뷰가 로드된 후에 JavaScript 함수를 호출
        webView.evaluateJavascript("sendMessageToWebView($messageJson)", null)
    }

    // 앱 내 저장된 토큰 정보 가져오기
    private fun getSavedAccessToken(): String = runBlocking {
        dsManager.getAccessToken().first().orEmpty()
    }

    override fun onMessageReceived(type: MessageType, page: WebViewPage, id: String) {
        //TODO: 콜백 처리 작업 진행
    }
}