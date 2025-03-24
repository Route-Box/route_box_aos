package com.daval.routebox.presentation.ui.route.write

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.Activity
import com.daval.routebox.domain.model.ActivityImage
import com.daval.routebox.domain.model.ActivityResult
import com.daval.routebox.domain.model.Category
import com.daval.routebox.domain.model.RoutePoint
import com.daval.routebox.domain.model.RoutePointRequest
import com.daval.routebox.domain.model.SearchActivityResult
import com.daval.routebox.domain.repositories.OpenApiRepository
import com.daval.routebox.domain.repositories.RouteRepository
import com.daval.routebox.presentation.ui.route.write.RouteCreateActivity.Companion.TODAY
import com.daval.routebox.presentation.utils.DateConverter
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteWriteViewModel @Inject constructor(
    private val repository: RouteRepository,
): ViewModel() {
    private val _routeId = MutableLiveData<Int>()
    val routeId: LiveData<Int> = _routeId

    private var _activityId: Long = 0

    private val _activity = MutableLiveData<Activity?>()
    val activity: LiveData<Activity?> = _activity

    val placeSearchKeyword = MutableLiveData("")
    var imageList = ArrayList<ActivityImage>(arrayListOf())
    var deletedImageIds = ArrayList<Int>(arrayListOf())

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

    private val _isRequestSuccess = MutableLiveData<Boolean>()
    val isRequestSuccess: LiveData<Boolean> = _isRequestSuccess

    private val _currentCoordinate = MutableLiveData<LatLng>()
    val currentCoordinate: LiveData<LatLng> = _currentCoordinate

    private val _preCoordinate = MutableLiveData<LatLng>()
    val preCoordinate: LiveData<LatLng> = _preCoordinate

    private val _checkIsContinuedActivity = MutableLiveData<Boolean>(false)
    val checkIsContinuedActivity = _checkIsContinuedActivity

    init {
        _activity.value = Activity()
        _categoryETC.value = false
        _currentCoordinate.value = LatLng(0.0, 0.0)
    }

    fun initActivityInEditAndSaveMode(activity: ActivityResult) {
        _activityId = activity.activityId.toLong()
        _activity.value = activity.convertToActivity()
        placeSearchKeyword.value = activity.locationName // 장소
        imageList = activity.activityImages // 이미지
        // 시간 초기화
        _date.value = DateConverter.convertDateStringToLocalDate(activity.visitDate)

        if (activity.startTime != "") {
            _startTimePair.value = DateConverter.convertTimeStringToIntPair(activity.startTime)
        }
        if (activity.endTime != "") {
            _endTimePair.value = DateConverter.convertTimeStringToIntPair(activity.endTime)
        }

        if (activity.category != "") {
            if (Category.getCategoryByName(activity.category) == Category.ETC) { // 기타 카테고리의 경우
                _categoryETC.value = true
            }
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

    // 구글 텍스트로 장소 검색
    fun searchPlace(placesClient: PlacesClient) {
        viewModelScope.launch {
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.LOCATION
            )

            val request = SearchByTextRequest.builder(placeSearchKeyword.value.toString(), placeFields)
                .build()

            placesClient.searchByText(request)
                .addOnSuccessListener { response ->
                    _placeSearchPage.value = 1
                    val places = response.places
                    // places를 RecyclerView에 표시
                    _placeSearchResult.value = places.map { place ->
                        SearchActivityResult(
                            place.id!!,
                            place.displayName!!,
                            place.location!!,
                            place.formattedAddress!!
                        )
                    } as ArrayList
                    _isKeywordEndPage.value = true //TODO: 페이징 처리 필요
                    _placeSearchMode.value = true
                }
                .addOnFailureListener { exception ->
                    Log.e("RouteWriteVM", "Error searching places", exception)
                }
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

    // 활동 임시저장을 위한 데이터 작성 여부 확인
    fun returnActivityDataIsEmpty(): Boolean {
        return activity.value?.locationName != ""
                || activity.value?.startTime != "" || activity.value?.endTime != ""
                || activity.value?.category != "" || activity.value?.description != "" || activity.value?.activityImages?.size != 0
    }

    fun returnActivity(): ActivityResult {
        return ActivityResult(
            -1,
            _activity.value?.locationName!!,
            _activity.value?.address!!,
            _activity.value?.latitude!!,
            _activity.value?.longitude!!,
            _activity.value?.visitDate!!,
            _activity.value?.startTime!!,
            _activity.value?.endTime!!,
            _activity.value?.category!!,
            _activity.value?.description!!,
            arrayListOf()
        )
    }

    // 활동 추가
    fun addActivity(context: Context) {
        viewModelScope.launch {
            _isRequestSuccess.value = repository.createActivity(
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

    // 활동 수정
    fun editActivity(context: Context) {
        val addedImages = imageList.filter{ it.id == -1 }.map { it.url } // 새로 추가한 이미지
        viewModelScope.launch {
            _isRequestSuccess.value = repository.updateActivity(
                context,
                _routeId.value!!,
                _activityId.toInt(),
                _activity.value!!,
                addedImages,
                deletedImageIds
            )
        }
    }

    // 지도 점 추가
    fun addDots(routeDotsList: ArrayList<RoutePointRequest?>?) {
        viewModelScope.launch {
            if (routeId.value != null && _currentCoordinate.value != null && routeDotsList?.size != 0) {
                repository.addRouteDots(
                    _routeId.value!!,
                    RoutePoint(routeDotsList)
                )
                setPreCoordinate(_currentCoordinate.value!!)
            }
        }
    }
}

const val MapCameraRadius = 2500