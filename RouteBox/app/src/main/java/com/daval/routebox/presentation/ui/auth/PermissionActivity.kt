package com.daval.routebox.presentation.ui.auth

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityPermissionBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity: AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission)

        initClickListener()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()

        checkPermissions() // 권한 확인
    }

    private fun initClickListener() {
        // 확인 버튼 클릭
        binding.permissionConfirmBtn.setOnClickListener {
            // 회원가입 화면으로 이동
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissions() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Log.i("TedPermission", "permission granted")
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Log.i("TedPermission", "permission denied ..")
                }
            })
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]") // 권한이 없을 경우 띄울 문구
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.POST_NOTIFICATIONS) // 체크할 권한 확인
            .check()
    }
}