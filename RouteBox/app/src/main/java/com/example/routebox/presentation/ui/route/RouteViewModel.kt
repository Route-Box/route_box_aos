package com.example.routebox.presentation.ui.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.Route

class RouteViewModel: ViewModel() {
    private val _routeList = MutableLiveData<List<Route>>(emptyList())
    val routeList: LiveData<List<Route>> = _routeList

    init {
        _routeList.value = listOf(
            Route("루트1", "내용 영역", true),
            Route("루트2", "루트 내용", false),
            Route("루트3", "내용 영역3", false),
            Route("루트4", "내용 영역4", true),
            Route("루트5", "내용 영역5", true),
        )
    }

}