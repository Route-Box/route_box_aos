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
    }

    private fun initClickListener() {
        // 확인 버튼 클릭
        binding.permissionConfirmBtn.setOnClickListener {
            // 회원가입 화면으로 이동
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }
}