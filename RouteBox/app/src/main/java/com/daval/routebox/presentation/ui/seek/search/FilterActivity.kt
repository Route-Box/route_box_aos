package com.daval.routebox.presentation.ui.seek.search

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityFilterBinding
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.presentation.ui.common.routeStyle.FilterOptionClickListener
import com.daval.routebox.presentation.ui.common.routeStyle.RouteStyleFragment

class FilterActivity : AppCompatActivity(), FilterOptionClickListener {
    private lateinit var binding: ActivityFilterBinding

    private val viewModel: FilterViewModel by viewModels()

    private lateinit var routeStyleFragment: RouteStyleFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter)

        binding.apply {
            viewModel = this@FilterActivity.viewModel
            lifecycleOwner = this@FilterActivity
        }

        initClickListeners()
        highlightingTitleText()
        setFilterFragment()
        initObserve()
    }

    private fun initClickListeners() {
        // 뒤로가기 버튼
        binding.filterBackIv.setOnClickListener {
            finish()
        }

        // x 버튼
        binding.filterCloseIv.setOnClickListener {
            finish()
        }
    }

    private fun initObserve() {
        viewModel.isResetButtonClick.observe(this) {
            if (it == true) {
                Log.d("FilterActivity", "리셋 버튼 클릭")
                // 프래그먼트의 resetAllOptions 호출
                routeStyleFragment.resetAllOptions()
                // 필터 옵션 초기화 진행
                viewModel.setResetDone()
            }
        }
    }

    private fun highlightingTitleText() {
        binding.filterTitleTv.text = Html.fromHtml(getString(R.string.filter_title))
    }

    private fun setFilterFragment() {
        // 프래그먼트를 생성하고 저장
        routeStyleFragment = RouteStyleFragment.newInstance(this, isFilterScreen = true, null)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_route_style_frm, routeStyleFragment)
            .commit()
    }

    override fun onOptionItemClick(option: FilterOption, isSelected: Boolean) {
        viewModel.updateSelectedOption(option, isSelected)
    }
}