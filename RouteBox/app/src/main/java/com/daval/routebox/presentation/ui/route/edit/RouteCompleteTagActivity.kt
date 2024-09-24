package com.daval.routebox.presentation.ui.route.edit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityRouteCompleteTagBinding
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.domain.model.RouteDetail
import com.daval.routebox.presentation.ui.common.routeStyle.FilterOptionClickListener
import com.daval.routebox.presentation.ui.common.routeStyle.RouteStyleFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class RouteCompleteTagActivity : AppCompatActivity(), FilterOptionClickListener {
    private lateinit var binding: ActivityRouteCompleteTagBinding

    private val viewModel: RouteCompleteTagViewModel by viewModels()

    private lateinit var routeStyleFragment: RouteStyleFragment
    private var routeId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_complete_tag)

        binding.apply {
            viewModel = this@RouteCompleteTagActivity.viewModel
            lifecycleOwner = this@RouteCompleteTagActivity
        }
        routeId = intent.getIntExtra("routeId", -1)

        initObserve()
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
            // 루트 수정 API
            viewModel.tryEditRoute()
        }
    }

    private fun setRouteStyleFragment() {
        // 프래그먼트를 생성하고 저장
        routeStyleFragment = RouteStyleFragment.newInstance(this, isFilterScreen = false, null)
        supportFragmentManager.beginTransaction()
            .replace(R.id.route_style_frm, routeStyleFragment)
            .commit()
    }

    private fun initObserve() {
        viewModel.isEditSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                // 태그 수정 완료 후 루트 수정 화면으로 이동
                startActivity(
                    Intent(this, RouteEditBaseActivity::class.java)
                        .putExtra("route", Gson().toJson(RouteDetail())) //TODO: 루트 정보 넘기기
                        .putExtra("isEditMode", false)
                )
                finish()
            }
        }

        viewModel.setRouteId(routeId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionItemClick(option: FilterOption, isSelected: Boolean) {
        viewModel.updateSelectedOption(option, isSelected)
    }
}