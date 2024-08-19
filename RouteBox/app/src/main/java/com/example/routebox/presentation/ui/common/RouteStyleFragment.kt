package com.example.routebox.presentation.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.routebox.databinding.FragmentRouteStyleBinding
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.presentation.ui.seek.search.adapter.FilterOptionsRVAdapter
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
        adapterList = List(filterOptionList.size) { FilterOptionsRVAdapter() } // 리사이클러뷰와 연결할 어댑터 리스트 정의
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
            adapter.setOptionClickListener(object : FilterOptionsRVAdapter.MyItemClickListener {
                override fun onItemClick(position: Int, isSelected: Boolean) {
                    listner?.onOptionItemClick(filterOptionList[index][position], isSelected)
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

    companion object {
        fun newInstance(listener: FilterOptionClickListener, isFilterScreen: Boolean): RouteStyleFragment {
            val fragment = RouteStyleFragment()
            fragment.listner = listener
            fragment.isFilterScreen = isFilterScreen
            return fragment
        }
    }
}