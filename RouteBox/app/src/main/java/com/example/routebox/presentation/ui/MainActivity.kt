package com.example.routebox.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.routebox.R
import com.example.routebox.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initNavigation()
    }

    private fun initNavigation() {
        binding.mainBottomNav.itemIconTintList = null

        NavigationUI.setupWithNavController(binding.mainBottomNav, findNavController(R.id.main_nav_host))
    }
}