package com.example.routebox.presentation.ui.route

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteCompleteBinding

class RouteCompleteActivity: AppCompatActivity() {

    lateinit var binding: ActivityRouteCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_complete)

        initClickListener()
    }

    private fun initClickListener() {
        binding.closeIv.setOnClickListener {
            finish()
        }

        binding.finishBtn.setOnClickListener {
            finish()
        }
    }
}