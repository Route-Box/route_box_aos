package com.example.routebox.presentation.ui.search.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.FilterType

class FilterViewModel: ViewModel() {
    private val _selectedOptionMap = MutableLiveData<Map<FilterType, Set<Int>>>() // 필터 유형: 선택된 index들
    val selectedOptionMap: LiveData<Map<FilterType, Set<Int>>> = _selectedOptionMap

    fun updateSelectedOption(filterType: FilterType, optionIndex: Int, isSelected: Boolean) {
        //TODO: 필터를 선택할 때마다 검색 결과 루트 개수 업데이트
        _selectedOptionMap.value?.let {
            val prevOptionMap = _selectedOptionMap.value as MutableMap
            if (prevOptionMap.contains(filterType)) { // 기존 filterType가 존재하는 경우
                val set = prevOptionMap[filterType] as MutableSet
                if (isSelected) { // set에 추가
                    set.add(optionIndex)
                } else { // set에서 삭제
                    set.remove(optionIndex)
                }
                prevOptionMap[filterType] = set
            } else { // 기존 filterType가 비어있을 경우
                if (!isSelected) return
                prevOptionMap[filterType] = setOf(optionIndex) // 처음 추가
            }
            _selectedOptionMap.value = prevOptionMap
        }
    }

    fun activateButton() {
        //TODO: 필터 옵션이 하나라도 들어갔다면 초기화 버튼 활성화
    }
}