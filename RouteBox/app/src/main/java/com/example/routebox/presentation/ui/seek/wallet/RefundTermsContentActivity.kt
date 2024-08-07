package com.example.routebox.presentation.ui.seek.wallet

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRefundTermsContentBinding

class RefundTermsContentActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRefundTermsContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refund_terms_content)

        binding.contentTv.movementMethod = ScrollingMovementMethod.getInstance()

        initClickListener()

        setContentView(binding.root)
    }

    private fun initClickListener() {
        binding.closeIv.setOnClickListener {
            finish()
        }
    }
}