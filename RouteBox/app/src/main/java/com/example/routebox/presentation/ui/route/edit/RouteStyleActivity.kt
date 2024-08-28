package com.example.routebox.presentation.ui.route.edit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteStyleBinding
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.RouteDetail
import com.example.routebox.presentation.ui.common.routeStyle.FilterOptionClickListener
import com.example.routebox.presentation.ui.common.routeStyle.RouteStyleFragment
import com.google.gson.Gson

class RouteStyleActivity : AppCompatActivity(), FilterOptionClickListener {
    private lateinit var binding: ActivityRouteStyleBinding

    private val viewModel: RouteStyleViewModel by viewModels()

    private lateinit var routeStyleFragment: RouteStyleFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_style)

        binding.apply {
            viewModel = this@RouteStyleActivity.viewModel
            lifecycleOwner = this@RouteStyleActivity
        }

        initClickListeners()
        setRouteStyleFragment()
    }

    private fun initClickListeners() {
        // x 버튼
        binding.routeStyleCloseIv.setOnClickListener {
            finish()
        }

        // 완료 버튼
        binding.routeStyleDoneBtn.setOnClickListener {
            // 루트 수정 화면으로 이동
            startActivity(
                Intent(this, RouteEditBaseActivity::class.java)
                    .putExtra("route", Gson().toJson(RouteDetail())) //TODO: 루트 정보 넘기기
                    .putExtra("isEditMode", false)
            )
            finish()
        }
    }

    private fun setRouteStyleFragment() {
        // 프래그먼트를 생성하고 저장
        routeStyleFragment = RouteStyleFragment.newInstance(this, isFilterScreen = false, null)
        supportFragmentManager.beginTransaction()
            .replace(R.id.route_style_frm, routeStyleFragment)
            .commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionItemClick(option: FilterOption, isSelected: Boolean) {
        viewModel.updateSelectedOption(option, isSelected)
    }
}