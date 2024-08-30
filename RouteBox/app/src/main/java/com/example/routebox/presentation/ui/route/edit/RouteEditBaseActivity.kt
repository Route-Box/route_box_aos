package com.example.routebox.presentation.ui.route.edit

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteBaseEditBinding
import com.example.routebox.domain.model.RouteDetail
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class RouteEditBaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteBaseEditBinding

    private val viewModel: RouteEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_base_edit)

        binding.apply {
            viewModel = this@RouteEditBaseActivity.viewModel
            lifecycleOwner = this@RouteEditBaseActivity
        }

        setInit()
        initClickListeners()
    }

    private fun setInit() {
        // intent가 넘어왔는지 확인
        intent.getStringExtra("route")?.let { routeJson ->
            val route = Gson().fromJson(routeJson, RouteDetail::class.java) // 값이 넘어왔다면 route 인스턴스에 gson 형태로 받아온 데이터를 넣어줌
            viewModel.setRoute(route)
            viewModel.initRouteTitleAndContent()
        }
        viewModel.isEditMode = intent.getBooleanExtra("isEditMode", false)
    }

    private fun initClickListeners() {
        binding.routeEditBackIv.setOnClickListener {
            // 이전 화면으로 이동
            Navigation.findNavController(binding.routeEditContainer).popBackStack()
        }

        binding.routeEditCloseIv.setOnClickListener {
            // 홈 화면으로 이동
            finish()
        }
    }
}