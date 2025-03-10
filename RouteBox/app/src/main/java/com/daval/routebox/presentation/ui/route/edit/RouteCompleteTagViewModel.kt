package com.daval.routebox.presentation.ui.route.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.domain.model.FilterType
import com.daval.routebox.domain.model.RouteUpdateRequest
import com.daval.routebox.domain.repositories.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteCompleteTagViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    private val selectedOptionMap: MutableLiveData<Map<FilterType, List<FilterOption>>> =
        MutableLiveData(mapOf())

    private val _routeId = MutableLiveData<Int>()
    val routeId: LiveData<Int> = _routeId

    private val _isEnabledButton = MutableLiveData<Boolean>()
    val isEnabledButton: LiveData<Boolean> = _isEnabledButton

    private val _isEditSuccess = MutableLiveData<Boolean>()
    val isEditSuccess: LiveData<Boolean> = _isEditSuccess

    fun setRouteId(routeId: Int) {
        _routeId.value = routeId
    }

    /** 루트 수정 */
    fun tryEditRoute() {
        viewModelScope.launch {
            val routeUpdateRequest = RouteUpdateRequest(
                null, null,
                whoWith = convertToServerTagData(FilterType.WITH_WHOM)?.first(),
                numberOfPeople = convertToServerTagData(FilterType.HOW_MANY)?.first()?.take(1)?.toInt(),
                numberOfDays = convertToServerTagData(FilterType.HOW_LONG)?.first(),
                routeStyles = convertToServerTagData(FilterType.ROUTE_STYLE),
                transportation = convertToServerTagData(FilterType.MEANS_OF_TRANSPORTATION)?.first()
            )
            _isEditSuccess.value = repository.updateRoute(
                _routeId.value!!,
                routeUpdateRequest
            ).routeId != -1
            Log.d("RouteCompleteTagViewModel", "EditRouteRequest: $routeUpdateRequest")
        }
    }

    fun updateSelectedOption(option: FilterOption, isSelected: Boolean) {
        val prevOptionMap = selectedOptionMap.value!!.toMutableMap()
        val updatedList = prevOptionMap[option.filterType]?.toMutableList() ?: mutableListOf()

        if (isSelected) {
            updatedList.add(option)
        } else {
            updatedList.remove(option)
        }

        if (updatedList.isEmpty()) {
            prevOptionMap.remove(option.filterType)
        } else {
            prevOptionMap[option.filterType] = updatedList
        }

        if (option == FilterOption.WITH_ALONE) {
            prevOptionMap.remove(FilterType.HOW_MANY)
        }

        selectedOptionMap.value = prevOptionMap
        Log.d("RouteEditViewModel", "selectedOptionMap: ${selectedOptionMap.value}")
        checkButtonEnable()
    }

    // 선택된 옵션 중에 '누구와 - 혼자'가 있을 경우
    private fun isHasWithAloneOption(): Boolean {
        return selectedOptionMap.value?.get(FilterType.WITH_WHOM)?.contains(FilterOption.WITH_ALONE)
            ?: false
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

    private fun checkButtonEnable() {
        _isEnabledButton.value = isAllQuestionTypeSelected()
    }


    private fun convertToServerTagData(filterType: FilterType): List<String>? {
        return selectedOptionMap.value?.get(filterType)?.map {
            it.optionName
        }
    }
}