package com.example.routebox.presentation.ui.my

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.routebox.databinding.FragmentMyBinding

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
        binding.myWebView.apply {
            // 웹뷰 설정
            this.webViewClient = WebViewClient()
            this.webChromeClient = WebChromeClient()
            //TODO: URL 변경
            this.loadUrl("https://www.google.com")
            this.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (event.action != KeyEvent.ACTION_DOWN) return@OnKeyListener true
                // 뒤로가기 버튼을 눌렀을 때, WebView에서 뒤로가기가 된다면 뒤로가고 아니라면 종료
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (this.canGoBack()) {
                        this.goBack()
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
    }
}