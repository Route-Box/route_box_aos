package com.example.routebox.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityMainBinding
import com.example.routebox.R
import com.example.routebox.databinding.ActivityMainBinding
import com.example.routebox.presentation.ui.route.RouteFragment
import com.example.routebox.presentation.ui.seek.SeekFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var backPressedTime: Long = 0 // 뒤로가기 버튼을 눌렀던 시간 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initNavigation()
    }

    private fun initNavigation() {
        binding.mainBottomNav.itemIconTintList = null

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.mainBottomNav, navController)

        // 뒤로가기 클릭 시 정의
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setAppFinishedFlow()
            }
        })
    }

    private fun setAppFinishedFlow() {
        if (System.currentTimeMillis() > backPressedTime + BACK_PRESSED_DURATION) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }
        // 2초 이내에 뒤로가기 버튼을 한 번 더 누르면 앱 종료
        if (System.currentTimeMillis() <= backPressedTime + BACK_PRESSED_DURATION) {
            finish()
        }
    }

    companion object {
        const val BACK_PRESSED_DURATION = 2_000L
    }
}