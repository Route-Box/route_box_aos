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
import com.daval.routebox.databinding.ActivitySettingInquiryBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingInquiryActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingInquiryBinding
    private val viewModel: UserViewModel by viewModels()
    private val inquiryViewModel: InquiryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_inquiry)

        binding.apply {
            viewModel = this@SettingInquiryActivity.viewModel
            lifecycleOwner = this@SettingInquiryActivity
        }

        initObserve()
        initClickListener()
        setTabLayout()
    }

    private fun initObserve() {

    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }
    }

    private fun setTabLayout() {
        binding.inquiryTl.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        findNavController(binding.inquiryContainer).navigate(R.id.action_inquiryHistoryFragment_to_inquiryFragment)
                    }
                    1 -> {
                        findNavController(binding.inquiryContainer).navigate(R.id.action_inquiryFragment_to_inquiryHistoryFragment)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })
    }
}