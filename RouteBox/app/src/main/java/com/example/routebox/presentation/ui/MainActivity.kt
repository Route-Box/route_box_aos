package com.example.routebox.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.routebox.R
import com.example.routebox.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var backPressedTime: Long = 0 // 뒤로가기 버튼을 눌렀던 시간 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initNavigation()
    }

    override fun onBackPressed() {
        // 기존의 뒤로가기 버튼 기능 제거
        // super.onBackPressed()
        setAppFinishedFlow()
    }

    private fun initNavigation() {
        binding.mainBottomNav.itemIconTintList = null

        NavigationUI.setupWithNavController(binding.mainBottomNav, findNavController(R.id.main_nav_host))
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