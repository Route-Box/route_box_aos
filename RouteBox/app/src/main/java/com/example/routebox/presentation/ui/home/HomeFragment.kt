package com.example.routebox.presentation.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.routebox.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
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
        var webSetting = binding.homeWebView.settings
        // WebView의 페이지에 JavaScript가 사용된 경우, 아래 코드 추가!
        webSetting.javaScriptEnabled = true
        // 컨텐츠의 크기가 WebView 보다 클 경우, 스크린에 맞게 자동 조정
        webSetting.loadWithOverviewMode = true

        // WebView 리다이렉트할 때 브라우저가 열리는 것을 방지!
        binding.homeWebView.webViewClient = WebViewClient()
        binding.homeWebView.webChromeClient = WebChromeClient()

        // TODO: URL 추가하기
        binding.homeWebView.loadUrl("https://www.naver.com")

        // WebView 뒤로가기 설정
        binding.homeWebView.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action != KeyEvent.ACTION_DOWN) return@OnKeyListener true
            // 뒤로가기 버튼을 눌렀을 때, WebView에서 뒤로가기가 된다면 뒤로가고 아니라면 종료
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (binding.homeWebView.canGoBack()) {
                    binding.homeWebView.goBack()
                } else {
                    requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() { }
                    })
                }
                return@OnKeyListener true
            }
            false
        })
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