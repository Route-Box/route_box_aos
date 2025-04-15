package com.daval.routebox.presentation.ui.seek

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.MyInfoResponse
import com.daval.routebox.domain.model.PointHistory
import com.daval.routebox.domain.model.PointHistoryResponse
import com.daval.routebox.domain.model.RoutePreview
import com.daval.routebox.domain.repositories.AuthRepository
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.domain.repositories.SeekRepository
import com.daval.routebox.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeekViewModel @Inject constructor(
    private val repository: RouteRepository,
    private val userRepository: UserRepository,
    private val seekRepository: SeekRepository
): ViewModel() {
    private val _routeList = MutableLiveData<ArrayList<RoutePreview>>()
    val routeList: LiveData<ArrayList<RoutePreview>> = _routeList

    var selectedRouteId = 0

    private val _page = MutableLiveData(0)
    val page: LiveData<Int> = _page

    private val _myInfo = MutableLiveData<MyInfoResponse>()
    val myInfo: LiveData<MyInfoResponse> = _myInfo

    private val _pointHistoryList = MutableLiveData<PointHistoryResponse>()
    val pointHistoryList: LiveData<PointHistoryResponse> = _pointHistoryList

    private val _pointHistoryPage = MutableLiveData<Int>(0)
    val pointHistoryPage: LiveData<Int> = _pointHistoryPage

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

    fun returnPointHistory(): List<PointHistory> {
        return if (pointHistoryList.value != null && pointHistoryList.value!!.content.isNotEmpty()) {
            pointHistoryList.value!!.content
        } else listOf()
    }

    fun getRouteList() {
        viewModelScope.launch {
            val getRouteListResult = repository.getSearchRouteList(_page.value!!, ROUTE_SIZE).result
            if (getRouteListResult.size != 0) {
                _routeList.value = getRouteListResult
            }
        }
    }

    fun getPointHistories() {
        viewModelScope.launch {
            val pointHistoryList = seekRepository.getPointHistories(_pointHistoryPage.value!!.toInt(), ROUTE_HISTORY_SIZE)
            if (pointHistoryList.content.isNotEmpty()) {
                _pointHistoryList.value = pointHistoryList
            }
            _pointHistoryPage.value = _pointHistoryPage.value!! + 1
        }
    }

    fun getMyInformation() {
        viewModelScope.launch {
            _myInfo.value = userRepository.getMyInfo()
        }
    }

    companion object {
        const val ROUTE_SIZE = 3
        const val ROUTE_HISTORY_SIZE = 10
    }
}