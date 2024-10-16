package com.daval.routebox.presentation.ui.route.edit

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.domain.model.FilterType
import com.daval.routebox.domain.model.RouteDetail
import com.daval.routebox.domain.model.RouteUpdateRequest
import com.daval.routebox.domain.repositories.RouteRepository
import com.kakao.vectormap.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class RouteEditViewModel @Inject constructor(
    private val repository: RouteRepository
) : ViewModel() {
    private val _routeId = MutableLiveData<Int>()
    val routeId: LiveData<Int> = _routeId

    private val _deleteActivityId = MutableLiveData<Int>()
    val deleteActivityId = _deleteActivityId

    private val _route = MutableLiveData<RouteDetail>()
    val route: LiveData<RouteDetail> = _route

    // 현재 단계 ID를 담는 LiveData
    private val _stepId = MutableLiveData<Int>()
    val stepId: LiveData<Int> = _stepId

    var isEditMode by Delegates.notNull<Boolean>()

    val routeTitle: MutableLiveData<String> = MutableLiveData()

    val routeContent: MutableLiveData<String> = MutableLiveData()

    var tagList: ArrayList<String> = arrayListOf()

    private val selectedOptionMap: MutableLiveData<Map<FilterType, List<FilterOption>>> =
        MutableLiveData(mapOf())

    private val _isEnabledButton = MutableLiveData<Boolean>()
    val isEnabledButton: LiveData<Boolean> = _isEnabledButton

    private val _isEditSuccess = MutableLiveData<Boolean>()
    val isEditSuccess: LiveData<Boolean> = _isEditSuccess

    init {
        _routeId.value = -1
        _deleteActivityId.value = -1
        _route.value = RouteDetail()
    }

    fun setRouteId(routeId: Int) {
        this._routeId.value = routeId
    }

    /** 내 루트 상세조회 */
    fun tryGetMyRouteDetail() {
        viewModelScope.launch {
            _route.value = repository.getRouteDetail(_routeId.value!!)
        }
    }

    /** 루트 수정 */
    fun tryEditRoute() {
        viewModelScope.launch {
            val routeUpdateRequest = RouteUpdateRequest(
                routeTitle.value, routeContent.value,
                whoWith = convertToServerTagData(FilterType.WITH_WHOM)?.first(),
                numberOfPeople = convertToServerTagData(FilterType.HOW_MANY)?.first()?.take(1)?.toInt(),
                numberOfDays = convertToServerTagData(FilterType.HOW_LONG)?.first(),
                routeStyles = convertToServerTagData(FilterType.ROUTE_STYLE),
                transportation = convertToServerTagData(FilterType.MEANS_OF_TRANSPORTATION)?.first()
            )
            _isEditSuccess.value = repository.updateRoute(
                _route.value!!.routeId,
                routeUpdateRequest
            ).routeId != -1
            Log.d("RouteEditViewModel", "EditRouteRequest: $routeUpdateRequest")
        }
    }

    /** 활동 삭제 */
    fun deleteActivity(activityId: Int) {
        viewModelScope.launch {
            _deleteActivityId.value = repository.deleteActivity(_routeId.value!!, activityId).activityId
        }
    }

    fun setDeleteActivityId(activityId: Int) {
        _deleteActivityId.value = activityId
    }

    fun setStepId(stepId: Int) {
        _stepId.value = stepId
    }

    fun setRoute(route: RouteDetail) {
        _route.value = route
        tagList = combineAllServerTagsByList()
        initSelectedOptionMap(FilterOption.findOptionsByNames(tagList))
    }
    
    // 서버에서 받아온 whoWith, numberOfPeople, routeStyles, transportation를 통합
    private fun combineAllServerTagsByList(): ArrayList<String> {
        val tagNameList: ArrayList<String> = arrayListOf()
        tagNameList.add(_route.value!!.whoWith)
        tagNameList.add(_route.value!!.numberOfDays)
        tagNameList.add(FilterOption.getNumberOfPeopleText(_route.value!!.numberOfPeople))
        tagNameList.addAll(_route.value!!.routeStyles)
        tagNameList.add(_route.value!!.transportation)
        return tagNameList
    }

    private fun initSelectedOptionMap(filterOptions: List<FilterOption>) {
        // 현재 selectedOptionMap을 가져옴
        val currentMap = selectedOptionMap.value?.toMutableMap() ?: mutableMapOf()

        // filterOptions 리스트를 순회하며 각 FilterOption의 filterType에 따라 그룹화
        filterOptions.groupBy { it.filterType }.forEach { (filterType, options) ->
            // 필터 타입에 따라 Set<FilterOption>으로 변환하여 맵에 저장
            currentMap[filterType] = options
        }

        // MutableLiveData에 새로운 맵을 저장
        selectedOptionMap.value = currentMap
    }

    fun updateSelectedOption(option: FilterOption, isSelected: Boolean) {
        val prevOptionMap = selectedOptionMap.value!!.toMutableMap()
        if (prevOptionMap.contains(option.filterType)) { // 기존 filterType가 존재하는 경우
            val tagList = prevOptionMap[option.filterType]?.toMutableList() ?: mutableListOf()
            if (isSelected) { // 리스트에 추가
                tagList.add(option)
            } else { // 리스트에서 삭제
                tagList.remove(option)
            }
            if (tagList.isEmpty()) { // 리스트가 비어있을 경우
                prevOptionMap.remove(option.filterType) // key도 삭제
            } else {
                prevOptionMap[option.filterType] = tagList.toList()
            }
        } else { // 기존 filterType이 비어있을 경우
            if (!isSelected) return
            prevOptionMap[option.filterType] = listOf(option) // 해당 필터에 처음 추가
        }
        if (option == FilterOption.WITH_ALONE) { // '누구와'의 혼자 옵션
            prevOptionMap.remove(FilterType.HOW_MANY) // 몇 명과 옵션 삭제
        }
        selectedOptionMap.value = prevOptionMap
        Log.d("RouteEditViewModel", "selectedOptionMap: ${selectedOptionMap.value}")
        checkButtonEnable()
    }

    fun initRouteTitleAndContent() {
        routeTitle.value = _route.value?.routeName
        routeContent.value = _route.value?.routeDescription
    }

    // 선택된 옵션 중에 '누구와 - 혼자'가 있을 경우
    private fun isHasWithAloneOption(): Boolean {
        return selectedOptionMap.value?.get(FilterType.WITH_WHOM)?.contains(FilterOption.WITH_ALONE)
            ?: false
    }

    // 루트 제목과 내용이 잘 채워졌는지 확인
    private fun isAllContentFilled(): Boolean {
        return !routeTitle.value.isNullOrEmpty() && !routeContent.value.isNullOrEmpty()
    }

    // 모든 선택지가 잘 채워졌는지 확인
    private fun isAllQuestionTypeSelected(): Boolean {
        // 모든 FilterType에 해당하는 키가 있는지 확인
        val selectedMap = selectedOptionMap.value ?: return false
        // 모든 FilterType을 가져옴
        val allFilterTypes = FilterType.entries.toSet()
        // isHasWithAloneOption()이 true인 경우 FilterType.HOW_MANY를 제외
        val requiredFilterTypes = if (isHasWithAloneOption()) {
            allFilterTypes - FilterType.HOW_MANY
        } else {
            allFilterTypes
        }
        // selectedMap의 key들과 requiredFilterTypes를 비교
        return selectedMap.keys.containsAll(requiredFilterTypes) && requiredFilterTypes.all { selectedMap[it]?.isNotEmpty() == true }
    }

    fun checkButtonEnable() {
        _isEnabledButton.value =
            if (isEditMode) (isAllContentFilled() && isAllQuestionTypeSelected()) else isAllContentFilled()
    }

    // 서버에서 받은 tag 데이터를 필터 타입 별로 String으로 변환
    private fun convertToServerTagData(filterType: FilterType): List<String>? {
        return selectedOptionMap.value?.get(filterType)?.map {
            it.optionName
        }
    }

    fun hasActivity(): Boolean {
        return !_route.value!!.routeActivities.isNullOrEmpty()
    }
    // 루트 경로의 평균 좌표로 지도 중심에 위치할 지점을 반환
    fun getRoutePathCenterPoint(): LatLng {
        val routeActivityList = getLatLngRoutePath()
        return LatLng.from(
            routeActivityList.map { it.latitude }.average(),
            routeActivityList.map { it.longitude }.average()
        )
    }

    fun getLatLngRoutePath(): List<LatLng> {
        return _route.value!!.routeActivities.map {
            LatLng.from(it.latitude.toDouble(), it.longitude.toDouble())
        }
    }
}