package com.example.routebox.presentation.ui.route.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.Route

class RouteEditViewModel: ViewModel() {

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route

    val routeTitle: MutableLiveData<String> = MutableLiveData()

    val routeContent: MutableLiveData<String> = MutableLiveData()

    private val _isEnabledButton = MutableLiveData<Boolean>()
    val isEnabledButton: LiveData<Boolean> = _isEnabledButton

    //TODO: 현재 프래그먼트 스탭을 저장 - 이에 따른 상단 앱바 내용 수정하기

    fun setRoute(route: Route) {
        _route.value = route
    }

    fun initRouteTitleAndContent() {
        routeTitle.value = _route.value?.title
        routeContent.value = _route.value?.content
    }

    fun checkButtonEnable() {
        _isEnabledButton.value = (!routeTitle.value.isNullOrEmpty() && !routeContent.value.isNullOrEmpty())
    }
}