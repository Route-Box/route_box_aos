package com.daval.routebox.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivitySplashBinding
import com.kakao.sdk.common.util.Utility
import android.util.Log
import androidx.activity.viewModels
import com.daval.routebox.presentation.ui.auth.AuthViewModel
import com.daval.routebox.presentation.ui.auth.LoginActivity
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

        var sha1: ByteArray = byteArrayOfInts(
            0x02, 0xEF, 0xDD, 0xBF, 0xE4, 0x6F,
            0x10, 0x03, 0x04, 0xFB, 0xE2, 0xA7,
            0xBC, 0x32, 0xDB, 0x6E, 0x16, 0x7B,
            0xBD, 0xD9)
        Log.d("keyhash : ", Base64.encodeToString(sha1, Base64.NO_WRAP));

        binding.mainIcHead.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_animation))

        initObserve()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            viewModel.tryRefreshToken()
        }, 1000)
    }
    
    fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    private fun checkHashKey() {
         Log.d("Hash_Key", Utility.getKeyHash(this))
    }

    private fun initObserve() {
        viewModel.refreshResponse.observe(this) { response ->
            if (response?.accessToken?.token?.isEmpty() == true) { // 토큰 재발급 실패 -> 로그인 화면
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