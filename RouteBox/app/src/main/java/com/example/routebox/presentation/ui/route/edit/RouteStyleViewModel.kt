package com.example.routebox.presentation.ui.route.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.FilterType

class RouteStyleViewModel: ViewModel() {
    private val selectedOptionMap: MutableLiveData<Map<FilterType, Set<FilterOption>>> = MutableLiveData(mapOf())

    private val _isEnabledButton = MutableLiveData<Boolean>()
    val isEnabledButton: LiveData<Boolean> = _isEnabledButton

    fun updateSelectedOption(option: FilterOption, isSelected: Boolean) {
        val prevOptionMap = selectedOptionMap.value!!.toMutableMap()
        if (prevOptionMap.contains(option.filterType)) { // 기존 filterType가 존재하는 경우
            val set = prevOptionMap[option.filterType]?.toMutableSet() ?: mutableSetOf()
            if (isSelected) { // set에 추가
                set.add(option)
            } else { // set에서 삭제
                set.remove(option)
            }
            if (set.isEmpty()) { // set이 비어있을 경우
                // key도 삭제
                prevOptionMap.remove(option.filterType)
            } else {
                prevOptionMap[option.filterType] = set
            }
        } else { // 기존 filterType가 비어있을 경우
            if (!isSelected) return
            prevOptionMap[option.filterType] = setOf(option) // 해당 필터에 처음 추가
        }
        selectedOptionMap.value = prevOptionMap
        Log.d("RouteStyleViewModel", "selectedOptionMap: ${selectedOptionMap.value}")
        checkButtonEnable()
    }

    // 모든 선택지가 잘 채워졌는지 확인
    private fun isAllQuestionTypeSelected(): Boolean {
        // 모든 FilterType에 해당하는 키가 있는지 확인
        val selectedMap = selectedOptionMap.value ?: return false
        // 모든 FilterType을 가져옴
        val allFilterTypes = FilterType.entries.toSet()
        // selectedMap의 key들과 allFilterTypes를 비교
        return selectedMap.keys.containsAll(allFilterTypes) && allFilterTypes.all { selectedMap[it]?.isNotEmpty() == true }
    }

    private fun checkButtonEnable() {
        _isEnabledButton.value = isAllQuestionTypeSelected()
    }
}