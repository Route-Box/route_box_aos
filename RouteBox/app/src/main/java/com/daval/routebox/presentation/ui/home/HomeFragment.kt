package com.daval.routebox.presentation.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daval.routebox.R
import com.daval.routebox.databinding.FragmentHomeBinding
import com.daval.routebox.domain.model.MessageType
import com.daval.routebox.domain.model.NativeTokenRequestMessage
import com.daval.routebox.domain.model.TokenPayload
import com.daval.routebox.domain.model.WebViewPage
import com.daval.routebox.presentation.config.ApplicationClass.Companion.dsManager
import com.daval.routebox.presentation.config.Constants.ENDPOINT_HOME
import com.daval.routebox.presentation.config.Constants.WEB_BASE_URL
import com.daval.routebox.presentation.ui.route.RouteDetailActivity
import com.daval.routebox.presentation.utils.WebViewBridge
import com.daval.routebox.presentation.utils.NativeMessageCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment(), NativeMessageCallback {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initWebViewSetting()

        // SDK 32 이하에서는 자동으로 알림 권한이 활성화! So, SDK 33 이상일 경우에만 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) checkNotificationPermission()

        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSetting() {

        binding.homeWebView.apply {
            // 세팅
            settings.javaScriptEnabled = true // JavaScript를 사용한 웹뷰를 로드한다면 활성화 필요
            settings.loadWithOverviewMode = true // 컨텐츠의 크기가 WebView 보다 클 경우, 스크린에 맞게 자동 조정
            settings.domStorageEnabled = true // 웹뷰에서 LocalStorage를 사용해야 하는 경우 활성화 필요

            webViewClient = WebViewClient()

            addJavascriptInterface(object : WebViewBridge(this@HomeFragment) {}, WebViewBridge.INTF)
            loadUrl("$WEB_BASE_URL$ENDPOINT_HOME")

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
    private fun sendTokenToWebView() {
        // 0.5초 지연 후 토큰 전송
        Handler(Looper.getMainLooper()).postDelayed({
            binding.homeWebView.evaluateJavascript("javascript:sendMessageToWebView(${getRequestMessage()})", null)
        }, 500)
    }

    private fun getRequestMessage(): String {
        val nativeMessage = NativeTokenRequestMessage(
            type = MessageType.TOKEN.text,
            payload = TokenPayload(getSavedAccessToken())
        )

        val messageJson = Gson().toJson(nativeMessage)
        Log.d("HomeWebView", "messageJson: $messageJson")
        return messageJson
    }

    // 앱 내 저장된 토큰 정보 가져오기
    private fun getSavedAccessToken(): String = runBlocking {
        dsManager.getAccessToken().first().orEmpty()
    }

    override fun onReactComponentLoaded(boolean: Boolean) {
        sendTokenToWebView()
    }

    // JavaScriptBridge에서 전달받은 데이터를 기반으로 화면 이동 처리
    override fun onMessageReceived(type: MessageType, page: WebViewPage, id: String) {
        when (page) {
            WebViewPage.MY_ROUTE -> { // 내 루트 탭으로 이동
                selectBottomNavTab(R.id.myRouteFragment)
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMyRouteFragment())
            }

            WebViewPage.SEARCH -> { // 탐색 탭으로 이동
                selectBottomNavTab(R.id.seekFragment)
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSeekFragment())
            }

            WebViewPage.ROUTE -> { // 루트 조회 화면으로 이동
                startActivity(
                    Intent(
                        requireActivity(),
                        RouteDetailActivity::class.java
                    ).putExtra("routeId", id.toInt())
                )
            }

            else -> Log.d("MyFragment", "Unknown page: $page")
        }
    }

    private fun selectBottomNavTab(tabId: Int) {
        // 바텀네비 아이템 선택 처리
        val bottomNavView =
            requireActivity().findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomNavView.selectedItemId = tabId
    }

    private fun checkNotificationPermission() {
        // 권한을 구분하기 위한 LOCATION_PERMISSION_REQUEST_CODE 필요!
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    }
}