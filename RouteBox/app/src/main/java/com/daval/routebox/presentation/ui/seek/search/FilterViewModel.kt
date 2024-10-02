package com.daval.routebox.presentation.ui.seek.search

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.domain.model.FilterType
import com.daval.routebox.domain.repositories.SeekRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    val repository: SeekRepository
): ViewModel() {

    var searchWord = "" // 검색 화면에서 받아온 검색어

    // 필터링을 적용한 검색 결과 개수
    private val _searchResultNum = MutableLiveData<Int>(0)
    val searchResultNum: LiveData<Int> = _searchResultNum

    private val _selectedOptionMap = MutableLiveData<Map<FilterType, Set<FilterOption>>>(mapOf()) // 필터 유형: 선택된 index들
    val selectedOptionMap: LiveData<Map<FilterType, Set<FilterOption>>> = _selectedOptionMap

    var selectedFilterTagList: List<String> = emptyList()

    private val _isActivateButton = MutableLiveData<Boolean>() // 버튼 활성화 여부
    val isActivateButton: LiveData<Boolean> = _isActivateButton

    private val _isResetButtonClick = MutableLiveData<Boolean>() // 초기화 버튼 클릭 여부
    val isResetButtonClick: LiveData<Boolean> = _isResetButtonClick

    // 루트를 조회해서 결과 개수 확인
    fun inquirySearchResultNum() {
        Log.d("FilterViewModel", "받아온 검색어: $searchWord")
        viewModelScope.launch {
            _searchResultNum.value = repository.searchRoute(
                searchWord = searchWord,
                sortBy = OrderOptionType.ORDER_RECENT.serverEnum,
                withWhom = convertAnyToStringList(FilterType.WITH_WHOM),
                numberOfPeople = convertAnyToIntList(),
                numberOfDays = convertAnyToStringList(FilterType.HOW_LONG),
                routeStyle = convertAnyToStringList(FilterType.ROUTE_STYLE),
                transportation = convertAnyToStringList(FilterType.MEANS_OF_TRANSPORTATION)
            ).size
        }
    }

    fun initSearchResultNum(num: Int) {
        _searchResultNum.value = num
    }

    fun initSelectedFilterTagList(tagList: List<String>) {
        selectedFilterTagList = tagList

        // selectedFilterTagList의 값들로 FilterOption을 찾고, FilterType에 따라 그룹화
        val optionMap = selectedFilterTagList
            .mapNotNull { optionName -> FilterOption.findOptionsByNames(listOf(optionName)).firstOrNull() }
            .groupBy { it.filterType }
            .mapValues { entry -> entry.value.toSet() } // Set<FilterOption>으로 변환

        // _selectedOptionMap을 업데이트
        _selectedOptionMap.value = optionMap

        checkResetBtnActivation() // 초기화 버튼 활성화 여부 체크 (선택한 태그가 있다면 활성화)
    }

    fun updateSelectedOption(option: FilterOption, isSelected: Boolean) {
        Log.d("FilterViewModel", "option: $option, \nisSelected: $isSelected")
        val prevOptionMap = _selectedOptionMap.value!!.toMutableMap()
        if (prevOptionMap.contains(option.filterType)) { // 기존 filterType가 존재하는 경우
            val set = prevOptionMap[option.filterType]?.toMutableSet() ?: mutableSetOf()
            Log.d("FilterViewModel", "prevSet: $set")
            if (isSelected) { // set에 추가
                set.add(option)
            } else { // set에서 삭제
                set.remove(option)
            }
            Log.d("FilterViewModel", "newSet: $set")
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
        _selectedOptionMap.value = prevOptionMap
        Log.d("FilterViewModel", "selectedOptionMap: ${_selectedOptionMap.value}")
        checkResetBtnActivation()
    }

    // 버튼 활성화 여부 체크
    private fun checkResetBtnActivation() {
        // 필터 옵션이 하나라도 들어갔다면 초기화 버튼 활성화
        _isActivateButton.value = !_selectedOptionMap.value.isNullOrEmpty()
    }

    // 초기화 버튼 클릭
    fun clickFilterResetButton(view: View) {
        _isResetButtonClick.value = true // 버튼 클릭함
        // 선택했던 태그 초기화
        _selectedOptionMap.value = emptyMap()
        selectedFilterTagList = emptyList()
        inquirySearchResultNum() // 초기화 후 루트 개수 다시 조회
    }

    fun setResetDone() {
        _isResetButtonClick.value = false
        _isActivateButton.value = false
    }

    // type에 해당하는 List<Any>?를 List<String>?으로 변환
    private fun convertAnyToStringList(type: FilterType): List<String>? {
        return FilterOption.getOptionNamesByTypeAndNames(selectedFilterTagList, type)
            ?.mapNotNull { it as? String }
    }

    // type에 해당하는 List<Any>?를 List<Int>?으로 변환 ('몇 명과' 옵션의 경우 정수 리스트로 전달)
    private fun convertAnyToIntList(): List<Int>? {
        return FilterOption.getOptionNamesByTypeAndNames(selectedFilterTagList, FilterType.HOW_MANY)
            ?.mapNotNull { it as? Int }
    }

    fun getSelectedOptionNames(): List<String> {
        val titles = ArrayList<String>()

        _selectedOptionMap.value?.forEach { (_, filterOptions) ->
            filterOptions.forEach { filterOption ->
                titles.add(filterOption.optionName)  // FilterOption에서 optionName을 추출
            }
        }
        return titles
    }

}