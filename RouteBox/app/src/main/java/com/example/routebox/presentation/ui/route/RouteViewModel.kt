package com.example.routebox.presentation.ui.route

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routebox.domain.model.Insight
import com.example.routebox.domain.model.MyRoute
import com.example.routebox.domain.model.RouteDetail
import com.example.routebox.domain.model.RoutePublicRequest
import com.example.routebox.domain.repositories.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    private val _insight = MutableLiveData<Insight>()
    val insight: LiveData<Insight> = _insight

    private val _route = MutableLiveData<RouteDetail>(RouteDetail())
    val route: LiveData<RouteDetail> = _route

    var isPublic: Boolean = false
    var selectedRouteId: Int = 0

    private val _isDeleteRouteSuccess = MutableLiveData<Boolean>(false)
    val isDeleteRouteSuccess: LiveData<Boolean> = _isDeleteRouteSuccess

    init {
        _isTracking.value = false
    }

    /** 내 루트 목록 조회 */
    fun tryGetMyRouteList() {
        viewModelScope.launch {
            _routeList.value = repository.getMyRouteList()
        }
    }

    /** 내 루트 인사이트 조회 */
    fun tryGetInsight() {
        viewModelScope.launch {
            _insight.value = repository.getInsight()
        }
    }

    /** 내 루트 상세조회 */
    fun tryGetMyRouteDetail(routeId: Int) {
       viewModelScope.launch {
           _route.value = repository.getRouteDetail(routeId)
           selectedRouteId = routeId
           isPublic = _route.value!!.isPublic
       }
    }

    /** 루트 공개 여부 수정 */
    fun tryChangePublic() {
        viewModelScope.launch {
            val response = repository.updateRoutePublic(selectedRouteId, RoutePublicRequest(!isPublic))
            _route.value = _route.value!!.copy(isPublic = response.isPublic)
        }
    }

    /** 루트 삭제 */
    fun tryDeleteRoute() {
        viewModelScope.launch {
            _isDeleteRouteSuccess.value = (repository.deleteRoute(selectedRouteId).routeId != -1)
        }
    }

    fun setIsTracking() {
        _isTracking.value = !_isTracking.value!!
    }
}