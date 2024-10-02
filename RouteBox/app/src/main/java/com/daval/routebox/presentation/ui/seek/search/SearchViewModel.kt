package com.daval.routebox.presentation.ui.seek.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.R
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.domain.model.FilterType
import com.daval.routebox.domain.model.SearchRoute
import com.daval.routebox.domain.repositories.SeekRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val repository: SeekRepository
): ViewModel() {

    private val _searchResultRoutes = MutableLiveData<List<SearchRoute>>() // 검색 결과 조회
    val searchResultRoutes: LiveData<List<SearchRoute>> = _searchResultRoutes

    private val _routeSearchKeyWord = MutableLiveData<String>("") // 타이틀 입력용
    val routeSearchKeyWord: LiveData<String> = _routeSearchKeyWord

    val searchWord = MutableLiveData<String>("") // 사용자가 입력한 검색어

    private val _resentSearchWordSet = MutableLiveData<MutableSet<String>?>(linkedSetOf()) // 최근 검색어
    val resentSearchWordSet: LiveData<MutableSet<String>?> = _resentSearchWordSet

    private val _selectedOrderOption = MutableLiveData<OrderOptionType>(OrderOptionType.ORDER_RECENT) // 선택된 검색 결과 정렬 옵션
    val selectedOrderOption: LiveData<OrderOptionType> = _selectedOrderOption

    private val _selectedFilterTagList = MutableLiveData<List<String>>(emptyList()) // 선택한 검색 태그 (optionsName)
    val selectedFilterTagList: LiveData<List<String>> = _selectedFilterTagList

    // 루트 검색
    fun inputRouteSearchWord() {
        Log.d("SearchViewModel", "입력한 검색어: ${searchWord.value}")
        // 검색 결과 수정
        _routeSearchKeyWord.value = searchWord.value
        // 루트 검색 결과 저장
        viewModelScope.launch {
            _searchResultRoutes.value = repository.searchRoute(
                searchWord = searchWord.value!!,
                sortBy = _selectedOrderOption.value!!.serverEnum,
                withWhom = convertAnyToStringList(FilterType.WITH_WHOM),
                numberOfPeople = convertAnyToIntList(),
                numberOfDays = convertAnyToStringList(FilterType.HOW_LONG),
                routeStyle = convertAnyToStringList(FilterType.ROUTE_STYLE),
                transportation = convertAnyToStringList(FilterType.MEANS_OF_TRANSPORTATION)
            )
        }

        // 최근 검색어에 해당 검색어 저장
        updateRecentSearchWord(searchWord.value!!, SearchType.ADD)
    }

    fun setCurrentSearchWord(word: String) {
        searchWord.value = word
    }

    // 최근 검색어 저장
    fun setRecentSearchWordSet(set: Set<String>?) {
        if (set == null) {
            _resentSearchWordSet.value = linkedSetOf()
            return
        }
        _resentSearchWordSet.value = set as MutableSet<String>?
    }

    // 최근 검색어 모두 초기화
    fun clearAllRecentSearchWords() {
        _resentSearchWordSet.value = linkedSetOf()
    }

    // 최근 검색어 업데이트
    fun updateRecentSearchWord(word: String, searchType: SearchType) {
        val tempSet = _resentSearchWordSet.value!!
        when (searchType) {
            SearchType.ADD -> { // 추가
                if (_resentSearchWordSet.value!!.contains(word)) { // 이미 존재하는 검색어를 다시 입력했을 경우
                    tempSet.remove(word) // 이전 검색어 삭제
                }
                if (_resentSearchWordSet.value!!.size >= MAX_RECENT_SEARCHWORD) { // 최대 검색어 개수를 넘겼을 경우
                    tempSet.remove(_resentSearchWordSet.value!!.last()) // 가장 오래된 검색어 삭제
                }
                tempSet.add(word)
            }
            SearchType.DELETE -> { // 삭제
                tempSet.remove(word)
            }
            SearchType.REBROWSING -> { // 재검색
                // 가장 최근 검색어에 저장
                tempSet.remove(word)
                tempSet.add(word)
                // 해당 단어로 검색 결과 보여주기
                _routeSearchKeyWord.value = word
            }
        }
        _resentSearchWordSet.value = tempSet // 관측을 위해 다시 저장
    }

    // 선택한 정렬 옵션 값 업데이트
    fun updateSelectedOrderOption(type: OrderOptionType) {
        _selectedOrderOption.value = type
    }

    // 선택한 태그 리스트 업데이트
    fun updateSelectedTagList(tagList: List<String>) {
        _selectedFilterTagList.value = tagList
    }

    // type에 해당하는 List<Any>?를 List<String>?으로 변환
    private fun convertAnyToStringList(type: FilterType): List<String>? {
        return FilterOption.getOptionNamesByTypeAndNames(_selectedFilterTagList.value!!, type)
            ?.mapNotNull { it as? String }
    }

    // type에 해당하는 List<Any>?를 List<Int>?으로 변환 ('몇 명과' 옵션의 경우 정수 리스트로 전달)
    private fun convertAnyToIntList(): List<Int>? {
        return FilterOption.getOptionNamesByTypeAndNames(_selectedFilterTagList.value!!, FilterType.HOW_MANY)
            ?.mapNotNull { it as? Int }
    }

    companion object {
        const val MAX_RECENT_SEARCHWORD = 10 // 최근 검색어 최대 저장 개수
    }
}

// 검색어 입력 유형
enum class SearchType {
    ADD, // 새로 추가
    DELETE, // 삭제
    REBROWSING // 재검색
}

// 정렬 기준 타입
enum class OrderOptionType(val serverEnum: String, val stringResourceId: Int) {
    ORDER_RECENT("NEWEST", R.string.search_order_menu_recent), // 최신 순
    ORDER_OLD("OLDEST", R.string.search_order_menu_old), // 오래된 순
    ORDER_POPULARITY("POPULAR", R.string.search_order_menu_popularity), // 인기 순
    ORDER_COMMENT("COMMENTS", R.string.search_order_menu_many_comment); // 댓글 많은 순
}