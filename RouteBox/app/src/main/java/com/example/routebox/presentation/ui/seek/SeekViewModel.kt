package com.example.routebox.presentation.ui.seek

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routebox.domain.model.RoutePreview
import com.example.routebox.domain.repositories.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeekViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    private val _routeList = MutableLiveData<ArrayList<RoutePreview>>()
    val routeList: LiveData<ArrayList<RoutePreview>> = _routeList

    private val _page = MutableLiveData<Int>()
    val page: LiveData<Int> = _page

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

    companion object {
        const val ROUTE_SIZE = 3
    }
}