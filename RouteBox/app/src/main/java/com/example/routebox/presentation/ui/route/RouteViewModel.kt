package com.example.routebox.presentation.ui.route

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routebox.domain.model.MyRoute
import com.example.routebox.domain.model.RouteDetail
import com.example.routebox.domain.repositories.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    private val _isTracking = MutableLiveData<Boolean>()
    val isTracking: LiveData<Boolean> = _isTracking

    private val _routeList = MutableLiveData<List<MyRoute>>(emptyList())
    val routeList: LiveData<List<MyRoute>> = _routeList

    private val _route = MutableLiveData<RouteDetail>()
    val route: LiveData<RouteDetail> = _route

    var selectedPosition: Int = 0

    init {
        _isTracking.value = false
        _routeList.value = listOf(
            MyRoute()
        )
    }

    fun setIsTracking() {
        _isTracking.value = !_isTracking.value!!
    }

    fun setRoute(route: RouteDetail) {
        _route.value = route
    }
}