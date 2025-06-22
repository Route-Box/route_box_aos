package com.daval.routebox.presentation.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivitySettingBinding
import com.daval.routebox.domain.model.DialogType
import com.daval.routebox.presentation.utils.CommonPopupDialog
import com.daval.routebox.presentation.utils.PopupDialogInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity: AppCompatActivity(), PopupDialogInterface {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        binding.apply {
            lifecycleOwner = this@SettingActivity
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
        binding.settingNotification.setOnClickListener {
            startActivity(Intent(this@SettingActivity, SettingNotificationActivity::class.java))
        }
        binding.settingService.setOnClickListener {
            startActivity(Intent(this@SettingActivity, SettingServiceActivity::class.java))
        }
        binding.logout.setOnClickListener {
            showLogoutPopupDialog()
        }
    }

    private fun showLogoutPopupDialog() {
        val dialog = CommonPopupDialog(this, DialogType.LOGOUT.id, String.format(resources.getString(R.string.logout_content)), null, null)
        dialog.isCancelable = false // 배경 클릭 막기
        dialog.show(supportFragmentManager, "PopupDialog")
    }

    override fun onClickPositiveButton(id: Int) {
        // TODO: 로그아웃 API 연동
    }

    override fun onClickNegativeButton(id: Int) { }
}