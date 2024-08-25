package com.example.routebox.presentation.ui.route

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteCompleteTagBinding
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.presentation.ui.common.routeStyle.FilterOptionClickListener
import com.example.routebox.presentation.ui.common.routeStyle.RouteStyleFragment
import com.example.routebox.presentation.ui.route.write.RouteCompleteBaseActivity
import com.example.routebox.presentation.ui.route.write.RouteCompleteViewModel

class RouteCompleteTagActivity: AppCompatActivity(), FilterOptionClickListener {

    private lateinit var binding: ActivityRouteCompleteTagBinding
    private lateinit var routeStyleFragment: RouteStyleFragment
    private val viewModel: RouteCompleteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_complete_tag)
        binding.apply {
            viewModel = this@RouteCompleteTagActivity.viewModel
            lifecycleOwner = this@RouteCompleteTagActivity
        }

        initClickListener()
        setRouteStyleFragment()
    }

    private fun initClickListener() {
        binding.closeIv.setOnClickListener {
            finish()
        }

        binding.completeBtn.setOnClickListener {
            startActivity(Intent(this, RouteCompleteBaseActivity::class.java))
            finish()
        }
    }

    private fun setRouteStyleFragment() {
        // 프래그먼트를 생성하고 저장
        routeStyleFragment = RouteStyleFragment.newInstance(this, isFilterScreen = false, null)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_route_style_frm, routeStyleFragment)
            .commit()
    }

    override fun onOptionItemClick(option: FilterOption, isSelected: Boolean) {
        viewModel.updateSelectedOption(option, isSelected)
        Log.d("ROUTE-TEST", "viewModel = ${viewModel.selectedOptionMap.value}")
    }
}