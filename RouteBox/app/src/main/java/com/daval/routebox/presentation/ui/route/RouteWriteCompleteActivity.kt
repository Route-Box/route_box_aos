package com.daval.routebox.presentation.ui.route

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityRouteWriteCompleteBinding
import com.daval.routebox.presentation.ui.route.edit.RouteCompleteTagActivity
import com.daval.routebox.presentation.ui.route.edit.RouteEditViewModel
import dagger.hilt.android.AndroidEntryPoint

class RouteWriteCompleteActivity: AppCompatActivity() {

    lateinit var binding: ActivityRouteWriteCompleteBinding
    var routeId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_write_complete)

        routeId = intent.getIntExtra("routeId", -1)

       initClickListener()
    }

    private fun initClickListener() {
        binding.closeIv.setOnClickListener {
            finish()
        }

        binding.finishBtn.setOnClickListener {
            startActivity(Intent(this, RouteCompleteTagActivity::class.java).putExtra("routeId", routeId))
            finish()
        }

        binding.laterTv.setOnClickListener {
            finish()
        }
    }
}