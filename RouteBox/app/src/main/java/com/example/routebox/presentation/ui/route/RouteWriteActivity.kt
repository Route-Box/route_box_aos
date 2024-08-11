package com.example.routebox.presentation.ui.route

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteWriteBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteWriteActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRouteWriteBinding
    private val viewModel: RouteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_write)
        binding.apply {
            viewModel = this@RouteWriteActivity.viewModel
            lifecycleOwner = this@RouteWriteActivity
        }

        setTabLayout()
        initClickListener()
    }

    private fun initClickListener() {
        binding.backIv.setOnClickListener {
            finish()
        }
    }

    private fun setTabLayout() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.route_container) as NavHostFragment
        val navController = navHostFragment.navController

        binding.mapTb.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        navController.navigate(R.id.action_routeTrackingFragment_to_routeConvenienceFragment)
                    }
                    1 -> {
                        navController.navigate(R.id.action_routeConvenienceFragment_to_routeTrackingFragment)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })
    }
}