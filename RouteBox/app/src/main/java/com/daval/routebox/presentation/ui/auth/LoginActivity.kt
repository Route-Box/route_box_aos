package com.daval.routebox.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityLoginBinding
import com.daval.routebox.presentation.ui.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initObserve()
        initClickListener()
    }

    private fun initClickListener() {
        binding.loginKakaoBtn.setOnClickListener {
            startKakaoLogin()
        }
    }

    private fun startKakaoLogin() {
        // 카카오계정 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {  // 토큰 에러 발생
                Log.e("KakaoLogin", "카카오 로그인 실패 $error")
                if (error.toString().contains("statusCode=302")){ // 카카오톡 설치는 되어있지만, 로그인이 안 되어있는 경우 예외 처리
                    // 카카오 계정으로 로그인
                    loginWithKakaoAccount()
                }
            } else if (token != null) {
                Log.d("KakaoLogin", "카카오 로그인 성공 ${token.accessToken}")

                viewModel.tryLogin(token.accessToken)
            }
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) { // 카카오톡이 설치되어 있으면 카카오톡으로 로그인
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else { // 아니면 카카오계정으로 로그인
            loginWithKakaoAccount()
        }
    }

    private fun loginWithKakaoAccount() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (token != null) {
                viewModel.tryLogin(token.accessToken)
            }
        }
        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
    }

    private fun initObserve() {
        viewModel.loginResponse.observe(this) {
            if (it == null) return@observe

            if (it.isNew) {
                // 권한 확인 화면으로 이동
                startActivity(Intent(this, PermissionActivity::class.java))
                finish()
                return@observe
            }
            if (it.accessToken.token.isNotEmpty()) {
                // Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                // 메인 화면으로 이동
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}