package com.example.routebox.presentation.ui.search.search

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel: ViewModel() {
    private val _routeSearchKeyWord = MutableLiveData<String>("") // 타이틀 입력용
    val routeSearchKeyWord: LiveData<String> = _routeSearchKeyWord

    val searchWord = MutableLiveData<String>("") // 사용자가 입력한 검색어

    private val _resentSearchWordSet = MutableLiveData<MutableSet<String>?>(linkedSetOf())
    val resentSearchWordSet: LiveData<MutableSet<String>?> = _resentSearchWordSet

    // 루트 검색
    fun inputRouteSearchWord(view: View) {
        Log.d("SearchViewModel", "검색어: ${searchWord.value}")
        // 검색 결과 수정
        _routeSearchKeyWord.value = searchWord.value
        //TODO: 서버에서 받아온 루트 검색 결과로 업데이트

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

    fun updateRecentSearchWord(word: String, searchType: SearchType) {
        when (searchType) {
            SearchType.ADD -> { // 추가
                if (_resentSearchWordSet.value!!.contains(word)) { // 이미 존재하는 검색어를 다시 입력했을 겨우
                    // 이전 검색어 삭제
                    _resentSearchWordSet.value?.remove(word)
                }
                if (_resentSearchWordSet.value!!.size >= MAX_RECENT_SEARCHWORD) { // 최대 검색어 개수를 넘겼을 경우
                    // 가장 오래된 검색어 삭제
                    _resentSearchWordSet.value?.remove(_resentSearchWordSet.value!!.last())
                }
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
        Log.d("SearchViewModel", "최근 검색어: ${_resentSearchWordSet.value}")
    }

    companion object {
        const val MAX_RECENT_SEARCHWORD = 10 // 최근 검색어 최대 저장 개수
    }
}

enum class SearchType() {
    ADD, // 새로 추가
    DELETE, // 삭제
    REBROWSING // 재검색
}