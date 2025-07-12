package com.daval.routebox.presentation.ui.user

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityIntroductionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionActivity: AppCompatActivity() {

    private lateinit var binding: ActivityIntroductionBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_introduction)

        binding.apply {
            viewModel = this@IntroductionActivity.viewModel
            lifecycleOwner = this@IntroductionActivity
        }

        initObserve()
        initClickListener()
    }

    private fun initObserve() {
        viewModel.userInfo.observe(this) {

        }
    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }
    }
}