package com.daval.routebox.presentation.ui.seek.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityFilterBinding
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.presentation.ui.MainActivity
import com.daval.routebox.presentation.ui.common.routeStyle.FilterOptionClickListener
import com.daval.routebox.presentation.ui.common.routeStyle.RouteStyleFragment
import com.daval.routebox.presentation.ui.seek.search.SearchFragment.Companion.TAG_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        initData()
        initClickListeners()
        highlightingTitleText()
        setFilterFragment()
        initObserve()
    }

    private fun initData() {
        viewModel.searchWord = intent.getStringExtra("searchWord").toString() // 받아온 검색어
        viewModel.initSearchResultNum(intent.getIntExtra("searchResultNum", 0)) // 받아온 검색 결과 개수
        // 받아온 태그 리스트
        intent.getStringArrayListExtra("tagList").let { tagList ->
            Log.e("FilterActivity", "get tagList: $tagList")
            if (tagList.isNullOrEmpty()) return
            viewModel.initSelectedFilterTagList(tagList)
        }
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

        // N개 루트 보기
        binding.filterLookRouteBtn.setOnClickListener {
            // 태그 적용 결과 전달
            sendTagList()
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

    private fun sendTagList() { // 검색 화면으로 돌아가면서 데이터 넘기기
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(TAG_KEY, viewModel.getSelectedOptionNames() as ArrayList) // 적용된 태그 목록 전달
        }
        setResult(Activity.RESULT_OK, intent)
    }

    private fun highlightingTitleText() {
        binding.filterTitleTv.text = Html.fromHtml(getString(R.string.filter_title))
    }

    private fun setFilterFragment() {
        // 프래그먼트를 생성하고 저장
        routeStyleFragment = RouteStyleFragment.newInstance(
            this,
            isFilterScreen = true,
            FilterOption.findOptionsByNames(viewModel.selectedFilterTagList)
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_route_style_frm, routeStyleFragment)
            .commit()
    }

    // 아이템 클릭 시
    override fun onOptionItemClick(option: FilterOption, isSelected: Boolean) {
        viewModel.updateSelectedOption(option, isSelected)
        viewModel.selectedFilterTagList = viewModel.getSelectedOptionNames()
        Log.d("FilterActivity", "select tagList: ${viewModel.selectedFilterTagList}")
        viewModel.inquirySearchResultNum() // 필터 옵션을 선택할 때마다 조회 결과 개수 업데이트
    }
}