package com.example.routebox.presentation.ui.route.write

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteBaseCompleteBinding
import com.example.routebox.presentation.ui.route.edit.RouteEditViewModel

class RouteCompleteBaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteBaseCompleteBinding

    private val viewModel: RouteEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_base_complete)

        binding.apply {
            viewModel = this@RouteCompleteBaseActivity.viewModel
            lifecycleOwner = this@RouteCompleteBaseActivity
        }

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.routeCompleteBackIv.setOnClickListener {
            // 이전 화면으로 이동
            Navigation.findNavController(binding.routeCompleteContainer).popBackStack()
        }

        binding.routeCompleteCloseIv.setOnClickListener {
            // 홈 화면으로 이동
            finish()
        }
    }
}