package com.daval.routebox.presentation.ui.user

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityDeleteAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAccountActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDeleteAccountBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_delete_account)

        binding.apply {
            viewModel = this@DeleteAccountActivity.viewModel
            lifecycleOwner = this@DeleteAccountActivity
        }

        initObserve()
        initClickListener()
    }

    private fun initObserve() {
        viewModel.userInfo.observe(this) {

        }
    }

    private fun initClickListener() {
        binding.closeIv.setOnClickListener {
            finish()
        }
    }
}