package com.daval.routebox.presentation.ui.seek

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.BuyRouteRequest
import com.daval.routebox.domain.model.RoutePreview
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.domain.repositories.SeekRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeekViewModel @Inject constructor(
    private val repository: RouteRepository,
    private val seekRepository: SeekRepository
): ViewModel() {
    private val _routeList = MutableLiveData<ArrayList<RoutePreview>>()
    val routeList: LiveData<ArrayList<RoutePreview>> = _routeList

    var selectedRouteId = 0

    private val _page = MutableLiveData<Int>()
    val page: LiveData<Int> = _page

    private val _buyResult = MutableLiveData<String>()
    val buyResult: LiveData<String> = _buyResult
    
    // TODO: Enum으로 변경
    private val _paymentMethod = MutableLiveData("POINT")
    val paymentMethod: LiveData<String> = _paymentMethod

    init {
        _routeList.value = arrayListOf()
        _page.value = 0
    }

    fun setPage(page: Int) {
        _page.value = page
    }

    fun refresh() {
        _page.value = 0
        _routeList.value = arrayListOf()
    }

    fun refreshRouteList() {
        _routeList.value = arrayListOf()
    }

    fun getRouteList() {
        viewModelScope.launch {
            val getRouteListResult = repository.getSearchRouteList(_page.value!!, ROUTE_SIZE).result
            if (getRouteListResult.size != 0) {
                _routeList.value = getRouteListResult
            }
        }
    }

    fun buyRoute(routeId: Int) {
        viewModelScope.launch {
            _buyResult.value = seekRepository.buyRoute(routeId, BuyRouteRequest(paymentMethod.value!!))
        }
    }

    companion object {
        const val ROUTE_SIZE = 3
    }
}