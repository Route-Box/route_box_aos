package com.example.routebox.presentation.ui.route.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RouteEditViewModel: ViewModel() {

    val routeTitle: MutableLiveData<String> = MutableLiveData()

    val routeContent: MutableLiveData<String> = MutableLiveData()

    private val _isEnabledButton = MutableLiveData<Boolean>()
    val isEnabledButton: LiveData<Boolean> = _isEnabledButton

    //TODO: 현재 프래그먼트 스탭을 저장 - 이에 따른 상단 앱바 내용 수정하기

    init {
        routeTitle.value = "타이틀"
        routeContent.value = "내용"
    }

    fun checkButtonEnable() {
        _isEnabledButton.value = (!routeTitle.value.isNullOrEmpty() && !routeContent.value.isNullOrEmpty())
    }
}