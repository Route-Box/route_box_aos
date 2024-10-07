package com.daval.routebox.presentation.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        checkGPSPermission()
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

            if (it.isNew) { // 새로운 유저
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

    // Background GPS 권한 허용을 위한 부분
    // Android 11 이상부터는 Background에서 접근하는 권한이 처음 권한 확인 문구에 뜨지 않는다 -> So, 추가로 권한을 한번 더 확인하여 Background에서 동작이 가능하도록 구성
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 첫 권한 확인이 완료 되었는지 확인
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SDK 29 이상일 때는 한번 더 권한 요청
                // SDK 29 미만일 경우, 항상 허용 옵션이 첫 권한 요청 화면에 뜨기 때문에 추가로 요청 X
                // 만약 첫 권한을 허용했다면, Background에서 작동하도록 "항상 허용" 권한 요청
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    && ContextCompat.checkSelfPermission(this@LoginActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this@LoginActivity, ContextCompat.getString(this@LoginActivity, R.string.gps_always_grant), Toast.LENGTH_SHORT).show()
                    ActivityCompat.requestPermissions(this@LoginActivity, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), LOCATION_BACKGROUND_PERMISSION_REQUEST_CODE)
                }
            } else {
                // 권한이 거부되었을 경우, 아래 문구 띄우기
                Toast.makeText(this@LoginActivity, ContextCompat.getString(this@LoginActivity, R.string.gps_deny), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkGPSPermission() {
        // 권한을 구분하기 위한 LOCATION_PERMISSION_REQUEST_CODE 필요!
        ActivityCompat.requestPermissions(
            this@LoginActivity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
        private const val LOCATION_BACKGROUND_PERMISSION_REQUEST_CODE = 3
    }
}