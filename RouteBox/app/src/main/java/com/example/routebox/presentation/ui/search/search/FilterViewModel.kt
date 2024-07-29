package com.example.routebox.presentation.ui.search.search

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.FilterType

class FilterViewModel: ViewModel() {
    private val _selectedOptionMap = MutableLiveData<Map<FilterType, Set<Int>>>(mapOf()) // 필터 유형: 선택된 index들
    val selectedOptionMap: LiveData<Map<FilterType, Set<Int>>> = _selectedOptionMap

    private val _isActivateButton = MutableLiveData<Boolean>() // 버튼 활성화 여부
    val isActivateButton: LiveData<Boolean> = _isActivateButton

    private val _isResetButtonClick = MutableLiveData<Boolean>() // 초기화 버튼 클릭 여부
    val isResetButtonClick: LiveData<Boolean> = _isResetButtonClick

    fun updateSelectedOption(filterType: FilterType, optionIndex: Int, isSelected: Boolean) {
        Log.d("FilterViewModel", "filterType: $filterType, \noptionIndex: $optionIndex, \nisSelected: $isSelected")
        //TODO: 필터를 선택할 때마다 검색 결과 루트 개수 업데이트
        val prevOptionMap = _selectedOptionMap.value!!.toMutableMap()
        if (prevOptionMap.contains(filterType)) { // 기존 filterType가 존재하는 경우
            val set = prevOptionMap[filterType]?.toMutableSet() ?: mutableSetOf()
            Log.d("FilterViewModel", "prevSet: $set")
            if (isSelected) { // set에 추가
                set.add(optionIndex)
            } else { // set에서 삭제
                set.remove(optionIndex)
            }
            Log.d("FilterViewModel", "newSet: $set")
            if (set.isEmpty()) { // set이 비어있을 경우
                // key도 삭제
                prevOptionMap.remove(filterType)
            } else {
                prevOptionMap[filterType] = set
            }
        } else { // 기존 filterType가 비어있을 경우
            if (!isSelected) return
            prevOptionMap[filterType] = setOf(optionIndex) // 해당 필터에 처음 추가
        }
        _selectedOptionMap.value = prevOptionMap
        Log.d("FilterViewModel", "selectedOptionMap: ${_selectedOptionMap.value}")
        checkButtonActivation()
    }

    // 버튼 활성화 여부 체크
    private fun checkButtonActivation() {
        // 필터 옵션이 하나라도 들어갔다면 초기화 버튼 활성화
        _isActivateButton.value = !_selectedOptionMap.value.isNullOrEmpty()
    }

    // 초기화 버튼 클릭
    fun clickFilterResetButton(view: View) {
        _isResetButtonClick.value = true
        _selectedOptionMap.value = emptyMap()
    }

    fun setResetDone() {
        _isResetButtonClick.value = false
        _isActivateButton.value = false
    }
}