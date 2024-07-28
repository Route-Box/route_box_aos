package com.example.routebox.presentation.ui.search.search

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel: ViewModel() {
    private val _routeSearchKeyWord = MutableLiveData<String>("") // 타이틀 입력 용
    val routeSearchKeyWord: LiveData<String> = _routeSearchKeyWord

    val searchWord = MutableLiveData<String>("") // 사용자가 입력한 검색어

    private val _resentSearchWordSet = MutableLiveData<MutableSet<String>?>()
    val resentSearchWordSet: LiveData<MutableSet<String>?> = _resentSearchWordSet

    fun searchWordRoute(view: View) {
        // 검색 결과 수정
        _routeSearchKeyWord.value = searchWord.value
        //TODO: 서버에서 받아온 루트 검색 결과로 업데이트

        // 최근 검색어에 해당 검색어 저장
        updateRecentSearchWord(searchWord.value!!, SearchType.ADD)
        searchWord.value = ""
    }

    fun setCurrentSearchWord(word: String) {
        searchWord.value = word
    }

    // 최근 검색어 저장
    fun setSearchWordSet(set: Set<String>?) {
        _resentSearchWordSet.value = set as MutableSet<String>?
    }

    fun clearAllSearchWord() {
        _resentSearchWordSet.value = null
    }

    fun updateRecentSearchWord(word: String, searchType: SearchType) {
        when (searchType) {
            SearchType.ADD -> { // 추가
                _resentSearchWordSet.value?.add(word)
            }
            SearchType.DELETE -> { // 삭제
                _resentSearchWordSet.value?.remove(word)
            }
            SearchType.REBROWSING -> { // 재검색
                // 가장 최근 검색어에 저장
                _resentSearchWordSet.value?.remove(word)
                _resentSearchWordSet.value?.add(word)
                // 해당 단어로 검색 결과 보여주기
                _routeSearchKeyWord.value = word
            }
        }
    }

    fun setSearchResultTitle(): String { // 입력한 검색어로 검색 결과 타이틀 설정
        Log.d("SearchViewModel", "searchKeyWord: ${_routeSearchKeyWord.value}")
        return "\'${_routeSearchKeyWord.value}\' 루트"
    }
}

enum class SearchType() {
    ADD, // 새로 추가
    DELETE, // 삭제
    REBROWSING // 재검색
}