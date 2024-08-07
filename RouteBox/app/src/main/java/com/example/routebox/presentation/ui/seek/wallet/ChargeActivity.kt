package com.example.routebox.presentation.ui.seek.wallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityChargeBinding

class ChargeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChargeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_charge)

        initClickListener()
    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }
    }
}