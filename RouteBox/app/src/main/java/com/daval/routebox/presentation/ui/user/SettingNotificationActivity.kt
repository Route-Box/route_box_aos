package com.daval.routebox.presentation.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivitySettingNotificationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingNotificationActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_notification)

        binding.apply {
            lifecycleOwner = this@SettingNotificationActivity
        }

        initObserve()
        initClickListener()
    }

    private fun initObserve() {

    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.alertIv.setOnClickListener {
            binding.notification2Cv.visibility = View.VISIBLE
            binding.notification2Triangle.visibility = View.VISIBLE
        }

        binding.closeIv.setOnClickListener {
            binding.notification2Cv.visibility = View.GONE
            binding.notification2Triangle.visibility = View.GONE
        }
    }
}