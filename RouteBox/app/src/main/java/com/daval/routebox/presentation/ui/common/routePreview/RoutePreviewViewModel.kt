package com.daval.routebox.presentation.ui.common.routePreview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daval.routebox.domain.model.FilterOption
import com.daval.routebox.domain.model.RoutePreview
import com.daval.routebox.domain.repositories.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutePreviewViewModel @Inject constructor(
    val repository: RouteRepository
): ViewModel() {

    private val _routePreviewDetail = MutableLiveData<RoutePreview>()
    val routePreviewDetail: LiveData<RoutePreview> = _routePreviewDetail

    private val _tagList = MutableLiveData<ArrayList<String>>()
    val tagList: LiveData<ArrayList<String>> = _tagList

    var routeId: Int = 0

    /** 루트 조회 */
    fun getRoutePreviewData() {
        Log.d("RoutePreviewVM", "routeId: $routeId")
        viewModelScope.launch {
            _routePreviewDetail.value = repository.getRouteDetailPreview(routeId)
            _tagList.value = combineAllServerTagsByList(_routePreviewDetail.value!!)
        }
    }

    // 서버에서 받아온 whoWith, numberOfPeople, routeStyles, transportation를 통합
    private fun combineAllServerTagsByList(routeData: RoutePreview): ArrayList<String> {
        val tagNameList: ArrayList<String> = arrayListOf()
        tagNameList.addAll(
            listOfNotNull(
                routeData.whoWith,
                routeData.numberOfDays,
                FilterOption.getNumberOfPeopleText(routeData.numberOfPeople),
                routeData.transportation
            )
        )
        routeData.routeStyles.let { styles ->
            tagNameList.addAll(styles)
        }
        return tagNameList
    }

    fun getImageUrlList() = _routePreviewDetail.value!!.routeImageUrls
}