package com.example.routebox.presentation.ui.seek.wallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRefundCompleteBinding
import com.example.routebox.presentation.ui.MainActivity

@SuppressLint("ResourceType")
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

        binding.homeBtn.setOnClickListener {
            startActivity(Intent(this@RefundCompleteActivity, MainActivity::class.java))
            finishAffinity()
        }
    }
}