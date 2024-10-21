package com.daval.routebox.presentation.ui.route

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.domain.model.Insight
import com.daval.routebox.domain.model.MyRoute
import com.daval.routebox.domain.model.RouteDetail
import com.daval.routebox.domain.model.RoutePublicRequest
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.presentation.utils.DateConverter
import com.daval.routebox.presentation.utils.SharedPreferencesHelper
import com.daval.routebox.presentation.utils.SharedPreferencesHelper.Companion.APP_PREF_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    // 기록중인 루트가 있을 경우
    private val _isTracking = MutableLiveData<Boolean>(false)
    val isTracking: LiveData<Boolean> = _isTracking

    private val _isLiveTracking = MutableLiveData<Boolean>()
    val isLiveTracking: LiveData<Boolean> = _isLiveTracking

    private val _routeList = MutableLiveData<List<MyRoute>>(emptyList())
    val routeList: LiveData<List<MyRoute>> = _routeList

    private val _insight = MutableLiveData<Insight>()
    val insight: LiveData<Insight> = _insight

    private val _route = MutableLiveData<RouteDetail>(RouteDetail())
    val route: LiveData<RouteDetail> = _route

    private val _tagList = MutableLiveData<ArrayList<String>>()
    val tagList: LiveData<ArrayList<String>> = _tagList

    var isPublic: Boolean = false
    var selectedRouteId: Int = 0
    var recordingRouteId: Int? = null // 현재 기록 중인 루트의 id (null = 기록 중인 루트 X / Int = 기록 중인 루트 O)

    private val _isGetRouteDetailSuccess = MutableLiveData<Boolean>(false)
    val isGetRouteDetailSuccess: LiveData<Boolean> = _isGetRouteDetailSuccess

    private val _isDeleteRouteSuccess = MutableLiveData<Boolean>(false)
    val isDeleteRouteSuccess: LiveData<Boolean> = _isDeleteRouteSuccess

    /** 내 루트 목록 조회 */
    fun tryGetMyRouteList() {
        viewModelScope.launch {
            _routeList.value = repository.getMyRouteList()
        }
    }

    /** 기록 진행 중인 루트 여부 조회 */
    fun tryGetIsRouteRecording() {
        val time = DateConverter.convertKSTLocalDateTimeToUTCString(LocalDateTime.now())
        Log.d("RouteViewModel", "time: $time")
        viewModelScope.launch {
            recordingRouteId = repository.checkRouteIsRecording(time).routeId
            Log.d("RemoteRouteDataSource", "recordingRouteId = $recordingRouteId")
            _isTracking.value = (recordingRouteId != null)
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
           _isGetRouteDetailSuccess.value = (_route.value?.routeId != -1)
           selectedRouteId = routeId
           isPublic = _route.value!!.isPublic
           _tagList.value = combineAllServerTagsByList()
           Log.d("RouteViewModel", "tagList: ${_tagList.value}")
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

    // 서버에서 받아온 whoWith, numberOfPeople, routeStyles, transportation를 통합
    private fun combineAllServerTagsByList(): ArrayList<String> {
        val tagNameList: ArrayList<String> = arrayListOf()
        Log.d("RouteViewModel", "numberOfPeople: ${_route.value!!.numberOfPeople}")
        tagNameList.addAll(
            listOfNotNull(
                _route.value?.whoWith, // 누구와
                _route.value?.numberOfDays, // 며칠 동안
                _route.value?.transportation // 이동 수단
            )
        )
        FilterOption.getNumberOfPeopleText(_route.value!!.numberOfPeople)?.let {
            tagNameList.add(it) // 몇 명과
        }
        _route.value?.routeStyles?.let { styles ->
            tagNameList.addAll(styles) // 루트 스타일
        }
        return tagNameList
    }

    fun getIsLiveTracking(isLiveTracking: Boolean) {
        _isLiveTracking.value = isLiveTracking
    }

    fun setIsLiveTracking(context: Context) {
        var sharedPreferencesHelper = SharedPreferencesHelper(context.getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE))
        _isLiveTracking.value = !sharedPreferencesHelper.getRouteTracking()
    }
}