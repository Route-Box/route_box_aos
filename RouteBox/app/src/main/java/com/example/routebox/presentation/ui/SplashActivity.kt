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
import com.example.routebox.presentation.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        // Kakao HashKey 확인할 때만 사용
        // checkHashKey()

        binding.mainIcHead.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_animation))

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            // TODO: 자동 로그인 -> MainActivity / 로그인 X -> LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))

            // 화면이 중간에 깜빡 거리는 문제가 있어 finish에 delay 추가
            handler.postDelayed({
                finish()
            }, 500)
        }, 1500)
    }

    private fun checkHashKey() {
        // var keyHash = Utility.getKeyHash(this)
        // Log.d("Hash_Key", keyHash)
    }
}