package com.example.routebox.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityLoginBinding
import com.example.routebox.presentation.ui.MainActivity

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initClickListener()
    }

    private fun initClickListener() {
        binding.loginKakaoBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}