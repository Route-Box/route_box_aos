package com.example.routebox.presentation.ui.search.search

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.R
import com.example.routebox.databinding.ActivityFilterBinding
import com.example.routebox.domain.model.FilterOption
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class FilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding

    private val viewModel: FilterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter)

        initClickListeners()
        highlightingTitleText()
        setFilterOptions()
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

    private fun highlightingTitleText() {
        val fullText = binding.filterTitleTv.text

        val spannableString = SpannableString(fullText)

        // 시작 인덱스와 끝 인덱스 사이의 텍스트에 다른 색상 적용
        val word = String.format(resources.getString(R.string.filter_title_highlighting_text))
        val startIndex = fullText.indexOf(word)
        val endIndex = startIndex + word.length
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.main)),
            startIndex,
            endIndex,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.filterTitleTv.text = spannableString
    }

    private fun setFilterOptions() {
        val filterOptionList = FilterOption.getOptionsSortedByFilterType() // 필터 유형마다의 옵션 목록을 리스트에 저장
        val adapterList = List(filterOptionList.size) { FilterOptionsRVAdapter() } // 리사이클러뷰와 연결할 어댑터 리스트 생성
        binding.apply {
            val recyclerViewList = listOf<RecyclerView>(
                filterQuestion1WithWhomRv,
                filterQuestion2HowManyRv,
                filterQuestion3HowLongRv,
                filterQuestion4RouteStyleRv,
                filterQuestion5MeansOfTransportationRv
            )
            // 리사이클러뷰에 어댑터 연결
            recyclerViewList.forEachIndexed { index, rv ->
                rv.apply {
                    adapter = adapterList[index]
                    layoutManager = FlexboxLayoutManager(context).apply {
                        flexWrap = FlexWrap.WRAP
                        flexDirection = FlexDirection.ROW
                    }
                }
            }
        }
        // FilterType 순서대로 필터 옵션을 어댑터에 추가
        adapterList.forEachIndexed { index, adapter ->
            adapter.addOption(filterOptionList[index])
            adapter.setOptionClickListener(object : FilterOptionsRVAdapter.MyItemClickListener {
                override fun onItemClick(position: Int, isSelected: Boolean) {
                    viewModel.updateSelectedOption(filterOptionList[index].first().filterType, position, isSelected)
                }
            })
        }
    }
}