package com.example.routebox.presentation.ui.search.search

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel: ViewModel() {
    private val _routeSearchKeyWord = MutableLiveData<String>() // 타이틀 입력 용
    val routeSearchKeyWord: LiveData<String> = _routeSearchKeyWord

    val searchWord = MutableLiveData<String>() // 사용자가 입력한 검색어

    init {
        _routeSearchKeyWord.value = "경주"
    }

    fun searchWordRoute(view: View) {
        // 검색 결과 수정
        _routeSearchKeyWord.value = searchWord.value
        //TODO: 서버에서 받아온 루트 검색 결과로 업데이트
    }

    fun setSearchTitle(): String {
        Log.d("SearchViewModel", "searchKeyWord: ${_routeSearchKeyWord.value}")
        return "\'${_routeSearchKeyWord.value}\' 루트"
    }
}