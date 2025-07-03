package com.daval.routebox.presentation.ui.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivitySettingUserBinding
import com.daval.routebox.domain.model.ActivityImage
import com.daval.routebox.presentation.ui.route.write.tracking.RoutePictureAlbumActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingUserActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingUserBinding
    private val viewModel: UserViewModel by viewModels()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    // 앨범 접근 권한 요청
    private fun checkVersion(): String {
        // SDK 버전에 따라 특정 버전 이상일 경우, READ_MEDIA_IMAGES, 아닐 경우, READ_EXTERNAL_STORAGE 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return Manifest.permission.READ_MEDIA_IMAGES
        else return Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // 권한 허용 요청
    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (ContextCompat.checkSelfPermission(this@SettingUserActivity, it.toString()) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this@SettingUserActivity, RoutePictureAlbumActivity::class.java)
            resultLauncher.launch(intent)
        } else {
            Log.d("ALBUM-PERMISSION", "권한 필요")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_user)

        binding.apply {
            viewModel = this@SettingUserActivity.viewModel
            lifecycleOwner = this@SettingUserActivity
        }

        initObserve()
        initClickListener()

        // 선택한 사진을 받기 위한 launcher
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK) { }
        }
    }

    private fun initObserve() {

    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.cameraCv.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@SettingUserActivity, checkVersion()) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this@SettingUserActivity, RoutePictureAlbumActivity::class.java)
                resultLauncher.launch(intent)
            } else {
                galleryPermissionLauncher.launch(checkVersion())
            }
        }
    }
}