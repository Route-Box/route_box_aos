package com.daval.routebox.presentation.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    override fun onStart() {
        super.onStart()

        checkPermissions() // 권한 확인
    }

    private fun initClickListener() {
        // 확인 버튼 클릭
        binding.permissionConfirmBtn.setOnClickListener {
            // 회원가입 화면으로 이동
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun checkPermissions() {
        val permissionsToRequest =mutableListOf<String>()

        // 체크해야 하는 권한 목록 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            return
        }

        checkAlertPermission()
    }

    private fun checkAlertPermission() {
        TedPermission.create()
            .setPermissions("android.permission.POST_NOTIFICATIONS")
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Log.i("AlertPermission", "permission granted")
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Log.i("AlertPermission", "permission denied ..")
                }
            })
            .check()
    }

    companion object {
        const val PERMISSION_REQUEST_CODE= 1001
    }
}