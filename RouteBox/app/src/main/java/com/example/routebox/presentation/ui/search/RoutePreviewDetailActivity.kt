package com.example.routebox.presentation.ui.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRoutePreviewDetailBinding

class RoutePreviewDetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRoutePreviewDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_preview_detail)
    }
}