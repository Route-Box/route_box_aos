package com.example.routebox.presentation.ui.route

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.Activity
import com.example.routebox.domain.model.Category
import com.example.routebox.domain.model.FilterOption
import com.example.routebox.domain.model.FilterType
import com.example.routebox.domain.model.Route
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class RouteViewModel : ViewModel() {
    private val _isTracking = MutableLiveData<Boolean>()
    val isTracking: LiveData<Boolean> = _isTracking

    private val _routeList = MutableLiveData<List<Route>>(emptyList())
    val routeList: LiveData<List<Route>> = _routeList

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route

    var selectedPosition: Int = 0

    init {
        _isTracking.value = false
        _routeList.value = listOf(
            Route(
                "루트1", "모든 활동 표시 유형 테스트", false,
                FilterOption.findOptionsByFilterType(FilterType.MEANS_OF_TRANSPORTATION).map { it.optionName },
                mutableListOf(
                    Activity("강릉 해파랑물회", "강릉시 경포동 경포로", "22", "23", "2024-08-25", "10:00", "12:00", "식당", "장소 설명설명설명", arrayOf("https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f", "https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f", "https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f")),
                    Activity("설명만 있을 경우", "강릉시 경포동 경포로", "22", "23", "2024-08-25", "10:00", "12:00", "식당", "", arrayOf("https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f", "https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f", "https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f")),
                    Activity("설명만 있을 경우", "강릉시 경포동 경포로", "22", "23", "2024-08-25", "10:00", "12:00", "식당", "장소에 대한 설명이 들어갑니다. 내용이 많을 때는 2줄로 표현합니다.", null),
                    Activity("이미지 & 설명", "강릉시 경포동 경포로", "22", "23", "2024-08-25", "10:00", "12:00", "식당", "장소 설명설명설명", arrayOf("https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f", "https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f", "https://github.com/nahy-512/nahy-512/assets/101113025/3fb8e968-e482-4aff-9334-60c41014a80f"))
                )
            ),
            Route(
                "루트2", "태그 X, 활동 하나 있음", false,
                emptyList(),
                mutableListOf(
                    Activity("강릉 해파랑물회", "강릉시 경포동 경포로", "22", "23", "2024-08-25", "10:00", "12:00", "식당", "장소 설명설명설명", arrayOf())
                )
            ),
            Route(
                "루트3", "태그 O, 활동 X", true,
                FilterOption.findOptionsByFilterType(FilterType.ROUTE_STYLE).map { it.optionName },
                mutableListOf()
            ),
            Route("루트4", "nothing", true, emptyList(), mutableListOf()),
        )
    }

    fun setIsTracking() {
        _isTracking.value = !_isTracking.value!!
    }

    fun setRoute(route: Route) {
        _route.value = route
    }
}