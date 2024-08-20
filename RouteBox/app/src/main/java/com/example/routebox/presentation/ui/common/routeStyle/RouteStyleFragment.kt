package com.example.routebox.presentation.ui.common.routeStyle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.example.routebox.databinding.FragmentRouteStyleBinding
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.FilterType
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

interface FilterOptionClickListener {
    fun onOptionItemClick(option: FilterOption, isSelected: Boolean)
}

class RouteStyleFragment : Fragment() {
    private lateinit var binding: FragmentRouteStyleBinding

    private var listner: FilterOptionClickListener? = null
    private var isFilterScreen: Boolean = false
    private var selectedOptions: List<FilterOption>? = null

    private lateinit var adapterList: List<FilterOptionsRVAdapter>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRouteStyleBinding.inflate(inflater, container, false)

        binding.apply {
            isFilterScreen = this@RouteStyleFragment.isFilterScreen
            lifecycleOwner = this@RouteStyleFragment
        }

        setFilterOptions()
        return binding.root
    }

    private fun setFilterOptions() {
        val filterOptionList = FilterOption.getOptionsSortedByFilterType() // 필터 유형마다의 옵션 목록을 리스트에 저장
        // 리사이클러뷰와 연결할 어댑터 리스트 정의
        adapterList = List(filterOptionList.size) { FilterOptionsRVAdapter(isFilterScreen) } // 필터 화면 내에서는 모든 아이템 중복 선택 가능
        binding.apply {
            val recyclerViewList = listOf<RecyclerView>(
                question1WithWhomRv,
                question2HowManyRv,
                question3HowLongRv,
                question4RouteStyleRv,
                question5MeansOfTransportationRv
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
            selectedOptions?.let { adapter.initSelectedOptions(it) } // 선택된 아이템
            adapter.setOptionClickListener(object : FilterOptionsRVAdapter.MyItemClickListener {
                override fun onItemClick(selectedOption: FilterOption, isSelected: Boolean) {
                    if (!isFilterScreen && selectedOption.filterType == FilterType.WITH_WHOM) { // '누구와' 질문
                        if (selectedOption == FilterOption.WITH_ALONE) { // 혼자 옵션 클릭
                            // 레이아웃 숨기기
                            showHowManyLayout(View.GONE)
                        } else {
                            // 레이아웃 표시
                            showHowManyLayout(View.VISIBLE)
                        }
                    }
                    listner?.onOptionItemClick(selectedOption, isSelected)
                }
            })
        }
    }

    // 선택된 모든 옵션을 삭제
    fun resetAllOptions() {
        for (adapter in adapterList) {
            adapter.deleteAllOptions()
        }
    }

    fun showHowManyLayout(visibility: Int) {
        binding.apply {
            question2HowManyTv.visibility = visibility
            question2HowManyRv.visibility = visibility
        }
    }

    companion object {
        fun newInstance(listener: FilterOptionClickListener, isFilterScreen: Boolean, selectedOptions: List<FilterOption>?): RouteStyleFragment {
            val fragment = RouteStyleFragment()
            fragment.listner = listener
            fragment.isFilterScreen = isFilterScreen
            fragment.selectedOptions = selectedOptions
            return fragment
        }
    }
}