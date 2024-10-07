package com.daval.routebox.presentation.ui.route.write

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.Activity
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.Category
import com.daval.routebox.domain.model.RoutePointRequest
import com.daval.routebox.domain.model.SearchActivityResult
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.domain.repositories.OpenApiRepository
import com.daval.routebox.presentation.ui.route.write.RouteCreateActivity.Companion.TODAY
import com.daval.routebox.presentation.utils.DateConverter
import com.daval.routebox.presentation.utils.DateConverter.convertKSTLocalDateTimeToUTCString
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteWriteViewModel @Inject constructor(
    private val repository: RouteRepository,
    private val openApiRepository: OpenApiRepository
): ViewModel() {
    private val _routeId = MutableLiveData<Int>()
    val routeId: LiveData<Int> = _routeId

    private var _activityId: Long = 0

    private val _activity = MutableLiveData<Activity>()
    val activity: LiveData<Activity> = _activity

    val placeSearchKeyword = MutableLiveData<String>("")

    private val _placeSearchMode = MutableLiveData<Boolean>()
    val placeSearchMode: LiveData<Boolean> = _placeSearchMode

    private val _placeSearchResult = MutableLiveData<ArrayList<SearchActivityResult>>()
    val placeSearchResult: LiveData<ArrayList<SearchActivityResult>> = _placeSearchResult

    private val _placeSearchPage = MutableLiveData<Int>()
    val placeSearchPage: LiveData<Int> = _placeSearchPage

    private val _isEditMode = MutableLiveData<Boolean>(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    // 장소 검색 API 결과가 마지막 페이지인지 확인 후 페이징 마무리
    private val _isKeywordEndPage = MutableLiveData<Boolean>()
    val isKeywordEndPage: LiveData<Boolean> = _isKeywordEndPage

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
        _activity.value = Activity()
        _categoryETC.value = false
        _activityResult.value = ActivityResult()
        _currentCoordinate.value = LatLng.from(null)
    }

    fun initActivityInEditMode(activity: ActivityResult) {
        _activityId = activity.activityId.toLong()
        _activity.value = activity.convertToActivity()
        placeSearchKeyword.value = activity.locationName
        // 시간 초기화
        _date.value = DateConverter.convertDateStringToLocalDate(activity.visitDate)
        _startTimePair.value = DateConverter.convertTimeStringToIntPair(activity.startTime)
        _endTimePair.value = DateConverter.convertTimeStringToIntPair(activity.endTime)
        if (Category.getCategoryByName(activity.category) == Category.ETC) { // 기타 카테고리의 경우
            _categoryETC.value = true
        }
    }

    fun setIsEditMode(bool: Boolean) {
        _isEditMode.value = bool
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

    fun updateDate(date: LocalDate) {
        _date.value = date
        _activity.value?.visitDate = DateConverter.getAPIFormattedDate(date)

        checkBtnEnabled()
    }

    fun updateTime(isStartTime: Boolean, timePair: Pair<Int, Int>) {
        if (isStartTime) {
            _activity.value?.startTime = DateConverter.getAPIFormattedTime(timePair)
            _startTimePair.value = timePair
        }
        else {
            _activity.value?.endTime = DateConverter.getAPIFormattedTime(timePair)
            _endTimePair.value = timePair
        }

        checkBtnEnabled()
    }

    fun setCategoryETC(category: Boolean) {
        _categoryETC.value = category
    }

    fun resetActivity() {
        _activity.value = Activity()
        checkBtnEnabled()
    }

    // 카카오 키워드로 장소 검색
    fun searchPlace() {
        viewModelScope.launch {
            _placeSearchPage.value = 1

            val response = repository.searchKakaoPlace(placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isKeywordEndPage.value = response.meta.is_end
            _placeSearchMode.value = true
        }
    }

    // 장소 검색 페이징 처리
    fun pagingPlace() {
        viewModelScope.launch {
            val response = repository.searchKakaoPlace(placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isKeywordEndPage.value = response.meta.is_end
        }
    }

    fun resetCategory() {
        _activity.value = _activity.value?.copy(
            category = ""
        )
    }

    fun checkBtnEnabled() {
        _btnEnabled.value = _activity.value?.locationName != ""
                && _date.value != null && _startTimePair.value != null
                && _endTimePair.value != null && _activity.value?.category != ""
    }

    // 활동 추가
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

    // 지도 점 추가
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

const val MapCameraRadius = 2500