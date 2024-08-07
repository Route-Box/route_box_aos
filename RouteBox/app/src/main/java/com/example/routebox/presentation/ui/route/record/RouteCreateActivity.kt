package com.example.routebox.presentation.ui.route.record

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteCreateBinding
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class RouteCreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRouteCreateBinding

    private val viewModel: RouteCreateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_create)

        binding.apply {
            viewModel = this@RouteCreateActivity.viewModel
            lifecycleOwner = this@RouteCreateActivity
        }
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val TODAY: LocalDate = LocalDate.now()
    }
}