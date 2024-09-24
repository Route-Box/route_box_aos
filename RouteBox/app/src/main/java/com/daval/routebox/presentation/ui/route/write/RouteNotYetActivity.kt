package com.daval.routebox.presentation.ui.route.write

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityRouteNotYetBinding

class RouteNotYetActivity :  AppCompatActivity() {

    private lateinit var binding: ActivityRouteNotYetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_not_yet)

        initClickListeners()
    }

    private fun initClickListeners() {
        // x 버튼
        binding.routeNotYetCloseIv.setOnClickListener {
            finish()
        }

        // 여행 취소 버튼
        binding.routeNotYetCancelRouteBtn.setOnClickListener {
            //TODO: 여행 취소 API 연동
        }
    }
}