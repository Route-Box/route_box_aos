package com.daval.routebox.presentation.ui.user

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityMyInquiryBinding
import com.daval.routebox.databinding.ActivitySettingInquiryBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyInquiryActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMyInquiryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_inquiry)

        binding.apply {
            lifecycleOwner = this@MyInquiryActivity
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
    }
}