package com.example.routebox.presentation.ui.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.Activity
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.FilterType
import com.example.routebox.domain.model.Route

class RouteViewModel : ViewModel() {
    private val _routeList = MutableLiveData<List<Route>>(emptyList())
    val routeList: LiveData<List<Route>> = _routeList

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route

    var selectedPosition: Int = 0

    init {
        _routeList.value = listOf(
            Route(
                "루트1", "모든 활동 표시 유형 테스트", false,
                FilterOption.findOptionsByFilterType(FilterType.HOW_LONG).map { it.optionName },
                listOf(
                    Activity("기본", "음식점", "강릉시 경포동 경포로1", "13:00", "17:00", emptyList(), null),
                    Activity("이미지만 있을 경우", "SNS 스팟", "강릉시 경포동 경포로3", "13:00", "17:00", listOf("https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f", "https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f", "https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f"), null),
                    Activity("설명만 있을 경우", "화장실", "강릉시 경포동 경포로4", "13:00", "17:00", emptyList(), "장소에 대한 설명이 들어갑니다. 내용이 많을 때는 2줄로 표현합니다."),
                    Activity("이미지와 설명 모두 있을 경우", "관광명소", "강릉시 경포동 경포로2", "13:00", "17:00", listOf("https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f"), "설명 와랄라"),
                )
            ),
            Route(
                "루트2", "태그 X, 활동 하나 있음", false,
                emptyList(),
                listOf(
                    Activity("강릉 해파랑물회", "관광명소", "강릉시 경포동 경포로", "13:00", "17:00", emptyList(), null)
                )
            ),
            Route(
                "루트3", "태그 O, 활동 X", true,
                FilterOption.findOptionsByFilterType(FilterType.ROUTE_STYLE).map { it.optionName },
                emptyList()
            ),
            Route("루트4", "nothing", true, emptyList(), emptyList()),
        )
    }

    fun setRoute(route: Route) {
        _route.value = route
    }
}