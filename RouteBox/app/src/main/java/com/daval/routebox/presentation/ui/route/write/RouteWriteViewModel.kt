package com.daval.routebox.presentation.ui.route.write

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.Activity
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.CategoryGroupCode
import com.daval.routebox.domain.model.ConvenienceCategoryResult
import com.daval.routebox.domain.model.RoutePointRequest
import com.daval.routebox.domain.model.SearchActivityResult
import com.daval.routebox.domain.model.WeatherData
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.domain.repositories.OpenApiRepository
import com.daval.routebox.presentation.config.Constants.OPEN_API_BASE_URL
import com.daval.routebox.presentation.ui.route.write.RouteCreateActivity.Companion.TODAY
import com.daval.routebox.presentation.utils.DateConverter.convertKSTLocalDateTimeToUTCString
import com.daval.routebox.presentation.utils.DateConverter.getAPIFormattedDate
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
    private val repository: RouteRepository,
    private val openApiRepository: OpenApiRepository
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

    // 편의기능
    private val _placeCategoryResult = MutableLiveData<ArrayList<ConvenienceCategoryResult>>()
    val placeCategoryResult: LiveData<ArrayList<ConvenienceCategoryResult>> = _placeCategoryResult

    private val _placeCategory = MutableLiveData<CategoryGroupCode>()
    val placeCategory: LiveData<CategoryGroupCode> = _placeCategory

    private val _placeCategoryPage = MutableLiveData<Int>()
    val placeCategoryPage: LiveData<Int> = _placeCategoryPage

    private val _isCategoryEndPage = MutableLiveData<Boolean>()
    val isCategoryEndPage: LiveData<Boolean> = _isCategoryEndPage

    private val _cameraPosition = MutableLiveData<LatLng>()
    val cameraPosition: LiveData<LatLng> = _cameraPosition

    private val _weatherRegion = MutableLiveData<String>()
    val weatherRegion: LiveData<String> = _weatherRegion

    private val _weatherMainData = MutableLiveData<WeatherData>()
    val weatherMainData: LiveData<WeatherData> = _weatherMainData


    init {
        _activity.value = Activity("", "", "", "",
            TODAY.toString(), changeTimeToString(_startTimePair.value), changeTimeToString(_endTimePair.value),
            "", "", arrayListOf()
        )
        _categoryETC.value = false
        _activityResult.value = ActivityResult()
        _isCategoryEndPage.value = false
        _placeCategoryPage.value = 1
        _placeCategoryResult.value = arrayListOf()
        _currentCoordinate.value = LatLng.from(null)
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

    fun setWeatherMainData(weatherData: WeatherData) {
        _weatherMainData.value = weatherData
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

    fun setKakaoCategory(category: CategoryGroupCode) {
        _placeCategory.value = category
        _placeCategoryPage.value = 1
        _placeCategoryResult.value = arrayListOf()
        _isCategoryEndPage.value = false

        searchCategory()
    }

//    fun setTourCategory() {
//        getTourList()
//    }

    fun setCameraPosition(position: LatLng) {
        _cameraPosition.value = position
    }

    fun setPlaceCategoryResult() {
        _placeCategoryResult.value = arrayListOf()
    }

    fun resetActivity() {
        _activity.value = Activity("", "", "", "",
            TODAY.toString(), changeTimeToString(_startTimePair.value), changeTimeToString(_endTimePair.value),
            "", "", arrayListOf()
        )
        checkBtnEnabled()
    }

    // 카카오 키워드로 장소 검색
    fun searchPlace() {
        viewModelScope.launch {
            _placeSearchPage.value = 1

            val response = repository.searchKakaoPlace(_placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isKeywordEndPage.value = response.meta.is_end
            _placeSearchMode.value = true
        }
    }

    // 장소 검색 페이징 처리
    fun pagingPlace() {
        viewModelScope.launch {
            val response = repository.searchKakaoPlace(_placeSearchKeyword.value.toString(), _placeSearchPage.value!!)
            _placeSearchResult.value = response.documents as ArrayList
            _isKeywordEndPage.value = response.meta.is_end
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

    // 편의 기능 카테고리 검색
    private fun searchCategory() {
        viewModelScope.launch {
            while (true) {
                if (cameraPosition.value != null && placeCategory.value != null) {
                    val response = repository.searchKakaoCategory(_placeCategory.value!!, cameraPosition.value!!.latitude.toString(), cameraPosition.value!!.longitude.toString(), placeCategoryPage.value!!, MapCameraRadius)
                    var result = response.documents.map {
                        ConvenienceCategoryResult(it.place_name, null, it.y, it.x)
                    }
                    _placeCategoryResult.value!!.addAll(result)
                    _isCategoryEndPage.value = response.meta.is_end
                    _placeCategoryPage.value = _placeCategoryPage.value!! + 1

                    if (_isCategoryEndPage.value == true) break
                }
            }
        }
    }

    // 장소 검색 페이징 처리
//    fun pagingSearchCategory() {
//        viewModelScope.launch {
//            val response = repository.searchKakaoCategory(
//                _placeCategory.value!!, cameraPosition.value!!.latitude.toString(), cameraPosition.value!!.longitude.toString()
//            )
//            _placeCategoryResult.value = response.documents as ArrayList
//            _isCategoryEndPage.value = response.meta.is_end
//        }
//    }

    // TODO: 나중에 아래 방식으로 수정
//    private fun getTourList() {
//        viewModelScope.launch {
//            val response = tourRepository.getTourList(
//                "AND", "Route Box", BuildConfig.OPEN_API_SERVICE_KEY,
//                mapX = cameraPosition.value!!.longitude.toString(), mapY = cameraPosition.value!!.latitude.toString(),
//                MapCameraRadius.toString(), "12", "json"
//            )
//            Log.d("ROUTE-TEST", "response = $response")
//        }
//    }

    @SuppressLint("DefaultLocale")
    fun getWeatherList() {
        viewModelScope.launch {
            val response = openApiRepository.getWeatherList(
                OPEN_API_BASE_URL, 1, 50, "JSON",
                "20240922", "0500",
                55,
                127
            )
//            Log.d("RemoteTourDataSource", "response = $response")
        }
    }

    fun getRegionCode(latitude: String, longitude: String) {
        viewModelScope.launch {
            val response = repository.getKakaoRegionCode(
                latitude, longitude
            )
            _weatherRegion.value = "${response.documents[0].region_1depth_name} ${response.documents[0].region_2depth_name} ${response.documents[0].region_3depth_name}"
        }
    }
}

const val MapCameraRadius = 2500