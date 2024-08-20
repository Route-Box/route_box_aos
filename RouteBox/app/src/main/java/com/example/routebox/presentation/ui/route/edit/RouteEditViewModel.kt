package com.example.routebox.presentation.ui.route.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.FilterType
import com.example.routebox.domain.model.Route

class RouteEditViewModel: ViewModel() {

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route

    // 현재 단계 ID를 담는 LiveData
    private val _stepId = MutableLiveData<Int>()
    val stepId: LiveData<Int> = _stepId

    val routeTitle: MutableLiveData<String> = MutableLiveData()

    val routeContent: MutableLiveData<String> = MutableLiveData()

    private val selectedOptionMap: MutableLiveData<Map<FilterType, Set<FilterOption>>> = MutableLiveData(mapOf())

    private val _isEnabledButton = MutableLiveData<Boolean>()
    val isEnabledButton: LiveData<Boolean> = _isEnabledButton

    fun setStepId(stepId: Int) {
        _stepId.value = stepId
    }

    fun setRoute(route: Route) {
        _route.value = route
        initSelectedOptionMap(FilterOption.findOptionsByNames(_route.value!!.tags))
    }

    private fun initSelectedOptionMap(filterOptions: List<FilterOption>) {
        // 현재 selectedOptionMap을 가져옴
        val currentMap = selectedOptionMap.value?.toMutableMap() ?: mutableMapOf()

        // filterOptions 리스트를 순회하며 각 FilterOption의 filterType에 따라 그룹화
        filterOptions.groupBy { it.filterType }.forEach { (filterType, options) ->
            // 필터 타입에 따라 Set<FilterOption>으로 변환하여 맵에 저장
            currentMap[filterType] = options.toSet()
        }

        // MutableLiveData에 새로운 맵을 저장
        selectedOptionMap.value = currentMap
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
        selectedOptionMap.value = prevOptionMap
        Log.d("RouteEditViewModel", "selectedOptionMap: ${selectedOptionMap.value}")
        checkButtonEnable()
    }

    fun initRouteTitleAndContent() {
        routeTitle.value = _route.value?.title
        routeContent.value = _route.value?.content
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

    fun checkButtonEnable() {
        _isEnabledButton.value = (!routeTitle.value.isNullOrEmpty() && !routeContent.value.isNullOrEmpty() && isAllQuestionTypeSelected())
    }
}