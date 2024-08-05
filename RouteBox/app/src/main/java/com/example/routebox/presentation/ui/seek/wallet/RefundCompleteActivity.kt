package com.example.routebox.presentation.ui.seek.wallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRefundCompleteBinding

class RefundCompleteActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRefundCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refund_complete)

        initClickListener()
    }

    private fun initClickListener() {
        binding.closeIv.setOnClickListener {
            finish()
        }

        // TODO: 홈으로 연결 필요
        binding.homeBtn.setOnClickListener {
            finish()
        }
    }
}