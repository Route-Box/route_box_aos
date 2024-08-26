package com.example.routebox.presentation.ui.route.write

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.FilterType
import com.example.routebox.domain.model.MyRoute
import com.example.routebox.domain.repositories.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteCompleteViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    private val _route = MutableLiveData<MyRoute>()
    val route: LiveData<MyRoute> = _route

    val selectedOptionMap: MutableLiveData<Map<FilterType, Set<FilterOption>>> = MutableLiveData(mapOf())

    private val _isEnabledButton = MutableLiveData<Boolean>()
    val isEnabledButton: LiveData<Boolean> = _isEnabledButton

    val routeTitle: MutableLiveData<String> = MutableLiveData()
    val routeContent: MutableLiveData<String> = MutableLiveData()

    init {
        _route.value = MyRoute()
    }

    fun initRouteTitleAndContent() {
        routeTitle.value = _route.value?.routeName
        routeContent.value = _route.value?.routeDescription
    }

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
        if (option == FilterOption.WITH_ALONE) { // '누구와'의 혼자 옵션
            prevOptionMap.remove(FilterType.HOW_MANY) // 몇 명과 옵션 삭제
        }
        selectedOptionMap.value = prevOptionMap
        Log.d("RouteEditViewModel", "selectedOptionMap: ${selectedOptionMap.value}")
        checkButtonEnable()
    }

    // 선택된 옵션 중에 '누구와 - 혼자'가 있을 경우
    private fun isHasWithAloneOption(): Boolean {
        return selectedOptionMap.value?.get(FilterType.WITH_WHOM)?.contains(FilterOption.WITH_ALONE) ?: false
    }

    // 모든 선택지가 잘 채워졌는지 확인
    private fun isAllQuestionTypeSelected(): Boolean {
        // 모든 FilterType에 해당하는 키가 있는지 확인
        val selectedMap = selectedOptionMap.value ?: return false
        // 모든 FilterType을 가져옴
        val allFilterTypes = FilterType.entries.toSet()
        // isHasWithAloneOption()이 true인 경우 FilterType.HOW_MANY를 제외
        val requiredFilterTypes = if (isHasWithAloneOption()) {
            allFilterTypes - FilterType.HOW_MANY
        } else {
            allFilterTypes
        }
        // selectedMap의 key들과 requiredFilterTypes를 비교
        return selectedMap.keys.containsAll(requiredFilterTypes) && requiredFilterTypes.all { selectedMap[it]?.isNotEmpty() == true }
    }

    // 루트 제목과 내용이 잘 채워졌는지 확인
    private fun isAllContentFilled(): Boolean {
        return !routeTitle.value.isNullOrEmpty() && !routeContent.value.isNullOrEmpty()
    }

    // 옵션 선택 버튼 활성화 여부 체크
    private fun checkButtonEnable() {
        Log.d("ROUTE-TEST", "checkButtonEnable = ${isAllQuestionTypeSelected()}")
        _isEnabledButton.value = isAllQuestionTypeSelected()
    }

    // 루트 제목/설명 버튼 활성화 여부 체크
    fun checkContentButtonEnable() {
        _isEnabledButton.value = isAllContentFilled()
    }
}