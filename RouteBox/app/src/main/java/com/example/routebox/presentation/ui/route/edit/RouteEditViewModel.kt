package com.example.routebox.presentation.ui.route.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.Route

class RouteEditViewModel: ViewModel() {

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route

    // 현재 단계 ID를 담는 LiveData
    private val _stepId = MutableLiveData<Int>()
    val stepId: LiveData<Int> = _stepId

    val routeTitle: MutableLiveData<String> = MutableLiveData()

    val routeContent: MutableLiveData<String> = MutableLiveData()

    private val _isEnabledButton = MutableLiveData<Boolean>()
    val isEnabledButton: LiveData<Boolean> = _isEnabledButton

    fun setStepId(stepId: Int) {
        _stepId.value = stepId
    }

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