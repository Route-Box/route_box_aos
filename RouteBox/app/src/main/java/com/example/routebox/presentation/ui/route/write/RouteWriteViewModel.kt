package com.example.routebox.presentation.ui.route.write

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routebox.domain.model.Activity
import com.example.routebox.domain.model.ActivityResult
import com.example.routebox.domain.model.RoutePointRequest
import com.example.routebox.domain.model.SearchActivityResult
import com.example.routebox.domain.repositories.RouteRepository
import com.example.routebox.presentation.ui.route.write.RouteCreateActivity.Companion.TODAY
import com.example.routebox.presentation.utils.DateConverter
import com.example.routebox.presentation.utils.DateConverter.convertKSTLocalDateTimeToUTCString
import com.example.routebox.presentation.utils.DateConverter.getAPIFormattedDate
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteWriteViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    private val _routeId = MutableLiveData<Int>()
    val routeId: LiveData<Int> = _routeId

    private val _activity = MutableLiveData<Activity>()
    val activity: LiveData<Activity> = _activity

    private val _placeSearchKeyword = MutableLiveData<String>()
    val placeSearchKeyword: LiveData<String> = _placeSearchKeyword

    private val _placeSearchMode = MutableLiveData<Boolean>()
    val placeSearchMode: LiveData<Boolean> = _placeSearchMode

    private val _placeSearchResult = MutableLiveData<ArrayList<SearchActivityResult>>()
    val placeSearchResult: LiveData<ArrayList<SearchActivityResult>> = _placeSearchResult

    private val _placeSearchPage = MutableLiveData<Int>()
    val placeSearchPage: LiveData<Int> = _placeSearchPage

    // 장소 검색 API 결과가 마지막 페이지인지 확인 후 페이징 마무리
    private val _isEndPage = MutableLiveData<Boolean>()
    val isEndPage: LiveData<Boolean> = _isEndPage

    private val _date = MutableLiveData<LocalDate>(TODAY)
    val date: LiveData<LocalDate> = _date

    private val _startTimePair = MutableLiveData<Pair<Int, Int>?>()
    val startTimePair: LiveData<Pair<Int, Int>?> = _startTimePair

    private val _endTimePair = MutableLiveData<Pair<Int, Int>?>()
    val endTimePair: LiveData<Pair<Int, Int>?> = _endTimePair

    private val _categoryETC = MutableLiveData<Boolean>()
    val categoryETC: LiveData<Boolean> = _categoryETC

    private val _btnEnabled = MutableLiveData<Boolean>()
    val btnEnabled: LiveData<Boolean> = _btnEnabled

    private val _activityResult = MutableLiveData<ActivityResult>()
    val activityResult: LiveData<ActivityResult> = _activityResult

    private val _currentCoordinate = MutableLiveData<LatLng>()
    val currentCoordinate: LiveData<LatLng> = _currentCoordinate

    private val _preCoordinate = MutableLiveData<LatLng>()
    val preCoordinate: LiveData<LatLng> = _preCoordinate

    init {
        _activity.value = Activity("", "", "", "",
            TODAY.toString(), changeTimeToString(_startTimePair.value), changeTimeToString(_endTimePair.value),
            "", "", arrayListOf()
        )
        _categoryETC.value = false
        _activityResult.value = ActivityResult()
    }

    fun setRouteId(routeId: Int) {
        this._routeId.value = routeId
    }

    fun setCurrentCoordinate(coordinate: LatLng) {
        _currentCoordinate.value = coordinate
    }

    fun setPreCoordinate(coordinate: LatLng) {
        _preCoordinate.value = coordinate
    }

    fun setPlaceSearchResult(result: ArrayList<SearchActivityResult>) {
        _placeSearchResult.value = result
    }

    fun setPlaceSearchPage(page: Int) {
        _placeSearchPage.value = page
    }

    fun setPlaceSearchMode(mode: Boolean) {
        _placeSearchMode.value = mode
    }

    fun setPlaceSearchKeyword(query: String) {
        _placeSearchKeyword.value = query
    }

    fun updateDate(date: LocalDate) {
        _activity.value?.visitDate = getAPIFormattedDate(date)

        checkBtnEnabled()
    }

    fun updateTime(isStartTime: Boolean, timePair: Pair<Int, Int>) {
        if (isStartTime) {
            _activity.value?.startTime = changeTimeToString(timePair)
            _startTimePair.value = timePair
        }
        else {
            _activity.value?.endTime = changeTimeToString(timePair)
            _endTimePair.value = timePair
        }

        checkBtnEnabled()
    }

    fun changeTimeToString(time: Pair<Int, Int>?): String {
        if (time != null) {
            val df = DecimalFormat("00")
            return "${df.format(time.first)}:${df.format(time.second)}"
        }

        return ""
    }

    fun setCategoryETC(category: Boolean) {
        _categoryETC.value = category
    }

    fun resetActivity() {
        _activity.value = Activity("", "", "", "",
            TODAY.toString(), changeTimeToString(_startTimePair.value), changeTimeToString(_endTimePair.value),
            "", "", arrayListOf()
        )
        checkBtnEnabled()
    }

    fun searchPlace() {
        viewModelScope.launch {
            _placeSearchPage.value = 1

            val response = repository.searchKakaoPlace(_placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isEndPage.value = response.meta.is_end
            _placeSearchMode.value = true
        }
    }

    fun pagingPlace() {
        viewModelScope.launch {
            val response = repository.searchKakaoPlace(_placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isEndPage.value = response.meta.is_end
        }
    }

    fun resetCategory() {
        _activity.value?.category = ""
    }

    fun checkBtnEnabled() {
        _btnEnabled.value = _activity.value?.locationName != ""
                && _activity.value?.visitDate != "" && _activity.value?.startTime != ""
                && _activity.value?.endTime != "" && _activity.value?.category != ""
    }

    fun addActivity(context: Context) {
        viewModelScope.launch {
            _activityResult.value = repository.createActivity(
                context,
                _routeId.value!!,
                _activity.value?.locationName!!,
                _activity.value?.address!!,
                _activity.value?.latitude!!,
                _activity.value?.longitude!!,
                _activity.value?.visitDate!!,
                _activity.value?.startTime!!,
                _activity.value?.endTime!!,
                _activity.value?.category!!,
                _activity.value?.description,
                _activity.value?.activityImages!!
            )
        }
    }

    fun addDot(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            if (routeId.value != null && _currentCoordinate.value != null) {
                repository.addRouteDot(
                    _routeId.value!!,
                    RoutePointRequest(
                        latitude.toString(),
                        longitude.toString(),
                        convertKSTLocalDateTimeToUTCString(LocalDateTime.now())
                    )
                )

                setPreCoordinate(_currentCoordinate.value!!)
            }
        }
    }
}