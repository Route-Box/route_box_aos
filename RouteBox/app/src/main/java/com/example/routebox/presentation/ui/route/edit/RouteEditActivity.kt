package com.example.routebox.presentation.ui.route.edit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteEditBinding

class RouteEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_edit)
    }
}