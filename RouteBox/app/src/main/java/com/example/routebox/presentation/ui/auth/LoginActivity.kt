package com.example.routebox.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.BuildConfig
import com.example.routebox.R
import com.example.routebox.databinding.ActivityLoginBinding
import com.example.routebox.presentation.ui.MainActivity
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initKakaoSetting()
        initClickListener()
    }

    private fun initKakaoSetting() {
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
        KakaoMapSdk.init(this,  BuildConfig.KAKAO_API_KEY)
    }

    private fun initClickListener() {
        binding.loginKakaoBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}