package com.daval.routebox.presentation.ui.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivitySettingServiceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingServiceActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_service)

        binding.apply {
            lifecycleOwner = this@SettingServiceActivity
        }

        initClickListener()
    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.settingService1Iv.setOnClickListener {  }

        binding.settingService2Iv.setOnClickListener {  }

        binding.settingService3Iv.setOnClickListener {  }
    }
}