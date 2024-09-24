package com.daval.routebox.presentation.ui.route

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityRouteWriteCompleteBinding
import com.daval.routebox.presentation.ui.route.edit.RouteCompleteTagActivity

class RouteWriteCompleteActivity: AppCompatActivity() {

    lateinit var binding: ActivityRouteWriteCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_write_complete)

        initClickListener()
    }

    private fun initClickListener() {
        binding.closeIv.setOnClickListener {
            finish()
        }

        binding.finishBtn.setOnClickListener {
            startActivity(Intent(this, RouteCompleteTagActivity::class.java).putExtra("routeId", intent.getIntExtra("routeId", -1)))
            finish()
        }

        binding.laterTv.setOnClickListener {
            // TODO: 나중에 하기 API 연결
            finish()
        }
    }
}