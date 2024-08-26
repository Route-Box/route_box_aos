package com.example.routebox.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivitySplashBinding
import com.kakao.sdk.common.util.Utility
import android.util.Log
import androidx.activity.viewModels
import com.example.routebox.presentation.ui.auth.AuthViewModel
import com.example.routebox.presentation.ui.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        // Kakao HashKey 확인할 때만 사용
        // checkHashKey()

        binding.mainIcHead.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_animation))

        initObserve()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            viewModel.tryRefreshToken()
        }, 1000)
    }

    private fun checkHashKey() {
         Log.d("Hash_Key", Utility.getKeyHash(this))
    }

    private fun initObserve() {
        viewModel.refreshResponse.observe(this) { response ->
            if (response?.accessToken == null) { // 토큰 재발급 실패 -> 로그인 화면
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            } else { // 토큰 재발급 성공 -> 메인 화면
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }
}